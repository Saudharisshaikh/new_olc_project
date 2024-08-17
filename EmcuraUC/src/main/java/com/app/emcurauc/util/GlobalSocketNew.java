package com.app.emcurauc.util;


import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class GlobalSocketNew {

    private static GlobalSocketNew instance;
    private Socket mSocket;
    private Activity activity;
    private SocketEmitterCallBack socketEmitterCallBack;

    private GlobalSocketNew(Activity activity) {
        this.activity = activity;

        // Configure the options for the socket connection
        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"}; // Use only WebSocket transport

        // Connect to the Socket.IO server
        try {
            mSocket = IO.socket("https://telelivecare.com:3000/", options);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Event handling when the connection is successful
        mSocket.on(Socket.EVENT_CONNECT, args -> {
            // Handle successful connection
            postToast("SocketIO Connected to the server!");
        });

        // Event handling when the connection is lost
        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
            // Handle disconnection
            postToast("SocketIO Disconnected from the server!");
        });

        // Add other event listeners for receiving messages from the server
        mSocket.on("chat message", args -> {
            // Handle incoming messages from the server
            String message = args[0].toString();
            postToast("SocketIO Received message: " + message);
            // Call the callback if available
            if (socketEmitterCallBack != null) {
                DATA.print("-- onConnected: data: " + message);
                socketEmitterCallBack.onSocketCallBack(message);
            }
        });
        //mSocket.connect();
    }

    public static synchronized GlobalSocketNew getInstance(Activity activity) {
        if (instance == null) {
            instance = new GlobalSocketNew(activity);
        }
        return instance;
    }

    public void setSocketEmitterCallBack(SocketEmitterCallBack socketEmitterCallBack) {
        this.socketEmitterCallBack = socketEmitterCallBack;
    }

    public void emitEvent(String id, String userType, String status) {
        JSONObject data = new JSONObject();
        try {
            data.put("id", id);
            data.put("usertype", userType);
            data.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Emit a 'message' event to the server
        mSocket.emit("webmsg", data);
    }

    public void disconnect() {
        // Disconnect the socket when the activity is destroyed
        mSocket.disconnect();
    }

    private void postToast(String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            //Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            DATA.print("-- Socket Toast: " + message);
        });
    }

    public interface SocketEmitterCallBack {
        void onSocketCallBack(String emitterResponse);
    }
}
