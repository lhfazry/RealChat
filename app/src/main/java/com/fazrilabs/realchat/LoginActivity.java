package com.fazrilabs.realchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fazrilabs.realchat.util.RequestUtil;
import com.fazrilabs.realchat.util.PopupUtil;
import com.fazrilabs.realchat.util.PrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    private EditText mUsernameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameEditText = (EditText) findViewById(R.id.username);
        Button registerButton = (Button) findViewById(R.id.button);

        registerButton.setOnClickListener(this);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(LoginActivity.this);
        String username = sharedPreferences.getString("USERNAME", "");

        if(!username.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.button) {
            String username = mUsernameEditText.getText().toString();

            if(username.isEmpty()) {
                mUsernameEditText.setError("Username can't be empty");
                return;
            }

            register(mUsernameEditText.getText().toString());
        }
    }

    private void register(final String username) {
        PopupUtil.showLoading(this, "Please wait ...");
        final String URL = getString(R.string.base_url) + "register/" + username;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
            (Request.Method.GET, URL, "", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    PopupUtil.dismiss();

                    try {
                        boolean success = response.getBoolean("success");

                        if(success) {
                            PrefUtil.setUsername(LoginActivity.this, username);
                            ((MyApplication) getApplication()).newUser(username);

                            Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent2);

                            finish();
                        }
                        else {
                            String message = response.getString("message");
                            PopupUtil.showMessage(LoginActivity.this, message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    PopupUtil.dismiss();
                    error.printStackTrace();
                }
            });

        RequestUtil.getInstance(this).addToRequestQueue(jsonRequest);
    }
}
