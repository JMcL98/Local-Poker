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
                                    int playerMessageIndex;
                                    int myIndex;
                                    switch (msgType) {
                                        case (1) :

                                        case (2) :
                                            // update user values
                                        case (3) :
                                            // post amounts on UI
                                            final int callAmount = Integer.parseInt(cgm.clientObj.clientInput.readUTF());
                                            uiHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (callAmount == cgm.players[cgm.myPlayerIndex].chipsInPlay) {
                                                        activity.callButton.setText("Check");
                                                    }
                                                }
                                            });
                                            cgm.reply(activity.getBufferedAction());
                                            uiHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (callAmount == cgm.players[cgm.myPlayerIndex].chipsInPlay) {
                                                        activity.callButton.setText("Call");
                                                    }
                                                }
                                            });
                                        case (4) :
                                            cgm.addCard(Integer.parseInt(cgm.clientObj.clientInput.readUTF()));
                                        case (5) :
                                            cgm.players[cgm.myPlayerIndex].resetHand();
                                        case (6) :
                                            activity.onDestroy();
                                        case (9) :
                                            String playersMessage = cgm.clientObj.clientInput.readUTF();
                                            myIndex = Integer.parseInt(playersMessage.substring(0, 0));
                                            String numPlayersS = playersMessage.substring(1);
                                            int numPlayers = Integer.parseInt(numPlayersS);
                                            cgm.initialisePlayers(numPlayers, myIndex);
                                        case (10) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (11) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (12) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (13) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (14) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (15) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (16) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (17) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (18) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
                                        case (19) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientObj.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientObj.clientInput.readUTF());
                                            }
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

    private void updatePlayerInfo(int index, String message) {
        char type = message.charAt(0);
        switch (type) {
            case ('c') :
                cgm.players[index].chipsInPlay = (cgm.callAmount - cgm.players[index].chipsInPlay);
                break;
            case ('r') :
                int raiseAmount = Integer.parseInt(message.substring(1));
                cgm.players[index].chipsInPlay = (raiseAmount);
                cgm.callAmount = raiseAmount;
                break;
            case ('f') :
                cgm.players[index].fold();
                break;
            case ('e') :
                cgm.players[index].eliminated = true;
                break;
        }
        activity.updateInfo();
    }


    Handler getHandler() {
        return handler;
    }


}
