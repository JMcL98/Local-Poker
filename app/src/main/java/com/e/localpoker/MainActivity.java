package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    DeckManager deckManager;
    Player testPlayer;
    Player[] players;
    String serviceName = "LocalPoker";
    hThread hthread1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deckManager = new DeckManager();
        hthread1 = new hThread(this);
        hthread1.start();
    }

    public void onHostClick(View v) throws IOException {
        Button clientButton = (Button) findViewById(R.id.button2);
        clientButton.setVisibility(View.INVISIBLE);
        EditText e1 = (EditText) findViewById(R.id.editText);
        String dName = e1.getText().toString();
        HostGameManager hostManager = new HostGameManager(deckManager, this, dName, this);
        Bundle hostBundle = new Bundle();
        hostBundle.putParcelable("hostmanager", hostManager);

        Message hostMessage = Message.obtain();
        hostMessage.setData(hostBundle);
        hostMessage.what = 1;
        hthread1.getHandler().sendMessage(hostMessage);
    }

    public void onClientClick(View v) {
        Button hostButton = (Button) findViewById(R.id.button);
        hostButton.setVisibility(View.INVISIBLE);
        EditText e1 = (EditText) findViewById(R.id.editText);
        String dName = e1.getText().toString();
        Bundle clientBundle = new Bundle();
        clientBundle.putString("devicename", dName);
        ClientGameManager clientManager = new ClientGameManager(this, dName, this);
        clientBundle.putParcelable("clientmanager", clientManager);
        Message clientMessage = Message.obtain();
        clientMessage.what = 2;
        clientMessage.setData(clientBundle);
        hthread1.getHandler().sendMessage(clientMessage);
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
        testPlayer.addChips(strength);
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

    public void onClickStart(View v) {

    }

    public void makeStartVisible() {
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setVisibility(View.VISIBLE);
    }

    public void onDestroy() {
        hthread1.quit();
        super.onDestroy();
    }
}
