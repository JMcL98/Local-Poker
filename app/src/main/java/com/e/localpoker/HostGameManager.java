package com.e.localpoker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;

import java.util.Arrays;

public class HostGameManager extends Service {

    private Player[] players;
    private int chipsPot;
    private int gameStage;
    private DeckManager dm;
    private Card[] communityCards;
    String serviceName;
    Context calledContext;
    NsdManager nsdManager;
    NsdManager.RegistrationListener registrationListener;

    public HostGameManager(Player[] players, DeckManager dm, Context context) {
        this.players = players;
        this.chipsPot = 0;
        this.gameStage = 0;
        this.dm = dm;
        this.communityCards = new Card[5];
        this.serviceName = "LocalPoker";
        this.calledContext = context;
    }

    void registerService(int port) {
        NsdServiceInfo info = new NsdServiceInfo();

        info.setServiceName(serviceName);
        info.setServiceType("_http._tcp.");
        info.setPort(port);

        nsdManager = (NsdManager) this.calledContext.getSystemService(Context.NSD_SERVICE);

        if (nsdManager != null) {
            nsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, registrationListener);
        }
    }

    void initialiseListener() {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                serviceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        };
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
