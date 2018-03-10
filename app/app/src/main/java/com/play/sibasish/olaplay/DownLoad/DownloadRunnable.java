
package com.play.sibasish.olaplay.DownLoad;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.play.sibasish.olaplay.Activities.PlayerActivity;
import com.play.sibasish.olaplay.Database_model.Song_Db;
import com.play.sibasish.olaplay.Helper;
import com.play.sibasish.olaplay.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * Created by Sibasish Mohanty on 16/12/17.
 */


public class DownloadRunnable implements Runnable
{



	private static final String TAG = DownloadRunnable.class.getSimpleName();
	
	private DownloadMission mMission;
    private Context mcontext;
    File folderName;
    File imgFolder;
    java.net.URL vid_path ;
    URLConnection connection;
    long connection_length;

    long progress=0;
    Realm realm;
    String videopath="";
    String imagepath="";
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Bitmap larIcon;
    private  SaveAudio downloadTask;
    private OndownloadListener ondownloadListener;
    public long lastTimeStamp = -1;
    public long tillNow=-1;

	
	public DownloadRunnable(DownloadMission mission,Context context) {
		mMission = mission;
        mcontext=context;
        ondownloadListener=mMission.ondownloadListener;

	}
	
	@Override
	public void run() {

         larIcon = BitmapFactory.decodeResource(mcontext.getResources(),
                R.mipmap.ic_launcher);//put the thumbnail of video
        notificationManager=mMission.noticeManager;
        imagepath=mMission.ilocation+"/"+ UUID.randomUUID().toString()+".jpg";
        videopath=mMission.slocation+"/"+ UUID.randomUUID().toString()+".mp3";


        builder=new NotificationCompat.Builder(mcontext);
        builder.setContentTitle("Download in queue")
                .setSmallIcon(android.R.drawable.stat_sys_download)//put the download downloading here
                .setLargeIcon(larIcon)
        .setOngoing(true);

        downloadTask = new SaveAudio();

        downloadTask.execute();

	}


    public class  SaveAudio extends AsyncTask<String,Long,String> {

        private PowerManager.WakeLock mWakeLock;
        int comp=0;


        public SaveAudio() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Intent intent = new Intent(mcontext, NoticeCancelActivity.class);
            intent.putExtra(NoticeCancelActivity.NOTIFICATION_ID, mMission.mSongId);
            PendingIntent dismissIntent = PendingIntent.getActivity(mcontext,mMission.mSongId,intent, PendingIntent.FLAG_CANCEL_CURRENT);


            builder.setProgress(0, 0, false)
                    .addAction(R.drawable.ic_close_notif,"Cancel download",dismissIntent)
                    .setContentTitle(mMission.name);
            notificationManager.notify(mMission.mSongId, builder.build());
            PowerManager pm = (PowerManager) mcontext.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();



            realm= Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm){
                    Song_Db videoDb = realm.where(Song_Db.class).equalTo("id", mMission.mSongId).findFirst();
                    videoDb.setAudio_path(videopath);
                    videoDb.setImg_path("");
                }

            });
            realm.close();



            //mProgressDialog.show();
        }

        @Override

        protected String doInBackground(String... params) {


                InputStream is = null;
                InputStream img = null;
                OutputStream outImage = null;


                OutputStream os = null;

                try {
                    URL short_i_url = new URL(mMission.iurl);
                    final HttpURLConnection i_urlConnection = (HttpURLConnection) short_i_url.openConnection();
                    i_urlConnection.setInstanceFollowRedirects(false);
                    String i_location = i_urlConnection.getHeaderField("location");

                    URL short_s_url = new URL(mMission.surl);
                    final HttpURLConnection s_urlConnection = (HttpURLConnection) short_s_url.openConnection();
                    s_urlConnection.setInstanceFollowRedirects(false);
                    String s_location = s_urlConnection.getHeaderField("location");

                    URL imagePath = new URL(i_location);
                    URLConnection imgCon = imagePath.openConnection();
                    imgCon.connect();

                    vid_path = new URL(s_location);
                    connection = vid_path.openConnection();
                    connection_length = connection.getContentLength();
                    connection.connect();


                    is = connection.getInputStream();
                    img = imgCon.getInputStream();
                    Log.w("path", mMission.slocation);

                    if (android.os.Environment.getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED)) {
                        folderName = new File(mMission.slocation);
                        Log.w("can write", "yes");

                    }
                    if (!folderName.exists()) {
                        boolean a = folderName.mkdirs();
                        Log.e("Folder created", a + "");
                    }

                    if (android.os.Environment.getExternalStorageState().equals(
                            android.os.Environment.MEDIA_MOUNTED)) {
                        imgFolder = new File(mMission.ilocation);

                    }
                    if (!imgFolder.exists()) {
                        boolean a = imgFolder.mkdirs();
                        Log.e("Folder created", a + "");
                    }

                    outImage = new FileOutputStream(imagepath);
                    int count;
                    byte[] c = new byte[1024];
                    while ((count = img.read(c)) != -1) {
                        if (isCancelled()) {
                            img.close();
                            return null;
                        }
                        outImage.write(c, 0, count);

                    }


                    os = new FileOutputStream(videopath);
                    int i;
                    long length = connection_length;
                    progress = 0;
                    byte[] b = new byte[1024];
                    while ((i = is.read(b)) != -1) {
                        if (!isCancelled()) {
                            progress += i;
                            if (length > 0) {
                                publishProgress(progress);
                            }

                            os.write(b, 0, i);
                        }
                        else{
                            is.close();
                            Log.w("iscancelled", "called");
                            return null;
                        }
                    }
                } catch (Exception e) {
                    Log.e("Network", "Failed to read all data!");
                    e.printStackTrace();

                    return e.toString();
                } finally {
                    try {
                        if (os != null){
                            os.flush();
                            os.close();}
                        if (is != null)
                            is.close();
                        if (img != null)
                            img.close();
                        if (outImage != null) {
                            outImage.flush();
                            outImage.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
                return "success";



        }
        @Override
        protected void onProgressUpdate(Long... progres) {
            super.onProgressUpdate(progres);
            int percentage=0;

            if(!isCancelled()) {

                long length = connection_length;
                long done=progres[0];
                if(length>0) {
                    percentage = (int) ((done*100)/length);
                }
                long now = System.currentTimeMillis();

                if(lastTimeStamp==-1){
                    lastTimeStamp=now;
                }
                if(tillNow==-1){
                    tillNow=done;
                }


                if (percentage > comp) {
                    String string="";
                    float speed=0f;
                    if((now-lastTimeStamp)==0){
                         speed=0;

                    }
                    else{
                         speed=(float)(done-tillNow)/(now-lastTimeStamp);
                    }
                    string= Helper.formatBytes(done)+"/"+Helper.formatBytes(length)+" ("+percentage+"%) ("+Helper.formatSpeed(speed*1000)+")";



                  builder.setContentText(string)
                            .setProgress(100,percentage,false)
                    ;
                    notificationManager.notify(mMission.mSongId, builder.build());
                    comp = percentage;
                    lastTimeStamp=now;
                    tillNow=done;

                }
            }


        }
        protected void onPostExecute(String result) {
            // mProgressDialog.dismiss();
            if(!result.equals("success") ) {
                if(ondownloadListener!=null){
                    ondownloadListener.OnFailure(mMission.mSongId);
                }
                if(result.contains("ENOSPC")){
                    Toast.makeText(mcontext, "Download failed due to insufficient space.Free some space and try again. ", Toast.LENGTH_LONG).show();
                }
                else if(result.contains("EAI_NODATA") ||result.contains("No address associated with hostname")){
                    Toast.makeText(mcontext, "No Internet connection.Try again.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(mcontext, "Download Fail", Toast.LENGTH_LONG).show();
                }
                Log.w("Download fail:",result);
                NotificationCompat.Builder builder1=new NotificationCompat.Builder(mcontext);
                builder1.setContentText("Download fail")
                        .setProgress(0,0,false)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setOngoing(false)
                        .setContentTitle(mMission.name)
                        .setLargeIcon(larIcon);

                notificationManager.notify(mMission.mSongId, builder1.build());
                mWakeLock.release();


                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Song_Db videoDb = realm.where(Song_Db.class).equalTo("id", mMission.mSongId).findFirst();
                        videoDb.setDownload_status(0);
                    }

                });


                File deleteFile=new File(videopath);
                boolean videodelete=deleteFile.delete();
                Log.w("Video delete=",videodelete+"");

                File imageDelete=new File(imagepath);
                boolean imgDelet=imageDelete.delete();
                Log.w("Image delete=",imgDelet+"");



            }
            else if(result.equals("success")){
                if(ondownloadListener!=null){
                    ondownloadListener.OnComplete(mMission.mSongId,videopath,imagepath);
                }



                Intent intent = new Intent(mcontext, PlayerActivity.class);

                intent.putExtra("songID",mMission.mSongId);
                PendingIntent successIntent = PendingIntent.getActivity(mcontext,mMission.mSongId,intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder1=new NotificationCompat.Builder(mcontext);
                builder1.setContentText("Download complete")
                        .setProgress(0,0,false)
                        .setSmallIcon(R.drawable.finish)
                        .setOngoing(false)
                        .setContentTitle(mMission.name)
                        .setLargeIcon(larIcon)
                        .setContentIntent(successIntent)
                        .setAutoCancel(true);
                notificationManager.notify(mMission.mSongId, builder1.build());
                mWakeLock.release();

                Toast.makeText(mcontext,"File downloaded", Toast.LENGTH_SHORT).show();

                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Song_Db videoDb = realm.where(Song_Db.class).equalTo("id",mMission.mSongId).findFirst();
                        if(null!=videoDb){
                            videoDb.setImg_path(imagepath);
                            Log.w("Insert Image","Yes");
                         }
                        else{
                            Log.w("Delete","Ok");
                            File deleteFile=new File(videopath);
                            boolean videodelete=deleteFile.delete();
                            Log.w("Video delete=",videodelete+"");

                            File imageDelete=new File(imagepath);
                            boolean imgDelet=imageDelete.delete();
                            Log.w("Image delete=",imgDelet+"");
                        }
                    }

                });
            }
            realm.close();
        }
    }
    public void destroy(){
        Log.w("In runnable des","Yes"+mMission.mSongId);
        if(ondownloadListener!=null){
             ondownloadListener.OnDestroyed(mMission.mSongId);
        }
        else{
            Log.w("ondownloadListener","Is Null");
        }


        if(downloadTask!=null) {
            Log.w("Task cancel","Yes");
            downloadTask.cancel(true);
        }
        if(notificationManager!=null && mMission!=null)
        {
            Log.w("Notice cancel","Yes");
            notificationManager.cancel(mMission.mSongId);
        }


        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Song_Db videoDb = realm.where(Song_Db.class).equalTo("id", mMission.mSongId).findFirst();
                videoDb.setDownload_status(0);
                Log.w("delete","Yes");

            }

        });

        File deleteFile=new File(videopath);
        boolean videodelete=deleteFile.delete();
        Log.w("Video delete=",videodelete+"");

        File imageDelete=new File(imagepath);
        boolean imgDelet=imageDelete.delete();
        Log.w("Image delete=",imgDelet+"");
        realm.close();

    }

}

