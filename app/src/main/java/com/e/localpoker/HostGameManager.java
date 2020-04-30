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
    int callAmount;
    int playersCalled;
    int playersInPlay;
    private DeckManager dm;
    private Card[] communityCards;
    private Context calledContext;
    private String hostName;
    NsdHost hostObj;

    int dealerIndex;

    public HostGameManager(DeckManager dm, Context context, String hostname) {
        this.tempPlayers = new Player[MAX_PLAYERS];
        this.hostName = hostname;
        this.chipsPot = 0;
        this.gameStage = 1;
        this.numPlayers = 0;
        this.smallBlind = 10;
        this.playersCalled = 0;
        this.bigBlind = 20;
        this.dm = dm;
        this.communityCards = new Card[5];
        this.calledContext = context;
        this.dealerIndex = 0;
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
        resetRound();
    }

    void eliminatePlayer(int ID) {
        int i = 0;
        while (true) {
            if (players[i].getPlayerID() == ID) {
                players[i].eliminated = true;
                updateClientPlayerInfo(i, "e");
                break;
            }
            i++;
        }
        playersLeft--;
        smallBlind = smallBlind * 2;
        bigBlind = bigBlind * 2;
    }

    void addToPot(int chips) {
        this.chipsPot += chips;
    }

    void receiveCommand(String reply, int playerIndex) {
        switch (reply) {
            case "call":
                players[playerIndex].addChipsInPlay(callAmount - players[playerIndex].chipsInPlay);
                playersCalled++;
                updateClientPlayerInfo(playerIndex, "c");
                break;
            case "raise":
                players[playerIndex].addChipsInPlay((callAmount * 2) - players[playerIndex].chipsInPlay); // double current call
                playersCalled = 1;
                updateClientPlayerInfo(playerIndex, "r" + (callAmount * 2));
                break;
            case "fold":
                players[playerIndex].fold();
                playersInPlay--;
                updateClientPlayerInfo(playerIndex, "f");
                break;
        }
    }

    void updateClientPlayerInfo(int index, String message) {
        for (int i = 0; i < numPlayers; i++) {
            try {
                players[i].playerOutput.writeByte(index + 10);
                players[i].playerOutput.writeUTF(message);
                players[i].playerOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void advanceStage() {
        for (Player player : players) {
            addToPot(player.chipsInPlay);
            player.chipsInPlay = 0;
        }
        gameStage++;
        callAmount = 0;
        switch (gameStage) {
            case (2) :
                dealCommunityCard(0);
                dealCommunityCard(1);
                dealCommunityCard(2);
            case (3) :
                dealCommunityCard(3);
            case (4) :
                dealCommunityCard(4);
            case (5) :
                int handStrength = 0;
                int roundWinner = 0;
                for (Player player : players) {
                    if (!player.eliminated) {
                        int newStrength = HandStrength.calculateStrength(player.getHand());
                        if (newStrength > handStrength) {
                            roundWinner = player.getPlayerID();
                            handStrength = newStrength;
                        }
                    }
                }
                finishRound(roundWinner);
        }
    }

    void finishRound(int winningPlayerID) {
        for (Player player : players) {
            if (player.getPlayerID() == winningPlayerID) {
                player.addChips(takePot());
            }
            if (player.getChips() < 1) {
                eliminatePlayer(player.getPlayerID());
            }
        }
        boolean dealerFound = false;
        while (!dealerFound) {
            dealerIndex++;
            if (dealerIndex >= numPlayers) {
                dealerIndex = 0;
            }
            if (players[dealerIndex].eliminated) {
                dealerIndex++;
            } else {
                dealerFound = true;
            }

        }
        resetRound();
    }

    void resetRound() {
        for (Player player : players) {
            player.resetHand();
            player.unFold();
        }
        Arrays.fill(communityCards, null);
        dm.resetDeck();
        playersInPlay = numPlayers;
        this.callAmount = bigBlind;
        gameStage = 1;
    }

    int blinds() {
        if (playersLeft == 2) {
            for (int i = 0; i < players.length; i++) {
                if (!players[i].eliminated) {
                    if (i == dealerIndex) {
                        players[i].addChipsInPlay(bigBlind);
                    } else {
                        players[i].addChipsInPlay(smallBlind);
                    }
                }
            }
        } else {
            int blindsFound = 0;
            int i = 1;
            while (true) {
                if (players[dealerIndex + i].eliminated) {
                    i++;
                } else {
                    if (blindsFound == 0) {
                        players[dealerIndex + i].addChipsInPlay(smallBlind);
                    } else if (blindsFound == 1) {
                        players[dealerIndex + i].addChipsInPlay(bigBlind);
                    } else if (blindsFound == 2) {
                        return dealerIndex + i; // return starting player
                    }
                    blindsFound++;
                    i++;
                }
                if ((dealerIndex + i) == numPlayers) {
                    i = 0 - dealerIndex;
                }
            }

        }
        return 0;
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
            if (!player.eliminated && !player.folded) {
                player.addCard(communityCards[index]);
            }
        }
    }

    void addPlayer(Socket clientSocket) throws IOException {
        Player newPlayer = new Player(numPlayers, clientSocket);
        this.tempPlayers[numPlayers] = newPlayer;
        this.numPlayers++;
    }

    void endGame(Player winningPlayer) {
        for (Player player : players) {
            try {
                player.playerOutput.writeByte(6);
                player.playerOutput.writeUTF(winningPlayer.getPlayerName());
                player.playerOutput.flush();
                player.playerOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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


