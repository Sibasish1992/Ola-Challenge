package com.play.sibasish.olaplay.Database_model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Sibasish Mohanty on 20/12/17.
 */

public class HistoryDb extends RealmObject {

    private String action;
    private String song_name;
    private int song_id;
    public Date push_date;

    public Date getPush_date() {
        return push_date;
    }

    public void setPush_date(Date push_date) {
        this.push_date = push_date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }
}
