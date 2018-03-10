package com.play.sibasish.olaplay.DownLoad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by Sibasish Mohanty on 16/12/17.
 */


public class NoticeCancelActivity extends Activity {
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          Log.w("In notice activity","Yes");
        int id=getIntent().getIntExtra(NOTIFICATION_ID, -1);
        Intent intent=new Intent(getApplicationContext(),DownloadManagerService.class);
        intent.putExtra(DownloadManagerService.STOP,true);
        intent.putExtra(DownloadManagerService.SONG_ID, id);

        startService(intent);
        Log.w("Cancel notice",getIntent().getIntExtra(NOTIFICATION_ID, -1)+"");

        finish();
    }
    /*
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);

    }
*/
}


