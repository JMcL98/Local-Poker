package com.e.localpoker;

class Card {
    private char suit;
    private int value;
    public int index;
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

    public void reset() {
        dealt = false;
    }

    public String getCardValue() {
        return suit + " " + value;
    }

}
