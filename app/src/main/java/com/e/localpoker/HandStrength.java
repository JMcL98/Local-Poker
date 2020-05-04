package com.e.localpoker;

final class HandStrength {

    static int calculateStrength(Card[] hand) {
        int j;
        int i = checkStraight(hand);
        if (i >= 100000) {
            return i;
        } else if ((j = checkFourOfAKind(hand)) >= 75000) {
            return j;
        } else if ((j = checkFullHouse(hand)) >= 60000) {
            return j;
        } else if ((j = checkFlush(hand)) >= 50000) {
            return j;
        } else if (i >= 10000) {
            return i;
        } else if ((j = checkThreeOfAKind(hand)) >= 500) {
            return j;
        } else if ((j = checkPair(hand)) >= 100) {
            return j;
        }
        Card[] orderedHand = orderHand(hand);
        return orderedHand[0].getValue();
    }

    private static int checkFlush(Card[] hand) {
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
            return 50000;
        } else {
            return 0;
        }
    }

    private static int checkStraight(Card[] hand) {
        Card[] orderedHand = orderHand(hand);
        for (int i = 0; i < 3; i++) {
            int j = nextStraightNumberCheck(1, orderedHand, i, 1);
            if (j > 0) {
                return j;
            }
        }
        return 0;
    }

    private static Card[] orderHand(Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            for (int j = 0; j < hand.length - 1 - i; j++) {
                if (hand[j].getValue() < hand[j+1].getValue()) {
                    Card card = hand[j];
                    hand[j] = hand[j+1];
                    hand[j+1] = card;
                }
            }
        }
        return hand;
    }

    private static int nextStraightNumberCheck(int numFound, Card[] hand, int originalIndex, int flushCheck) {
        for (Card card : hand) {
            if (card.getValue() == hand[originalIndex].getValue() - numFound) {
                if (numFound == 4) {
                    if ((card.getSuit() == hand[originalIndex].getSuit()) && (flushCheck == 4)) {
                        return hand[originalIndex].getValue() + 100000;
                    } else {
                        if (flushCheck == 4) {
                            for (Card card2 : hand) {
                                if ((card2.getValue() == card.getValue()) && (card2.getSuit() == hand[originalIndex].getSuit())) {
                                    return hand[originalIndex].getValue() + 100000;
                                }
                            }
                        }
                        return hand[originalIndex].getValue() + 10000;
                    }
                } else {
                    if (card.getSuit() == hand[originalIndex].getSuit()) {
                        return nextStraightNumberCheck(numFound + 1, hand, originalIndex, flushCheck + 1);
                    } else {
                        if (flushCheck > 0) {
                            for (Card card2 : hand) {
                                if ((card2.getValue() == card.getValue()) && (card2.getSuit() == hand[originalIndex].getSuit())) {
                                    return nextStraightNumberCheck(numFound + 1, hand, originalIndex, flushCheck + 1);
                                }
                            }
                        }
                        return nextStraightNumberCheck(numFound + 1, hand, originalIndex, 0);
                    }
                }
            } else if ((hand[originalIndex].getValue() == 5) && (card.getValue() == 13) && (numFound == 4)) {
                return hand[originalIndex].getValue();
            }
        }
        return 0;
    }

    private static int checkFourOfAKind(Card[] hand) {
        int i = 0;
        int numMatches = 1;
        while (i < 4) {
            int v = hand[i].getValue();
            for (int j = i + 1; j < 7; j++) {
                if (hand[j].getValue() == v) {
                    numMatches++;
                }
            }
            if (numMatches == 4) {
                return 75000 + v;
            }
            numMatches = 1;
            i++;
        }

        return 0;
    }

    private static int checkFullHouse(Card[] hand) {
        int i;
        if ((i = checkThreeOfAKind(hand)) > 0) {
            if (checkPair(hand) >= 200) {
                return 60000 + i;
            }
        }
        return 0;
    }

    private static int checkThreeOfAKind(Card[] hand) {
        int v;
        Card[] orderedHand = orderHand(hand);
        for (int i = 0; i < (orderedHand.length - 2); i++) {
            v = orderedHand[i].getValue();
            if (orderedHand[i + 1].getValue() == v) {
                if (orderedHand[i + 2].getValue() == v) {
                    return 500 + v;
                }
            }
        }
        return 0;
    }

    private static int checkPair(Card[] hand) {
        int numberOfPairs = 0;
        int highPair = 0;
        Card[] orderedHand = orderHand(hand);
        for (int i = 0; i < orderedHand.length - 1; i++) {
            if (orderedHand[i].getValue() == orderedHand[i + 1].getValue()) {
                if (highPair == 0) { highPair = orderedHand[i].getValue(); }
                numberOfPairs++;
                if (i < 5 && (orderedHand[i].getValue() == orderedHand[i + 2].getValue())) {
                    i++;
                    if (i < 4 && (orderedHand[i].getValue() == orderedHand[i + 3].getValue())) {
                        i++;
                    }
                }
            }
            if (numberOfPairs == 2) {
                return 200 + highPair;
            }
        }
        if (numberOfPairs == 1) {
            return 100 + highPair;
        }
        return 0;
    }


}
