package com.e.localpoker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HostGameManager extends Service {

    private Player[] players;
    private int chipsPot;

    public HostGameManager(Player[] players) {
        this.players = players;
        this.chipsPot = 0;
    }

    void addToPot(int chips) {
        this.chipsPot += chips;
    }

    int takePot() {
        int temp = this.chipsPot;
        this.chipsPot = 0;
        return temp;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
