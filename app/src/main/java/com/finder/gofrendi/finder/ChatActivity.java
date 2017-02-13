package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {
    String targetEmail;
    String targetName;
    String currentEmail;
    String currentName;
    Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        this.targetEmail = intent.getStringExtra("targetEmail");
        this.targetName = intent.getStringExtra("targetName");
        AppBackEnd backEnd = new AppBackEnd(this);
        this.currentEmail = backEnd.getCurrentUserEmail();
        this.currentName = backEnd.getCurrentUserName();
        showChat();
        try {
            mSocket = IO.socket(backEnd.protocol + "://" + backEnd.server);
            Log.d("my.listen", String.valueOf(backEnd.protocol + "://" + backEnd.server));
            mSocket.on("chat-to-"+this.currentEmail, new Emitter.Listener() {
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showChat();
                            Log.d("my.listen", "ok")                                                                                ;
                        }
                    });
                }
            });
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onButtonChatSendClick(View v) {
        EditText editTextChatMessage = (EditText) findViewById(R.id.editText_chat_message);
        AppBackEnd backEnd = new AppBackEnd(this);
        if(backEnd.chat(this.targetEmail, String.valueOf(editTextChatMessage.getText()))){
            editTextChatMessage.setText("");
            showChat();
        }
    }

    public void showChat() {
        LinearLayout layoutChatList = (LinearLayout) findViewById(R.id.layout_chat_list);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        //layoutChatList.setLayoutParams(params);
        layoutChatList.removeAllViews();
        AppBackEnd backEnd = new AppBackEnd(this);
        JSONArray chatList = backEnd.getChatList(this.targetEmail);
        for(int i=0; i<chatList.length(); i++){
            try {
                JSONObject chat = chatList.getJSONObject(i);
                String chatMessage = chat.getString("email_from").equals(this.targetEmail)? this.targetName : this.currentName;
                chatMessage += " (" + chat.getString("time") + ") : " + chat.getString("message");
                TextView chatContainer = new TextView(this);
                chatContainer.setText(chatMessage);
                chatContainer.setLayoutParams(params);
                layoutChatList.addView(chatContainer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
