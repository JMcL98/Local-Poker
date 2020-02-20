package com.e.localpoker;

import android.widget.Switch;

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

    Card dealCard(int index) {
        int i;
        if (index > 52) {
            i = rng.nextInt(52);
        } else {
            i = index;
        }
        for (int j = 0; j < 500; j++) {
            if (!deck[i].checkDealtStatus()) {
                return deck[i].dealCard();
            }
            i = rng.nextInt(52);
        }
        return new Card('j', 0, 53);
    }
}
