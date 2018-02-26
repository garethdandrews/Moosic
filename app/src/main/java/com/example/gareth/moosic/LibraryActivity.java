package com.example.gareth.moosic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Main activity of the application and is on display when the app is launched
 * Displays a list of songs that can be selected for listening
 * Features a condensed control bar at the bottom of view
 */

public class LibraryActivity extends AppCompatActivity {

    Song[] songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        loadSongs();
        populateSongListView();
        Player.setSongList(songList);
        RelativeLayout controlBar = findViewById(R.id.layout_controlBar);
        controlBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Notifies the Player class of the change of activity and updates the song information on screen
     */
    @Override
    protected void onResume() {
        super.onResume();

        Player.libraryActivity = true;
        if (Player.nowPlaying != null) {
            RelativeLayout controlBar = findViewById(R.id.layout_controlBar);
            controlBar.setVisibility(View.VISIBLE);
            Player.setLibrarySongInfo(this);
        }
    }

    /**
     * Adds menu items to the tool bar from the menu xml
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Runs when the user clicks a button on the toolbar
     * Only button available for selection is used to navigate to the now playing activity
     * @param item the selected item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nowPlaying:
                if (Player.nowPlaying != null) {
                    Intent intent = new Intent(this, NowPlayingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.no_song_playing, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Runs when the control bar at the bottom of view is clicked
     * Navigates to now playing activity on click
     * @param view view
     */
    public void controlBarClick(View view) {
        if (Player.nowPlaying != null) {
            Intent intent = new Intent (this, NowPlayingActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Runs when the play/pause button is clicked on the control bar
     * @param view view
     */
    public void playPauseButtonClick(View view) {
        Player.playPauseButtonClick(this);
    }

    /**
     * Runs when the back button is clicked on the control bar
     * @param view view
     */
    public void backButtonClick(View view) {
        Player.backButtonClick(this);
    }

    /**
     * Runs when the forward button is clicked on the control bar
     * @param view view
     */
    public void forwardButtonClick(View view) {
        Player.forwardButtonClick(this);
    }

    /**
     * Loads songs from raw file into an array list
     */
    public void loadSongs() {
        // Scan the raw folder for items
        Field[] fields = R.raw.class.getFields();
        songList = new Song[fields.length];

        // Iterate through each raw file and generate a Song object
        for (int i = 0; i < fields.length; i++) {
            int rid = 0;
            String fileName = null;

            try {
                rid = fields[i].getInt(fields);
                fileName = fields[i].getName();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // Add each Song object to an array
            songList[i] = createSong(rid, fileName);
        }
    }

    /**
     * Get the meta data for a select song and store as an object
     * Takes the filename and uses a media meta date retriever to import the meta data for the track
     * @param rid - resource id
     * @param fileName - name of file
     * @return song object to be stored in an array list
     */
    public Song createSong(int rid, String fileName) {
        String uriPath = "android.resource://com.example.gareth.moosic/raw/" + fileName;
        Uri uri = Uri.parse(uriPath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, uri);

        String songTitle;
        String artist;
        String album;
        String duration;
        Bitmap artwork;

        try { // Store meta data in local variables
            songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            byte[] data = mmr.getEmbeddedPicture();
            artwork = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (IllegalArgumentException e) { // Set to default if no meta data can be found for a track
            songTitle = "Title - Error";
            artist = "Artist - Error";
            album = "Album - Error";
            duration = "Duration - Error";
            artwork = null;
        }
        mmr.release();

        // Use local variables to create Song object
        return new Song(rid, songTitle, artist, album, duration, artwork);
    }

    /**
     * Runs from the onCreate method
     * Populates the list view with song titles
     */
    public void populateSongListView() {
        // Create the song adapter to convert the array to views
        SongAdapter adapter = new SongAdapter(this, songList);
        // Attach the adapter to a ListView
        ListView songListView = findViewById(R.id.listView_songs);
        songListView.setAdapter(adapter);

        // When a song is selected from the list it begins to play
        final Context context = this;
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Player.player.isPlaying()) {
                    Player.player.release();
                }
                // Display the control bar
                RelativeLayout controlBar = findViewById(R.id.layout_controlBar);
                controlBar.setVisibility(View.VISIBLE);
                // Generate a queue from the selected song
                Player.createSongQueue(i);
                // Play the selected song
                Player.playSong(context, Player.nowPlaying.getSong());
            }
        });

        // Clicking a song for an extended period will add it to the next position in the queue
        songListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Player.nowPlaying != null) {
                    Toast.makeText(context, songList[i].getSongTitle() + " up next!", Toast.LENGTH_SHORT).show();
                    Node n = new Node(songList[i]);
                    Player.nowPlaying.upNext(n);
                } else {
                    Toast.makeText(context, R.string.no_song_playing, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

}