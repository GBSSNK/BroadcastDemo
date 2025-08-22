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

public class MainActivity extends AppCompatActivity {
    TextView tvMessages, tvTime;
    BroadcastService mBCService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        tvMessages = findViewById(R.id.tvMessages);
        tvTime = findViewById(R.id.tvTime);
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> startService(new Intent(getBaseContext(), BroadcastService.class)));
        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> stopService(new Intent(getBaseContext(), BroadcastService.class)));
        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> moveToNext());
        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> moveExit());
    }

    private void moveExit() {
        unregisterReceiver(updateTextViewReceiver);
        finish();
    }

    private void moveToNext() {
        Intent i = new Intent(this, activity_next.class);
        startActivity(i);
        Toast.makeText(this, "start next and finish main", Toast.LENGTH_LONG).show();
//        finish();
    }
    private final BroadcastReceiver updateTextViewReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals("ACTION_UPDATE_TEXT_VIEW")) {
                String text = intent.getStringExtra("text");
                if (text != null) tvMessages.setText(tvMessages.getText() + "\n" + text);
            }
            else if (intent.getAction().equals("ACTION_UPDATE_TIME")) {
                String text = intent.getStringExtra("time");
                if (text != null) tvTime.setText(text);
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BroadcastService.LocalBinder lb = (BroadcastService.LocalBinder) service;
            mBCService = lb.getService();
//            mBCService.startTimer();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        // start bound service, will end on finish of this activity
        Intent intent = new Intent(this, BroadcastService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "onStart main", Toast.LENGTH_LONG).show();
        super.onStart();
        IntentFilter i = new IntentFilter();
        i.addAction("ACTION_UPDATE_TEXT_VIEW");
        i.addAction("ACTION_UPDATE_TIME");
        registerReceiver(updateTextViewReceiver, i, RECEIVER_EXPORTED);
    }
    @Override
    protected void onStop() {
        Toast.makeText(this, "onStop main", Toast.LENGTH_LONG).show();
        unregisterReceiver(updateTextViewReceiver);
        super.onStop();
    }
}