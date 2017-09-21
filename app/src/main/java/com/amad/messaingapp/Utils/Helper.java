package com.amad.messaingapp.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pushparajparab on 9/11/17.
 */

public class Helper {

    private static String _SYSTEM  = "PROD";
    public static String URL = _SYSTEM == "DEV" ? "http://inclass01.dev" : "http://ec2-13-59-39-123.us-east-2.compute.amazonaws.com";
    public enum Api_url {
        ALL_MESSAGES( URL +  "/api/getAllMessages"),
        MESSAGES_REGION_TYPE( URL + "/api/getMessagesByRegion/"),
        ALL_USERS( URL + "/api/getAllUsers"),
        SEND_MESSAGE( URL + "/api/createNewMessage"),
        DELETE_MESSAGE( URL + "/api/deleteMessage/"),
        UNLOCK_MESSAGE( URL + "/api/setMessageAsUnlock/"),
        MARK_READ_MESSAGE( URL + "/api/setMessageAsRead/");


        private final String text;
        private Api_url(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {
            return text;
        }
    }



}
