package com.amad.messaingapp.Utils;
import android.util.Log;


import com.amad.messaingapp.Entities.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pushparajparab on 9/11/17.
 */

public class JsonParser {

    public static class JsonParse{

        public static ArrayList<Message> Parse(String s) throws JSONException {
            ArrayList<Message> toReturn = new ArrayList<Message>();
            JSONObject jsonObject =new JSONObject(s);
            JSONArray products = jsonObject.getJSONArray("Messages");


            for(int i =0 ;i<products.length();i++){
                Message toAdd = new Message();
                JSONObject eachObj = products.getJSONObject(i);
                toAdd.setId(eachObj.getInt("Id"));
                toAdd.setContent(eachObj.getString("Content"));
                toAdd.setFromId(eachObj.getInt("fromID"));
                toAdd.setToID(eachObj.getInt("toID"));
                toAdd.setFromName(eachObj.getString("FromName"));
                toAdd.setToname(eachObj.getString("ToName"));
                toAdd.setLocked(eachObj.getString("MsgLock"));
                toAdd.setRead(eachObj.getString("MsgRead"));
                toAdd.setTime(eachObj.getString("Time"));
                toAdd.setRegionID(eachObj.getString("Region"));

                toReturn.add(toAdd);
            }


            return  toReturn;
        }
    }
}
