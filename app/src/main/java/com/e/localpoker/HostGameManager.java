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

    public HostGameManager(Context context, String hostname) {
        this.tempPlayers = new Player[MAX_PLAYERS];
        this.hostName = hostname;
        this.calledContext = context;
    }

    public HostGameManager(Player[] players) {
        this.players = players;
        this.chipsPot = 0;
        this.gameStage = 1;
        this.numPlayers = players.length;
        this.smallBlind = 10;
        this.playersCalled = 0;
        this.bigBlind = 20;
        this.dm = new DeckManager();
        this.communityCards = new Card[5];
        this.dealerIndex = 0;
        for (Player player : this.players) {
            player.addChips(STARTING_CHIPS);
        }
        this.playersLeft = numPlayers;
    }


    protected HostGameManager(Parcel in) {
        MAX_PLAYERS = in.readInt();
        STARTING_CHIPS = in.readInt();
        numPlayers = in.readInt();
        playersLeft = in.readInt();
        chipsPot = in.readInt();
        gameStage = in.readInt();
        smallBlind = in.readInt();
        bigBlind = in.readInt();
        callAmount = in.readInt();
        playersCalled = in.readInt();
        playersInPlay = in.readInt();
        hostName = in.readString();
        dealerIndex = in.readInt();
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

    void startHostNsd() throws IOException {
        hostObj = new NsdHost(this.calledContext, this);
        addPlayer(null);
        tempPlayers[0].setPlayerName(this.hostName);
    }

    void startGame() {
        for (int i = 0; i < tempPlayers.length; i++) {
            if (tempPlayers[i] != null) {
                if (tempPlayers[i].playerOutput != null) {
                    try {
                        tempPlayers[i].playerOutput.writeByte(1);
                        tempPlayers[i].playerOutput.writeUTF("start_game");
                        tempPlayers[i].playerOutput.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    void sendNames() {
        for (int a = 1; a < numPlayers; a++) {
            try {
                players[a].playerOutput.writeByte(9);
                players[a].playerOutput.writeUTF(a + "" + numPlayers);
                players[a].playerOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < numPlayers; i++) {
            for (int j = 1; j < numPlayers; j++) {
                try {
                    players[j].playerOutput.writeByte(10 + i);
                    players[j].playerOutput.writeUTF(players[j].getPlayerName());
                    players[j].playerOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void increaseBlinds(int multiplyValue) {
        smallBlind = smallBlind * multiplyValue;
        bigBlind = bigBlind * multiplyValue;
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
        increaseBlinds(2);
    }

    void addToPot(int chips) {
        this.chipsPot += chips;
    }

    int getPot() {
        return chipsPot;
    }

    void receiveCommand(String reply, int playerIndex) {
        char type = reply.charAt(0);
        switch (type) {
            case 'c':
                players[playerIndex].addChipsInPlay(callAmount - players[playerIndex].chipsInPlay);
                playersCalled++;
                updateClientPlayerInfo(playerIndex, "c");
                break;
            case 'r':
                players[playerIndex].addChipsInPlay((Integer.parseInt(reply.substring(1))) - players[playerIndex].chipsInPlay); // double current call
                playersCalled = 1;
                updateClientPlayerInfo(playerIndex, "r" + (reply.substring(1)));
                callAmount = Integer.parseInt(reply.substring(1));
                break;
            case 'f':
                players[playerIndex].fold();
                playersInPlay--;
                updateClientPlayerInfo(playerIndex, "f");
                break;
        }
        if (!players[playerIndex].folded && players[playerIndex].getChips() == 0) {
            players[playerIndex].allIn = true;
        }
    }

    void updateClientPlayerInfo(int index, String message) {
        for (int i = 1; i < numPlayers; i++) {
            try {
                players[i].playerOutput.writeByte(index + 10);
                players[i].playerOutput.writeUTF(message);
                players[i].playerOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateClientPotInfo();
    }

    void updateClientPotInfo() {
        for (int i = 1; i < numPlayers; i++) {
            try {
                players[i].playerOutput.writeByte(7);
                players[i].playerOutput.writeUTF(chipsPot + "");
                players[i].playerOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    void advanceStage() {
        for (Player player : players) {
            if (!player.folded) {
                addToPot(callAmount);
            } else {
                addToPot(player.chipsInPlay);
            }
            player.chipsInPlay = 0;
        }
        gameStage++;
        playersCalled = 0;
        callAmount = 0;
        switch (gameStage) {
            case (2) :
                dealCommunityCard(0);
                dealCommunityCard(1);
                dealCommunityCard(2);
                break;
            case (3) :
                dealCommunityCard(3);
                break;
            case (4) :
                dealCommunityCard(4);
                break;
            case (5) :
                int handStrength = 0;
                int roundWinner = 0;
                for (int i = 0; i < players.length; i++) {
                    if (!players[i].eliminated && !players[i].folded) {
                        int newStrength = HandStrength.calculateStrength(players[i].getHand());
                        if (newStrength > handStrength) {
                            roundWinner = i;
                            handStrength = newStrength;
                        }
                    }
                }
                finishRound(roundWinner);
                break;
        }
    }

    void finishRound(int winningPlayerIndex) {
        Log.d("Jordan", "Round winner: " + players[winningPlayerIndex].getPlayerName());
        int winningChips = takePot();
        players[winningPlayerIndex].addChips(winningChips);
        updateClientPlayerInfo(winningPlayerIndex, "w" + winningChips);
        for (Player player : players) {
            if (player.getChips() < 1) {
                eliminatePlayer(player.getPlayerID());
            }
            if (player.playerOutput != null) {
                try {
                    player.playerOutput.writeByte(5);
                    player.playerOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    }

    void resetRound() {
        for (Player player : players) {
            player.resetHand();
            player.unFold();
            player.allIn = false;
        }
        Arrays.fill(communityCards, null);
        dm.resetDeck();
        playersInPlay = numPlayers;
        this.callAmount = bigBlind;
        gameStage = 1;
    }

    int blinds() {
        if (playersLeft == 2) {
            int temp = 0;
            for (int i = 0; i < players.length; i++) {
                if (!players[i].eliminated) {
                    if (i == dealerIndex) {
                        players[i].addChipsInPlay(bigBlind);
                        updateClientPlayerInfo(i, "b");
                    } else {
                        players[i].addChipsInPlay(smallBlind);
                        updateClientPlayerInfo(i, "s");
                        temp = i;
                    }
                }
            }
            return temp;
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
    }

    int takePot() {
        int temp = this.chipsPot;
        this.chipsPot = 0;
        return temp;
    }

    void initialDeal() throws InterruptedException {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MAX_PLAYERS);
        dest.writeInt(STARTING_CHIPS);
        dest.writeInt(numPlayers);
        dest.writeInt(playersLeft);
        dest.writeInt(chipsPot);
        dest.writeInt(gameStage);
        dest.writeInt(smallBlind);
        dest.writeInt(bigBlind);
        dest.writeInt(callAmount);
        dest.writeInt(playersCalled);
        dest.writeInt(playersInPlay);
        dest.writeString(hostName);
        dest.writeInt(dealerIndex);
    }
}


