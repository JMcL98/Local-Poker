package com.e.localpoker;

class Player {

    private final int playerID;
    private final String playerName;
    private int chips;
    private Card[] hand;
    private int numCardsInHand;

    Player (String name, int playerID) {
        playerName = name;
        this.playerID = playerID;
        chips = 0;
        numCardsInHand = 0;
        hand = new Card[7];
        resetHand();
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
