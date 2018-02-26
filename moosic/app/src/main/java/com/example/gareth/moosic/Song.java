package com.example.gareth.moosic;

import android.graphics.Bitmap;

/**
 * Song object stores the meta data obtained from each song in the raw folder
 */

class Song {

    private int rid;
    private String songTitle;
    private String artist;
    private String album;
    private String duration;
    private Bitmap artwork;

    /**
     * Initialises a song object
     * @param rid resource id
     * @param songTitle title
     * @param artist artist
     * @param album album
     * @param duration duration
     * @param artwork artwork
     */
    Song(int rid, String songTitle, String artist, String album, String duration, Bitmap artwork) {
        this.rid = rid;
        this.songTitle = songTitle;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.artwork = artwork;
    }

    /**
     * @return resource id
     */
    int getRid() {
        return rid;
    }

    /**
     * @return song title
     */
    String getSongTitle() {
        return songTitle;
    }

    /**
     * @return artist
     */
    String getArtist() {
        return artist;
    }

    /**
     * @return album
     */
    String getAlbum() {
        return album;
    }

    /**
     * @return duration
     */
    String getDuration() {
        return duration;
    }

    /**
     * @return artwork
     */
    Bitmap getArtwork() {
        return artwork;
    }
}