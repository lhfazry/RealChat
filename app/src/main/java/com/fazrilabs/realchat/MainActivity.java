package com.fazrilabs.realchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fazrilabs.realchat.adapter.TopicListAdapter;
import com.fazrilabs.realchat.event.TopicEvent;
import com.fazrilabs.realchat.model.Topic;
import com.fazrilabs.realchat.util.MyRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";
    private ProgressBar mProgressBar;
    private ListView mListView;
    private TopicListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (ListView) findViewById(R.id.listView);

        mAdapter = new TopicListAdapter(this, new ArrayList<Topic>());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        loadTopics();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add) {
            Intent intent = new Intent(this, AddTopicActivity.class);
            startActivityForResult(intent, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadTopics() {
        Log.d(TAG, "Load topics");
        mProgressBar.setVisibility(View.VISIBLE);
        final String URL = getString(R.string.base_url) + "topic";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressBar.setVisibility(View.GONE);

                try {
                    JSONArray data = response.getJSONArray("data");
                    Log.d(TAG, "topic length: " + data.length());

                    for(int i=0; i<data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        String id = jsonObject.getString("_id");
                        String title = jsonObject.getString("title");

                        Log.d(TAG, "id: " + id);
                        Log.d(TAG, "title: " + title);

                        Topic topic = new Topic();
                        topic.id = id;
                        topic.title = title;
                        mAdapter.add(topic);
                    }

                    mAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {
                    Log.e(TAG, "Error parsing json on load topics: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error load topics: ");
                mProgressBar.setVisibility(View.GONE);
            }
        });

        MyRequest.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Topic topic = mAdapter.getItem(position);

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("topicId", topic.id);
        intent.putExtra("topicTitle", topic.title);
        startActivity(intent);
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
    public void onEvent(TopicEvent event){
        Topic topic = event.topic;
        Log.d(TAG, "onEvent TopicEvent: " + topic.title);

        mAdapter.add(topic);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String title = data.getStringExtra("title");

        ((MyApplication) getApplication()).newTopic(title);
    }
}
