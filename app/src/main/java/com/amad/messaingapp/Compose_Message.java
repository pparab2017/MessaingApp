package com.amad.messaingapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Region;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amad.messaingapp.Entities.Message;
import com.amad.messaingapp.Entities.MyRegion;
import com.amad.messaingapp.Entities.User;
import com.amad.messaingapp.Utils.Helper;
import com.amad.messaingapp.Utils.JsonParser;
import com.amad.messaingapp.Utils.UserListParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Compose_Message extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_users, img_region;
    private Button btn_send;
    private String token;

    private TextView name,region;
    private final OkHttpClient client = new OkHttpClient();
    private ArrayList<User> mUsers;
    private ArrayList<MyRegion> mRegions;
    private Message curr_Msg;
    private int selected_userId, send_msg_from ;
    private String selected_region ;
    ArrayAdapter adpater, regionAdapter;
    EditText msg_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose__message);

        SetTheAppIcon();
        btn_send = (Button)findViewById(R.id.btn_send_msg);
        img_users = (ImageView)findViewById(R.id.img_users);
        img_region = (ImageView)findViewById(R.id.img_regions);
        name = (TextView) findViewById(R.id.txt_ToUser) ;
        region = (TextView) findViewById(R.id.txt_resionTo);
        msg_content = (EditText) findViewById(R.id.txt_msg_content);


        if(getIntent().hasExtra(ReadMessage.MSG_REPLY)) {
            if (getIntent().getExtras().containsKey(ReadMessage.MSG_REPLY)) {
                curr_Msg = (Message) getIntent().getExtras().getParcelable(ReadMessage.MSG_REPLY);
                Log.d("Edit reply", curr_Msg.toString());

                name.setText("To: "+ curr_Msg.getFromName());
                selected_userId =curr_Msg.getFromId();
                send_msg_from = curr_Msg.getToID(); // get the to of the msg which is this user
                region.setText("Region: "+ curr_Msg.getRegionID());
                selected_region = curr_Msg.getRegionID();


            }
        }else{
            img_users.setOnClickListener(this);
            img_region.setOnClickListener(this);
            SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED,MODE_PRIVATE);
            send_msg_from = Integer.parseInt( (String) mPrefs.getString("MyUserId", null));
        }

        btn_send.setOnClickListener(this);
        setUserList();

    }

    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


    private void setUserList()
    {
        SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED,MODE_PRIVATE);
        if(mPrefs.contains("MyAppKey")) {
             token = (String) mPrefs.getString("MyAppKey", null);

            Request request = new Request.Builder()
                    .url(Helper.Api_url.ALL_USERS.toString())
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
                    Compose_Message.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                ArrayList<User> users = new ArrayList<User>();
                                users = UserListParser.JsonParse.Parse(responseData);
                                Log.d("test", users.toString());
                                mUsers = users;
                                Log.d("users are here ", mUsers.toString());


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
        }
    }


    private void setUserPopup()
    {
        adpater = new ArrayAdapter<User>(this,android.R.layout.simple_list_item_1, mUsers){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("User")
                .setCancelable(false)
                .setAdapter(adpater, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        name.setText("To: "+ mUsers.get(which).getFullName());
                        selected_userId = mUsers.get(which).getId();

                    }


                });

        final AlertDialog d = alertBuilder.create();
        d.show();
    }


    private void setRegionPopup()
    {
        regionAdapter = new ArrayAdapter<MyRegion>(this,android.R.layout.simple_list_item_1, mRegions){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Region")
                .setCancelable(true)
                .setAdapter(regionAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        region.setText( "Region: " +mRegions.get(which).getName());
                        selected_region = mRegions.get(which).getName();

                    }


                });

        final AlertDialog d = alertBuilder.create();
        d.show();
    }



    public  void clearControls()
    {
        msg_content.setText("");
    }


    public void sendMessage(int from, int to, String region){


        RequestBody formBody = new FormBody.Builder()
                .add("from", from+"")
                .add("to", to+"")
                .add("region", region)
                .add("content", msg_content.getText().toString())
                .build();


        Request request = new Request.Builder()
                .url(Helper.Api_url.SEND_MESSAGE.toString())
                .addHeader("Authorization", "BEARER " + token)
                .post(formBody)
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
                Compose_Message.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if(responseData.contains("\"status\":\"ok\""))
                            {
                                Log.d("msg","sent");
                                Toast.makeText(Compose_Message.this,"Message Sent Successfully!",Toast.LENGTH_LONG).show();
                                clearControls();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_users:

                setUserPopup();
                break;
            case R.id.img_regions:

                mRegions = new ArrayList<MyRegion>();
                mRegions.add(new MyRegion("BooksStand"));
                mRegions.add(new MyRegion("Woodward333F"));
                mRegions.add(new MyRegion("Woodward332"));

                setRegionPopup();

                break;
            case R.id.btn_send_msg:
                sendMessage(send_msg_from,selected_userId,selected_region);
                break;

        }
    }
}
