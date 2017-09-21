package com.amad.messaingapp.Utils;

import android.util.Log;

import com.amad.messaingapp.Entities.Message;
import com.amad.messaingapp.Entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 9/21/17.
 */

public class UserListParser {

    public static class JsonParse{

        public   static ArrayList<User> Parse(String s) throws JSONException {
            ArrayList<User> toReturn = new ArrayList<User>();
            JSONObject jsonObjectOut =new JSONObject(s);
            JSONArray users = jsonObjectOut.getJSONArray("Users");

            //JSONObject jsonObject =new JSONObject(s);
            for(int i =0 ;i<users.length();i++) {
                User toAdd = new User();
                JSONObject jsonObject = users.getJSONObject(i);
                Log.d("obj", jsonObject.toString());
               // toAdd.setStatus(jsonObject.getString("status"));
//                if (toAdd.getStatus().toLowerCase().equals("ok")) {
              //  {
                    toAdd.setfName(jsonObject.getString("Fname"));
                    toAdd.setlName(jsonObject.getString("Lname"));
                    if (jsonObject.has("token")) {
                        toAdd.setToken(jsonObject.getString("token"));
                    }
                    toAdd.setId(Integer.parseInt(jsonObject.getString("Id")));
                    toAdd.setEmail(jsonObject.getString("Email"));
                    toAdd.setGender(jsonObject.getString("Gender"));
                    toAdd.setAge(Integer.parseInt(jsonObject.getString("Age")));
                    toAdd.setWeight(Integer.parseInt(jsonObject.getString("Weight")));
                    toAdd.setAddress(jsonObject.getString("Address"));
//                } else {
//                    toAdd.setErrorMessage(jsonObject.getString("message"));
//                }
                toReturn.add(toAdd);
            }
            return toReturn;
        }
    }
}
