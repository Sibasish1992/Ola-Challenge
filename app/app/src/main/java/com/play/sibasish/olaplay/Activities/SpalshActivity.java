package com.play.sibasish.olaplay.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.play.sibasish.olaplay.R;

public class SpalshActivity extends AppCompatActivity {
    private static String LOG_TAG = "SpalshActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spalsh);


        Thread timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Intent intent = new Intent(getApplicationContext(),SongListingActivity.class);
                    startActivity(intent);
                    finish();

                } catch (InterruptedException e) {
                    Toast.makeText(SpalshActivity.this, R.string.something_wrong, Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        };
        timerThread.start();
    }
}




