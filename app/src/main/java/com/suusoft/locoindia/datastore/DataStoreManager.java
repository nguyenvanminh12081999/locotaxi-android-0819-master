package com.suusoft.locoindia.datastore;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.users.model.QBUser;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.objects.ContactObj;
import com.suusoft.locoindia.objects.DealObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.objects.ReservationObj;
import com.suusoft.locoindia.objects.SettingsObj;
import com.suusoft.locoindia.objects.TransportDealObj;
import com.suusoft.locoindia.objects.UserObj;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 9/13/2016.
 */

public class DataStoreManager extends BaseDataStore {


    // ============== User ============================
    private static final String PREF_USER = "PREF_USER";
    private static final String PREF_TOKEN_USER = "PREF_TOKEN_USER";

    // ============== Settings ============================
    private static final String PREF_SETTINGS_NOTIFY = "PREF_SETTINGS_NOTIFY";
    private static final String PREF_SETTINGS_NOTIFY_MESSAGE = "PREF_SETTINGS_NOTIFY_MESSAGE";
    private static final String PREF_SETTINGS_MY_FAVOURITE = "PREF_SETTING_MY_FAVOURITE";
    private static final String PREF_SETTINGS_TRANSPORT_DELIVERIES = "PREF_SETTINGS_TRANSPORT_DELIVERIES";
    private static final String PREF_SETTINGS_LABOR = "PREF_SETTINGS_LABOR";
    private static final String PREF_SETTINGS_FOOD_BERVERAGES = "PREF_SETTINGS_FOOD_BERVERAGES";
    private static final String PREF_SETTINGS_TRAVELLING = "PREF_SETTINGS_TRAVELLING";
    private static final String PREF_SETTINGS_SHOPPING = "PREF_SETTINGS_SHOPPING";
    private static final String PREF_SETTINGS_NEW_AND_EVENTS = "PREF_SETTINGS_NEW_AND_EVENTS";
    private static final String PREF_SETTINGS_NEAR_BY_DEALS = "PREF_SETTINGS_NEAR_BY_DEALS";
    private static final String PREF_LOGIN_CHECKED = "PREF_LOGIN_CHECKED";
    private static final String PREF_RECENT_CHAT = "PREF_RECENT_CHAT";
    private static final String PREF_CURRENT_CHAT = "PREF_CURRENT_CHAT";
    // setting utitlity
    private static final String PREF_SETTING_UTILITY = "SETTING_UTILITY";
    private static final String PREF_TRANSPORT_DEAL = "transportDeal";
    private static final String PREF_CONVERSATION_STATUS = "conversationStatus";
    private static final String PREF_TRACKING_TRIP = "PREF_TRACKING_TRIP";
    private static final String PREF_SCREEN_MAIN = "PREF_SCREEN_MAIN";
    private static final String PREF_DEAL_IS_NEGOTIATED = "dealIsNegotiated";
    private static final String PREF_CONTACTS_LIST = "contactsList";
    private static final String PREF_NEGOTIATED_RESERVATION = "reservationNegotiatedObj";
    private static final String PREF_SETTINGS_ONLINE = "PREF_SETTINGS_ONLINE";

    /**
     * save and get user
     */
    public static void saveUser(UserObj user) {
        if (user != null) {
            String jsonUser = new Gson().toJson(user);
            getInstance().sharedPreferences.putStringValue(PREF_USER, jsonUser);
        }
    }

    public static void removeUser() {
        getInstance().sharedPreferences.putStringValue(PREF_USER, null);
    }

    public static UserObj getUser() {
        String jsonUser = BaseDataStore.getInstance().sharedPreferences.getStringValue(PREF_USER);
        UserObj user = new Gson().fromJson(jsonUser, UserObj.class);
        return user;
    }

    public static void clearUserToken() {
        UserObj userObj = getUser();
        userObj.setToken(null);
        saveUser(userObj);
    }

    public static void addToRecentChat(RecentChatObj obj) {
        if (obj != null) {
            QBUser qbUser = obj.getQbUser();
            ArrayList<RecentChatObj> recentChats = getRecentChat();
            for (int i = 0; i < recentChats.size(); i++) {
                if (recentChats.get(i).getQbUser().getId().equals(qbUser.getId())) {
                    // Remove old obj
                    recentChats.remove(i);
                    BaseDataStore.getInstance().sharedPreferences.putStringValue(PREF_RECENT_CHAT,
                            new Gson().toJson(recentChats));
                    break;
                }
            }

            // Add new obj and save into preference
            recentChats.add(obj);
            BaseDataStore.getInstance().sharedPreferences.putStringValue(PREF_RECENT_CHAT,
                    new Gson().toJson(recentChats));
        }
    }

    public static ArrayList<RecentChatObj> getRecentChat() {
        String json = BaseDataStore.getInstance().sharedPreferences.getStringValue(PREF_RECENT_CHAT);
        if (json != null && !json.equals("")) {
            Type type = new TypeToken<ArrayList<RecentChatObj>>() {
            }.getType();

            return new Gson().fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public static void clearRecentChat() {
        BaseDataStore.getInstance().sharedPreferences.putStringValue(PREF_RECENT_CHAT, "");
    }

    public static void saveCurrentChat(RecentChatObj obj) {
        if (obj != null) {
            String jsonUser = new Gson().toJson(obj);
            getInstance().sharedPreferences.putStringValue(PREF_CURRENT_CHAT, jsonUser);
        }
    }

    public static RecentChatObj getCurrentChat() {
        String jsonUser = BaseDataStore.getInstance().sharedPreferences.getStringValue(PREF_CURRENT_CHAT);

        return new Gson().fromJson(jsonUser, RecentChatObj.class);
    }

    public static void clearCurrentChat() {
        BaseDataStore.getInstance().sharedPreferences.putStringValue(PREF_CURRENT_CHAT, "");
    }

    /**
     * save status live conversation
     **/
    public static void saveConversationStatus(boolean status) {
        BaseDataStore.getInstance().sharedPreferences.putBooleanValue(PREF_CONVERSATION_STATUS, status);
    }

    public static boolean conversationIsLive() {
        return BaseDataStore.getInstance().sharedPreferences.getBooleanValue(PREF_CONVERSATION_STATUS);
    }


    /**
     * save status trip tracking
     **/
    public static boolean isTrackingTrip() {
        return BaseDataStore.getInstance().sharedPreferences.getBooleanValue(PREF_TRACKING_TRIP);
    }

    public static void saveTrackingTrip(boolean status) {
        BaseDataStore.getInstance().sharedPreferences.putBooleanValue(PREF_TRACKING_TRIP, status);
    }


    /**
     * save status app in screen main
     **/
    public static boolean isScreenMain() {
        return BaseDataStore.getInstance().sharedPreferences.getBooleanValue(PREF_SCREEN_MAIN);
    }

    public static void saveScreenMain(boolean status) {
        BaseDataStore.getInstance().sharedPreferences.putBooleanValue(PREF_SCREEN_MAIN, status);
    }


    /**
     *  Save deals transport is negotiantion
     **/

    public static void saveDealNegotiation(RecentChatObj obj, boolean isNegotiated) {
        String key = "", userId = "";
        if (obj.getTransportDealObj() != null) {

            userId = obj.getTransportDealObj().getPassengerId() + "" + obj.getTransportDealObj().getDriverId();
            key = PREF_DEAL_IS_NEGOTIATED + userId
                    + obj.getTransportDealObj().getLatLngPickup().latitude
                    + obj.getTransportDealObj().getLatLngPickup().longitude
                    + obj.getTransportDealObj().getLatLngDestination().latitude
                    + obj.getTransportDealObj().getLatLngDestination().longitude;
        }
        Log.e("key", "key = " + key);
        BaseDataStore.getInstance().sharedPreferences.putBooleanValue(key, isNegotiated);
    }

    public static boolean dealIsNegotiated(RecentChatObj obj) {
        if (obj.justForChatting() || obj.getQbUser() == null) {
            return false;
        }

        String key = "", userId = "";
        if (obj.getTransportDealObj() != null) {

            userId = obj.getTransportDealObj().getPassengerId() + "" + obj.getTransportDealObj().getDriverId();

            key = PREF_DEAL_IS_NEGOTIATED + userId
                    + obj.getTransportDealObj().getLatLngPickup().latitude
                    + obj.getTransportDealObj().getLatLngPickup().longitude
                    + obj.getTransportDealObj().getLatLngDestination().latitude
                    + obj.getTransportDealObj().getLatLngDestination().longitude;
        }

        Log.e("key", "key = " + key);
        return BaseDataStore.getInstance().sharedPreferences.getBooleanValue(key);
    }

    /**
     * =======================================================
     * Settings screen
     */
    public static void saveSettingsNotify(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_NOTIFY, isChecked);
    }

    public static boolean getSettingsNotify() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_NOTIFY);
    }

    public static void saveSettingsMyFavourite(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_MY_FAVOURITE, isChecked);
    }

    public static boolean getSettingsMyFavourite() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_MY_FAVOURITE);
    }

    public static void saveSettingsTransportDeliveries(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_TRANSPORT_DELIVERIES, isChecked);
    }

    public static boolean getSettingsTransportDeliveries() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_TRANSPORT_DELIVERIES);
    }

    public static void saveSettingsFoodBeverages(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_FOOD_BERVERAGES, isChecked);
    }

    public static boolean getSettingsFoodBeverages() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_FOOD_BERVERAGES);
    }

    public static void saveSettingsLabor(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_LABOR, isChecked);
    }

    public static boolean getSettingsLabor() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_LABOR);
    }

    public static void saveSettingsTravelling(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_TRAVELLING, isChecked);
    }

    public static boolean getSettingsTravelling() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_TRAVELLING);
    }

    public static void saveSettingsShopping(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_SHOPPING, isChecked);
    }

    public static boolean getSettingsShopping() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_SHOPPING);
    }

    public static void saveSettingsNewsAndEvents(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_NEW_AND_EVENTS, isChecked);
    }

    public static boolean getSettingsNewsAndEvents() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_NEW_AND_EVENTS);
    }

    public static void saveSettingsNearByDeals(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_NEAR_BY_DEALS, isChecked);
    }

    public static boolean getSettingsNearByDeals() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_NEAR_BY_DEALS);
    }

    public static void saveOnline(boolean isOnline) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_ONLINE, isOnline);
    }

    public static boolean isOnline() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_ONLINE);
    }

    public static void saveSettingsNotifyMessage(boolean isChecked) {
        getInstance().sharedPreferences.putBooleanValue(PREF_SETTINGS_NOTIFY_MESSAGE, isChecked);
    }

    public static boolean getSettingsNotifyMessage() {
        return getInstance().sharedPreferences.getBooleanValue(PREF_SETTINGS_NOTIFY_MESSAGE);
    }

    public static void saveSettingUtility(String setting) {
        getInstance().sharedPreferences.putStringValue(PREF_SETTING_UTILITY, setting);
    }

    public static SettingsObj getSettingUtility() {
        String setting = getInstance().sharedPreferences.getStringValue(PREF_SETTING_UTILITY);
        if (setting.isEmpty()) {
            return null;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(setting);
                ApiResponse apiResponse = new ApiResponse(jsonObject);
                if (!apiResponse.isError()) {
                    SettingsObj utitlityObj = apiResponse.getDataObject(SettingsObj.class);
                    return utitlityObj;

                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void saveTransport(TransportDealObj obj) {
        if (obj != null) {
            String jsonUser = new Gson().toJson(obj);
            getInstance().sharedPreferences.putStringValue(PREF_TRANSPORT_DEAL + obj.getDriverId(), jsonUser);
        }
    }

    public static TransportDealObj getTransport(String driverId) {
        String jsonUser = BaseDataStore.getInstance().sharedPreferences.getStringValue(PREF_TRANSPORT_DEAL + driverId);

        return new Gson().fromJson(jsonUser, TransportDealObj.class);
    }

    public static void saveContactsList(ArrayList<ContactObj> arr) {
        if (arr != null) {
            String contacts = new Gson().toJson(arr);
            getInstance().sharedPreferences.putStringValue(PREF_CONTACTS_LIST + getUser().getId(), contacts);
        }
    }

    public static ArrayList<ContactObj> getContactsList() {
        String json = BaseDataStore.getInstance().sharedPreferences.getStringValue(PREF_CONTACTS_LIST + getUser().getId());
        if (json != null && !json.equals("")) {
            Type type = new TypeToken<ArrayList<ContactObj>>() {
            }.getType();

            return new Gson().fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    public static void saveNegotiatedReservation(ReservationObj obj, boolean reset) {
        String json = new Gson().toJson(obj);

        String key = "", userId = "";
        if (obj.getDeal() != null) {
            /*if (obj.getDeal().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                userId = obj.getDeal().getBuyerId();
            } else {
                userId = obj.getDeal().getSeller_id();
            }*/
            userId = obj.getDeal().getBuyerId() + "" + obj.getDeal().getSeller_id();

            key = PREF_NEGOTIATED_RESERVATION + userId + obj.getDeal().getId();
        }

        if (reset) {
            json = "";
        }
        getInstance().sharedPreferences.putStringValue(key, json);
    }

    public static ReservationObj getNegotiatedReservation(DealObj dealObj) {
        String key = "", userId = "";
        if (dealObj != null) {
            /*if (dealObj.getSeller_id().equals(DataStoreManager.getUser().getId())) {
                userId = dealObj.getBuyerId();
            } else {
                userId = dealObj.getSeller_id();
            }*/
            userId = dealObj.getBuyerId() + "" + dealObj.getSeller_id();

            key = PREF_NEGOTIATED_RESERVATION + userId + dealObj.getId();
        }

        String json = getInstance().sharedPreferences.getStringValue(key);
        return new Gson().fromJson(json, ReservationObj.class);
    }
}
