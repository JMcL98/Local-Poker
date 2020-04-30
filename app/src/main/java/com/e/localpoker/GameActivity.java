package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    HostGameManager hgm;
    ClientGameManager cgm;
    boolean host; // true = host, false = client
    gameThread gameThread;
    TextView tv;
    Button callButton, raiseButton, foldButton;
    private String bufferedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        callButton = (Button) findViewById(R.id.callButton);
        raiseButton = (Button) findViewById(R.id.raiseButton);
        foldButton = (Button) findViewById(R.id.foldButton);
        gameThread = new gameThread(this, new Handler());
        gameThread.start();
        while (true) {
            if (gameThread.getHandler() != null) {
                Bundle receivedBundle = getIntent().getExtras();
                host = receivedBundle.getBoolean("type");
                Message gameMessage = Message.obtain(gameThread.getHandler());
                gameMessage.setData(receivedBundle);
                if (host) {
                    hgm = receivedBundle.getParcelable("manager");
                    gameMessage.what = 1;
                } else {
                    cgm = receivedBundle.getParcelable("manager");
                    cgm.setGameActivity(this);
                    gameMessage.what = 2;
                }
                gameMessage.sendToTarget();
                break;
            }
        }
        tv = (TextView) findViewById(R.id.textView);
    }

    public void onCall(View v) {
        bufferedAction = "call";
    }

    public void onRaise(View v) {
        bufferedAction = "raise";
    }

    public void onFold(View v) {
        bufferedAction = "fold";
    }

    String getBufferedAction() {
        String temp = bufferedAction;
        bufferedAction = "";
        return temp;
    }
}
