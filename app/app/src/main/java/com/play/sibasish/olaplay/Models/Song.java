package com.play.sibasish.olaplay.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sibasish Mohanty on 16/12/17.
 */

public class Song implements Parcelable {

    public int id;
    public String title;
    public String artist;
    public String song_url;
    public String album_art;
    public boolean is_fav;
    public String audio_path;
    public String img_path;
    public int download_status;


    public Song(){

    }

    public Song(int id, String title, String artist, String song_url, String album_art, boolean is_fav, String audio_path, String img_path,int download_status) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.song_url = song_url;
        this.album_art = album_art;
        this.is_fav = is_fav;
        this.audio_path = audio_path;
        this.img_path = img_path;
        this.download_status = download_status;
    }

    public int describeContents() {
        return 0;
    }

    public Song(Parcel in){
        this.id = in.readInt();
        this.title = in.readString();
        this.artist = in.readString();
        this.song_url = in.readString();
        this.album_art = in.readString();
        this.audio_path = in.readString();
        this.img_path = in.readString();
        this.is_fav = in.readByte()!=0;
        this.download_status = in.readInt();

    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeString(title);
        out.writeString(artist);
        out.writeString(song_url);
        out.writeString(album_art);
        out.writeString(audio_path);
        out.writeString(img_path);
        out.writeByte((byte) (is_fav ? 1 : 0));
        out.writeInt(download_status);


    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

}
