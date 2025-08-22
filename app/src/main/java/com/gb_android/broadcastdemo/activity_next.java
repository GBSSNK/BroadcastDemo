package com.gb_android.broadcastdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_next extends AppCompatActivity {
    TextView tvNextTime;
    BroadcastService mBCService;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BroadcastService.LocalBinder lb = (BroadcastService.LocalBinder) service;
            mBCService = lb.getService();
            mBCService.startTimer();
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next);
        tvNextTime = findViewById(R.id.tvNextTime);
        Button btnNextBack = findViewById(R.id.btnNextBack);
        btnNextBack.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            Toast.makeText(this, "start main again and finish next", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private final BroadcastReceiver nextUpdateTextViewReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals("ACTION_UPDATE_TEXT_VIEW")) {
                String text = intent.getStringExtra("text");
//                if (text != null) tvMessages.setText(text);
            }
            else if (intent.getAction().equals("ACTION_UPDATE_TIME")) {
                String text = intent.getStringExtra("time");
                if (text != null) tvNextTime.setText(text);
            }
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        // binding only needed if you like to use the services methods
        // not for receiving broadcasts
        // here we start the clock in the onServiceConnected
        Intent intent = new Intent(this, BroadcastService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "onStart next", Toast.LENGTH_LONG).show();
        IntentFilter i = new IntentFilter();
        i.addAction("ACTION_UPDATE_TEXT_VIEW");
        i.addAction("ACTION_UPDATE_TIME");
        registerReceiver(nextUpdateTextViewReceiver, i, RECEIVER_EXPORTED);
    }
    @Override
    protected void onStop() {
        Toast.makeText(this, "onStop next", Toast.LENGTH_LONG).show();
        unregisterReceiver(nextUpdateTextViewReceiver);
        super.onStop();
    }
}