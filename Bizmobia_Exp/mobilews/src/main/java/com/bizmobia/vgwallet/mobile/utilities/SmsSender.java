/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bizmobia.vgwallet.mobile.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 *
 * @author itdev7
 */
public class SmsSender {

    public static void sendSms(String mobile, String description) throws UnsupportedEncodingException {
        String authkey = "4646AWuiG1JhXP587617ef";
        String senderId = "BIZVGW";

//Multiple mobiles numbers separated by comma
        String mobiles = mobile;
        String message = description;

//define route
        String route = "6";//put 4 o 6 to send sms
        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

//encoding message 
        String encoded_message = URLEncoder.encode(message, "UTF-8");

//Send SMS API
        String mainUrl = "http://login.bulksmsglobal.in/sendhttp.php?";

//Prepare parameter string 
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + mobiles);
        sbPostData.append("&message=" + encoded_message);
        sbPostData.append("&route=" + route);
        sbPostData.append("&sender=" + senderId);

        mainUrl = sbPostData.toString();
        try {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            String response;
            while ((response = reader.readLine()) != null) {
                System.out.println(response);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
