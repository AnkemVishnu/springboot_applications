package com.vishnu.utilities;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author Vishnu
 */
public class AndroidPushNotification {

    public static void sendNotificationToAndroid(String title, String message, String userDeviceIdKey, String notificationtype, Object obj) throws Exception {
        URL url = new URL("https://fcm.googleapis.com/fcm/send");

//        String fcmsServerKey="AAAAOnfREfc:APA91bHwWYkHzT6KdoWi4DtZwBTiWKv548SDq8fUaC5U5JnSudgsMhJK8g4DdzKieYHJpzGPRcNv3dM_feiDPUFayvTxFKGkEs1lZjq8Tchv-TQpGj6qWJmJmXfImGDM5lrSStH9AVNL";
        String fcmsServerKey = "AIzaSyCVjGml3YhNtK_xRZ4ELJLwIXRC43vbXlU";
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + fcmsServerKey);
        conn.setRequestProperty("Content-Type", "application/json");
        JSONObject json = new JSONObject();
        json.put("to", userDeviceIdKey.trim());
        JSONObject dataobj = new JSONObject();
        dataobj.put("title", title);
        dataobj.put("ntype", notificationtype);
        dataobj.put("message", message);
        dataobj.put("content-available", 1);
        dataobj.put("force-start", 1);
        dataobj.put("object", obj);
        json.put("data", dataobj);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(json.toString());
        wr.flush();
        conn.getInputStream();
    }
}
