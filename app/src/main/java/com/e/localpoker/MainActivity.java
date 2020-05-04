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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int MAIN_TO_GAME_REQUEST_CODE = 1;

    String serviceName = "LocalPoker";
    networkThread hthread1;
    EditText e1;
    HostGameManager hgm;
    ClientGameManager cgm;
    LinearLayout playerList;
    LinearLayout playerList2;
    TextView pleaseWait;
    int numberOfPlayersDisplayed;
    private boolean ready;
    static Player[] players;
    static DataInputStream clientInput;
    static DataOutputStream clientOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberOfPlayersDisplayed = 0;
        this.ready = false;
        e1 = (EditText) findViewById(R.id.editText);
        playerList = (LinearLayout) findViewById(R.id.listOfPlayers);
        playerList2 = (LinearLayout) findViewById(R.id.listOfPlayers2);
        hthread1 = new networkThread(this, new Handler(), this);
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
                hgm = new HostGameManager(this, dName);
                Bundle hostBundle = new Bundle();
                hostBundle.putParcelable("hostmanager", hgm);
                Message hostMessage = Message.obtain();
                hostMessage.setData(hostBundle);
                hostMessage.what = 1;
                hthread1.getHandler().sendMessage(hostMessage);
                prepGame(1);
            }
        } else {
            if (hgm.tempPlayers.length > 1) {
                Bundle hostBundle = new Bundle();
                hostBundle.putBoolean("type", true);
                hgm.hostObj.acceptingPlayers = false;
                gameLaunch(hostBundle);
            } else {
                Toast toast2 = Toast.makeText(this,"Please wait for more player", Toast.LENGTH_SHORT);
                toast2.show();
            }
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
        cgm.nsdClient.stopDiscovery();
        clientInput = cgm.nsdClient.clientInput;
        clientOutput = cgm.nsdClient.clientOutput;
        Bundle clientBundle = new Bundle();
        clientBundle.putBoolean("type", false);
        clientBundle.putString("name", e1.getText().toString());
        gameLaunch(clientBundle);
    }

    private void gameLaunch(Bundle bundle) {
        players = new Player[numberOfPlayersDisplayed];
        for (int i = 0; i < numberOfPlayersDisplayed; i++) {
            players[i] = hgm.tempPlayers[i];
        }
        Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
        gameIntent.putExtras(bundle);
        startActivityForResult(gameIntent, MAIN_TO_GAME_REQUEST_CODE);
    }

    public void prepGame(int type) {
        Button hostButton = (Button) findViewById(R.id.button);
        Button clientButton = (Button) findViewById(R.id.button2);
        pleaseWait = (TextView) findViewById(R.id.textView);
        if (type == 1) {
            ready = true;
            hostButton.setText("Start");
            clientButton.setVisibility(View.INVISIBLE);
            pleaseWait.setText("Joined Players:");
            pleaseWait.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAIN_TO_GAME_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                String winner = bundle.getString("winner");
                pleaseWait.setText("Game Winner: " + winner);
                playerList.setVisibility(View.INVISIBLE);
                playerList2.setVisibility(View.INVISIBLE);
            } else if (resultCode == RESULT_CANCELED) {
                onDestroy();
            }
            if (hgm != null) {
                try {
                    hgm.hostObj.closeService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (cgm != null) {
                try {
                    cgm.nsdClient.closeService();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onDestroy() {
        hthread1.quit();
        super.onDestroy();
    }
}
