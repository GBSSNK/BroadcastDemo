package com.gb_android.broadcastdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BroadcastService extends Service {
    private final IBinder binder = new LocalBinder();
    public class LocalBinder extends Binder {
        BroadcastService getService() {
            return BroadcastService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent("ACTION_UPDATE_TEXT_VIEW");
        i. putExtra("text", "Broadcasting service started");
        getApplicationContext().sendBroadcast(i);
        startTimer();
        return START_STICKY;
    }

    Boolean running = true;
    public void startTimer() {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (running) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
//                    Intent i = new Intent("ACTION_UPDATE_TEXT_VIEW");
                    Intent i = new Intent("ACTION_UPDATE_TIME");
                    i.putExtra("time", currentTime);
                    getApplicationContext().sendBroadcast(i);
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }
    @Override
    public void onDestroy() {
        running = false;
        Intent i = new Intent("ACTION_UPDATE_TEXT_VIEW");
        i. putExtra("text", "Broadcasting service stopped");
        getApplicationContext().sendBroadcast(i);
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}