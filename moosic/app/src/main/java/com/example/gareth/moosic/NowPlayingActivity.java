package com.example.gareth.moosic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

/**
 * A child activity of the LibraryActivity. Displays more information about the currently playing track
 * such as duration/current time, title, artist, album and artwork. Buttons for traversing the queue
 * are located here along with shuffle and repeat options.
 */

public class NowPlayingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
    }

    /**
     * Creates the listeners for the seek bar
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Seek bar allows user to skip forward and backwards through the song
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override // When progress changed, plays song at selected time
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(Player.player != null && fromUser){
                    Player.player.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /**
     * Notifies the Player class of the change of activity and updates the song information on screen
     */
    @Override
    protected void onResume() {
        super.onResume();
        Player.libraryActivity = false;
        Player.setNowPlayingSongInfo(this);
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
     * Runs when the repeat button is clicked
     * @param view view
     */
    public void repeatButtonClick(View view) {
        Player.repeatButtonClick(this);
    }

    /**
     * Runs when the shuffle button is clicked
     * @param view view
     */
    public void shuffleButtonClick(View view) {
        Player.shuffleButtonClick(this);
    }

    /**
     * Runs when the android back button is pressed
     */
    public void onBackPressed() {
        super.onBackPressed();
    }



}
