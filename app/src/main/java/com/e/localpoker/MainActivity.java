package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    DeckManager deckManager;
    Player testPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deckManager = new DeckManager();
        testPlayer = new Player();
        testPlayer.addChips(100);
    }

    public void dealButton(View v) {

        //Card[] hand = new Card[7];
        /*hand[6] = deckManager.dealCard(1);
        hand[5] = deckManager.dealCard(14);
        hand[4] = deckManager.dealCard(2);
        hand[3] = deckManager.dealCard(3);
        hand[2] = deckManager.dealCard(4);
        hand[1] = deckManager.dealCard(5);
        hand[0] = deckManager.dealCard(8);*/
        for (int i = 0; i < 7; i++) {
            testPlayer.addCard(deckManager.dealCard(60));

            //hand[i] = deckManager.dealCard(60);
            Log.d("Jordan", testPlayer.getHand()[i].getSuit() + "" + testPlayer.getHand()[i].getValue());
        }
        int strength = HandStrength.calculateStrength(testPlayer.getHand());
        Log.d("Jordan", "Strength of hand: " + strength);
        testPlayer.resetHand();
        deckManager.resetDeck();

        /*if (deckManager.deckInitialised) {
            for (int i = 0; i < 53; i++) {
                Card card = deckManager.dealCard();
                if (card.getValue() > 0) {
                    Log.d("Jordan", i + ": " + card.getSuit() + card.getValue() + "\n");
                } else {
                    Log.d("Jordan", i + ": Last card dealt");
                }
            }
            deckManager.resetDeck();
        }*/
    }
}
