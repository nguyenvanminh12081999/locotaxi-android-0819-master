package com.suusoft.locoindia.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.quickblox.messages.services.SubscribeService;
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.globals.GlobalFunctions;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onNewToken(String refreshedToken) {

        // Get updated InstanceID token.
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // Save fcm token into preference
        GlobalFunctions.saveFCMToken(this, refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        // Subscribe quickblox
        SubscribeService.subscribeToPushes(AppController.getInstance(), true);
    }
    // [END refresh_token]

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
}
