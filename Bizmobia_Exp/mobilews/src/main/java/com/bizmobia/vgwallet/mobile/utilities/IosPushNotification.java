package com.bizmobia.vgwallet.mobile.utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONException;

/**
 *
 * @author Vishnu
 */
public class IosPushNotification {

    public static String sendNotificationToIOS(String token, String title, String message, String notificationtype, Object obj) throws javapns.communication.exceptions.CommunicationException, KeystoreException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        System.out.println(token + " --- " + message + " --- " + sdf.format(new Date()));
        String description = title + "\n" + message;
        BasicConfigurator.configure();
        Boolean isLive = false;
        List<PushedNotification> NOTIFICATIONS = new ArrayList<>();
        try {
            PushNotificationPayload payload = PushNotificationPayload.complex();
            payload.addAlert(description);
            payload.addCustomDictionary("ntype", notificationtype);
            payload.addCustomDictionary("object", obj);
            payload.addBadge(1);
            payload.addSound("Enabled");
            payload.addCustomDictionary("bookingid", "1234");
            System.out.println(payload.toString());
            if (isLive) {
                try {
                    NOTIFICATIONS = Push.payload(payload, "/var/www/html/ticket/vgwallet_development.p12", "bizmobia@123", isLive, token);
                } catch (CommunicationException | KeystoreException e) {
                    e.printStackTrace();
                    try {
                        NOTIFICATIONS = Push.payload(payload, "D:\\Bizmobia Projects\\VG-Wallet\\Resources\\VGW Ios\\vgwallet_development.p12", "bizmobia@123", isLive, token);
                    } catch (Exception ex) {
                        NOTIFICATIONS = Push.payload(payload, "D:\\Bizmobia Projects\\VG-Wallet\\Resources\\VGW Ios\\vgwallet_development.p12", "bizmobia@123", isLive, token);
                    }
                }
            } else {
                try {
                    NOTIFICATIONS = Push.payload(payload, "/var/www/html/ticket/vgwallet_production.p12", "bizmobia@123", isLive, token);
                } catch (CommunicationException | KeystoreException e) {
                    e.printStackTrace();
                    try {
                        NOTIFICATIONS = Push.payload(payload, "D:\\Bizmobia Projects\\VG-Wallet\\Resources\\VGW Ios\\vgwallet_production.p12", "bizmobia@123", isLive, token);
                    } catch (Exception ex) {
                        NOTIFICATIONS = Push.payload(payload, "D:\\Bizmobia Projects\\VG-Wallet\\Resources\\VGW Ios\\vgwallet_production.p12", "bizmobia@123", isLive, token);
                    }
                }
            }
            for (PushedNotification NOTIFICATION : NOTIFICATIONS) {
                if (NOTIFICATION.isSuccessful()) {
                    System.out.println("************notification sent to************" + NOTIFICATION.getDevice());
                } else {
                    String INVALIDTOKEN = NOTIFICATION.getDevice().getToken();
                    Exception THEPROBLEM = NOTIFICATION.getException();
                    THEPROBLEM.printStackTrace();
                    /* IF THE PROBLEM WAS AN ERROR-RESPONSE PACKET RETURNED BY APPLE, GET IT */
                    ResponsePacket THEERRORRESPONSE = NOTIFICATION.getResponse();
                    if (THEERRORRESPONSE != null) {
                        System.out.println(THEERRORRESPONSE.getMessage());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(token + " --- " + message + " --- " + sdf.format(new Date()));
        return "success";
    }
}
