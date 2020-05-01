package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    HostGameManager hgm;
    ClientGameManager cgm;
    boolean host; // true = host, false = client
    int myIndex;
    gameThread gameThread;
    EditText raiseAmount;
    Button callButton, raiseButton, foldButton;
    TextView currentBet, chipsInPlay, totalChips;
    private String bufferedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        callButton = (Button) findViewById(R.id.callButton);
        raiseButton = (Button) findViewById(R.id.raiseButton);
        foldButton = (Button) findViewById(R.id.foldButton);
        currentBet = (TextView) findViewById(R.id.currentBet);
        chipsInPlay = (TextView) findViewById(R.id.chipsInPlay);
        totalChips = (TextView) findViewById(R.id.myChips);
        raiseAmount = (EditText) findViewById(R.id.raiseAmount);
        bufferedAction = "";
        gameThread = new gameThread(this, new Handler());
        gameThread.start();
        while (true) {
            if (gameThread.getHandler() != null) {
                Bundle receivedBundle = getIntent().getExtras();
                host = receivedBundle.getBoolean("type");
                Message gameMessage = Message.obtain(gameThread.getHandler());
                gameMessage.setData(receivedBundle);
                if (host) {
                    hgm = receivedBundle.getParcelable("manager");
                    gameMessage.what = 1;
                    setMyIndex(0);
                } else {
                    cgm = receivedBundle.getParcelable("manager");
                    cgm.setGameActivity(this);
                    gameMessage.what = 2;
                }
                gameMessage.sendToTarget();
                break;
            }
        }
    }

    void setMyIndex(int i) {
        this.myIndex = i;
    }

    public void onCall(View v) {
        bufferedAction = "c";
    }

    public void onRaise(View v) {
        bufferedAction = "r" + raiseAmount.getText().toString();
    }

    public void onFold(View v) {
        bufferedAction = "f";
    }

    String getBufferedAction(int minRaise) {
        callButton.setVisibility(View.VISIBLE);
        raiseButton.setVisibility(View.VISIBLE);
        foldButton.setVisibility(View.VISIBLE);
        while (true) {
            if (!bufferedAction.equals("")) {
                if (bufferedAction.charAt(0) == 'r') {
                    if (Integer.parseInt(bufferedAction.substring(1)) < minRaise) {
                        Toast toast = Toast.makeText(this, "Raise Amount too Low", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        String temp = bufferedAction;
                        bufferedAction = "";
                        callButton.setVisibility(View.INVISIBLE);
                        raiseButton.setVisibility(View.INVISIBLE);
                        foldButton.setVisibility(View.INVISIBLE);
                        return temp;
                    }
                } else {
                    String temp = bufferedAction;
                    bufferedAction = "";
                    callButton.setVisibility(View.INVISIBLE);
                    raiseButton.setVisibility(View.INVISIBLE);
                    foldButton.setVisibility(View.INVISIBLE);
                    return temp;
                }
            }
        }
    }

    void updateInfo(boolean host) { // so it knows which manager to take information from
        if (host) {
            totalChips.setText("Total Chips: " + hgm.players[myIndex].getChips());
            chipsInPlay.setText("Chips in play: " + hgm.players[myIndex].chipsInPlay);
            currentBet.setText("Current Bet: " + hgm.callAmount);
        } else {
            totalChips.setText("Total Chips: " + cgm.players[myIndex].getChips());
            chipsInPlay.setText("Chips in play: " + cgm.players[myIndex].chipsInPlay);
            currentBet.setText("Current Bet: " + cgm.callAmount);
        }
    }

    @Override
    public void onDestroy() {
        gameThread.quit();
        super.onDestroy();
    }
}
