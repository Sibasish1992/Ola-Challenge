package com.play.sibasish.olaplay.Database_model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by Sibasish Mohanty on 17/12/17.
 */

public class Song_Db extends RealmObject  {
    @Index
    private int id;
    private String title;
    private String artist;
    private String song_url;
    private String album_art;
    private boolean is_fav;
    private String audio_path;
    private String img_path;
    private int download_status;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public boolean isIs_fav() {
        return is_fav;
    }

    public void setIs_fav(boolean is_fav) {
        this.is_fav = is_fav;
    }

    public String getAudio_path() {
        return audio_path;
    }

    public void setAudio_path(String audio_path) {
        this.audio_path = audio_path;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public int getDownload_status() {
        return download_status;
    }

    public void setDownload_status(int download_status) {
        this.download_status = download_status;
    }





}
