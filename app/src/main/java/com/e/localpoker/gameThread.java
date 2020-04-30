package com.e.localpoker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

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
                        assert hgm != null;
                        hgm.players[0].setGameActivity(activity);
                        int i = 1;
                        while (i > 0) {
                            int startingPlayer = hgm.blinds();
                            hgm.initialDeal();
                            int j = startingPlayer;
                            while (i < 5) {
                                if (j < hgm.numPlayers) {
                                    if (!hgm.players[j].folded) {
                                        hgm.receiveCommand(hgm.players[j].requestMove(hgm.callAmount), j);
                                    }
                                    j++;
                                } else {
                                    j = 0;
                                }

                                if (hgm.playersInPlay == hgm.playersCalled) {
                                    i++;
                                    hgm.advanceStage();
                                    j = startingPlayer;
                                }
                            }
                            if (hgm.playersLeft == 1) {
                                i = 0;
                            } else {
                                i = 1;
                            }

                        }
                        Player winningPlayer = null;
                        for (Player player : hgm.players) {
                            if (!player.eliminated) {
                                winningPlayer = player;
                                Log.d("Jordan", "Game Finished");
                                Log.d("Jordan", "Winning player: " + winningPlayer.getPlayerName());
                                break;
                            }
                        }
                        hgm.endGame(winningPlayer);


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
                                        case (2) :
                                            // update user values
                                        case (3) :
                                            // post amounts on UI
                                            cgm.reply(activity.getBufferedAction());
                                        case (4) :
                                            cgm.addCard(Integer.parseInt(cgm.clientObj.clientInput.readUTF()));
                                        case (5) :
                                            cgm.myPlayer.resetHand();
                                        case (6) :
                                            activity.onDestroy();
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
