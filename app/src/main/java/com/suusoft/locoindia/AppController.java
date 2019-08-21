package com.suusoft.locoindia;

import android.app.Application;
import android.location.Location;

import com.quickblox.auth.session.QBSettings;
import com.suusoft.locoindia.datastore.BaseDataStore;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.interfaces.IRedeem;
import com.suusoft.locoindia.objects.DealCateObj;
import com.suusoft.locoindia.quickblox.conversation.utils.QBResRequestExecutor;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by SuuSoft.com on 1/8/2016.
 */
public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();
    private static AppController instance;
    private Location myLocation;
    private ArrayList<DealCateObj> mDealCategories;
    private QBResRequestExecutor qbResRequestExecutor;
    public static AppController getInstance() {
        return instance;
    }

    // true if user is updated.
    private boolean isUserUpdated;
    private IRedeem iRedeem;

    // dành cho việc check status foreground and background
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;
    // dành cho việc check status foreground and background

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BaseDataStore.init(this);
        isUserUpdated = true;

        initCredentials();
        initSetting();
        //ActivityLifecycle.init(this);
    }

    private void initSetting() {
        DataStoreManager.saveSettingsNotify(true);
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    // Init Quickblox credentials
    private void initCredentials() {
        QBSettings.getInstance().init(
                getApplicationContext(),
                getString(R.string.QB_APP_ID),
                getString(R.string.QB_AUTH_KEY) ,
                getString(R.string.QB_AUTH_SECRET));
        QBSettings.getInstance().setAccountKey(getString(R.string.QB_ACCOUNT_KEY));
    }


    public Double getLatMyLocation() {
        if (myLocation == null)
            return 0.0;
        return myLocation.getLatitude();
    }

    public Double getLongMyLocation() {
        if (myLocation == null)
            return 0.0;
        return myLocation.getLongitude();
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    public boolean isUserUpdated() {
        return isUserUpdated;
    }

    public void setUserUpdated(boolean userUpdated) {
        isUserUpdated = userUpdated;
    }

    public IRedeem getiRedeem() {
        return iRedeem;
    }

    public void setiRedeem(IRedeem iRedeem) {
        this.iRedeem = iRedeem;
    }

    //////////////-----------------------------------------------------------------------------------------------

    //[Start] dành cho việc check status foreground and background
    public void startActivityTransitionTimer() {
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                wasInBackground = true;
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        wasInBackground = false;
    }

    public static void onResume(){
        if (instance.wasInBackground) {
            //Do specific came-here-from-background code
        }
        instance.stopActivityTransitionTimer();
    }


    public static void onPause(){
       instance.startActivityTransitionTimer();
    }

    //[End] dành cho việc check status foreground and background








}
