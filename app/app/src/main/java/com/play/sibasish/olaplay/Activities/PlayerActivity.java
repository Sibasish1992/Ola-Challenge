package com.play.sibasish.olaplay.Activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.play.sibasish.olaplay.Database_model.Song_Db;
import com.play.sibasish.olaplay.Helper;
import com.play.sibasish.olaplay.Models.Song;
import com.play.sibasish.olaplay.R;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * Created by Sibasish Mohanty on 17/12/17.
 */

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener {



    public int song_id;
    Realm realm;

    private MediaPlayer mediaPlayer;
    private int mediaFileLength;
    private int realtimeLength;

    private TextView title;
    private TextView artist;
    private ImageView alubum_art;


    private ImageButton btn_play_pause;
    private SeekBar seekBar;
    private PlayAudio task;

    final Handler handler = new Handler();

    boolean from_locl = false;
    String videopath ;
    String imagepath;



    Runnable updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.player_skin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();

        song_id = extras.getInt("songID");
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        seekBar.setMax(99); // 100% (0~99)

        title = (TextView)findViewById(R.id.title);
        artist = (TextView)findViewById(R.id.artist);
        alubum_art = (ImageView)findViewById(R.id.art);
        realm = Realm.getDefaultInstance();
        final Song_Db videoDb = realm.where(Song_Db.class).equalTo("id",song_id).findFirst();

        if (videoDb==null){
            Toast.makeText(PlayerActivity.this,R.string.something_wrong, Toast.LENGTH_LONG).show();
        }
        else{
            if(videoDb.getDownload_status()==3){
                // doenloded
                Toast.makeText(PlayerActivity.this,"Playing From Local", Toast.LENGTH_LONG).show();
                videopath = videoDb.getAudio_path();
                imagepath = videoDb.getImg_path();


                if(imagepath.equals("")){
                    from_locl = false;
                    Glide.with(this).load(videoDb.getAlbum_art()).placeholder(R.drawable.olaplay_logo).into(alubum_art);

                }
                else{
                    from_locl = true;
                    File imageFile=new File(imagepath);
                    if(imageFile!=null){
                        //from local
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        alubum_art.setImageBitmap(bitmap);
                        alubum_art.setBackgroundResource(0);

                    }
                    else{
                        // From Internet
                        Glide.with(this).load(videoDb.getAlbum_art()).placeholder(R.drawable.olaplay_logo).into(alubum_art);
                    }
                }
            }
            else{
                //
                Toast.makeText(PlayerActivity.this,"Streaming from Web", Toast.LENGTH_LONG).show();
                Glide.with(this).load(videoDb.getAlbum_art()).placeholder(R.drawable.olaplay_logo).into(alubum_art);
            }
            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mediaPlayer.isPlaying())
                    {
                        SeekBar seekBar = (SeekBar)v;
                        int playPosition = (mediaFileLength/100)*seekBar.getProgress();
                        mediaPlayer.seekTo(playPosition);
                    }
                    return false;
                }
            });

            title.setText(videoDb.getTitle());
            artist.setText(videoDb.getArtist());
            btn_play_pause = (ImageButton) findViewById(R.id.btn_play_pause);



            btn_play_pause.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                    task = new PlayAudio();

                    try {
                        if (videopath != null && from_locl){
                            File audifile = new File(videopath);
                            if (audifile != null) {
                                task.execute(audifile.getAbsolutePath());
                            } else {
                                task.execute(videoDb.getSong_url());
                            }
                        }
                        else{
                            task.execute(videoDb.getSong_url());
                        }
                    }
                    catch (Exception e){

                        Toast.makeText(PlayerActivity.this,"Unable to stream", Toast.LENGTH_LONG).show();
                    }
                }
            });

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        btn_play_pause.setImageResource(R.drawable.ic_play);

    }



    private void updateSeekBar() {
        seekBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition() / mediaFileLength)*100));
        if(mediaPlayer.isPlaying())
        {
             updater = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                    realtimeLength-=1000; // declare 1 second

                }

            };
            handler.postDelayed(updater,1000); // 1 second
        }
    }


    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            super.onBackPressed();
        }
        return true;
    }


    public class PlayAudio extends AsyncTask<String,String,String>{

        final ProgressDialog mDialog = new ProgressDialog(PlayerActivity.this);
        @Override
        protected void onPreExecute() {
            mDialog.setMessage("Please wait");
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{

                Uri myUri1 = Uri.parse(params[0]);
                mediaPlayer.setDataSource(getApplicationContext(),myUri1);
                mediaPlayer.prepare();
            }
            catch (Exception ex)
            {

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            mediaFileLength = mediaPlayer.getDuration();
            realtimeLength = mediaFileLength;
            if(!mediaPlayer.isPlaying())
            {
                mediaPlayer.start();
                btn_play_pause.setImageResource(R.drawable.ic_pause);
            }
            else
            {
                mediaPlayer.pause();
                btn_play_pause.setImageResource(R.drawable.ic_play);
            }

            updateSeekBar();
            mDialog.dismiss();
        }

    }


    public void onDestroy(){

        if(task!=null){
            task.cancel(true);
        }
        if (updater != null){
            handler.removeCallbacks(updater);
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        super.onDestroy();

    }




}
