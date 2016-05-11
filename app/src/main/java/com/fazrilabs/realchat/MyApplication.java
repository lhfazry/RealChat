package com.fazrilabs.realchat;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.fazrilabs.realchat.event.MessageEvent;
import com.fazrilabs.realchat.event.TopicEvent;
import com.fazrilabs.realchat.model.Message;
import com.fazrilabs.realchat.model.Topic;
import com.fazrilabs.realchat.util.NotificationUtil;
import com.fazrilabs.realchat.util.PrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by blastocode on 4/25/16.
 */
public class MyApplication extends android.app.Application {
    private static final String TAG = "MyApplication";
    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Log.d(TAG, "MyApplication onCreate");

            mSocket = IO.socket(getString(R.string.base_url));
            mSocket.on("newMessage", onNewMessage);
            mSocket.on("newTopic", onNewTopic);
            mSocket.connect();

            String username = PrefUtil.getUsername(this);

            // user has login
            if(!username.isEmpty()) {
                newUser(username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newUser(String username) {
        Log.d(TAG, "emit addUser");
        mSocket.emit("addUser", new String[]{username});
    }

    public void newMessage(String topicId, String message) {
        Log.d(TAG, "emit newMessage");
        mSocket.emit("newMessage", new String[]{topicId, message});
    }

    public void newTopic(String title) {
        Log.d(TAG, "emit newTopic");
        mSocket.emit("newTopic", new String[]{title});
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];

            try {
                String id = data.getString("id");
                String topicId = data.getString("topicId");
                String username = data.getString("username");
                String msg = data.getString("message");
                long createdAt = data.getLong("createdAt");

                Message message = new Message();
                message.id = id;
                message.topicId = topicId;
                message.username = username;
                message.message = msg;
                message.createdAt = new Date(createdAt);

                Intent intent = new Intent(MyApplication.this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyApplication.this, 1,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationUtil.showNotification(MyApplication.this, pendingIntent, msg);

                MessageEvent event = new MessageEvent(message);
                EventBus.getDefault().post(event);

                Log.d(TAG, "new message:" + message);
            } catch (Exception e) {
                Log.e(TAG, "onNewMessage: " +  e.getMessage());
                return;
            }
        }
    };

    private Emitter.Listener onNewTopic = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];

            try {
                String id = data.getString("id");
                String title = data.getString("title");

                Topic topic = new Topic();
                topic.id = id;
                topic.title = title;

                TopicEvent event = new TopicEvent(topic);
                EventBus.getDefault().post(event);

                Log.d(TAG, "new topic:" + title);
            } catch (JSONException e) {
                Log.e(TAG, "onNewTopic: " + e.getMessage());
                return;
            }
        }
    };
}