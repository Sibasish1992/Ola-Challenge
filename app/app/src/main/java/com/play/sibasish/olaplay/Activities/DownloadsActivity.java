package com.play.sibasish.olaplay.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.play.sibasish.olaplay.Adapters.SongAdapters;
import com.play.sibasish.olaplay.Database_model.HistoryDb;
import com.play.sibasish.olaplay.Database_model.Song_Db;
import com.play.sibasish.olaplay.Listners.SongListner;
import com.play.sibasish.olaplay.R;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class DownloadsActivity extends AppCompatActivity implements SongListner {
    public RecyclerView recyclerView;
    Realm realm;
    private  SongAdapters mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Song_Db> downToDispaly = new ArrayList<Song_Db>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SongAdapters(new ArrayList<Song_Db>(),this,true);
        mAdapter.setSongListner(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
        getDownload();
    }


    public void getDownload(){
        realm=Realm.getDefaultInstance();
        RealmResults<Song_Db> favs=realm.where(Song_Db.class).equalTo("download_status",3).findAll();

        for (Song_Db song:
                favs) {
            downToDispaly.add(song);
        }
        mAdapter.setItems(downToDispaly);
        realm.close();
    }




    @Override
    public void LetsDownLoad(int id) {
        //Toast.makeText(FavouriteActivity.this, url, Toast.LENGTH_LONG).show();

    }
    @Override
    public void LetsPlay(int id) {
        //Toast.makeText(FavouriteActivity.this, url, Toast.LENGTH_LONG).show();

        realm = Realm.getDefaultInstance();
        final Song_Db song = getSongObject(id);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(song!= null) {
                    HistoryDb history_db = realm.createObject(HistoryDb.class);
                    history_db.setAction("played");
                    history_db.setSong_id(song.getId());
                    history_db.setSong_name(song.getTitle());
                    history_db.setPush_date(new Date());
                }
            }
        });
        realm.close();

        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("songID",id);
        startActivity(intent);

    }

    @Override
    public void LetsMakeFav(final int id) {

        /*realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if(song!= null) {
                    song.setIs_fav(!song.isIs_fav());
                }
            }
        });
        realm.close();*/
        final Song_Db song = getSongObject(id);
        if(song.isIs_fav()) {

            Toast.makeText(DownloadsActivity.this, song.getTitle() + " " + "is now favourite.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(DownloadsActivity.this, song.getTitle() + " " + "removed from favourite.", Toast.LENGTH_LONG).show();
        }
    }

    public  Song_Db getSongObject(int id){
        for (Song_Db song:
                downToDispaly) {
            if (song.getId()==id){
                return  song;
            }
        }
        return  null;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }





}
