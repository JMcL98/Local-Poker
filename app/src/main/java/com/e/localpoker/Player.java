package com.e.localpoker;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Player {

    private final int playerID;
    private String playerName;
    private int chips;
    private Card[] hand;
    private int numCardsInHand;
    Socket clientSocket;
    DataOutputStream playerOutput;
    DataInputStream playerInput;

    Player (int playerID, Socket clientSocket) throws IOException {
        this.playerID = playerID;
        if (playerID != 0) {
            this.clientSocket = clientSocket;
            this.playerOutput = new DataOutputStream(clientSocket.getOutputStream());
            this.playerInput = new DataInputStream(clientSocket.getInputStream());
        } else {
            this.clientSocket = null;
            this.playerOutput = null;
            this.playerInput = null;
        }
        chips = 0;
        numCardsInHand = 0;
        hand = new Card[7];
        resetHand();
    }

    void setPlayerName(String name) {
        this.playerName = name;
        Log.d("Jordan", "New player added: " + getPlayerName());
    }

    void addChips(int numChips) {
        this.chips += numChips;
    }

    void addCard(Card newCard) {
        if (numCardsInHand < 7) {
            hand[numCardsInHand] = newCard;
            numCardsInHand++;
        }
    }

    void resetHand() {
        for (int i = 0; i < numCardsInHand; i++) {
            this.hand[i] = null;
        }
        numCardsInHand = 0;
    }

    Card[] getHand() {
        Card[] returnHand = new Card[numCardsInHand];
        System.arraycopy(hand, 0, returnHand, 0, numCardsInHand);
        return returnHand;
    }

    String getPlayerName() {
        return playerName;
    }

    int getPlayerID() {
        return playerID;
    }
}
