package com.amad.messaingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amad.messaingapp.Entities.Message;
import com.amad.messaingapp.Entities.User;
import com.amad.messaingapp.Utils.UserJsonParser;
import com.amad.messaingapp.Utils.Utils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private ImageButton imgbtn_boy,imgbtn_girl;
    private Button  btn_signUp,  btn_cancel ;
    private String GENDER = "MALE";
    private User updatedUser;
    private final OkHttpClient client = new OkHttpClient();
    private TextView fname,lname,email,pass,rpass,age,weight,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SetTheAppIcon();

        btn_signUp = (Button) findViewById(R.id.button_singUp);
        btn_cancel =(Button)findViewById(R.id.button_cancel);

        fname = (TextView) findViewById(R.id.editText_fname);
        lname = (TextView) findViewById(R.id.editText_lname);
        email = (TextView) findViewById(R.id.editText_email);
        pass = (TextView) findViewById(R.id.editText_pass);
        rpass = (TextView) findViewById(R.id.editText_rePass);




        btn_signUp.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }


    private void SetTheAppIcon()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
    private boolean ValidateForm()
    {
        boolean toCheck  =false;
        if(fname.getText().toString().equals(""))
        {
            fname.setError("Please Enter First name!");
            toCheck  =true;
        }
        if(lname.getText().toString().equals(""))
        {
            lname.setError("Please Enter Last name!");
            toCheck  =true;
        }
        if(email.getText().toString().equals(""))
        {
            email.setError("Please Enter Email!");
            toCheck  =true;
        }
        else{
            if(!Utils.isEmailValid(email.getText().toString()))
            {
                email.setError("Please enter proper username (email)!");
                toCheck = true;
            }
        }
        if(pass.getText().toString().equals(""))
        {
            pass.setError("Please Enter Password!");
            toCheck  =true;
        }
        if(rpass.getText().toString().equals(""))
        {
            rpass.setError("Please confirm Password!");
            toCheck  =true;
        }



        if(!rpass.getText().toString().equals(pass.getText().toString()) && (!rpass.getText().toString().equals("") && !pass.getText().toString().equals("") ) )
        {
            rpass.setError("Make sure to confirm password again!");
            toCheck  =true;
        }
        return  toCheck;
    }

    private void postToApi() {
        RequestBody formBody = new FormBody.Builder()
                .add("fname", fname.getText().toString())
                .add("lname", lname.getText().toString())
                .add("email", email.getText().toString())
                .add("password", pass.getText().toString())
                .add("weight", "23")
                .add("age", "23")
                .add("gender", GENDER)
                .add("address", "none")
                .build();




        Request request = new Request.Builder()
                .url(Utils.Api_url.SIGN_UP.toString())

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
                Log.d("userInfo", responseData);

                SignUp.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updatedUser = new User();
                        try {
                            updatedUser = UserJsonParser.JsonParse.Parse(responseData);

                            if(updatedUser.getToken() != null) {
                                SharedPreferences mPrefs = getSharedPreferences(MainActivity.STORED, MODE_PRIVATE);
                                SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                prefsEditor.putString("MyAppKey", updatedUser.getToken());
                                prefsEditor.commit();


                                Intent toSend = new Intent(SignUp.this, Messages.class);
                                toSend.putExtra(MainActivity.LOGGEDIN_USER, updatedUser);
                                startActivity(toSend);
                            }
                            else{
                                if(updatedUser.getStatus().toLowerCase().equals("error")){
                                    Toast.makeText(SignUp.this,updatedUser.getErrorMessage(),Toast.LENGTH_LONG).show();

                                }
                            }


                        } catch (JSONException e) {
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
//            case R.id.imgbtn_boy:
//                imgbtn_boy.setBackgroundResource(R.drawable.selected);
//                imgbtn_girl.setBackgroundResource(R.drawable.unselected);
//                GENDER = "MALE";
//                break;
//            case R.id.imgbtn_girl:
//                imgbtn_boy.setBackgroundResource(R.drawable.unselected);
//                imgbtn_girl.setBackgroundResource(R.drawable.selected);
//                GENDER = "FEMALE";
//                break;
            case R.id.button_singUp:
                if(!ValidateForm())
                {
                    postToApi();
                }
                break;
            case R.id.button_cancel:
                Intent toSend = new Intent(SignUp.this,MainActivity.class);
                startActivity(toSend);
                finish();
                break;

        }
    }
}


