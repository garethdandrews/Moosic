package com.example.gareth.moosic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A custom array adapter which creates a view for each song in the array to be inserted into a list view
 */

public class SongAdapter extends ArrayAdapter<Song> {

    SongAdapter(Context context, Song[] songList) {
        super(context, 0, songList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Song song = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_song, parent, false);
        }
        // Lookup view for data population
        TextView songTitle = convertView.findViewById(R.id.list_songTitle);
        TextView songArtist = convertView.findViewById(R.id.list_songArtist);
        // Populate the data into the template view using the data object
        if (song != null) {
            songTitle.setText(song.getSongTitle());
            songArtist.setText(song.getArtist());
        }
        // Return the completed view to render on screen
        return convertView;
    }
}