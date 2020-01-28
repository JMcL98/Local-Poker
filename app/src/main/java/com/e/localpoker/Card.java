package com.e.localpoker;

class Card {
    private char suit;
    private int value;
    public int index;
    private boolean dealt;

    Card(char suit, int value, int index) {
        this.suit = suit;
        this.value = value;
        this.index = index;
        dealt = false;
    }

    boolean checkDealtStatus() {
        return dealt;
    }

    Card dealCard() {
        dealt = true;
        return this;
    }

    public void reset() {
        dealt = false;
    }

    String getCardValue() {
        return suit + " " + value;
    }

}
