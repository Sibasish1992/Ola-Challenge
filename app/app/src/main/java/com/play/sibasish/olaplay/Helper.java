package com.play.sibasish.olaplay;

import com.play.sibasish.olaplay.Database_model.Song_Db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Sibasish Mohanty on 16/12/17.
 */

public class Helper {

    static Realm realm;
    public static Date stringToDate(String date_str) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date;
        try {
            date = format.parse(date_str);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        }

        return date;

    }

    public static String dateToString(Date start_time) {
        String str = "";
        DateFormat df = new SimpleDateFormat("dd");
        String a = df.format(start_time);
        df = new SimpleDateFormat("MMMM");
        String b = df.format(start_time);
        df = new SimpleDateFormat("yyyy");
        String y = df.format(start_time);
        df = new SimpleDateFormat("hh:mm a");
        String d = df.format(start_time);
        str = d + " "  + a + "th " + b+","+y;
        return str;
    }


    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return String.format("%d B", bytes);
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f kB", (float) bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", (float) bytes / 1024 / 1024);
        } else {
            return String.format("%.2f GB", (float) bytes / 1024 / 1024 / 1024);
        }
    }

    public static String formatSpeed(float speed) {
        if (speed < 1024) {
            return String.format("%.2f B/s", speed);
        } else if (speed < 1024 * 1024) {
            return String.format("%.2f kB/s", speed / 1024);
        } else if (speed < 1024 * 1024 * 1024) {
            return String.format("%.2f MB/s", speed / 1024 / 1024);
        } else {
            return String.format("%.2f GB/s", speed / 1024 / 1024 / 1024);
        }
    }


    public static boolean isSongInDb( int id){

        realm= Realm.getDefaultInstance();
        final RealmResults<Song_Db> puppies = realm.where(Song_Db.class).equalTo("id",id).findAll();
        realm.close();
        if(puppies.size()==0){
            return false;
        }


        return true;

    }




    public static boolean isDownLoaded(int songId){
        realm= Realm.getDefaultInstance();
        Song_Db videoDb = realm.where(Song_Db.class).equalTo("id",songId).findFirst();
        realm.close();
        if (videoDb.getDownload_status()==3){
            return true;
        }
        else{
            return false;
        }

    }



}



