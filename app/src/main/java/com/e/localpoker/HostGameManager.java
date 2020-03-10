package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Arrays;

public class HostGameManager extends Service {

    private Player[] players;
    private int chipsPot;
    private int gameStage;
    private DeckManager dm;
    private Card[] communityCards;
    NsdHost nsdHost;

    public HostGameManager(Player[] players, DeckManager dm, Context context) {
        this.players = players;
        this.chipsPot = 0;
        this.gameStage = 0;
        this.dm = dm;
        this.communityCards = new Card[5];
        nsdHost = new NsdHost(context);
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
