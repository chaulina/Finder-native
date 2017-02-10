package com.finder.gofrendi.finder;

import android.support.v4.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by gofrendi on 2/7/17.
 */

public class Pusher extends Fragment{
    private Socket mSocket;

    public Pusher()
    {
        try {
            mSocket = IO.socket("http://example.com");
            mSocket.on("key", new Emitter.Listener(){
                public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            String username;
                            String message;
                            try {
                                username = data.getString("username");
                                message = data.getString("message");
                            } catch (JSONException e) {
                                return;
                            }

                            //removeTyping(username);
                            //addMessage(username, message);
                        }
                    });
                }
            });
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
