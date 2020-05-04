package com.e.localpoker;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    TextView currentBet, chipsInPlay, totalChips, pot;
    TextView[] players;
    LinearLayout hand, comCards, commands;
    private String bufferedAction;
    private String[] names;
    ImageView hand0, hand1, card0, card1, card2, card3, card4;
    int numCardsInPlay;

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
        players = new TextView[3];
        players[0] = (TextView) findViewById(R.id.player0);
        players[1] = (TextView) findViewById(R.id.player1);
        players[2] = (TextView) findViewById(R.id.player2);
        pot = (TextView) findViewById(R.id.pot);
        raiseAmount = (EditText) findViewById(R.id.raiseAmount);
        hand = (LinearLayout) findViewById(R.id.hand);
        hand0 = (ImageView) findViewById(R.id.hand0);
        hand1 = (ImageView) findViewById(R.id.hand1);
        card0 = (ImageView) findViewById(R.id.card0);
        card1 = (ImageView) findViewById(R.id.card1);
        card2 = (ImageView) findViewById(R.id.card2);
        card3 = (ImageView) findViewById(R.id.card3);
        card4 = (ImageView) findViewById(R.id.card4);
        comCards = (LinearLayout) findViewById(R.id.communityCards);
        commands = (LinearLayout) findViewById(R.id.linearLayout2);
        numCardsInPlay = 0;
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
                    gameMessage.what = 1;
                    setMyIndex(0);
                } else {
                    gameMessage.what = 2;
                }
                gameMessage.sendToTarget();
                break;
            }
        }
    }

    void setPlayerNames(String[] names, boolean host) {
        this.names = names;
        if (host) {
            for (int i = 0; i < hgm.numPlayers; i++) {
                players[i].setText(names[i]);
            }
        } else {
            for (int j = 0; j < cgm.players.length; j++) {
                players[j].setText(names[j]);
            }
        }
    }

    void updatePlayerInfo(Player[] players) {
        for (int i = 0; i < hgm.numPlayers; i++) {
            this.players[i].setText(players[i].getPlayerName() + "\n" + "Total Chips: " + players[i].getChips() + "\n" + "Chips in Play: " + players[i].chipsInPlay);
        }
    }

    void setMyIndex(int i) {
        this.myIndex = i;
    }

    void setHostManager(HostGameManager h) {
        this.hgm = h;
    }

    void setClientManager(ClientGameManager c) {
        this.cgm = c;
        cgm.setGameActivity(this);
    }

    void resetCards() {
        hand0.setImageResource(R.drawable.bg);
        hand1.setImageResource(R.drawable.bg);
        card0.setImageResource(R.drawable.bg);
        card1.setImageResource(R.drawable.bg);
        card2.setImageResource(R.drawable.bg);
        card3.setImageResource(R.drawable.bg);
        card4.setImageResource(R.drawable.bg);
        numCardsInPlay = 0;
    }

    void addCard(int index, int cardNum) {
        switch (cardNum) {
            case (0) :
                getCardImage(index, hand0);
                break;
            case (1) :
                getCardImage(index, hand1);
                break;
            case (2) :
                getCardImage(index, card0);
                break;
            case (3) :
                getCardImage(index, card1);
                break;
            case (4) :
                getCardImage(index, card2);
                break;
            case (5) :
                getCardImage(index, card3);
                break;
            case (6) :
                getCardImage(index, card4);
                break;
        }
        numCardsInPlay++;
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
        if (!bufferedAction.equals("")) {
            if (bufferedAction.charAt(0) == 'r') {
                if (Integer.parseInt(bufferedAction.substring(1)) < minRaise) {
                    Toast toast = Toast.makeText(this, "Raise Amount too Low", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String temp = bufferedAction;
                    bufferedAction = "";
                    return temp;
                }
            } else {
                String temp = bufferedAction;
                bufferedAction = "";
                return temp;
            }
        }
        return "";
    }

    void updateInfo(boolean host) { // so it knows which manager to take information from
        if (host) {
            totalChips.setText("Total Chips: " + hgm.players[myIndex].getChips());
            chipsInPlay.setText("Chips in play: " + hgm.players[myIndex].chipsInPlay);
            currentBet.setText("Current Bet: " + hgm.callAmount);
            if (hgm.players[myIndex].folded) {
                currentBet.setText("Folded");
            }
            pot.setText("Pot: " + hgm.getPot());
        } else {
            totalChips.setText("Total Chips: " + cgm.players[myIndex].getChips());
            chipsInPlay.setText("Chips in play: " + cgm.players[myIndex].chipsInPlay);
            currentBet.setText("Current Bet: " + cgm.callAmount);
            if (cgm.players[myIndex].folded) {
                currentBet.setText("Folded");
            }
            pot.setText("Pot: " + cgm.totalPot);
        }
    }

    void endGame(String winningPlayer) {
        Bundle finishBundle = new Bundle();
        finishBundle.putString("winner", winningPlayer);
        Intent result = new Intent();
        result.putExtras(finishBundle);
        gameThread.quit();
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onDestroy() {
        gameThread.quit();
        super.onDestroy();
    }

    void getCardImage (int index, ImageView img) {
        //ImageView img = new ImageView(this);
        switch (index) {
            case (0) :
                img.setImageResource(R.drawable.c1);
                break;
            case (1) :
                img.setImageResource(R.drawable.c2);
                break;
            case (2) :
                img.setImageResource(R.drawable.c3);
                break;
            case (3) :
                img.setImageResource(R.drawable.c4);
                break;
            case (4) :
                img.setImageResource(R.drawable.c5);
                break;
            case (5) :
                img.setImageResource(R.drawable.c6);
                break;
            case (6) :
                img.setImageResource(R.drawable.c7);
                break;
            case (7) :
                img.setImageResource(R.drawable.c8);
                break;
            case (8) :
                img.setImageResource(R.drawable.c9);
                break;
            case (9) :
                img.setImageResource(R.drawable.c10);
                break;
            case (10) :
                img.setImageResource(R.drawable.c11);
                break;
            case (11) :
                img.setImageResource(R.drawable.c12);
                break;
            case (12) :
                img.setImageResource(R.drawable.c13);
                break;
            case (13) :
                img.setImageResource(R.drawable.c14);
                break;
            case (14) :
                img.setImageResource(R.drawable.c15);
                break;
            case (15) :
                img.setImageResource(R.drawable.c16);
                break;
            case (16) :
                img.setImageResource(R.drawable.c17);
                break;
            case (17) :
                img.setImageResource(R.drawable.c18);
                break;
            case (18) :
                img.setImageResource(R.drawable.c19);
                break;
            case (19) :
                img.setImageResource(R.drawable.c20);
                break;
            case (20) :
                img.setImageResource(R.drawable.c21);
                break;
            case (21) :
                img.setImageResource(R.drawable.c22);
                break;
            case (22) :
                img.setImageResource(R.drawable.c23);
                break;
            case (23) :
                img.setImageResource(R.drawable.c24);
                break;
            case (24) :
                img.setImageResource(R.drawable.c25);
                break;
            case (25) :
                img.setImageResource(R.drawable.c26);
                break;
            case (26) :
                img.setImageResource(R.drawable.c27);
                break;
            case (27) :
                img.setImageResource(R.drawable.c28);
                break;
            case (28) :
                img.setImageResource(R.drawable.c29);
                break;
            case (29) :
                img.setImageResource(R.drawable.c30);
                break;
            case (30) :
                img.setImageResource(R.drawable.c31);
                break;
            case (31) :
                img.setImageResource(R.drawable.c32);
                break;
            case (32) :
                img.setImageResource(R.drawable.c33);
                break;
            case (33) :
                img.setImageResource(R.drawable.c34);
                break;
            case (34) :
                img.setImageResource(R.drawable.c35);
                break;
            case (35) :
                img.setImageResource(R.drawable.c36);
                break;
            case (36) :
                img.setImageResource(R.drawable.c37);
                break;
            case (37) :
                img.setImageResource(R.drawable.c38);
                break;
            case (38) :
                img.setImageResource(R.drawable.c39);
                break;
            case (39) :
                img.setImageResource(R.drawable.c40);
                break;
            case (40) :
                img.setImageResource(R.drawable.c41);
                break;
            case (41) :
                img.setImageResource(R.drawable.c42);
                break;
            case (42) :
                img.setImageResource(R.drawable.c43);
                break;
            case (43) :
                img.setImageResource(R.drawable.c44);
                break;
            case (44) :
                img.setImageResource(R.drawable.c45);
                break;
            case (45) :
                img.setImageResource(R.drawable.c46);
                break;
            case (46) :
                img.setImageResource(R.drawable.c47);
                break;
            case (47) :
                img.setImageResource(R.drawable.c48);
                break;
            case (48) :
                img.setImageResource(R.drawable.c49);
                break;
            case (49) :
                img.setImageResource(R.drawable.c50);
                break;
            case (50) :
                img.setImageResource(R.drawable.c51);
                break;
            case (51) :
                img.setImageResource(R.drawable.c52);
                break;
        }
        //return img;
    }
}
