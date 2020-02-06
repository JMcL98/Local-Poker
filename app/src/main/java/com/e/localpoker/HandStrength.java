package com.e.localpoker;

import android.widget.Switch;

class HandStrength {

    int calculateStrength(Card[] hand) {
        int handStrength = 0;
        if (checkFlush(hand) > 1) {
            handStrength += 50;
        }


        return handStrength;
    }

    int checkFlush(Card[] hand) {
        boolean flush = false;
        int numHearts = 0;
        int numSpades = 0;
        int numDiamonds = 0;
        int numClubs = 0;
        for (Card card : hand) {
            char suit = card.getSuit();
            switch (suit) {
                case 'h':
                    numHearts++;
                    break;
                case 's':
                    numSpades++;
                    break;
                case 'd':
                    numDiamonds++;
                    break;
                case 'c':
                    numClubs++;
            }
        }
        if (numHearts > 4 || numSpades > 4 || numDiamonds > 4 || numClubs > 4) {
            flush = true;
        }

        if (flush) {
            return 1;
        } else {
            return 0;
        }
    }

    int checkStraight(Card[] hand) {

        return 0;
    }

    int checkFourOfAKind(Card[] hand) {

        return 0;
    }

    int checkFullHouse(Card[] hand) {

        return 0;
    }

    int checkThreeOfAKind(Card[] hand) {

        return 0;
    }

    int checkTwoPair(Card[] hand) {

        return 0;
    }

    int checkPair(Card[] hand) {

        return 0;
    }
}
