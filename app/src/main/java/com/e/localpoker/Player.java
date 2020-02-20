package com.e.localpoker;

class Player {

    private String playerName;
    private int chips;
    private Card[] hand;
    private int numCardsInHand;

    Player (String name) {
        playerName = name;
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
}
