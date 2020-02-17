package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    protected DeckManager deckManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deckManager = new DeckManager();
    }

    public void dealButton(View v) {

        Card[] hand = new Card[7];
        for (int i = 0; i < 7; i++) {
            hand[i] = deckManager.dealCard();
            Log.d("Jordan", hand[i].getSuit() + "" + hand[i].getValue());
        }
        int strength = HandStrength.calculateStrength(hand);
        Log.d("Jordan", "Strength of hand: " + strength);

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
