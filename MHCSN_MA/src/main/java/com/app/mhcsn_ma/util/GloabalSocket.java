package com.app.mhcsn_ma.util;

import android.app.Activity;

import com.app.mhcsn_ma.OnlineCareDr;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Engr G M on 11/22/2017.
 */

public class GloabalSocket {


    Activity activity;

    private Socket mSocket;

    SocketEmitterCallBack socketEmitterCallBack;

    public GloabalSocket(Activity activity, SocketEmitterCallBack socketEmitterCallBack) {
        this.activity = activity;
        this.socketEmitterCallBack = socketEmitterCallBack;

        OnlineCareDr app = (OnlineCareDr) activity.getApplication();
        //mSocket = app.getSocket();
        if(mSocket == null){
            mSocket = app.getSocket();
        }

        offSocket();

        mSocket.on("chat message",onConnect);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //if(!isConnected) {
                    // Map<String,String> data = new HashMap();
					/*JSONObject data = new JSONObject();
					try {
						data.put("room",roomId);
						data.put("sender_id",senderid);
						data.put("receiver_id",recieverId);
						data.put("username",sharedPrefsHelper.getUser().Studio_Name);

						DATA.print("-- joinroom data: "+data.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}*/
                    //mSocket.emit("joinroom", data);
                    //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    for (Object o: args) {
                        DATA.print("-- onConnected: data: "+o.toString());
                        socketEmitterCallBack.onSocketCallBack(o.toString());
                    }
                    DATA.print("--args size: "+args.length);
                }
            });
        }
    };

    public void offSocket(){
        mSocket.disconnect();
        mSocket.off("chat message",onConnect);
        mSocket.off("chat message");
        mSocket.off();
    }

    public void emitSocket(JSONObject jsonData){
        mSocket.emit("webmsg",jsonData);
    }


    public interface SocketEmitterCallBack{
        public void onSocketCallBack(String emitterResponse);
    }
}
