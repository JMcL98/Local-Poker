package com.e.localpoker;

import android.util.Log;
import android.widget.Switch;

class HandStrength {

    static int calculateStrength(Card[] hand) {
        int handStrength = 0;
        if (checkFlush(hand) > 1) {
            handStrength += 50;
        }

        return handStrength;
    }

    private static int checkFlush(Card[] hand) {
        boolean flush = false;
        int numHearts = 0;
        int numSpades = 0;
        int numDiamonds = 0;
        int numClubs = 0;
        for (int i = 0; i < 7; i++) {
            char suit = hand[i].getSuit();
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

    private static int checkStraight(Card[] hand) {
        // find lowest number, keep checking up until failure
        // when 4 cards left no straight is possible
        boolean lowestAce = false;
        Card lowestCard;
        lowestCard = hand[0];
        for (int i = 1; i < hand.length; i++) {
            if (hand[i].getValue() == 13) {
                lowestAce = true;
                lowestCard = hand[i];
                break;
            }
            if (hand[i].getValue() < lowestCard.getValue()) {
                lowestCard = hand[i];
            }
        }





        return 0;
    }

    private static int checkFourOfAKind(Card[] hand) {

        int i = 0;
        int numMatches = 0;
        while (i < 4) {
            int v = hand[i].getValue();
            for (int j = i + 1; j < 7; j++) {
                if (hand[j].getValue() == v) {
                    numMatches++;
                }
            }
            switch (numMatches) {
                case 3:
                    return 3000 + v;
                case 4:
                    return 4000 + v;
            }
            numMatches = 0;
            i++;
        }

        return 0;
    }

    private static int checkFullHouse(Card[] hand) {
        // check three of a kind
        // check pair

        return 0;
    }

    private static int checkThreeOfAKind(Card[] hand) {

        return 0;
    }

    private static int checkPair(Card[] hand) {
        int numberOfPairs = 0;


        return 0;
    }


}
