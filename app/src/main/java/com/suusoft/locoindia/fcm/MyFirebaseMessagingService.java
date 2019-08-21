package com.suusoft.locoindia.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.receiver.Action;
import com.suusoft.locoindia.view.activities.SplashLoginActivity;
import com.suusoft.locoindia.configs.ChatConfigs;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.objects.PaymentMethodObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.quickblox.SharedPreferencesUtil;

public class MyFirebaseMessagingService extends QBFcmPushListenerService {

    private static final String TAG = "MyFirebaseMsgService";

    private static int REQUEST_CODE = 0;
    private static int NOTIFICATION_ID = 0;

//    @Override
//    public void onNewToken(String refreshedToken) {
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//        // Save fcm token into preference
//        GlobalFunctions.saveFCMToken(this, refreshedToken);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
//
//        // Subscribe quickblox
//        SubscribeService.subscribeToPushes(AppController.getInstance(), true);
//    }

    /**
     * Persist token to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        //check allow notifi
        if (DataStoreManager.getSettingsNotify()) {

            // TODO(developer): Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.e(TAG, "From: " + remoteMessage.getFrom() + ", message: " + remoteMessage.getData().get("message"));

            // Check if message contains a data payload.
            String message = "", messagebody = "", type = "";
            if (remoteMessage.getData().size() > 0) {
                message = remoteMessage.getData().get(Args.MESSAGE);
                type = remoteMessage.getData().get(Args.NOTIFICATION_TYPE);
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            if (type.equals(ChatConfigs.QUICKBLOX_MESSAGE) || type.equalsIgnoreCase(ChatConfigs.QUICKBLOX_PAY_FOR_DEAL)) {
                String json = remoteMessage.getData().get(Args.RECENT_CHAT_OBJ);
                // Replace json if it's not correct format
                if (json.contains("=>")) {
                    json = json.replace("=>", ":");
                }
                Log.e(TAG, json);
                RecentChatObj obj = new Gson().fromJson(json, RecentChatObj.class);

                if (type.equals(ChatConfigs.QUICKBLOX_MESSAGE)) {
                /*trường comment ngày 26/3/18 để thay đổi điều kiện push từ thiết bị khác, phần comment là code ban đầu, dòng bên dưới là code đã sửa*/
                    //if (SharedPreferencesUtil.hasQbUser() && (!GlobalFunctions.isForeground(obj) || !DataStoreManager.conversationIsLive())) {
                    boolean hasQBUser = SharedPreferencesUtil.hasQbUser();
                    boolean isLive = DataStoreManager.conversationIsLive();
                    //boolean isForeground = GlobalFunctions.isForeground(obj);
                    Log.e("check", "hasQBUser = " +SharedPreferencesUtil.hasQbUser()  );
                    Log.e("check", "isLive = " +DataStoreManager.conversationIsLive() );
                    if (SharedPreferencesUtil.hasQbUser() &&
                            (!DataStoreManager.conversationIsLive())) {
                        // Remove sender's name(sender's name is just for iOS)
                        if (message.contains(":")) {
                            messagebody = message.substring((message.indexOf(":") + 1)).trim();
                        }

                        obj.setLastMessage(messagebody);

                        /**
                         *  add by truongnv ngày 26/3/18
                         *  Để show dialog (khi nhận push có chứa tin nhắn nội dung như check) trong App;
                         **/
                        Log.e("check", "wasInBackground = " +AppController.getInstance().wasInBackground  );
                        Log.e("check", "isScreenMain = " + DataStoreManager.isScreenMain() );
                        if ((AppController.getInstance().wasInBackground || !DataStoreManager.isScreenMain() )&& !DataStoreManager.conversationIsLive() ) {

                            if (obj != null)
                                sendQBNotification(messagebody, type, obj);
                        } else {

                            if (message.contains(getString(R.string.msg_check_notify_opponent_agreed_deal))
                                    || message.contains(getString(R.string.msg_check_notify_opponent_agreed_deal_with_id))) {
                                Log.e("message", "message = " + message);
                                Intent intent = new Intent(Action.ACTION_PUSH_DEAL);
                                if (obj != null)
                                    intent.putExtra(Args.RECENT_CHAT_OBJ, obj);
                                intent.putExtra(Args.NOTIFICATION_TYPE, type);
                                intent.putExtra(Action.ACTION_PUSH_DEAL, messagebody);
                                sendBroadcast(intent);
                            }

                        }


                        // Must also set negotiation to false here
                        if (messagebody.endsWith(String.format(getString(R.string.user_canceled_deal), "").trim())) {
                            DataStoreManager.saveDealNegotiation(obj, false);

                            /**
                             *  Nếu một trong 2 Passenger hoặc Driver cancel deal khi
                             *  người còn lại vẫn đang trong màn Trip tracking thì gửi Bcast tới Trip tracking
                             *  Gửi broadcast tới màn Trip tracking
                             */

                            Intent intent = new Intent(Action.ACTION_PUSH_CANCEL_DEAL);
                            intent.putExtra(Args.RECENT_CHAT_OBJ, obj);
                            intent.putExtra(Args.NOTIFICATION_TYPE, type);
                            String nameClient = message.substring(0, message.indexOf(":"));
                            intent.putExtra(Action.ACTION_PUSH_CANCEL_DEAL, nameClient);
                            sendBroadcast(intent);
                        }
                    }
                } else if (type.equalsIgnoreCase(ChatConfigs.QUICKBLOX_PAY_FOR_DEAL)) {
                    // Remove sender's name(sender's name is just for iOS)
                    if (message.contains(":")) {
                        message = message.substring((message.indexOf(":") + 1)).trim();
                    }

                    // Must also set negotiation to false here
                    DataStoreManager.saveDealNegotiation(obj, false);
                    if (GlobalFunctions.isForeground(obj)) {
                        // Update option menu
                        sendMessage(false);
                    }

                    // Update balance if client paid via credits
                    String paymentMethod = remoteMessage.getData().get(Args.PAYMENT_METHOD);
                    if (paymentMethod.equalsIgnoreCase(PaymentMethodObj.CREDITS)) {
                        String fare = remoteMessage.getData().get(Args.FARE);
                        if (fare == null || fare.equals("")) {
                            fare = "0";
                        } else if (fare.contains(",")) {
                            fare = fare.replace(",", "");
                        }
                        UserObj user = DataStoreManager.getUser();
                        user.setBalance(user.getBalance() + Float.parseFloat(fare));
                        DataStoreManager.saveUser(user);
                        if (AppController.getInstance().getiRedeem() != null) {
                            AppController.getInstance().getiRedeem().updateBalace(user.getBalance());
                        }
                    }

                    sendNotification(message);
                }
            } else if (type.equals(ChatConfigs.TYPE_DEAL)) {
                String id = remoteMessage.getData().get(Args.BALANCE);
                sendNewDeal(message, ChatConfigs.TYPE_DEAL, id);
            } else if (type.equals(Args.TYPE_BALANCE)) {
                UserObj user = DataStoreManager.getUser();
                String balance = remoteMessage.getData().get(Args.BALANCE);
                user.setBalance(Float.parseFloat(balance));
                DataStoreManager.saveUser(user);
                if (AppController.getInstance().getiRedeem() != null) {
                    AppController.getInstance().getiRedeem().updateBalace(user.getBalance());
                }

                sendNotification(message);
            } else {
                sendNotification(message);
            }

        /*if (type.equals(Args.TYPE_BALANCE)) {
            UserObj user = DataStoreManager.getUser();
            String balance = remoteMessage.getData().get(Args.BALANCE);
            Log.e("EE", "BAL: " + balance);
            user.setBalance(Float.parseFloat(balance));
            DataStoreManager.saveUser(user);
            if (AppController.getInstance().getiRedeem() != null) {
                AppController.getInstance().getiRedeem().updateBalace(user.getBalance());
            }
        }*/
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.e("notif", "sendNotification");
        Intent intent = new Intent(this, SplashLoginActivity.class);
        REQUEST_CODE++;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NOTIFICATION_ID++;
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void sendQBNotification(String messageBody, String notificationType, RecentChatObj obj) {
        Log.e("notif", "sendQBNotification");
        Intent intent = new Intent(this, SplashLoginActivity.class);
        intent.putExtra(Args.RECENT_CHAT_OBJ, obj);
        intent.putExtra(Args.NOTIFICATION_TYPE, notificationType);

        REQUEST_CODE++;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

//        Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(obj.getQbUser().getFullName())
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(obj.getQbUser().getId(), notificationBuilder.build());
    }

    private void sendNewDeal(String messageBody, String notificationType, String idDeal) {
        Log.e("notif", "sendNewDeal");
        Intent intent = new Intent(this, SplashLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putString(Args.KEY_ID_DEAL, idDeal);
        bundle.putString(Args.NOTIFICATION_TYPE, notificationType);
        intent.putExtras(bundle);

        REQUEST_CODE++;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(idDeal), notificationBuilder.build());
    }

    private void sendMessage(boolean negotiated) {
        Log.e("notif", "sendMessage");
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent(Constants.INTENT_ACTION_UPDATE_MENU);
        // Adding some data
        intent.putExtra(Args.NEGOTIATED, negotiated);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
