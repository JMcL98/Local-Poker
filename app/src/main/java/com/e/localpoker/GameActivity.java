package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GameActivity extends AppCompatActivity {

    HostGameManager hgm;
    ClientGameManager cgm;
    int type; // 1 = host, 2 = client
    gameThread gameThread;
    Handler gameThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameThreadHandler = gameThread.getHandler();

        Bundle receivedBundle = getIntent().getExtras();
        type = receivedBundle.getInt("type");
        Message gameMessage = Message.obtain(gameThreadHandler);
        gameMessage.setData(receivedBundle);
        if (type == 1) {
            hgm = receivedBundle.getParcelable("manager");
            gameMessage.what = 1;
        } else if (type == 2) {
            cgm = receivedBundle.getParcelable("manager");
            gameMessage.what = 2;
        }
        gameMessage.sendToTarget();
    }
}
