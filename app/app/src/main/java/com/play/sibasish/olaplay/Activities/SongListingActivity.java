package com.play.sibasish.olaplay.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.play.sibasish.olaplay.Adapters.SongAdapters;
import com.play.sibasish.olaplay.Config;
import com.play.sibasish.olaplay.Database_model.HistoryDb;
import com.play.sibasish.olaplay.Database_model.Song_Db;
import com.play.sibasish.olaplay.DownLoad.DownloadManagerService;
import com.play.sibasish.olaplay.DownLoad.OndownloadListener;
import com.play.sibasish.olaplay.Helper;
import com.play.sibasish.olaplay.Listners.SongListner;
import com.play.sibasish.olaplay.Models.Song;
import com.play.sibasish.olaplay.R;
import com.play.sibasish.olaplay.SingletonVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by Sibasish Mohanty on 16/12/17.
 */



// DownLoad Status :::::: 0- Not downloaded ,1- Downloading,2- downloaded



public class SongListingActivity extends AppCompatActivity implements SongListner {
    View view;
    private static String LOG_TAG = "SongListingActivity";
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private static SongAdapters mAdapter;
    private long mLastPress = 0;
    private static ArrayList<Song_Db> songsToDispaly = new ArrayList<Song_Db>();
    private static final long TIME_INTERVAL = 5000;

    Realm realm;
    LinearLayout history;
    LinearLayout favourites;
    LinearLayout downloads;
    LinearLayout playlist;


    String sdcard;
    File[] files;
    private OndownloadListener ondownloadListener=new OnDownloadListenerImpl();




    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_activity);
        view = findViewById(android.R.id.content);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SongAdapters(new ArrayList<Song_Db>(),this,false);
        mAdapter.setSongListner(this);
        realm= Realm.getDefaultInstance();


        history = (LinearLayout)findViewById(R.id.history);
        favourites = (LinearLayout)findViewById(R.id.fav);
        downloads = (LinearLayout)findViewById(R.id.down);
        playlist = (LinearLayout)findViewById(R.id.playlist);


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongListingActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongListingActivity.this, FavouriteActivity.class);
                startActivity(intent);
            }
        });
        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongListingActivity.this, DownloadsActivity.class);
                startActivity(intent);
            }
        });
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongListingActivity.this, PlayListActivity.class);
                startActivity(intent);
            }
        });




        // Set listner
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        if( android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            sdcard=getFiles();
        }

            LoadSongsFromServer();


    }

    private void LoadSongsFromServer(){

        String url = Config.BASE_URL;
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.TransparentProgressDialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fasten your seatbelts.Songs are arriving...");
        progressDialog.show();




        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Gson gson = new Gson();
                            JSONArray ParentArray = new JSONArray(s);

                            for (int i = 0; i < ParentArray.length(); i++) {
                                final JSONObject ParentObject = ParentArray.getJSONObject(i);
                                //see if song is in the db

                                if (Helper.isSongInDb(i)){

                                    final int finalI1 = i;
                                    realm =Realm.getDefaultInstance();
                                    realm.executeTransaction(new Realm.Transaction() {
                                         @Override
                                         public void execute(Realm realm) {
                                             Song_Db videoDb = realm.where(Song_Db.class).equalTo("id", finalI1).findFirst();
                                             songsToDispaly.add(videoDb);
                                         }
                                    });
                                    realm.close();

                                }
                                else{
                                    //Add to db
                                    realm= Realm.getDefaultInstance();
                                    final int finalI = i;
                                    realm.executeTransaction(new Realm.Transaction(){
                                        @Override
                                        public void execute(Realm realm){
                                            try {
                                                Song_Db videoDb = realm.createObject(Song_Db.class);
                                                videoDb.setAlbum_art(ParentObject.getString("cover_image"));
                                                videoDb.setSong_url(ParentObject.getString("url"));
                                                videoDb.setArtist(ParentObject.getString("artists"));
                                                videoDb.setTitle(ParentObject.getString("song"));
                                                videoDb.setId(finalI);
                                                songsToDispaly.add(videoDb);
                                            }
                                            catch (Exception e){

                                            }
                                        }

                                    });
                                    realm.close();
                                }
                                /*

                                final String[] import_videopath = new String[1];
                                final String[] import_imagepath = new String[1];
                                final boolean[] is_favo = new boolean[1];
                                if (chkDownloadStatus(song.id)){
                                    realm = Realm.getDefaultInstance();

                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Song_Db videoDb = realm.where(Song_Db.class).equalTo("id",song.id).findFirst();
                                            if(videoDb!=null) {
                                                import_videopath[0] = videoDb.getAudio_path();
                                                import_imagepath[0] = videoDb.getImg_path();
                                                is_favo[0] = videoDb.isIs_fav();


                                            }
                                            // if(null!=videoDb)
                                            // videoDb.deleteFromRealm();
                                        }


                                    });

                                    realm.close();
                                    if(import_imagepath[0].equals("")){
                                        song.download_status = 1;

                                    }
                                    else{
                                        song.download_status = 2;
                                        song.audio_path = import_videopath[0];
                                        song.img_path = import_imagepath[0];
                                        song.is_fav =is_favo[0];
                                    }
                                }
                                else{
                                    song.download_status = 0;
                                }

                                songsToDispaly.add(song);

                                */

                            }

                            mAdapter.setItems(songsToDispaly);
                            Log.d(LOG_TAG,songsToDispaly.size()+"");

                        } catch (JSONException e) {

                            Toast.makeText(SongListingActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                        } finally {

                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Snackbar snackbartest = Snackbar.make(view, "No Internet Connection.", Snackbar.LENGTH_LONG);
                View snckViewtest = snackbartest.getView();
                snckViewtest.setBackgroundColor(Color.parseColor("#f44336"));
                snackbartest.show();
            }
        });

        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }





    public View retrieveView(){
        return view;
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            //Toast onBackPressedToast = Toast.makeText(this, R.string.press_once_again_to_exit, Toast.LENGTH_SHORT);
            Snackbar exitsnack=Snackbar.make(retrieveView(), R.string.press_once_again_to_exit, Snackbar.LENGTH_LONG);
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastPress > TIME_INTERVAL) {
                // onBackPressedToast.show();
                exitsnack.show();
                mLastPress = currentTime;
            } else {
                //  onBackPressedToast.cancel();
                exitsnack.dismiss();
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(LOG_TAG,newText);
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void LetsDownLoad(int id) {

        ManageDownload(id);
        //Toast.makeText(SongListingActivity.this, url, Toast.LENGTH_LONG).show();

    }
    @Override
    public void LetsPlay(int id) {
        //Toast.makeText(SongListingActivity.this, url, Toast.LENGTH_LONG).show();

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

            Toast.makeText(SongListingActivity.this, song.getTitle() + " " + "is now favourite.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(SongListingActivity.this, song.getTitle() + " " + "removed from favourite.", Toast.LENGTH_LONG).show();
        }
    }



    // Download Section


    private static class OnDownloadListenerImpl implements OndownloadListener {

            Realm realm;

        @Override
        public void OnComplete(final int id, final String s_path, final String i_path) {


            // Update in db and list
                realm = Realm.getDefaultInstance();
            final Song_Db song = getSongObject(id);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if(song!= null) {
                            song.setAudio_path(s_path);
                            song.setImg_path(i_path);
                            song.setDownload_status(3);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if(song!= null) {
                            HistoryDb history_db = realm.createObject(HistoryDb.class);
                            history_db.setAction("downloaded");
                            history_db.setSong_id(song.getId());
                            history_db.setSong_name(song.getTitle());
                            history_db.setPush_date(new Date());
                        }
                    }
                });


                realm.close();


        }

        @Override
        public void OnFailure(final int id) {

            // Update in db and list

            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
             @Override
             public void execute(Realm realm) {
                 Song_Db song = getSongObject(id);
                 if(song!= null) {
                     song.setDownload_status(0);
                     mAdapter.notifyDataSetChanged();
                 }

             }
         });
            realm.close();



        }

        @Override
        public void OnDestroyed(final int id) {

            // Update in db and list
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Song_Db song = getSongObject(id);
                    if(song!= null) {
                        song.setDownload_status(0);
                        mAdapter.notifyDataSetChanged();
                    }
                }});
            realm.close();



        }
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getFiles(){
        files=getExternalFilesDirs(null);
        Log.w("no of storage",files.length+"");
        for (File file :
                files) {
            if(file!=null){
                String file_name=file.getAbsolutePath();
                if(file_name.contains("Card")||file_name.contains("card") || file_name.contains("sdcard")||file_name.contains("SdCard")||file_name.contains("SDCARD")) {
                    return file_name;
                }
            }

        }


        File file=getExternalFilesDir(null);
        return null;
    }


    // Ask Run Time Permission



    //persmission method.
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    public void ManageDownload(int id){

        final Song_Db song = getSongObject(id);

        if (verifyStoragePermissions(SongListingActivity.this)){
            if(song!= null){

                if(song.getAlbum_art() != null && song.getSong_url() != null){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SongListingActivity.this,R.style.MyDialogTheme);
                    alertDialog.setTitle("Download Confirm");

                    alertDialog.setMessage("Are you want to download the song?");
                    alertDialog.setIcon(R.mipmap.ic_launcher);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            realm=Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    song.setDownload_status(1);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                            realm.close();


                            Toast.makeText(SongListingActivity.this,"Downloading... "+song.getTitle(), Toast.LENGTH_LONG).show();
                            Log.w("In download  press", "Yes");
                            Intent intent = new Intent(SongListingActivity.this, DownloadManagerService.class);
                            intent.putExtra(DownloadManagerService.IMAGE_URL, song.getAlbum_art());
                            intent.putExtra(DownloadManagerService.SONG_URL, song.getSong_url());
                            intent.putExtra(DownloadManagerService.FILE_TITLE, song.getTitle());
                            intent.putExtra(DownloadManagerService.SONG_ID, song.getId());
                            intent.putExtra("Listener", ondownloadListener);
                            intent.putExtra(DownloadManagerService.STOP, false);

                            if (sdcard != null) {

                                intent.putExtra(DownloadManagerService.STORAGE_PATH, sdcard);
                            } else {
                                //for kitkat below and with no sdcard device
                                intent.putExtra(DownloadManagerService.STORAGE_PATH, Environment.getExternalStorageDirectory() + "/Android/data/com.play.sibasish.olaplay/files");
                            }

                            startService(intent);
                        }
                        });


                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

                }
                else{
                    Toast.makeText(SongListingActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(SongListingActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();

            }
        }
    }

    public boolean chkDownloadStatus(final int songId){

            return Helper.isSongInDb(songId);

    }

    public static Song_Db getSongObject(int id){
        for (Song_Db song:
             songsToDispaly) {
            if (song.getId()==id){
                return  song;
            }
        }
        return  null;
    }




}
