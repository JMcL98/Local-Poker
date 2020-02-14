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
        Card[] hand = new Card[2];
        hand[0] = deckManager.dealCard();
        hand[1] = deckManager.dealCard();

        Log.d("Jordan", hand[0].getSuit() + (hand[0].getValue()) + " " + hand[1].getSuit() + (hand[1].getValue()));
    }
}
