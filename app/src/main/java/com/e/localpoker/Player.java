package com.e.localpoker;

class Player {

    private int chips;
    private Card[] hand;
    private int numCardsInHand;

    public Player () {
        chips = 0;
        numCardsInHand = 0;
        hand = new Card[7];
        resetHand();
    }

    public void addChips(int numChips) {
        this.chips += chips;
    }

    public void addCard(Card newCard) {
        if (numCardsInHand < 7) {
            hand[numCardsInHand] = newCard;
            numCardsInHand++;
        }
    }

    public void resetHand() {
        for (Card card : hand) {
            card = null;
        }
        numCardsInHand = 0;
    }

    public Card[] getHand() {
        Card[] returnHand = new Card[numCardsInHand];
        for (int i = 0; i < numCardsInHand; i++) {
            returnHand[i] = hand[i];
        }
        return returnHand;
    }
}
