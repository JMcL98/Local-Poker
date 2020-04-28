package com.e.localpoker;

import android.app.Activity;
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
    private int STARTING_CHIPS = 1000;

    Player[] tempPlayers;
    Player[] players;
    int numPlayers;
    int playersLeft;
    private int chipsPot;
    private int gameStage; // 5th stage = show cards
    int smallBlind;
    int bigBlind;
    private DeckManager dm;
    private Card[] communityCards;
    private Context calledContext;
    private String hostName;
    NsdHost hostObj;

    public HostGameManager(DeckManager dm, Context context, String hostname) {
        this.tempPlayers = new Player[MAX_PLAYERS];
        this.hostName = hostname;
        this.chipsPot = 0;
        this.gameStage = 0;
        this.numPlayers = 0;
        this.smallBlind = 10;
        this.bigBlind = 20;
        this.dm = dm;
        this.communityCards = new Card[5];
        this.calledContext = context;
    }


    void startHostNsd() throws IOException {
        hostObj = new NsdHost(this.calledContext, this);
        addPlayer(null);
        tempPlayers[0].setPlayerName(this.hostName);
    }

    void startGame() {
        playersLeft = numPlayers;
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = tempPlayers[i];
        }
        for (Player player : players) {
            if (player.playerOutput != null) {
                try {
                    player.playerOutput.writeByte(1);
                    player.playerOutput.writeUTF("start_game");
                    player.playerOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            player.addChips(STARTING_CHIPS);
        }
    }

    void eliminatePlayer(int ID) {
        for (Player player : players) {
            if (player.getPlayerID() == ID) {
                player.eliminated = true;
            }
        }
        playersLeft--;
        smallBlind = smallBlind * 2;
        bigBlind = bigBlind * 2;
    }

    void addToPot(int chips) {
        this.chipsPot += chips;
    }

    void advanceStage() {
        for (Player player : players) {
            addToPot(player.chipsInPlay);
            player.chipsInPlay = 0;
        }
        gameStage++;
        switch (gameStage) {
            case (1) :
                dealCommunityCard(0);
                dealCommunityCard(1);
                dealCommunityCard(2);
            case (2) :
                dealCommunityCard(3);
            case (3) :
                dealCommunityCard(4);
            case (4) :
                int handStrength = 0;
                int roundWinner = 0;
                for (Player player : players) {
                    if (!player.eliminated) {
                        int newStrength = HandStrength.calculateStrength(player.getHand());
                        if (newStrength > handStrength) {
                            roundWinner = player.getPlayerID();
                        }
                    }
                }
                finishRound(roundWinner);
        }
    }

    void resetRound() {
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
                player.addChips(takePot());
            }
        }
        resetRound();
    }

    int takePot() {
        int temp = this.chipsPot;
        this.chipsPot = 0;
        return temp;
    }

    void initialDeal() {
        for (Player player : players) {
            if (!player.eliminated) {
                player.addCard(dm.dealCard(60));
                player.addCard(dm.dealCard(60));
            }
        }
    }

    void dealCommunityCard(int index) {
        communityCards[index] = dm.dealCard(60);
        for (Player player : players) {
            if (!player.eliminated) {
                player.addCard(communityCards[index]);
            }
        }
    }

    void addPlayer(Socket clientSocket) throws IOException {
        Player newPlayer = new Player(numPlayers, clientSocket);
        this.tempPlayers[numPlayers] = newPlayer;
        this.numPlayers++;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected HostGameManager(Parcel in) {
        MAX_PLAYERS = in.readInt();
        numPlayers = in.readInt();
        chipsPot = in.readInt();
        gameStage = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MAX_PLAYERS);
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


