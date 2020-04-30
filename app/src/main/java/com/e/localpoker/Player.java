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
    int chipsInPlay;
    private Card[] hand;
    private int numCardsInHand;
    boolean folded;
    boolean eliminated;
    Socket clientSocket;
    DataOutputStream playerOutput;
    DataInputStream playerInput;
    GameActivity ga;

    Player (int playerID, Socket clientSocket) throws IOException {
        this.playerID = playerID;
        if (playerID > 0) {
            this.clientSocket = clientSocket;
            this.playerOutput = new DataOutputStream(clientSocket.getOutputStream());
            this.playerInput = new DataInputStream(clientSocket.getInputStream());
        } else {
            this.clientSocket = null;
            this.playerOutput = null;
            this.playerInput = null;
        }
        chips = 0;
        chipsInPlay = 0;
        numCardsInHand = 0;
        folded = false;
        eliminated = false;
        hand = new Card[7];
        resetHand();
    }

    void setPlayerName(String name) {
        this.playerName = name;
        Log.d("Jordan", "New player added: " + getPlayerName());
    }

    void setGameActivity(GameActivity ga) {
        this.ga = ga;
    }

    String requestMove(int callAmount) {
        if (playerID > 0) {
            try {
                playerOutput.writeByte(3);
                playerOutput.writeUTF(callAmount + "");
                playerOutput.flush();
                while (true) {
                    if (playerInput != null) {
                        if (playerInput.readByte() == 3) {
                            return playerInput.readUTF();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return ga.getBufferedAction();
        }
        return null;
    }

    void fold() {
        folded = true;
        resetHand();
    }

    void unFold() {
        folded = false;
    }

    void addChips(int numChips) {
        this.chips += numChips;
    }

    int getChips() {
        return this.chips;
    }

    void addChipsInPlay(int numChips) {
        addChips(-(numChips));
        chipsInPlay += numChips;
    }

    void addCard(Card newCard) {
        if (numCardsInHand < 7) {
            hand[numCardsInHand] = newCard;
            if (playerOutput != null) {
                try {
                    playerOutput.writeByte(4);
                    playerOutput.writeUTF(newCard.getIndex() + "");
                    playerOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
