
package com.play.sibasish.olaplay.DownLoad;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;



/**
 * Created by Sibasish Mohanty on 16/12/17.
 */


public class DownloadManagerService extends Service {

	private static final String TAG = DownloadManagerService.class.getSimpleName();
	public static final String IMAGE_URL = "url_image";
	public static final String SONG_URL = "url_song";
	public static final String SONG_ID = "song_id";
	public static final String FILE_TITLE = "file_title";
	public static final String STORAGE_PATH = "storage_path";
	public static final String ARTIST = "artist";
	public static final String STOP="stop";




	private DownloadManager mManager;
	NotificationManager notificationManager;
	public static String STORE_IN_FOLDER = Environment.getExternalStorageDirectory() + "/Android/data/com.play.sibasish.olaplay/files/songs";
	public static String STORE_IN_IMAGE = Environment.getExternalStorageDirectory() + "/Android/data/com.play.sibasish.olaplay/files/thumbsnail";


	@Override
	public void onCreate() {

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(TAG, "Starting");
		if (intent != null) {

			Bundle extra = intent.getExtras();
			if (extra != null) {
				boolean stop=intent.getBooleanExtra(STOP,false);
				if(stop){
					//End Mission
					int id=intent.getIntExtra(SONG_ID, 0);
					if(mManager!=null){
						Log.d(TAG, "Destroying mission" + id);

						mManager.endMisson(id);
					}
				}
				else {//Start Mission

					String song_url = intent.getStringExtra(SONG_URL);
					String image_url = intent.getStringExtra(IMAGE_URL);
					String title = intent.getStringExtra(FILE_TITLE);
					int mSongId = intent.getIntExtra(SONG_ID, 0);
					OndownloadListener ondownloadListener = (OndownloadListener) intent.getSerializableExtra("Listener");

					if (ondownloadListener == null) {

						Log.w("Misson ", "Fail");

					} else {
						Log.w("Misson ", "Pass");

					}

					String storage_path = intent.getStringExtra(STORAGE_PATH);
					String artist =  intent.getStringExtra(ARTIST);

					if (storage_path != null) {

						STORE_IN_FOLDER = storage_path + "/songs";
						STORE_IN_IMAGE = storage_path + "/thumbsnail";
					}

					Log.w(TAG, "onStartCommand");

					if(notificationManager==null){
						notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);}

					if (mManager == null) {

						mManager = new DownloadManagerImpl(this, STORE_IN_FOLDER, STORE_IN_IMAGE, notificationManager, ondownloadListener);

						Log.d(TAG, "mManager == null");
						Log.d(TAG, "song Download directory: " + STORE_IN_FOLDER);
						Log.d(TAG, "Image Download directory: " + STORE_IN_IMAGE);

					}
					mManager.startMission(song_url, image_url, mSongId, title,artist);
				}
			}
		}


		return START_NOT_STICKY;
	}

	/*@Override
	public void onDestroy() {
		Log.w("Count is",mManager.getCount()+"");
		if(mManager.getCount()==1){
			Log.d(TAG, "Destroying service" + mConceptId);
		     super.onDestroy();
			mManager.endMisson(mConceptId);
		}
		else {
			Log.d(TAG, "Destroying mission" + mConceptId);

			mManager.endMisson(mConceptId);
		}

		*///stopForeground(true);
	//}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}



}

