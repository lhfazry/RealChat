package com.fazrilabs.realchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddTopicActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAddButton;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);

        mEditText = (EditText) findViewById(R.id.editText);
        mAddButton = (Button) findViewById(R.id.button);

        mAddButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.button) {
            String title = mEditText.getText().toString();

            if(title.isEmpty()) {
                mEditText.setError("Title can't be empty");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("title", title);
            setResult(1, intent);

            finish();
        }
    }
}