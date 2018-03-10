
package com.play.sibasish.olaplay.DownLoad;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;



/**
 * Created by Sibasish Mohanty on 16/12/17.
 */


public class DownloadMission
{
	private static final String TAG = DownloadMission.class.getSimpleName();

	public String name = "";
	public String surl = "";
    public String iurl = "";
	public String slocation = "";
    public String ilocation = "";
    public String artist = "";

	public long length = 0;
	public boolean running = false;
	public boolean finished = false;
	public long timestamp = 0;
    public int mSongId=0;
    public Context context;
	public NotificationManager noticeManager;
    public DownloadRunnable downloadRunnable;
	public OndownloadListener ondownloadListener;


	
	public void start() {
		downloadRunnable=new DownloadRunnable(this,context);
		new Thread(downloadRunnable).start();

	}

	public void destroy(){
		Log.w("In Download Miss des",this.mSongId+"");
		if(downloadRunnable!=null)
			downloadRunnable.destroy();
	}
	


	

}

