package com.e.localpoker;

class Card {
    private final char suit;
    private final int value;
    private final int index;
    private boolean dealt;

    Card(char suit, int value, int index) {
        this.suit = suit;
        this.value = value;
        this.index = index;
        dealt = false;
    }

    boolean checkDealtStatus() { // Used for the HostDeckManager to not deal cards that have already been dealt after picking a random card
        return dealt;
    }

    Card dealCard() {
        dealt = true;
        return this;
    }

    void reset() {
        dealt = false;
    }

    int getValue() {
        return value;
    }

    char getSuit() {
        return suit;
    }

    int getIndex() { // Used to deal specific card on client devices, and the get the correct card drawable
        return index;
    }

}
