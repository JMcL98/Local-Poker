package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientGameManager extends Service implements Parcelable {

    private int STARTING_CHIPS = 1000; // will be able to be modified in full game

    HostGameManager host;
    String name;
    Context calledContext;
    Player[] players;
    int myPlayerIndex;
    int callAmount;
    int totalPot;
    int smallBlind;
    int bigBlind;
    GameActivity gameActivity;
    DeckManager dm;
    DataInputStream clientInput;
    DataOutputStream clientOutput;
    NsdClient nsdClient;

    ClientGameManager(Context context, String name) { // Game manager on client's phone to keep track of player information sent by the host
        this.name = name;
        this.calledContext = context;
        this.dm = new DeckManager();
        this.callAmount = 0; // initialise game values
        this.totalPot = 0;
    }

    ClientGameManager(DataOutputStream o, DataInputStream i, String name) {
        this.name = name;
        clientOutput = o;
        clientInput = i;
        this.dm = new DeckManager();
        this.callAmount = 0;
        this.totalPot = 0;
        this.smallBlind = 10;
        this.bigBlind = 20;
    }

    protected ClientGameManager(Parcel in) {
        STARTING_CHIPS = in.readInt();
        host = in.readParcelable(HostGameManager.class.getClassLoader());
        name = in.readString();
        myPlayerIndex = in.readInt();
        callAmount = in.readInt();
        totalPot = in.readInt();
    }

    public static final Creator<ClientGameManager> CREATOR = new Creator<ClientGameManager>() {
        @Override
        public ClientGameManager createFromParcel(Parcel in) {
            return new ClientGameManager(in);
        }

        @Override
        public ClientGameManager[] newArray(int size) {
            return new ClientGameManager[size];
        }
    };

    void increaseBlinds(int multiplyValue) {
        smallBlind = smallBlind * multiplyValue;
        bigBlind = bigBlind * multiplyValue;
    }

    void resetHand() {
        for (Player player : players) {
            player.unFold();
            player.chipsInPlay = 0;
        }
        players[myPlayerIndex].resetHand();
        callAmount = bigBlind;
    }

    void initialisePlayers(int numPlayers, int myIndex) {
        if (numPlayers == 1) {
            numPlayers = 10;
        }
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            try {
                players[i] = new Player(0, null);
                players[i].addChips(STARTING_CHIPS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myPlayerIndex = myIndex;
        addPlayer(this.name, this.myPlayerIndex);
    }

    void addPlayer(String name, int index) {
        players[index].setPlayerName(name);
    }

    void reply(String action) {
        try {
            clientOutput.writeByte(4);
            clientOutput.writeUTF(action);
            clientOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addCard(int index) {
        players[myPlayerIndex].addCard(dm.dealSpecificCard(index));
        if (players[myPlayerIndex].r) {
            callAmount = 0;
            players[myPlayerIndex].r = false;
        }
    }

    void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }


    void startClientNsd() {
        nsdClient = new NsdClient(this.calledContext, "Client", this);
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(STARTING_CHIPS);
        dest.writeParcelable(host, flags);
        dest.writeString(name);
        dest.writeInt(myPlayerIndex);
        dest.writeInt(callAmount);
        dest.writeInt(totalPot);
    }
}
