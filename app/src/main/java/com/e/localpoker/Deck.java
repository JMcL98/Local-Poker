package com.e.localpoker;

public class DeckManager {

    private Card[] deck = null;
    private boolean deckInitialised = false;

    public DeckManager() {
        deckInitialised = initialiseDeck();

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
}
