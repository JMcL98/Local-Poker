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
        Card[] hand;
        hand = deckManager.dealHand();
        Log.d("Jordan", Integer.toString(hand[0].getValue()));
    }
}
