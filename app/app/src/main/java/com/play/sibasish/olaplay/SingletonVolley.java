package com.play.sibasish.olaplay;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Sibasish Mohanty on 16/12/17.
 */

public class SingletonVolley {

    private static SingletonVolley instance;
    private RequestQueue requestQueue;
    private static Context context;

    private SingletonVolley(Context context){
        this.context =  context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public static synchronized SingletonVolley getInstance(Context context){

        if (instance == null){
            instance = new SingletonVolley(context);
        }
        return instance;
    }

    public<T> void addToRequestQueue(Request<T> request){
        requestQueue.add(request);
    }

}
