package com.play.sibasish.olaplay;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Sibasish Mohanty on 17/12/17.
 */

public class OlaApplication extends Application {
    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);

    }
    public static Context getContext(){
        return mContext;
    }
}
