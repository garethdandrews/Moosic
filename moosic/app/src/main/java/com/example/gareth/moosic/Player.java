package com.example.gareth.moosic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A class to handle all of the functions of the media player.
 */

class Player {

    private static Song[] songList;
    static MediaPlayer player = new MediaPlayer();
    private static DoublyLinkedList songQueue = new DoublyLinkedList();
    private static DoublyLinkedList shuffledSongQueue = new DoublyLinkedList();
    static Node nowPlaying; // A pointer used to indicate the position in the queue
    static boolean libraryActivity;
    private static boolean shuffle = false;
    private static boolean repeat = false;

    /**
     * Called when the application is created
     * @param sl array of songs
     */
    static void setSongList(Song[] sl) {
        songList = sl;
    }

    /**
     * Plays the parsed song and updates the song information on the current activity of the user
     * @param s - song to be played and displayed
     */
    static void playSong(final Context context, Song s) {
        player = MediaPlayer.create(context, s.getRid());
        player.start();

        // Update the song information on the activity
        if (libraryActivity) {
            setLibrarySongInfo(context);
        } else {
            setNowPlayingSongInfo(context);
        }

        // When a song finishes, play the next one in the queue
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (nowPlaying != songQueue.getTail()) {
                        playSong(context, nowPlaying.getNext().getSong());
                }
            }
        });
    }

    /**
     * Sets the information of the currently playing song on the library activity view
     * @param context of the user
     */
    static void setLibrarySongInfo(Context context) {
        setSongInfo(context);

        final ProgressBar progressBar = ((Activity)context).findViewById(R.id.progressBar);
        progressBar.setMax(Integer.parseInt(nowPlaying.getSong().getDuration())/1000);
        final Handler mHandler = new Handler();
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player.getCurrentPosition() <= player.getDuration()){
                    int currentPosition = player.getCurrentPosition() / 1000;
                    progressBar.setProgress(currentPosition);
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }

    /**
     * Sets the information of the currently playing song on the now playing activity view
     * @param context of the user
     */
    static void setNowPlayingSongInfo(Context context) {
        setSongInfo(context);

        TextView songAlbum = ((Activity)context).findViewById(R.id.text_songAlbum);
        TextView duration = ((Activity)context).findViewById(R.id.text_duration);
        songAlbum.setText(nowPlaying.getSong().getAlbum());
        duration.setText(durationConvert(player.getDuration()));

        ImageView artwork = ((Activity)context).findViewById(R.id.artwork);
        artwork.setImageBitmap(nowPlaying.getSong().getArtwork());

        ImageButton btnRepeat = ((Activity)context).findViewById(R.id.button_repeat);
        if (repeat) {
            btnRepeat.setBackground(context.getDrawable(R.drawable.imageview_white_border));
        }
        ImageButton btnShuffle = ((Activity)context).findViewById(R.id.button_shuffle);
        if (shuffle) {
            btnShuffle.setBackground(context.getDrawable(R.drawable.imageview_white_border));
        }

        final TextView currentTime = ((Activity)context).findViewById(R.id.text_currentTime);
        final SeekBar seekBar = ((Activity)context).findViewById(R.id.seekBar);
        seekBar.setMax(Integer.parseInt(nowPlaying.getSong().getDuration())/1000);
        final Handler mHandler = new Handler();
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player.getCurrentPosition() <= player.getDuration()){
                    int currentPosition = player.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    currentTime.setText(durationConvert(player.getCurrentPosition()));
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
    }

    /**
     * A helper method called by setLibrarySongInfo and setNowPlayingSongInfo to avoid code duplication
     * @param context of the user
     */
    private static void setSongInfo(Context context) {
        TextView songTitle = ((Activity)context).findViewById(R.id.text_songTitle);
        TextView songArtist = ((Activity)context).findViewById(R.id.text_songArtist);
        songTitle.setText(Player.nowPlaying.getSong().getSongTitle());
        songArtist.setText(nowPlaying.getSong().getArtist());

        ImageButton btn = ((Activity)context).findViewById(R.id.button_playPause);
        btn.setImageResource(R.drawable.ic_pause_white_24dp);
    }

    /**
     * Converts the time into a user friendly format of 00:00
     * @param t time in integer format
     * @return string in a readable format
     */
    @SuppressLint("DefaultLocale")
    private static String durationConvert(int t) {
        int minutes = (t / 60000) % 60000;
        int seconds = t % 60000 / 1000;

        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Runs when the play/pause button is clicked
     */
    static void playPauseButtonClick(Context context) {
        ImageButton btn = ((Activity)context).findViewById(R.id.button_playPause);
        if (songQueue.isEmpty()) { // No song selected for play, create a queue from first item
            btn.setImageResource(R.drawable.ic_pause_white_24dp);
            createSongQueue(0);
        } else if (player.isPlaying()){
            btn.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            player.pause();
        } else {
            btn.setImageResource(R.drawable.ic_pause_white_24dp);
            player.start();
        }
    }

    /**
     * Runs when the back button is clicked
     */
    static void backButtonClick(Context context) {
        ImageButton btn = ((Activity)context).findViewById(R.id.button_playPause);
        if (nowPlaying.getPrev() != null) { // Checks if there is a song in the previous position of the queue
            if (player.isPlaying()) {
                player.release();
            }
            btn.setImageResource(R.drawable.ic_pause_white_24dp);
            // Play the song previous in the queue
            nowPlaying = nowPlaying.getPrev();
            playSong(context, nowPlaying.getSong());
        } else {
            Toast.makeText(context, "No more songs", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Runs when the forward button is clicked
     */
    static void forwardButtonClick(Context context) {
        ImageButton btn = ((Activity)context).findViewById(R.id.button_playPause);
        if (nowPlaying.getNext() != null) { // Checks if there is a song in the next position of the queue
            if (player.isPlaying()) {
                player.release();
            }
            btn.setImageResource(R.drawable.ic_pause_white_24dp);
            // Play the song next in the queue
            nowPlaying = nowPlaying.getNext();
            playSong(context, nowPlaying.getSong());
        } else {
            Toast.makeText(context, "No more songs", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Runs when the repeat button is clicked
     */
    static void repeatButtonClick(Context context) {
        repeat = !repeat;
        ImageButton btn = ((Activity)context).findViewById(R.id.button_repeat);
        if (repeat) { // Highlights the button to indicate repeat on
            btn.setBackground(context.getDrawable(R.drawable.imageview_white_border));
            // Makes the song queue circular
            if (shuffle) {
                shuffledSongQueue.makeCircular();
            } else {
                songQueue.makeCircular();
            }
        } else { // Sets the button back to default to indicate repeat off
            btn.setBackgroundResource(R.color.windowBackground);
            // Makes the song queue straight
            if (shuffle) {
                shuffledSongQueue.makeUncircular();
            } else {
                songQueue.makeUncircular();
            }
        }
    }

    /**
     * Runs when the shuffle button is clicked
     */
    static void shuffleButtonClick(Context context) {
        shuffle = !shuffle;
        ImageButton btn = ((Activity)context).findViewById(R.id.button_shuffle);
        if (shuffle) { // Highlights the button to indicate shuffle on
            btn.setBackground(context.getDrawable(R.drawable.imageview_white_border));
            // Creates a shuffled song queue
            createShuffledSongQueue();
        } else { // Sets the button back to default to indicate shuffle off
            btn.setBackgroundResource(R.color.windowBackground);
            // Creates a serial song queue
            createSongQueue(0);
        }
    }

    /**
     * This will run when a user clicks play or a song from the list
     * When a user selects a song, it will add all of the songs in the songList to a queue and
     * set the nowPlaying pointer to the selected song
     * @param i - the position of the selected song in the array
     */
    static void createSongQueue(int i) {
        if (!songQueue.isEmpty()) {
            songQueue.deleteList();
        }

        //Add songs to the queue
        for (int j = 0; j < songList.length; j++) {
            songQueue.addEnd(songList[j]);
            if (j == i) { // Sets the nowPlaying pointer to the selected song
                nowPlaying = songQueue.getTail();
            }
        }

        // If the shuffle option is on when the function is called, shuffle it
        if (shuffle) {
            createShuffledSongQueue();
        }

        // If the repeat option is on when the function is called, make queue circular
        if (repeat) {
            if (shuffle) {
                shuffledSongQueue.makeCircular();
            } else {
                songQueue.makeCircular();
            }
        }
    }

    /**
     * Runs when the shuffle option is turned on. A new song queue is created, opposed to shuffling
     * the current one, as it avoids creating another song queue when shuffle is turned off to allow
     * for quick switching between the two modes.
     */
    private static void createShuffledSongQueue() {
        if (!shuffledSongQueue.isEmpty()) {
            shuffledSongQueue.deleteList();
        }

        // Shuffle can only be turned on when there is already a song playing, so it adds the
        // nowPlaying to the front of the queue
        shuffledSongQueue.addStart(nowPlaying.getSong());
        ArrayList<Song> list = new ArrayList<>();
        for (Song aSongList : songList) {
            if (shuffledSongQueue.getHead().getSong() != aSongList) {
                list.add(aSongList);
            }
        }

        // Shuffle the list
        Collections.shuffle(list);
        for (int k = 0; k < list.size(); k++) {
            shuffledSongQueue.addEnd(list.get(k));
        }
        nowPlaying = shuffledSongQueue.getHead();

        // If the repeat option is on when the function is called, make queue circular
        if (repeat) {
            songQueue.makeCircular();
        }
    }
}