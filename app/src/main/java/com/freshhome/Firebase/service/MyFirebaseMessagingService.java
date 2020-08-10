package com.freshhome.Firebase.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.DriverModule.DriverNavDrawerActivity;
import com.freshhome.Firebase.app.Config;

import com.freshhome.Firebase.utils.NotificationUtils;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "PUSH";
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }


//        try {
//
//            handleMyDataMessage(remoteMessage);
//
//            //new BackgroundWork().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private void handleMyDataMessage(RemoteMessage remoteMessage) throws Exception {

        // Log.d("daas", remoteMessage + "");

        Map<String, String> data = remoteMessage.getData();
        JSONObject jsonObject = new JSONObject(data);

        String title = jsonObject.getString("request_type");
        String body = jsonObject.getString("message");

//        String type = jsonObject.getString("type");
//        notiType(type);



        if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
           /* pushNotification.putExtra(kBody, body);
            pushNotification.putExtra(kTitle, "");*/

            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            showNotificationMessage(getApplicationContext(), title, body, String.valueOf(System.currentTimeMillis()), new Intent());
            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();


        } else {
            if (!title.equalsIgnoreCase("New Message Received")) {
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                showNotificationMessage(getApplicationContext(), title, body, String.valueOf(System.currentTimeMillis()), new Intent());
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
//                Intent resultIntent = HomeMapActivity.getIntent(getApplicationContext(),"");
//                showNotificationMessage(getApplicationContext(), title, body, NotificationUtils.generateTimeStamp(), resultIntent);
        }
    }


    private void handleDataMessage(JSONObject obj) {
        Log.e(TAG, "push json: " + obj.toString());

        try {
//            String data = json.getString("data");
//            JSONObject obj = new JSONObject(data);
            String message = "", order_id = "", user_type = "";
            String order_type = obj.getString("request_type");
            if (order_type.equalsIgnoreCase("order")) {
                message = obj.getString("message");
                order_id = obj.getString("order_id");
            } else if (order_type.equalsIgnoreCase("help_request")) {
                message = obj.getString("message");

            }
            if (obj.has("user_type")) {
                user_type = obj.getString("user_type");

                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                    // app is in background, broadcast the push message
                    Intent pushNotification = new Intent(ConstantValues.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    showNotificationMessage(getApplicationContext(), order_type, message, String.valueOf(System.currentTimeMillis()), new Intent());
                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();

                } else {
                    // app is in foreground, show the notification in notification tray
                    Intent resultIntent;

                    if (user_type.equalsIgnoreCase(ConstantValues.Sales)) {
                        //sales
                        resultIntent = new Intent(getApplicationContext(), ActivitySalesNavDrawer.class);
                        resultIntent.putExtra("message", message);

                    } else if (user_type.equalsIgnoreCase(ConstantValues.Driver)) {
                        //driver
                        resultIntent = new Intent(getApplicationContext(), DriverNavDrawerActivity.class);
                        resultIntent.putExtra("message", message);

                    } else if (user_type.equalsIgnoreCase(ConstantValues.ToCook)) {
                        //supplier
//                        sessionManager.saveLoginType(ConstantValues.ToCook);
                        resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
                        resultIntent.putExtra("message", message);

                    } else if (user_type.equalsIgnoreCase(ConstantValues.ToEat)) {
                        //user
//                        sessionManager.saveLoginType(ConstantValues.ToEat);
                        resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
                        resultIntent.putExtra("message", message);

                    } else {
//                        sessionManager.saveLoginType(ConstantValues.ToEat);
                        resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
                        resultIntent.putExtra("message", message);
                    }
//                 check for image attachment
                    Intent pushNotification = new Intent(ConstantValues.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    showNotificationMessage(getApplicationContext(), order_type, message, String.valueOf(System.currentTimeMillis()), new Intent());
                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                   // showNotificationMessage(getApplicationContext(), order_type, message, String.valueOf(System.currentTimeMillis()), resultIntent);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    private void notiType(String type) {

    }

    private Integer convertIntoInteger(String value) {
        Integer intvalue = 0;
        try {
            intvalue = Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intvalue;
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


}
