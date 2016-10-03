package com.example.sai_praneeth7777.hasportal.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sai_praneeth7777.hasportal.Adapters.ChatAdapter;
import com.example.sai_praneeth7777.hasportal.Objects.ChatObject;
import com.example.sai_praneeth7777.hasportal.R;
import com.example.sai_praneeth7777.hasportal.UtilStrings;
import com.example.sai_praneeth7777.hasportal.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sai_praneeth7777 on 26-Jun-16.
 */
public class MessageChatActivity extends AppCompatActivity {

    ImageView send_message;
    TextView message,complaint;

    JSONObject obj;
    JSONArray arr;
    ChatAdapter content_adapter;
    ListView listView;
    LinearLayout textLayout;
    private String threadId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView complaint = (TextView)findViewById(R.id.complaint_resolved);
        LinearLayout textLayout = (LinearLayout)findViewById(R.id.listFooter);

        Intent i = getIntent();
        final String thread_id = i.getStringExtra("thread_id");
        final String solved = i.getStringExtra("solved");
        final String solvedBy = i.getStringExtra("solvedBy");
        //Toast.makeText(MessageChatActivity.this,"Solved-"+solved,Toast.LENGTH_LONG).show();

        if (Integer.valueOf(solved) == 0){
            complaint.setVisibility(View.GONE);
        }
        else{
            textLayout.setVisibility(View.GONE);
            complaint.setText("Complaint is resolved by - "+ solvedBy);
        }
        setThreadId(thread_id);
        fetchMessages(thread_id);

        send_message = (ImageView) findViewById(R.id.sendButton);
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = (EditText) findViewById(R.id.messageInput);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if(TextUtils.isEmpty(message.getText().toString())) {
                    message.setError("Cannot be Empty!");
                    return;
                }
                else {
                    sendMessage(message.getText().toString(), thread_id);
                    message.setText("");
                }
                ;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.complaint_resolve: {
                RequestQueue queue = Volley.newRequestQueue(this);
                final String url = getString(R.string.url_complaint_resolve);
                final String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
                final String thread_id = getThreadId();
                //Toast.makeText(MessageChatActivity.this,"Thread id"+thread_id+"Roll no"+roll_no, Toast.LENGTH_LONG).show();
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Toast.makeText(MessageChatActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MessageChatActivity.this, ThreadActivity.class);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("thread_id", thread_id);
                        params.put("roll_no", roll_no);
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

                int MY_SOCKET_TIMEOUT_MS = 10000;
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        MY_SOCKET_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                finish();
                break;
            }
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    private void fetchMessages(final String thread_id) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.url_get_messages);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MessageChatActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                        content_adapter = new ChatAdapter(MessageChatActivity.this, R.layout.messages);
                        listView = (ListView) findViewById(R.id.list_message);
                        //  listView.setOnItemClickListener(new ListAction());
                        listView.setAdapter(content_adapter);

                        try {
                            arr = new JSONArray(response.toString());
                            String subject, user, date, time, body, thread_id;
                            for (int i = 0; i < arr.length(); i++) {
                                obj = arr.getJSONObject(i);
                                body = obj.getString("body");
                                date = obj.getString("date");
                                time = obj.getString("time");
                                user = obj.getString("user");
                                ChatObject content = new ChatObject(body, time, date, user);
                                content_adapter.add(content);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessageChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("thread_id", thread_id);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

        int MY_SOCKET_TIMEOUT_MS = 10000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void sendMessage(final String message, final String thread_id) {
        //   getActionBar().hide();
        //Toast.makeText(MessageChatActivity.this, message, Toast.LENGTH_SHORT).show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.url_send_message);
        final String roll_no = Utils.getprefString(UtilStrings.ROLLNO, this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Toast.makeText(MessageChatActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                        fetchMessages(thread_id);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessageChatActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("thread_id", thread_id);
                params.put("message", message);
                params.put("roll_no", roll_no);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
//
//        int MY_SOCKET_TIMEOUT_MS = 10000;
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getThreadId() {
        return threadId;
    }
}
