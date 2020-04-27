package com.e.localpoker;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.InetAddresses;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.Socket;

public class ClientGameManager extends Service implements Parcelable {

    private int STARTING_CHIPS = 0;

    HostGameManager host;
    String name;
    Context calledContext;
    NsdClient clientObj;
    int numChips;
    int hand;

    ClientGameManager(Context context, String name) {
        this.name = name;
        this.calledContext = context;
        this.numChips = STARTING_CHIPS;
    }


    protected ClientGameManager(Parcel in) {
        host = in.readParcelable(HostGameManager.class.getClassLoader());
        name = in.readString();
    }

    void startClientNsd() {
        clientObj = new NsdClient(this.calledContext, "Client", this);
    }

    public static final Creator<ClientGameManager> CREATOR = new Creator<ClientGameManager>() {
        @Override
        public ClientGameManager createFromParcel(Parcel in) {
            return new ClientGameManager(in);
        }

        @Override
        public ClientGameManager[] newArray(int size) {
            return new ClientGameManager[size];
        }
    };

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(host, flags);
        dest.writeString(name);
    }
}
