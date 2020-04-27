package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    DeckManager deckManager;
    Player testPlayer;
    Player[] players;
    String serviceName = "LocalPoker";
    hThread hthread1;
    Handler hThread1Handler;
    EditText e1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        e1 = (EditText) findViewById(R.id.editText);
        deckManager = new DeckManager();
        hthread1 = new hThread(this);
        hthread1.start();
    }

    public void onHostClick(View v) throws IOException {

        String dName = e1.getText().toString();
        if (dName.equals("Name")) {
            Toast toast = Toast.makeText(this, "Please input a name", Toast.LENGTH_SHORT);
            toast.show();
        } else {


            HostGameManager hostManager = new HostGameManager(deckManager, this, dName, this);
            Bundle hostBundle = new Bundle();
            hostBundle.putParcelable("hostmanager", hostManager);

            Message hostMessage = Message.obtain();
            hostMessage.setData(hostBundle);
            hostMessage.what = 1;
            hThread1Handler = hthread1.getHandler();
            hThread1Handler.sendMessage(hostMessage);
            prepGame(1);
        }
    }

    public void onClientClick(View v) {
        String dName = e1.getText().toString();
        if (dName.equals("Name")) {
            Toast toast = Toast.makeText(this, "Please input a name", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Bundle clientBundle = new Bundle();
            clientBundle.putString("devicename", dName);
            ClientGameManager clientManager = new ClientGameManager(this, dName);
            clientBundle.putParcelable("clientmanager", clientManager);
            Message clientMessage = Message.obtain();
            clientMessage.what = 2;
            clientMessage.setData(clientBundle);
            hThread1Handler = hthread1.getHandler();
            hThread1Handler.sendMessage(clientMessage);
            prepGame(2);
        }
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

    public void prepGame(int type) {
        if (type == 1) {
            Button hostButton = (Button) findViewById(R.id.button);
            Button clientButton = (Button) findViewById(R.id.button2);
            Button startButton = (Button) findViewById(R.id.startButton);
            hostButton.setVisibility(View.INVISIBLE);
            clientButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            Button hostButton = (Button) findViewById(R.id.button);
            Button clientButton = (Button) findViewById(R.id.button2);
            TextView pleaseWait = (TextView) findViewById(R.id.textView);
            hostButton.setVisibility(View.INVISIBLE);
            clientButton.setVisibility(View.INVISIBLE);
            pleaseWait.setVisibility(View.VISIBLE);
        }
    }


    public void onDestroy() {
        hthread1.quit();
        super.onDestroy();
    }
}
