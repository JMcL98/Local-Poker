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
                        hgm = new HostGameManager(MainActivity.players);
                        activity.setHostManager(hgm);
                        while (true) {
                            if (hgm.players != null) {
                                break;
                            }
                        }
                        hgm.players[0].setGameActivity(activity);
                        hgm.sendNames();
                        hgm.resetRound();
                        int i = 1;
                        Log.d("Jordan", "Game Started");
                        while (i > 0) {
                            resetUICards();
                            int startingPlayer = hgm.blinds();
                            try {
                                hgm.initialDeal();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int j = startingPlayer;
                            while (i < 5) {
                                updateUIData(true);
                                for (int c = 0; c < hgm.players[0].getHand().length; c++) {
                                    if (hgm.players[0].getHand()[c] != null) {
                                        uiHandler.post(new addCardRunnable(hgm.players[0].getHand()[c].getIndex(), c));
                                    }
                                }
                                if (j < hgm.numPlayers) {
                                    if (!hgm.players[j].folded) {
                                        if (hgm.players[j].allIn) {
                                            hgm.playersCalled++;
                                        } else {
                                            String move;
                                            while (true) {
                                                move = hgm.players[j].requestMove(hgm.callAmount);
                                                if (!move.equals("")) {
                                                    hgm.receiveCommand(move, j);
                                                    updatePlayerInfo(j, move, true);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    j++;
                                } else {
                                    j = 0;
                                }

                                if (hgm.playersInPlay == hgm.playersCalled) {
                                    i++;
                                    resetUICards();
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
                        cgm = new ClientGameManager(MainActivity.clientOutput, MainActivity.clientInput, receivedClientBundle.getString("name"));
                        activity.setClientManager(cgm);
                        int j = 1;
                        while (j > 0) {
                            if (cgm.clientInput != null) {
                                try {
                                    byte msgType = cgm.clientInput.readByte();
                                    int playerMessageIndex;
                                    int myIndex;
                                    switch (msgType) {
                                        case (1) :

                                            break;
                                        case (2) :
                                            // update user values
                                            break;
                                        case (3) :
                                            cgm.callAmount = Integer.parseInt(cgm.clientInput.readUTF());
                                            updateUIData(false);
                                            /*uiHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (cgm.callAmount == cgm.players[cgm.myPlayerIndex].chipsInPlay) {
                                                        activity.callButton.setText("Check");
                                                    }
                                                    activity.raiseAmount.setText(cgm.callAmount);
                                                }
                                            });*/
                                            while (true) {
                                                String move = activity.getBufferedAction(cgm.callAmount);
                                                if (!move.equals("")) {
                                                    cgm.reply(move);
                                                    break;
                                                }
                                            }
                                       /*     uiHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (cgm.callAmount == cgm.players[cgm.myPlayerIndex].chipsInPlay) {
                                                        activity.callButton.setText("Call");
                                                    }
                                                }
                                            });*/
                                            break;
                                        case (4) :
                                            int cardI = Integer.parseInt(cgm.clientInput.readUTF());
                                            cgm.addCard(cardI, cgm.players[cgm.myPlayerIndex].getNumCardsInHand());
                                            for (int c = 0; c < cgm.players[cgm.myPlayerIndex].getHand().length; c++) {
                                                if (cgm.players[cgm.myPlayerIndex].getHand()[c] != null) {
                                                    uiHandler.post(new addCardRunnable(cgm.players[cgm.myPlayerIndex].getHand()[c].getIndex(), c));
                                                }
                                            }
                                            break;
                                        case (5) :
                                            resetUICards();
                                            cgm.players[cgm.myPlayerIndex].resetHand();
                                            // more
                                            break;
                                        case (6) :
                                            String lastPlayer = cgm.clientInput.readUTF();
                                            Log.d("Jordan", "Winning Player: " + lastPlayer);
                                            j = 0;
                                            activity.onDestroy();
                                            break;
                                        case (9) :
                                            String playersMessage = cgm.clientInput.readUTF();
                                            myIndex = Character.getNumericValue(playersMessage.charAt(0));
                                            activity.setMyIndex(myIndex);
                                            cgm.initialisePlayers(Character.getNumericValue(playersMessage.charAt(1)), myIndex);
                                            break;
                                        case (10) :
                                        case (11) :
                                        case (12) :
                                        case (13) :
                                        case (14) :
                                        case (15) :
                                        case (16) :
                                        case (17) :
                                        case (18) :
                                        case (19) :
                                            playerMessageIndex = msgType - 10;
                                            if (cgm.players[playerMessageIndex] == null) {
                                                cgm.addPlayer(cgm.clientInput.readUTF(), playerMessageIndex);
                                            } else {
                                                updatePlayerInfo(playerMessageIndex, cgm.clientInput.readUTF(), false);
                                            }
                                            break;
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

    private class commandRunnable implements Runnable {

        int index;
        commandRunnable(int j) {
            this.index = j;
        }

        @Override
        public void run() {
            while (true) {
                String move = hgm.players[index].requestMove(hgm.callAmount);
                if (!move.equals("")) {
                    hgm.receiveCommand(move, index);
                    break;
                }
            }
        }
    }

    private class addCardRunnable implements Runnable {
        int index;
        int cardNum;
        addCardRunnable(int i, int j) {
            this.index = i;
            this.cardNum = j;
        }
        @Override
        public void run() {
            //if (cgm != null) {
            //    cgm.addCard(this.index, this.cardNum);
           // } else {
                activity.addCard(this.index, this.cardNum);
           // }
        }
    }

    private void resetUICards() {
        uiHandler.post(new resetCards());
    }

    private class resetCards implements Runnable {
        @Override
        public void run() {
            activity.resetCards();
        }
    }

    private void updatePlayerInfo(int index, String message, boolean host) {
        char type = message.charAt(0);
        if (!host) {
            switch (type) {
                case ('c'):
                    cgm.players[index].chipsInPlay = (cgm.callAmount - cgm.players[index].chipsInPlay);
                    break;
                case ('r'):
                    int raiseAmount = Integer.parseInt(message.substring(1));
                    cgm.players[index].chipsInPlay = (raiseAmount);
                    cgm.callAmount = raiseAmount;
                    break;
                case ('f'):
                    cgm.players[index].fold();
                    break;
                case ('e'):
                    cgm.players[index].eliminated = true;
                    break;
            }
        } else {
            switch (type) {
                case ('c'):
                    hgm.players[index].chipsInPlay = (hgm.callAmount - hgm.players[index].chipsInPlay);
                    break;
                case ('r'):
                    int raiseAmount = Integer.parseInt(message.substring(1));
                    hgm.players[index].chipsInPlay = (raiseAmount);
                    hgm.callAmount = raiseAmount;
                    break;
                case ('f'):
                    hgm.players[index].fold();
                    break;
                case ('e'):
                    hgm.players[index].eliminated = true;
                    break;
            }
        }
        updateUIData(host);

    }

    private void updateUIData(final boolean host) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                activity.updateInfo(host);
            }
        });
    }


    Handler getHandler() {
        return handler;
    }


}
