package com.play.sibasish.olaplay.DownLoad;

/**
 * Created by Sibasish Mohanty on 16/12/17.
 */


public interface DownloadManager
{
	public static final int BLOCK_SIZE = 512 * 1024;
	
	public int startMission(String surl, String iurl, int song_id, String name,String artist);
	public DownloadMission getMission(int id);
	public int getCount();
	public String getLocation();
	public void endMisson(int id);
}
