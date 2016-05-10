package com.fazrilabs.realchat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fazrilabs.realchat.R;
import com.fazrilabs.realchat.model.Message;

import java.util.List;

/**
 * Created by blastocode on 5/4/16.
 */
public class MessageListAdapter extends ArrayAdapter<Message>{
    private static final int TYPE_FROM_ME = 0;
    private static final int TYPE_FROM_OTHER = 1;
    private String mUsername;

    public MessageListAdapter(Context context, List<Message> messageList) {
        super(context, R.layout.item_list_message1);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUsername = sharedPreferences.getString("USERNAME", "");
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = getItem(position);
        String username = message.username;

        if(mUsername.equals(username)) {
            return TYPE_FROM_ME;
        }
        else {
            return TYPE_FROM_OTHER;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;

        int type = getItemViewType(position);

        if(v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(type == TYPE_FROM_ME) {
                v = layoutInflater.inflate(R.layout.item_list_message1, null);
            }
            else {
                v = layoutInflater.inflate(R.layout.item_list_message2, null);
            }
        }

        Message message = getItem(position);

        TextView usernameTextView = (TextView) v.findViewById(R.id.username);
        TextView messageTextView = (TextView) v.findViewById(R.id.message);
        TextView dateTextView = (TextView) v.findViewById(R.id.date);

        usernameTextView.setText(message.username);
        messageTextView.setText(message.message);
        dateTextView.setText(DateUtils.getRelativeTimeSpanString(message.createdAt.getTime()));

        return v;
    }
}
