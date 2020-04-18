package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class HostGameManager extends Service implements Parcelable {

    private int MAX_PLAYERS = 10;
    private String HOST_NAME = "Jordan";

    Player[] players;
    int numPlayers;
    private int chipsPot;
    private int gameStage;
    private DeckManager dm;
    private Card[] communityCards;
    private Context calledContext;
    NsdHost hostObj;

    public HostGameManager(DeckManager dm, Context context) {
        this.players = new Player[MAX_PLAYERS];
        this.chipsPot = 0;
        this.gameStage = 0;
        this.numPlayers = 0;
        this.dm = dm;
        this.communityCards = new Card[5];
        this.calledContext = context;
    }


    void startHostNsd() throws IOException {
        hostObj = new NsdHost(this.calledContext, this);
        addPlayer(null);
        players[0].setPlayerName(HOST_NAME);
    }

    void startGame() {
        hostObj.acceptingPlayers = false;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getPlayerName() == null) {
                players[i] = null;
            }
        }
        initialDeal();
    }

    void addToPot(int chips) {
        this.chipsPot += chips;
    }

    void advanceStage() {
        gameStage++;
    }

    void resetRounds() {
        for (Player player : players) {
            player.resetHand();
        }
        Arrays.fill(communityCards, null);
        dm.resetDeck();
        gameStage = 0;
    }

    void finishRound(int winningPlayerID) {
        for (Player player : players) {
            if (player.getPlayerID() == winningPlayerID) {
                player.addChips(this.chipsPot);
            }
        }
        chipsPot = 0;
        resetRounds();
    }

    int takePot() {
        int temp = this.chipsPot;
        this.chipsPot = 0;
        return temp;
    }

    void initialDeal() {
        for (Player player : players) {
            player.addCard(dm.dealCard(60));
            player.addCard(dm.dealCard(60));
        }
    }

    void dealCommunityCard(int index) {
        communityCards[index] = dm.dealCard(60);
        for (Player player : players) {
            player.addCard(communityCards[index]);
        }
    }

    void addPlayer(Socket clientSocket) throws IOException {
        Player newPlayer = new Player(numPlayers, clientSocket);
        this.players[numPlayers] = newPlayer;
        this.numPlayers++;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected HostGameManager(Parcel in) {
        MAX_PLAYERS = in.readInt();
        HOST_NAME = in.readString();
        numPlayers = in.readInt();
        chipsPot = in.readInt();
        gameStage = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MAX_PLAYERS);
        dest.writeString(HOST_NAME);
        dest.writeInt(numPlayers);
        dest.writeInt(chipsPot);
        dest.writeInt(gameStage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HostGameManager> CREATOR = new Creator<HostGameManager>() {
        @Override
        public HostGameManager createFromParcel(Parcel in) {
            return new HostGameManager(in);
        }

        @Override
        public HostGameManager[] newArray(int size) {
            return new HostGameManager[size];
        }
    };
}


