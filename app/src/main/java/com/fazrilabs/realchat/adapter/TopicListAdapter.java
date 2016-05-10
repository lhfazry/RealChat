package com.fazrilabs.realchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fazrilabs.realchat.R;
import com.fazrilabs.realchat.model.Topic;

import java.util.List;

/**
 * Created by blastocode on 5/4/16.
 */
public class TopicListAdapter extends ArrayAdapter<Topic>{
    public TopicListAdapter(Context context, List<Topic> topicList) {
        super(context, R.layout.item_list_topic, topicList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;

        if(v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.item_list_topic, null);
        }

        Topic topic = getItem(position);

        TextView titleTextView = (TextView) v.findViewById(R.id.title);
        titleTextView.setText(topic.title);

        return v;
    }
}
