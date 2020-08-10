//package com.freshhome.ravisdn;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.content.LocalBroadcastManager;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.freshhome.CommonUtil.ConstantValues;
//import com.freshhome.CommonUtil.UserSessionManager;
//import com.freshhome.DriverModule.DriverNavDrawerActivity;
//import com.freshhome.MainActivity_NavDrawer;
//import com.freshhome.SalesModule.ActivitySalesNavDrawer;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "notification";
//    UserSessionManager sessionManager;
//    private NotificationUtils notificationUtils;
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.e(TAG, "From: " + remoteMessage.getFrom());
//        sessionManager = new UserSessionManager(getApplicationContext());
//        if (remoteMessage == null) {
//            return;
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
//        }
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData());
//                handleDataMessage(json);
//            } catch (Exception e) {
//                Log.e(TAG, "Exception: " + e.getMessage());
//            }
//        }
//    }
//
//    private void handleNotification(String message) {
////        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            // app is in foreground, broadcast the push message
//            Intent pushNotification = new Intent(ConstantValues.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();
////        } else {
////             If the app is in background, firebase itself handles the notification
////        }
//    }
//
//    private void handleDataMessage(JSONObject obj) {
//        Log.e(TAG, "push json: " + obj.toString());
//
//        try {
////            String data = json.getString("data");
////            JSONObject obj = new JSONObject(data);
//            String message = "", order_id = "", user_type = "";
//            String order_type = obj.getString("request_type");
//            if (order_type.equalsIgnoreCase("order")) {
//                message = obj.getString("message");
//                order_id = obj.getString("order_id");
//            } else if (order_type.equalsIgnoreCase("help_request")) {
//                message = obj.getString("message");
//
//            }
//            if (obj.has("user_type")) {
//                user_type = obj.getString("user_type");
//
//                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                    // app is in background, broadcast the push message
//                    Intent pushNotification = new Intent(ConstantValues.PUSH_NOTIFICATION);
//                    pushNotification.putExtra("message", message);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                    showNotificationMessage(getApplicationContext(), order_type, message, String.valueOf(System.currentTimeMillis()), new Intent());
//                    // play notification sound
//                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                    notificationUtils.playNotificationSound();
//                } else {
//                    // app is in foreground, show the notification in notification tray
//                    Intent resultIntent;
//
//                    if (user_type.equalsIgnoreCase(ConstantValues.Sales)) {
//                        //sales
//                        resultIntent = new Intent(getApplicationContext(), ActivitySalesNavDrawer.class);
//                        resultIntent.putExtra("message", message);
//
//                    } else if (user_type.equalsIgnoreCase(ConstantValues.Driver)) {
//                        //driver
//                        resultIntent = new Intent(getApplicationContext(), DriverNavDrawerActivity.class);
//                        resultIntent.putExtra("message", message);
//
//                    } else if (user_type.equalsIgnoreCase(ConstantValues.ToCook)) {
//                        //supplier
////                        sessionManager.saveLoginType(ConstantValues.ToCook);
//                        resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
//                        resultIntent.putExtra("message", message);
//
//                    } else if (user_type.equalsIgnoreCase(ConstantValues.ToEat)) {
//                        //user
////                        sessionManager.saveLoginType(ConstantValues.ToEat);
//                        resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
//                        resultIntent.putExtra("message", message);
//
//                    } else {
////                        sessionManager.saveLoginType(ConstantValues.ToEat);
//                        resultIntent = new Intent(getApplicationContext(), MainActivity_NavDrawer.class);
//                        resultIntent.putExtra("message", message);
//                    }
////                 check for image attachment
//                    showNotificationMessage(getApplicationContext(), order_type, message, String.valueOf(System.currentTimeMillis()), resultIntent);
//                }
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
//
//    }
//
//    /**
//     * Showing notification with text only
//     */
//    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
//    }
//
//    /**
//     * Showing notification with text and image
//     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
//    }
//}