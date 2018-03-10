
package com.play.sibasish.olaplay.DownLoad;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Sibasish Mohanty on 16/12/17.
 */


public class DownloadManagerImpl implements DownloadManager
{
	private static final String TAG = DownloadManagerImpl.class.getSimpleName();
	
	private Context mContext;
	private String sLocation;
    private String iLocation;

	private NotificationManager noticeManager;
	protected ArrayList<DownloadMission> mMissions = new ArrayList<DownloadMission>();
	private OndownloadListener ondownloadListener;
	
	public DownloadManagerImpl(Context context, String songlocation, String imagelocation, NotificationManager notificationManager, OndownloadListener ondownloadListener) {
		mContext = context;
		sLocation = songlocation;
        iLocation=imagelocation;
		noticeManager=notificationManager;
		this.ondownloadListener=ondownloadListener;



	}
	
	@Override
	public int startMission(String surl, String iurl, int song_id, String name,String artist) {


		DownloadMission mission = new DownloadMission();
        mission.context=mContext;
		mission.surl = surl;
        mission.iurl=iurl;
		mission.name = name;
		mission.artist = artist;

        mission.mSongId=song_id;
		mission.slocation = sLocation;
        mission.ilocation=iLocation;
		mission.timestamp = System.currentTimeMillis();
		mission.ondownloadListener=ondownloadListener;
		mission.noticeManager=noticeManager;
		new Initializer(mContext, mission).start();
		int i=insertMission(mission);
		Log.w("Size of array",getCount()+"");
		return i;
	}
	@Override
	public void endMisson(int id){
		if(!mMissions.isEmpty()) {
			DownloadMission misson = new DownloadMission();
			for (DownloadMission d : mMissions
					) {
				if (d.mSongId == id) {

					misson = d;
				}
			}
			if (misson != null) {
				Initializer initializer=new Initializer(mContext,misson);
				Log.w("In end mission::",misson.mSongId+"");
				mMissions.remove(misson);
				initializer.destry();
			}
		}


	}
	
	@Override
	public DownloadMission getMission(int i) {
		return mMissions.get(i);
	}
	
	@Override
	public int getCount() {
		return mMissions.size();
	}
	
	private int insertMission(DownloadMission mission) {
		int i = -1;
		
		DownloadMission m = null;
		
		if (mMissions.size() > 0) {
			do {
				m = mMissions.get(++i);
			} while (m.timestamp > mission.timestamp && i < mMissions.size() - 1);
			//if (i > 0) i--;
		} else {
			i = 0;
		}
		
		mMissions.add(i, mission);
		
		return i;
	}
	
	@Override
	public String getLocation() {
		return sLocation;
	}
	
	private class Initializer extends Thread {
		private Context context;
		private DownloadMission mission;
		
		public Initializer(Context context, DownloadMission mission) {
			this.context = context;
			this.mission = mission;
		}
		
		@Override
		public void run() {
			try {
				URL url = new URL(mission.surl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				mission.length = conn.getContentLength();
				mission.start();
			} catch (Exception e) {
				// TODO Notify
				throw new RuntimeException(e);
			}
		}

		public void destry(){
			Log.w("In end mission::",mission.mSongId+"");
			mission.destroy();

		}

	}
}
