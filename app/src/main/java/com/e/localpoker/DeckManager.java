package com.e.localpoker;

import java.util.Random;

class DeckManager {

    private Card[] deck = new Card[52];
    private boolean deckInitialised = false;
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

    private boolean initialiseDeck() {
        int j = 2;
        int i = 0;
        while (i < 13) {
            deck[i] = new Card('s', j, i);
            j++;
            i++;
        }
        j = 2;
        while (i < 26) {
            deck[i] = new Card('c', j, i);
            j++;
            i++;
        }
        j = 2;
        while (i < 39) {
            deck[i] = new Card('h', j, i);
            j++;
            i++;
        }
        j = 2;
        while (i < 52) {
            deck[i] = new Card('d', j, i);
            j++;
            i++;
        }
        return true;
    }

    public Card[] dealHand() {
        int i = rng.nextInt(51);
        boolean card1 = false;
        boolean card2 = false;
        Card[] hand = new Card[2];
        while (!card1) {
            if (!deck[i].checkDealtStatus()) {
                hand[0] = deck[i].dealCard();
                card1 = true;
            } else {
                i = rng.nextInt(51);
            }
        }
        while (!card2) {
            if (!deck[i].checkDealtStatus()) {
                hand[1] = deck[i].dealCard();
                card2 = true;
            } else {
                i = rng.nextInt(51);
            }
        }

        return hand;
    }
}
