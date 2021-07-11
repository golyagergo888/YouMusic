package com.fokakefir.musicplayer.model;

public class Music {

    private int id;
    private String videoId;
    private String title;
    private String artist;
    private int length;

    public Music() {
    }

    public Music(int id, String videoId, String title, String artist, int length) {
        this.id = id;
        this.videoId = videoId;
        this.title = title;
        this.artist = artist;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
