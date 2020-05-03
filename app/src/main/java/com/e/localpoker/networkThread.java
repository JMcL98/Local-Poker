package com.e.localpoker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.IOException;

public class networkThread extends HandlerThread {

    private Handler handler;
    private Context calledContext;
    private HostGameManager hgm;
    private ClientGameManager cgm;
    private Handler uiHandler;
    private MainActivity mainActivity;

    public networkThread(Context context, Handler uiHandler, MainActivity mainActivity) {
        super("hThread1");
        calledContext = context;
        this.uiHandler = uiHandler;
        this.mainActivity = mainActivity;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case (1):
                        Bundle bundle = message.getData();
                        hgm = (HostGameManager) bundle.get("hostmanager");
                        assert hgm != null;
                        try {
                            hgm.startHostNsd();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int i = 1;
                        byte msgType = 0;
                        while (i == 1) {
                            for (int j = 1; j < hgm.numPlayers; j++) {
                                if ((hgm.tempPlayers[j].getPlayerName() == null) && (hgm.tempPlayers[j].playerInput != null)) {
                                    try {
                                        msgType = hgm.tempPlayers[j].playerInput.readByte();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (msgType == 1) {
                                        try {
                                            final String newName = hgm.tempPlayers[j].playerInput.readUTF();
                                            uiHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mainActivity.acceptedPlayer(newName);
                                                }
                                            });
                                            hgm.tempPlayers[j].setPlayerName(newName);
                                            hgm.tempPlayers[j].playerOutput.writeByte(1);
                                            hgm.tempPlayers[j].playerOutput.writeUTF("name_received");
                                            hgm.tempPlayers[j].playerOutput.flush();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (!(hgm.hostObj.acceptingPlayers)) {
                                i = 0;
                            }
                        }
                        hgm.startGame();
                        break;
                    case (2):
                        Bundle receivedClientBundle = message.getData();
                        cgm = (ClientGameManager) receivedClientBundle.get("clientmanager");
                        assert cgm != null;
                        cgm.startClientNsd();
                        int j = 1;
                        byte msgType2;
                        while (j > 0) {
                            if (cgm.clientOutput != null) {
                                if (j == 1) {
                                    try {
                                        cgm.nsdClient.sendName();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (cgm.clientInput != null) {
                                    try {
                                        msgType2 = cgm.clientInput.readByte();
                                        if (msgType2 == 1) {
                                            String testMessage = cgm.clientInput.readUTF();
                                            if (testMessage.equals("name_received")) {
                                                j = 2;
                                            } else if (testMessage.equals("start_game")) {
                                                j = 0;
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.clientStart();
                            }
                        });

                        break;
                }
            }
        };
    }


    Handler getHandler() {
        return handler;
    }
}
