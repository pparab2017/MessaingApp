package com.amad.messaingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.amad.messaingapp.Entities.Message;
import com.amad.messaingapp.Utils.Helper;
import com.amad.messaingapp.Utils.JsonParser;
import com.amad.messaingapp.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadMessage extends AppCompatActivity {

    private Message curr_Msg;
    TextView from,region,content;
    final static String MSG_REPLY = "msgReply";
    final static int MSG_REPLY_CODE = 01101;
    private String token;
    private final OkHttpClient client = new OkHttpClient();

    private void markAsRead()
    {

        SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED,MODE_PRIVATE);
        if(mPrefs.contains("MyAppKey")) {
            token = (String) mPrefs.getString("MyAppKey", null);
            Request request = new Request.Builder()
                    .url(Helper.Api_url.MARK_READ_MESSAGE.toString() + curr_Msg.getId())
                    .addHeader("Authorization", "BEARER " + token)
                    .build();

            final boolean[] toReturn = {false};
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String responseData = response.body().string();

                    Log.d("Response", responseData);
                    if (responseData.equals("{\"status\":\"ok\"}")) {
                        // marked as read
                    }

                }
            });
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);
        SetTheAppIcon();

        from = (TextView)findViewById(R.id.txt_from);
        region = (TextView)findViewById(R.id.txt_region);
        content = (TextView)findViewById(R.id.txt_content);


        if(getIntent().hasExtra(Messages.MSG_READ)) {
            if (getIntent().getExtras().containsKey(Messages.MSG_READ)) {
                curr_Msg = (Message) getIntent().getExtras().getParcelable(Messages.MSG_READ);
                Log.d("Edit Intent", curr_Msg.toString());
                makeDisplay(curr_Msg);
                markAsRead();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenu = getMenuInflater();
        myMenu.inflate(R.menu.menu_msg_read,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void DeleteMessage(){

        SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED,MODE_PRIVATE);
        if(mPrefs.contains("MyAppKey")) {
             token = (String) mPrefs.getString("MyAppKey", null);
            Request request = new Request.Builder()
                    .url(Helper.Api_url.DELETE_MESSAGE.toString() + curr_Msg.getId())
                    .addHeader("Authorization", "BEARER " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String responseData = response.body().string();
                    Log.d("Response", responseData);
                    //Run view-related code back on the main thread
                    ReadMessage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if(responseData.contains("\"status\":\"ok\""))
                                {
                                    Intent editUser = new Intent(ReadMessage.this, Messages.class);
                                    startActivity(editUser);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.reply:


                Intent editUser = new Intent(ReadMessage.this, Compose_Message.class);
                editUser.putExtra(MSG_REPLY, curr_Msg );
                startActivityForResult(editUser, MSG_REPLY_CODE);

                return true;


            case R.id.delete:

                DeleteMessage();
                break;
                        default:
                            break;
                    }

        return super.onOptionsItemSelected(item);
    }
    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


    private void makeDisplay(Message msg)
    {
        from.setText("From: "  + msg.getFromName());
        region.setText("Region: "+ msg.getRegionID());
        content.setText(msg.getContent());
    }

}
