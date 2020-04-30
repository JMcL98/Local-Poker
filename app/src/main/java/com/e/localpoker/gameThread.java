package com.e.localpoker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class gameThread extends HandlerThread {
    HostGameManager hgm;
    ClientGameManager cgm;
    GameActivity activity;
    Handler uiHandler;

    private Handler handler;
    public gameThread(GameActivity activity, Handler handler) {
        super("gameThread");
        this.activity = activity;
        this.uiHandler = handler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case (1) :
                        Bundle receivedHostBundle = message.getData();
                        hgm = receivedHostBundle.getParcelable("manager");
                        hgm.players[0].setGameActivity(activity);
                        int i = 1;
                        while (i > 0) {
                            assert hgm != null;
                            int startingPlayer = hgm.blinds();
                            hgm.initialDeal();
                            while (i == 1) {
                                for (int j = startingPlayer; j < hgm.numPlayers; j++) {
                                    hgm.receiveCommand(hgm.players[j].requestMove(hgm.callAmount), j);
                                }

                                if (hgm.playersInPlay == hgm.playersCalled) {
                                    i++;
                                    hgm.advanceStage();
                                }
                            }
                            while (i == 2) {
                                // second round
                                // if all players have called
                                i++;
                                hgm.advanceStage();
                            }
                            while (i == 3) {
                                // third round
                                // if all players have called
                                i++;
                                hgm.advanceStage();
                            }
                            while (i == 4) {
                                // final round
                                // if all players called
                                i++;
                                hgm.advanceStage();
                            }




                            for (Player player : hgm.players) {
                                if (player.getChips() < 1) {
                                    hgm.eliminatePlayer(player.getPlayerID());
                                }
                            }
                            if (hgm.playersLeft == 1) {
                                i = 0;
                            } else {
                                hgm.resetRound();
                                i = 1;
                            }

                        }

                        for (Player player : hgm.players) {
                            if (!player.eliminated) {
                                Log.d("Jordan", "Game Finished");
                                Log.d("Jordan", "Winning player: " + player.getPlayerName());
                            }
                        }


                        break;
                    case (2) :
                        Bundle receivedClientBundle = message.getData();
                        cgm = receivedClientBundle.getParcelable("manager");
                        int j = 1;
                        while (j > 0) {
                            assert cgm != null;
                            if (cgm.clientObj.clientInput != null) {
                                try {
                                    byte msgType = cgm.clientObj.clientInput.readByte();
                                    switch (msgType) {
                                        case (3) :
                                            cgm.reply(activity.getBufferedAction());
                                        case (4) :
                                            cgm.addCard(Integer.parseInt(cgm.clientObj.clientInput.readUTF()));
                                        case (5) :
                                            cgm.myPlayer.resetHand();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                }

            }
        };
    }

    Handler getHandler() {
        return handler;
    }
}
