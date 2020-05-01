package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int MAIN_TO_GAME_REQUEST_CODE = 1;

    DeckManager deckManager;
    String serviceName = "LocalPoker";
    hThread hthread1;
    EditText e1;
    HostGameManager hgm;
    ClientGameManager cgm;
    LinearLayout playerList;
    LinearLayout playerList2;
    int numberOfPlayersDisplayed;
    private boolean ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberOfPlayersDisplayed = 0;
        this.ready = false;
        e1 = (EditText) findViewById(R.id.editText);
        playerList = (LinearLayout) findViewById(R.id.listOfPlayers);
        playerList2 = (LinearLayout) findViewById(R.id.listOfPlayers2);
        hthread1 = new hThread(this, new Handler(), this);
        hthread1.start();
    }

    public void onHostClick(View v) {
        if (!ready) {
            String dName = e1.getText().toString();
            acceptedPlayer(dName);
            if (dName.equals("Name")) {
                Toast toast = Toast.makeText(this, "Please input a name", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                hgm = new HostGameManager(deckManager, this, dName);
                Bundle hostBundle = new Bundle();
                hostBundle.putParcelable("hostmanager", hgm);
                Message hostMessage = Message.obtain();
                hostMessage.setData(hostBundle);
                hostMessage.what = 1;
                hthread1.getHandler().sendMessage(hostMessage);
                prepGame(1);
            }
        } else {
            Bundle hostBundle = new Bundle();
            hostBundle.putParcelable("manager", hgm);
            hostBundle.putBoolean("type", true);
            hgm.hostObj.acceptingPlayers = false;
            gameLaunch(hostBundle);
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
            cgm = new ClientGameManager(this, dName);
            clientBundle.putParcelable("clientmanager", cgm);
            Message clientMessage = Message.obtain();
            clientMessage.what = 2;
            clientMessage.setData(clientBundle);
            hthread1.getHandler().sendMessage(clientMessage);
            prepGame(2);
        }
    }


    public void clientStart() {
        Bundle clientBundle = new Bundle();
        clientBundle.putParcelable("manager", cgm);
        clientBundle.putBoolean("type", false);
        gameLaunch(clientBundle);
    }

    private void gameLaunch(Bundle bundle) {
        Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
        gameIntent.putExtras(bundle);
        startActivityForResult(gameIntent, MAIN_TO_GAME_REQUEST_CODE);
    }

    public void prepGame(int type) {
        Button hostButton = (Button) findViewById(R.id.button);
        Button clientButton = (Button) findViewById(R.id.button2);
        TextView pleaseWait = (TextView) findViewById(R.id.textView);
        if (type == 1) {
            ready = true;
            hostButton.setText("Start");
            clientButton.setVisibility(View.INVISIBLE);
        } else if (type == 2) {
            hostButton.setVisibility(View.INVISIBLE);
            clientButton.setVisibility(View.INVISIBLE);
            pleaseWait.setVisibility(View.VISIBLE);
        }
    }

    void acceptedPlayer(String name) {
        TextView tv = new TextView(this);
        tv.setText(name);
        tv.setTextSize(20);
        tv.setTextColor(Color.BLACK);
        if (numberOfPlayersDisplayed < 5) {
            playerList.addView(tv);
        } else {
            playerList2.addView(tv);
        }
        numberOfPlayersDisplayed++;
    }


    public void onDestroy() {
        hthread1.quit();
        super.onDestroy();
    }
}
