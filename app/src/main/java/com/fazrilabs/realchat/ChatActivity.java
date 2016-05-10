package com.fazrilabs.realchat;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fazrilabs.realchat.adapter.MessageListAdapter;
import com.fazrilabs.realchat.event.MessageEvent;
import com.fazrilabs.realchat.model.Message;
import com.fazrilabs.realchat.util.MyRequest;
import com.fazrilabs.realchat.util.PrefUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements AbsListView.OnScrollListener,
        View.OnClickListener {
    private static final String TAG = "ChatActivity";
    private MessageListAdapter mAdapter;
    private ListView mListView;
    private EditText mEditText;
    private String mTopicId;
    private boolean mIsLoading = false;
    private boolean mIsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null) {
            mIsLoading = savedInstanceState.getBoolean("mIsLoading");
            mIsLoaded = savedInstanceState.getBoolean("mIsLoaded");
        }

        mTopicId = getIntent().getStringExtra("topicId");
        String topicTitle = getIntent().getStringExtra("topicTitle");

        setTitle(topicTitle);

        mListView = (ListView) findViewById(R.id.listView);
        mEditText = (EditText) findViewById(R.id.message);
        ImageButton sendButton = (ImageButton) findViewById(R.id.send);

        mAdapter = new MessageListAdapter(this, new ArrayList<Message>());
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(this);
        sendButton.setOnClickListener(this);
    }

    private void loadMessages() {
        Log.d(TAG, "Load messages: message/" + mTopicId + "/" + mAdapter.getCount());
        mIsLoading = true;
        final String URL = getString(R.string.base_url) + "message/" + mTopicId + "/" + mAdapter.getCount()
                + "?t=" + new Date().getTime();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mIsLoading = false;

                        try {
                            JSONArray data = response.getJSONArray("data");
                            Log.d(TAG, "data length:" + data.length());

                            for(int i=data.length()-1; i>=0; i--) {
                                JSONObject jsonObject = data.getJSONObject(i);

                                String id = jsonObject.getString("id");
                                String topicId = jsonObject.getString("topicId");
                                String username = jsonObject.getString("username");
                                String msg = jsonObject.getString("message");
                                long createdAt = jsonObject.getLong("createdAt");

                                Message message = new Message();
                                message.id = id;
                                message.topicId = topicId;
                                message.username = username;
                                message.message = msg;
                                message.createdAt = new Date(createdAt);

                                mAdapter.insert(message, 0);
                            }

                            mAdapter.notifyDataSetChanged();

                            if(data.length() == 0) {
                                mIsLoaded = true;
                            }
                        }
                        catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mIsLoading = false;
            }
        });

        MyRequest.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int loadedItems = firstVisibleItem + visibleItemCount;

        if(loadedItems == totalItemCount && !mIsLoading && !mIsLoaded) {
            loadMessages();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        Message message = event.message;
        Log.d(TAG, "onEvent MessageEvent:" + message.message);

        mAdapter.add(message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.send) {
            String msg = mEditText.getText().toString();

            if(!msg.isEmpty()) {
                ((MyApplication) getApplication()).newMessage(mTopicId, msg);
                Message message = new Message();
                message.topicId = mTopicId;
                message.username = PrefUtil.getUsername(this);
                message.message = msg;
                message.createdAt = new Date();

                mEditText.setText("");
                mAdapter.add(message);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("mIsLoading", mIsLoading);
        outState.putBoolean("mIsLoaded", mIsLoaded);
    }
}
