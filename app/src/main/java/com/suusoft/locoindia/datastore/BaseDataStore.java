package com.suusoft.locoindia.datastore;

import android.content.Context;

import com.suusoft.locoindia.datastore.db.DbConnection;


/**
 * Created by SuuSoft.com on 5/20/2016.
 */
public class BaseDataStore {

    protected static BaseDataStore instance;
    protected MySharedPreferences sharedPreferences;
    protected DbConnection dbConnection;

    // ============== APP IS INSTALLED =====================
    private static final String KEY_PREF_IS_INSTALLED = "PREF_IS_INSTALLED";

    // ============== FCM TOKEN =====================
    private static final String PREF_TOKEN_FCM = "PREF_TOKEN_FCM";
    /**
     *
     * Call when start application
     *
     */
    public static void init(Context context){
        instance = new BaseDataStore();
        instance.sharedPreferences = new MySharedPreferences(context);
       // instance.dbConnection = new DbConnection(context);
    }

    public static BaseDataStore getInstance(){
        if (instance != null) {
            return instance;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }

    /**
     * Check app isIntalled?
     *
     */

    public static void setInstalled(boolean isInstalled){
        getInstance().sharedPreferences.putBooleanValue(KEY_PREF_IS_INSTALLED, isInstalled);
    }

    public static boolean isInstalled(){
        return getInstance().sharedPreferences.getBooleanValue(KEY_PREF_IS_INSTALLED);
    }

    /**
     * save and get token of fcm
     *
     */
    public static void saveTokenFCM(String token) {
        getInstance().sharedPreferences.putStringValue(PREF_TOKEN_FCM, token);
    }

    public static String getTokenFCM() {
        return getInstance().sharedPreferences.getStringValue(PREF_TOKEN_FCM);
    }

}
