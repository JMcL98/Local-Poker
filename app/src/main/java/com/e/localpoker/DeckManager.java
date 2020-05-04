package com.e.localpoker;

import java.util.Random;

class DeckManager {

    private Card[] deck = new Card[52];
    private boolean deckInitialised;
    private Random rng;

    DeckManager() {
        deckInitialised = initialiseDeck();
        rng = new Random();
    }

    void resetDeck() {
        for (int i = 0; i < 52; i++) {
            deck[i].reset();
        }
    }

    boolean checkInitialisedStatus() {
        return deckInitialised;
    }

    private boolean initialiseDeck() {
        int j = 2;
        int i = 0;
        while (i < 13) {
            deck[i] = new Card('s', j, i); // spades
            j++;
            i++;
        }
        j = 2;
        while (i < 26) {
            deck[i] = new Card('c', j, i); // clubs
            j++;
            i++;
        }
        j = 2;
        while (i < 39) {
            deck[i] = new Card('h', j, i); // hearts
            j++;
            i++;
        }
        j = 2;
        while (i < 52) {
            deck[i] = new Card('d', j, i); // diamonds
            j++;
            i++;
        }
        return true;
    }

    Card dealCard() {
        int i;
        for (int j = 0; j < 500; j++) {
            i = rng.nextInt(52);
            if (!deck[i].checkDealtStatus()) {
                return deck[i].dealCard();
            }
        }
        return new Card('j', 0, 53); // return a joker if cannot find a new card
    }

    Card dealSpecificCard(int index) {
        return deck[index];
    }
}
