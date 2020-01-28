package com.e.localpoker;

class Card {
    private char suit;
    private int value;
    private int index;
    private boolean dealt;

    public Card(char suit, int value, int index) {
        this.suit = suit;
        this.value = value;
        this.index = index;
        dealt = false;
    }

    public boolean checkDealtStatus() {
        return dealt;
    }

    public Card dealCard() {
        dealt = true;
        return this;
    }
}
