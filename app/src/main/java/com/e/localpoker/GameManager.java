package com.e.localpoker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GameManager extends Service {

    Player[] players;
    public GameManager(Player[] players) {
        this.players = players;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
