package com.suusoft.locoindia.modelmanager;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.configs.Apis;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IResponse;
import com.suusoft.locoindia.network1.MyProgressDialog;
import com.suusoft.locoindia.network1.VolleyGet;
import com.suusoft.locoindia.network1.VolleyPost;
import com.suusoft.locoindia.objects.DataPart;
import com.suusoft.locoindia.objects.DealCateObj;
import com.suusoft.locoindia.objects.TransportDealObj;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.quickblox.SharedPreferencesUtil;
import com.suusoft.locoindia.quickblox.chat.ChatHelper;
import com.suusoft.locoindia.utils.map.LocationService1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SuuSoft.com on 06/22/2016.
 */
public class ModelManager {

    private static final String TAG = ModelManager.class.getSimpleName();

    // Params
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_IME = "ime";
    private static final String PARAM_GCM_ID = "gcm_id";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_STATUS = "status";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_ADDRESS = "address";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_IMAGE = "image";
    private static final String PARAM_PHONE = "phone";
    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_FIELDS = "fields";
    private static final String PARAM_ACCESS_TOKEN = "access_token";
    private static final String PARAM_LOGIN_METHOD = "login_type";
    // params update profile pro
    private static final String PARAM_AVATAR = "avatar";
    private static final String PARAM_USER_ID = "user_id";
    private static final String PARAM_QB_USER_ID = "qb_id";

    private static final String PARAM_BUSINESS_NAME = "business_name";
    private static final String PARAM_BUSINESS_EMAIL = "business_email";
    private static final String PARAM_BUSINESS_PHONE = "business_phone";
    private static final String PARAM_BUSINESS_ADDRESS = "business_address";
    private static final String PARAM_BRAND = "brand";
    private static final String PARAM_MODEL = "model";
    private static final String PARAM_YEAR = "year";
    private static final String PARAM_COST_YOU_CHARGE_PER_KM = "fare";
    private static final String PARAM_CAR_COLOR = "color";
    private static final String PARAM_PLATE = "plate";
    private static final String PARAM_FARE_TYPE = "fare_type";

    private static final String PARAM_TYPE_OF_TRANSPORT = "type";
    private static final String PARAM_FUEL = "fuel_type";
    private static final String PARAM_YEAR_KM = "yearly_km";
    private static final String PARAM_YEAR_INTEND = "year_intend";
    private static final String PARAM_YEAR_TAX = "yearly_tax";
    private static final String PARAM_YEAR_GARA = "yearly_gara";
    private static final String PARAM_AVERAGE_CONSUMPTION = "average_consumption";
    private static final String PARAM_FUEL_UNIT_PRICE = "fuel_unit_price";
    private static final String PARAM_YEAR_INSURANCE = "yearly_insurance";
    private static final String PARAM_YEAR_MAINTENANCE = "yearly_maintenance";
    private static final String PARAM_PRICE_4_NEW_TYRES = "price_4_new_tyres";
    private static final String PARAM_YEAR_UNEXPECTED = "yearly_unexpected";
    private static final String PARAM_SOLD_VALUE = "sold_value";
    private static final String PARAM_BOUGHT_VALUE = "bought_value";
    private static final String PARAM_CERTIFICATION = "driver_license";
    private static final String PARAM_CAR = "image";
    private static final String PARAM_DELIVERY = "is_delivery";

    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_SEARCH_TYPE = "search_type";
    private static final String PARAM_IS_ONLINE = "is_online";
    private static final String PARAM_CATEGORY_ID = "category_id";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LONG = "long";
    private static final String PARAM_IS_PREMIUM = "is_premium";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_ACTION_CREATE = "create";
    private static final String PARAM_ACTION_UPDATE = "update";
    private static final String PARAM_ACTION_ONLINE = "online";
    private static final String PARAM_DURATION = "duration";
    private static final String PARAM_ESTIMATE_DURATION = "estimate_duration";
    private static final String PARAM_PRICE = "price";
    public static final String PARAM_DISCOUNT_TYPE = "discount_type";
    private static final String PARAM_DISCOUNT_PRICE = "discount";
    public static final String PARAM_DISCOUNT_PERCENT = "discount_rate";
    private static final String PARAM_AUTO_RENEW = "is_renew";
    private static final String PARAM_KEY_WORD = "keyword";
    private static final String PARAM_DISTANCE = "distance";
    private static final String PARAM_ESTIMATE_DISTANCE = "estimate_distance";
    private static final String PARAM_NUM_PER_PAGE = "number_per_page";
    private static final String PARAM_OBJECT_ID = "object_id";
    private static final String PARAM_OBJECT_TYPE = "object_type";
    public static final String FAVORITE_TYPE_DEAL = "deal";
    public static final String PARAM_SORT_BY = "sort_by";
    public static final String PARAM_SORT_TYPE = "sort_type";

    private static final String PARAM_DESTINATION_ID = "destination_id";
    private static final String PARAM_AUTHOR_ROLE = "author_role";
    private static final String PARAM_DESTINATION_ROLE = "destination_role";
    private static final String PARAM_CONTENT = "content";
    private static final String PARAM_RATE = "rate";
    private static final String PARAM_ATTACHED = "attachment";


    private static final String PARAM_NEW_PASS = "new_password";
    private static final String PARAM_CURRENT_PASS = "current_password";
    private static final String PARAM_START_LATITUDE = "start_lat";
    private static final String PARAM_END_LATITUDE = "end_lat";
    private static final String PARAM_START_LONGITUDE = "start_long";
    private static final String PARAM_END_LONGITUDE = "end_long";
    private static final String PARAM_DEAL_ID = "deal_id";
    private static final String PARAM_DEAL_NAME = "deal_name";
    private static final String PARAM_BUYER_ID = "buyer_id";
    private static final String PARAM_BUYER_NAME = "buyer_name";
    private static final String PARAM_RESERVATION_ID = "reservation_id";
    //    settings
    private static final String PARAM_NOTIFI = "notify";
    private static final String PARAM_NOTIFI_FAVOURITE = "notify_favourite";
    private static final String PARAM_NOTIFI_TRANSPORT = "notify_transport";
    private static final String PARAM_NOTIFI_FOOD = "notify_food";
    private static final String PARAM_NOTIFI_LABOR = "notify_labor";
    private static final String PARAM_NOTIFI_TRAVEL = "notify_travel";
    private static final String PARAM_NOTIFI_SHOPPING = "notify_shopping";
    private static final String PARAM_NOTIFI_NEW_AND_EVENT = "notify_news";
    private static final String PARAM_NOTIFI_NEARBY = "notify_nearby";
    //    Transaction
    private static final String PARAM_AMOUNT = "amount";
    private static final String PARAM_NOTE = "note";
    private static final String PARAM_METHOD_PAYMENT = "payment_method";
    private static final String PARAM_DESTINATION_EMAIL = "destination_email";
    private static final String PARAM_ID_TRANSACTION = "transaction_id";

    private static final String PARAM_TRIP_ID = "trip_id";
    private static final String PARAM_PASSENGER_ID = "passenger_id";
    private static final String PARAM_DRIVER_ID = "driver_id";
    private static final String PARAM_SEAT_COUNT = "seat_count";
    private static final String PARAM_START_LOCATION = "start_location";
    private static final String PARAM_END_LOCATION = "end_location";
    private static final String PARAM_TRANSACTION_ID = "transaction_id";
    private static final String PARAM_ACTUAL_FARE = "actual_fare";
    private static final String PARAM_ESTIMATE_FARE = "estimate_fare";
    private static final String PARAM_MODE = "mode";
    private static final String PARAM_FRIEND_ID = "friend_id";
    private static final String PARAM_LATLNG = "latlng";
    private static final String PARAM_SENSOR = "sensor";

    public static void login(final Context ctx, RequestQueue volleyQueue, String email, final String password,
                             String fullName, String avatar, String loginMethod, String ime, final ModelManagerListener listener) {
        String url = Apis.URL_LOGIN;

        Map<String, String> params = new HashMap<>();
        params.put(PARAM_USERNAME, email);
        params.put(PARAM_PASSWORD, password);
        params.put(PARAM_NAME, fullName);
        params.put(PARAM_AVATAR, avatar);
        params.put(PARAM_LOGIN_METHOD, loginMethod);
        params.put(PARAM_IME, ime);
        params.put(PARAM_GCM_ID, GlobalFunctions.getFCMToken(ctx));
        params.put(PARAM_TYPE, "1");

        Log.e(TAG, "gcm id: " +  GlobalFunctions.getFCMToken(ctx));

        new VolleyPost(ctx, true, false).request(volleyQueue, url, params, new IResponse() {
            @Override
            public void onResponse(final Object obj) {
                if (obj != null) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(obj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final ApiResponse response = new ApiResponse(jsonObject);
                    if (!response.isError()) {
                        final UserObj userObj = response.getDataObject(UserObj.class);

                        final QBUser qbUser = new QBUser();
                        qbUser.setLogin(userObj.getEmail());
                        qbUser.setPassword(AppController.getInstance().getString(R.string.QB_DEFAULT_PASSWORD));
                        qbUser.setFullName(userObj.getName());

                        if (userObj.getQb_id() != 0) {
                            // Save quickblox user
                            qbUser.setId(userObj.getQb_id());
                            SharedPreferencesUtil.saveQbUser(qbUser);

                            final MyProgressDialog progressDialog = new MyProgressDialog(ctx);
                            progressDialog.show();

                            ChatHelper.getInstance().login(qbUser, new QBEntityCallback<Void>() {
                                @Override
                                public void onSuccess(Void aVoid, Bundle bundle) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    listener.onSuccess(obj);
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            // Register quicblox account
                            // Create REST API session on QuickBlox
                            final MyProgressDialog progressDialog = new MyProgressDialog(ctx);
                            progressDialog.show();
                            QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(final QBUser user, Bundle bundle) {
                                    // Save quickblox user
                                    user.setPassword(AppController.getInstance().getString(R.string.QB_DEFAULT_PASSWORD));
                                    SharedPreferencesUtil.saveQbUser(user);

                                    ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid, Bundle bundle) {
                                            // Call api 'update quickblox id'
                                            updateQuickbloxId(ctx, String.valueOf(user.getId()),
                                                    response.getValueFromRoot(Args.TOKEN), new ModelManagerListener() {
                                                        @Override
                                                        public void onSuccess(Object object) {
                                                            JSONObject object1 = (JSONObject) object;
                                                            if (JSONParser.responseIsSuccess(object1)) {
                                                                if (progressDialog.isShowing()) {
                                                                    progressDialog.dismiss();
                                                                }

                                                                listener.onSuccess(obj);
                                                            } else {
                                                                // Call again one time if the first time is failed
                                                                updateQuickbloxId(ctx, String.valueOf(user.getId()),
                                                                        response.getValueFromRoot(Args.TOKEN), new ModelManagerListener() {
                                                                            @Override
                                                                            public void onSuccess(Object object) {
                                                                                JSONObject object1 = (JSONObject) object;
                                                                                if (JSONParser.responseIsSuccess(object1)) {
                                                                                    if (progressDialog.isShowing()) {
                                                                                        progressDialog.dismiss();
                                                                                    }

                                                                                    listener.onSuccess(obj);
                                                                                } else {
                                                                                    if (progressDialog.isShowing()) {
                                                                                        progressDialog.dismiss();
                                                                                    }

                                                                                    Toast.makeText(ctx, JSONParser.getMessage(object1), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onError() {
                                                                                if (progressDialog.isShowing()) {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }

                                                        @Override
                                                        public void onError() {
                                                            if (progressDialog.isShowing()) {
                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {
                                            if (progressDialog.isShowing()) {
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    e.printStackTrace();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(ctx, response.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void registerNormalAccount(Context ctx, RequestQueue volleyQueue, String fullName,
                                             String email, String phoneNumber, String password,
                                             final ModelManagerListener listener) {
        String url = Apis.URL_REGISTER_NORMAL_ACCOUNT;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_NAME, fullName);
        builder.appendQueryParameter(PARAM_USERNAME, email);
        builder.appendQueryParameter(PARAM_PHONE, phoneNumber);
        builder.appendQueryParameter(PARAM_PASSWORD, password);

        new VolleyGet(ctx, true, false).getJSONObject(builder, volleyQueue, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void getFacebookInfo(Context ctx, RequestQueue volleyQueue, String accessToken,
                                       final ModelManagerListener listener) {

        String url = Apis.URL_GET_FACEBOOK_INFO;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_FIELDS, "id,name,email,first_name,last_name,picture");
        builder.appendQueryParameter(PARAM_ACCESS_TOKEN, accessToken);

        new VolleyGet(ctx, true, false).getJSONObject(builder, volleyQueue, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }
// fragment my account - update profile normal - update profile pro

    public static void updateProfileNormal(Context context, DataPart file, String name, String phone, String address, final ModelManagerListener listener) {
        String url = Apis.URL_UPDATE_PROFILE_NORMAL;
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_NAME, name);
        params.put(PARAM_ADDRESS, address);
        params.put(PARAM_PHONE, phone);
        params.put(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        Map<String, DataPart> files = new HashMap<>();
        files.put(PARAM_AVATAR, file);
        new VolleyPost(context, true, false).multipartRequest(Volley.newRequestQueue(context), url, params, files, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void createPassword(Context context, String pass, final ModelManagerListener listener) {
        String url = Apis.URL_UPDATE_PROFILE_NORMAL;
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_PASSWORD, pass);

        params.put(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        new VolleyPost(context, true, false).multipartRequest(Volley.newRequestQueue(context), url, params, null, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void updateProfileProDriver(Context context, String name, DataPart avatar, String email, String phone, String address, boolean isDelivery, String brand, String model, String year, String carColor, String plate, String type_of_transport, String fuel_type, String year_km, String year_intend, String year_tax, String year_gara, String average_consumption, String fuel_unit_price, String year_insurance, String year_maintenance, String price_4_new_tyres, String year_unexpected, String sold_value, String bought_value, String fare, String fareType, DataPart imgCar, DataPart certification, final ModelManagerListener listener) {
        String url = Apis.URL_UPDATE_PROFILE_PRO;
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        params.put(PARAM_BUSINESS_NAME, name);
        params.put(PARAM_BUSINESS_ADDRESS, address);
        params.put(PARAM_BUSINESS_PHONE, phone);
        params.put(PARAM_BUSINESS_EMAIL, email);


        params.put(PARAM_BRAND, brand);
        params.put(PARAM_MODEL, model);
        params.put(PARAM_YEAR, year);
        params.put(PARAM_CAR_COLOR, carColor);
        params.put(PARAM_PLATE, plate);


        params.put(PARAM_TYPE_OF_TRANSPORT, type_of_transport);

        params.put(PARAM_FUEL, fuel_type);
        params.put(PARAM_YEAR_INTEND, year_intend);
        params.put(PARAM_YEAR_KM, year_km);
        params.put(PARAM_YEAR_TAX, year_tax);

        params.put(PARAM_YEAR_GARA, year_gara);
        params.put(PARAM_FUEL_UNIT_PRICE, fuel_unit_price);
        params.put(PARAM_AVERAGE_CONSUMPTION, average_consumption);
        params.put(PARAM_YEAR_INSURANCE, year_insurance);
        params.put(PARAM_YEAR_MAINTENANCE, year_maintenance);
        params.put(PARAM_PRICE_4_NEW_TYRES, price_4_new_tyres);

        params.put(PARAM_YEAR_UNEXPECTED, year_unexpected);
        params.put(PARAM_SOLD_VALUE, sold_value);
        params.put(PARAM_BOUGHT_VALUE, bought_value);
        params.put(PARAM_COST_YOU_CHARGE_PER_KM, fare);
        params.put(PARAM_FARE_TYPE, fareType);
        if (isDelivery) {
            params.put(PARAM_DELIVERY, "1");// value 1: delivery ; value 0: not delivery.
        } else {
            params.put(PARAM_DELIVERY, "0");
        }

        Map<String, DataPart> files = new HashMap<>();
        if (certification != null) {
            files.put(PARAM_CERTIFICATION, certification);
        }
        if (imgCar != null) {
            files.put(PARAM_CAR, imgCar);
        }
        if (avatar != null) {
            files.put(PARAM_AVATAR, avatar);
        }
        new VolleyPost(context, true, false).multipartRequest(Volley.newRequestQueue(context), url, params, files, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void updateProfilePro(Context context, String name, DataPart avatar, String email, String phone, String address, final ModelManagerListener listener) {
        String url = Apis.URL_UPDATE_PROFILE_PRO;
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        params.put(PARAM_BUSINESS_NAME, name);
        params.put(PARAM_BUSINESS_ADDRESS, address);
        params.put(PARAM_BUSINESS_PHONE, phone);
        params.put(PARAM_BUSINESS_EMAIL, email);
        Map<String, DataPart> files = new HashMap<>();

        if (avatar != null) {
            files.put(PARAM_AVATAR, avatar);
        }
        new VolleyPost(context, true, false).multipartRequest(Volley.newRequestQueue(context), url, params, files, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });

    }

    public static void getProfileDriver(Context context, String id, final ModelManagerListener listener) {
        String url = Apis.URL_GET_PROFILE_DRIVER;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_USER_ID, id);
        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void changePassword(Context context, String newPass, String currentPass, final ModelManagerListener listener) {
        String url = Apis.URL_CHANGE_PASS_WORD;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_NEW_PASS, newPass);
        builder.appendQueryParameter(PARAM_CURRENT_PASS, currentPass);
        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void forgotPassword(Context context, String email, final ModelManagerListener listener) {
        String url = Apis.URL_FORGET_PASSWORD;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_EMAIL, email);
        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void getDealList(Context ctx, String keyword, String isActive, String categoryId, String typeOfSearch, String distance, String num_per_page, double latitude,
                                   double longitude, String sortBy, String sortType, int page,
                                   final ModelManagerListener listener) {
        String url = Apis.URL_GET_DEAL_LIST;

        String is_active = (isActive == null) ? "1" : isActive;
        String sort_by = sortBy == null ? "" : sortBy;
        String sort_type = sortType == null ? "" : sortType;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        if (!categoryId.equals(DealCateObj.MY_FAVORITES)) {
            builder.appendQueryParameter(PARAM_CATEGORY_ID, categoryId);
        }

        if (!typeOfSearch.equals(Constants.SEARCH_FAVORIES)) {
            if (keyword != null) {
                if (is_active.equals("1")) {
                    builder.appendQueryParameter(PARAM_IS_ONLINE, is_active); // 0 is all, 1 is online
                }
            } else {
                builder.appendQueryParameter(PARAM_IS_ONLINE, is_active); // 0 is all, 1 is online
            }
        }


        builder.appendQueryParameter(PARAM_SORT_BY, sort_by);
        builder.appendQueryParameter(PARAM_SORT_TYPE, sort_type);
        builder.appendQueryParameter(PARAM_PAGE, page + "");
        builder.appendQueryParameter(PARAM_LAT, latitude + "");
        builder.appendQueryParameter(PARAM_LONG, longitude + "");
        if (keyword != null) {
            builder.appendQueryParameter(PARAM_KEY_WORD, keyword);
            if (categoryId.equals(DealCateObj.MY_FAVORITES)) {
                builder.appendQueryParameter(PARAM_SEARCH_TYPE, Constants.SEARCH_FAVORIES);
            } else {
                builder.appendQueryParameter(PARAM_SEARCH_TYPE, Constants.SEARCH_NEARBY);
            }

        } else {
            builder.appendQueryParameter(PARAM_SEARCH_TYPE, typeOfSearch);
        }
        if (distance != null) {
            builder.appendQueryParameter(PARAM_DISTANCE, distance);
        }
        if (num_per_page != null) {
            builder.appendQueryParameter(PARAM_NUM_PER_PAGE, num_per_page);
        }

        new VolleyGet(ctx, false, false).getJSONObject(builder, Volley.newRequestQueue(ctx), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void createDeal(Context ctx, String categoryId, String idDeal, String name, String price, String typeDiscount, float discount, String address, String duration,
                                  String description, double latitude, double longitude, int isPremium, int renew, DataPart image, DataPart file,
                                  final ModelManagerListener listener) {
        String url = Apis.URL_DEAL_ACTION;
        Map<String, String> params = new HashMap<>();
        if (idDeal != null) {
            params.put(PARAM_DEAL_ID, idDeal);
            params.put(PARAM_ACTION, PARAM_ACTION_UPDATE);
        } else {
            params.put(PARAM_ACTION, PARAM_ACTION_CREATE);
        }
        params.put(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        params.put(PARAM_NAME, name);
        params.put(PARAM_DESCRIPTION, description);
        params.put(PARAM_PRICE, price);
        params.put(PARAM_DISCOUNT_TYPE, typeDiscount);

        params.put(PARAM_DISCOUNT_PRICE, String.valueOf(discount));


        params.put(PARAM_ADDRESS, address);
        if (duration != null && !duration.isEmpty()) {
            params.put(PARAM_DURATION, duration);
        }
        params.put(PARAM_IS_PREMIUM, String.valueOf(isPremium));
        params.put(PARAM_AUTO_RENEW, String.valueOf(renew));
        params.put(PARAM_LAT, String.valueOf(latitude));
        params.put(PARAM_LONG, String.valueOf(longitude));
        params.put(PARAM_CATEGORY_ID, categoryId);

        Map<String, DataPart> files = new HashMap<>();
        if (image != null)
            files.put(PARAM_IMAGE, image);
        if (file != null) {
            files.put(PARAM_ATTACHED, file);
        }

        new VolleyPost(ctx, true, false).multipartRequest(Volley.newRequestQueue(ctx), url, params, files, new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });

    }

    public static void getDetailDeal(Context context, String id, final ModelManagerListener listener) {
        String url = Apis.URL_GET_DEAL_DETAIL;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_DEAL_ID, id);
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void favorite(Context context, String id, String type, final ModelManagerListener listener) {
        String url = Apis.URL_FAVORITE;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_OBJECT_ID, id);
        builder.appendQueryParameter(PARAM_OBJECT_TYPE, type);
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void postReview(Context context, String destinationId, String destinationRole, String authorRole,
                                  String objectId, String objectType, String content, String rate, final ModelManagerListener listener) {
        String url = Apis.URL_POST_REVIEW;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_DESTINATION_ID, destinationId);
        builder.appendQueryParameter(PARAM_DESTINATION_ROLE, destinationRole);
        builder.appendQueryParameter(PARAM_AUTHOR_ROLE, authorRole);
        builder.appendQueryParameter(PARAM_OBJECT_ID, objectId);
        builder.appendQueryParameter(PARAM_OBJECT_TYPE, objectType);
        builder.appendQueryParameter(PARAM_CONTENT, content);
        builder.appendQueryParameter(PARAM_RATE, rate);

        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void getReview(Context context, String objectId, String objectType, int page, final ModelManagerListener listener) {
        String url = Apis.URL_GET_REVIEW;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_OBJECT_ID, objectId);
        builder.appendQueryParameter(PARAM_OBJECT_TYPE, objectType);
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_PAGE, page + "");

        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });

    }

    public static void updateQuickbloxId(Context context, String qbId, String token, final ModelManagerListener listener) {
        String url = Apis.URL_UPDATE_PROFILE_NORMAL;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_QB_USER_ID, qbId);
        builder.appendQueryParameter(PARAM_TOKEN, token);

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        }
                    }
                });
    }

    public static void getNearbyDrivers(Context context, String token, String transportType, LatLng pickup,
                                        LatLng destination, final ModelManagerListener listener) {
        String url = Apis.URL_GET_NEARBY_DRIVERS;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, token);
        builder.appendQueryParameter(PARAM_TYPE, transportType);
        builder.appendQueryParameter(PARAM_START_LATITUDE, String.valueOf(pickup.latitude));
        builder.appendQueryParameter(PARAM_START_LONGITUDE, String.valueOf(pickup.longitude));

        if (destination != null) {
            builder.appendQueryParameter(PARAM_END_LATITUDE, String.valueOf(destination.latitude));
            builder.appendQueryParameter(PARAM_END_LONGITUDE, String.valueOf(destination.longitude));
        }

        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void settings(Context context, String notifi, String notifiFav, String notifiTran, String notifiFood, String notifiLabor, String notifiTravel, String notifiShopping, String notifiNews, String notifiNear, final ModelManagerListener listener) {
        String url = Apis.URL_SETTINGS;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        if (!notifi.isEmpty()) {
            builder.appendQueryParameter(PARAM_NOTIFI, notifi);
        }
        if (!notifiFav.isEmpty()) {

            builder.appendQueryParameter(PARAM_NOTIFI_FAVOURITE, notifiFav);
        }
        if (!notifiTran.isEmpty()) {
            builder.appendQueryParameter(PARAM_NOTIFI_TRANSPORT, notifiTran);
        }
        if (!notifiFood.isEmpty()) {
            builder.appendQueryParameter(PARAM_NOTIFI_FOOD, notifiFood);
        }

        if (!notifiLabor.isEmpty()) {

            builder.appendQueryParameter(PARAM_NOTIFI_LABOR, notifiLabor);
        }
        if (!notifiTravel.isEmpty()) {

            builder.appendQueryParameter(PARAM_NOTIFI_TRAVEL, notifiTravel);
        }
        if (!notifiShopping.isEmpty()) {
            builder.appendQueryParameter(PARAM_NOTIFI_SHOPPING, notifiShopping);
        }
        if (!notifiNews.isEmpty()) {
            builder.appendQueryParameter(PARAM_NOTIFI_NEW_AND_EVENT, notifiNews);
        }
        if (!notifiNear.isEmpty()) {
            builder.appendQueryParameter(PARAM_NOTIFI_NEARBY, notifiNear);
        }

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context), new IResponse() {
            @Override
            public void onResponse(Object response) {
                if (response != null) {
                    listener.onSuccess(response);
                } else {
                    listener.onError();
                }
            }
        });
    }

    public static void transaction(Context context, String idTransaction, String amount, String action, String note, String methodPayment, String email, final ModelManagerListener listener) {
        String url = Apis.URL_TRANSACTION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_AMOUNT, amount);
        builder.appendQueryParameter(PARAM_ACTION, action);
        builder.appendQueryParameter(PARAM_NOTE, note);
        if (!methodPayment.isEmpty())
            builder.appendQueryParameter(PARAM_METHOD_PAYMENT, methodPayment);
        builder.appendQueryParameter(PARAM_DESTINATION_EMAIL, email);
        if (!idTransaction.isEmpty()) {
            builder.appendQueryParameter(PARAM_ID_TRANSACTION, idTransaction);
        }

        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void getHistory(Context context, String page, final ModelManagerListener listener) {
        String url = Apis.URL_HISTORY;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_PAGE, page);
        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void deleteHistory(Context context, String id, final ModelManagerListener listener) {
        String url = Apis.URL_DELETE_HISTORY;
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        if (!id.isEmpty())
            builder.appendQueryParameter(PARAM_TRANSACTION_ID, id);
        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });

    }

    public static void getReservationList(Context context, String typeOfSearch, String dealId, int page,
                                          final ModelManagerListener listener) {

        String url = Apis.URL_GET_RESERVATION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_SEARCH_TYPE, typeOfSearch);
        builder.appendQueryParameter(PARAM_DEAL_ID, dealId);
        builder.appendQueryParameter(PARAM_PAGE, String.valueOf(page));

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void getTripHistoryList(Context context, int page, String searchType,
                                          boolean showProgress, final ModelManagerListener listener) {
        String url = Apis.URL_GET_TRIP_HISTORY;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_PAGE, String.valueOf(page));
        builder.appendQueryParameter(PARAM_SEARCH_TYPE, searchType);

        new VolleyGet(context, showProgress, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }


    public static void activateDriverMode(final Context context, final String mode, int duration,
                                          final ModelManagerListener listener) {
        String url = Apis.URL_ACTIVATE_DRIVER_MODE;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_MODE, mode);
        builder.appendQueryParameter(PARAM_DURATION, String.valueOf(duration));

        Log.e(TAG,builder.toString());
        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            JSONObject jsonObject = (JSONObject) response;
                            if (JSONParser.responseIsSuccess(jsonObject)) {
                                if (mode.equals(Constants.OFF)) {
//                                    LocationService.start(context, LocationService.STOP_REQUESTING_LOCATION);
                                } else {
                                    LocationService1.start(context, LocationService1.REQUEST_LOCATION);
                                }
                            }
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void manualReservation(Context context, String dealId, String action, String dealName,
                                         String buyerId, String buyerName, String reservationId, String paymentMethod,
                                         String price, float rate, String comment, final ModelManagerListener listener) {
        String url = Apis.URL_RESERVATION_ACTION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_DEAL_ID, dealId);
        builder.appendQueryParameter(PARAM_ACTION, action);
        builder.appendQueryParameter(PARAM_DEAL_NAME, dealName);
        builder.appendQueryParameter(PARAM_BUYER_ID, buyerId);
        builder.appendQueryParameter(PARAM_BUYER_NAME, buyerName);
        builder.appendQueryParameter(PARAM_RESERVATION_ID, reservationId);
        builder.appendQueryParameter(PARAM_METHOD_PAYMENT, paymentMethod);
        builder.appendQueryParameter(PARAM_PRICE, price);
        builder.appendQueryParameter(PARAM_RATE, String.valueOf(rate));
        builder.appendQueryParameter(PARAM_CONTENT, comment);

        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void getAccounting(Context context, String type, final ModelManagerListener listener) {
        String url = Apis.URL_GET_ACCOUNTING;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_TYPE, type); // deal or trip

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void manualTrip(Context context, String action, TransportDealObj obj, String paymentMethod,
                                  String price, float rate, String comment, final ModelManagerListener listener) {
        String url = Apis.URL_TRIP_ACTION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_ACTION, action);

        if (!action.equals(TransportDealObj.ACTION_CREATE)) {
            builder.appendQueryParameter(PARAM_TRIP_ID, obj.getId());
        }

        if (action.equals(TransportDealObj.ACTION_CREATE)) {
            builder.appendQueryParameter(PARAM_PASSENGER_ID, obj.getPassengerId());
            builder.appendQueryParameter(PARAM_DRIVER_ID, obj.getDriverId());
            builder.appendQueryParameter(PARAM_SEAT_COUNT, String.valueOf(obj.getPassengerQuantity()));
            builder.appendQueryParameter(PARAM_START_LATITUDE, String.valueOf(obj.getLatLngPickup().latitude));
            builder.appendQueryParameter(PARAM_START_LONGITUDE, String.valueOf(obj.getLatLngPickup().longitude));
            builder.appendQueryParameter(PARAM_START_LOCATION, obj.getPickup());
            builder.appendQueryParameter(PARAM_END_LATITUDE, String.valueOf(obj.getLatLngDestination().latitude));
            builder.appendQueryParameter(PARAM_END_LONGITUDE, String.valueOf(obj.getLatLngDestination().longitude));
            builder.appendQueryParameter(PARAM_END_LOCATION, obj.getDestination());
            builder.appendQueryParameter(PARAM_ESTIMATE_FARE, String.valueOf(obj.getEstimateFare()));
            builder.appendQueryParameter(PARAM_ESTIMATE_DURATION, String.valueOf(obj.getRouteDuration() + obj.getDuration()));
            builder.appendQueryParameter(PARAM_ESTIMATE_DISTANCE, String.valueOf(obj.getRouteDistance()));
        }

        if (action.equals(TransportDealObj.ACTION_FINISH)) {
            builder.appendQueryParameter(PARAM_METHOD_PAYMENT, paymentMethod);
            builder.appendQueryParameter(PARAM_ACTUAL_FARE, price);
            builder.appendQueryParameter(PARAM_RATE, String.valueOf(rate));
            builder.appendQueryParameter(PARAM_CONTENT, comment);
        }

        new VolleyGet(context, true, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void deleteAllTrip(Context context, final ModelManagerListener listener) {
        String url = Apis.URL_DELETE_ALL_TRIP;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void getSettingUtility(Context context, final ModelManagerListener listener) {
        String url = Apis.URL_SETTING_UTILITY;

        Uri.Builder builder = Uri.parse(url).buildUpon();

        new VolleyGet(context, false, false).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void updateLocation(final Context context, LatLng latLng) {
        String url = Apis.URL_UPDATE_LOCATION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_LAT, String.valueOf(latLng.latitude));
        builder.appendQueryParameter(PARAM_LONG, String.valueOf(latLng.longitude));

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                    }
                });
    }

    public static void getDriverLocation(final Context context, String tripId, final ModelManagerListener listener) {
        String url = Apis.URL_GET_DRIVER_LOCATION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_TRIP_ID, tripId);

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        }
                    }
                });
    }

    public static void getProfile(final Context context, String id, final ModelManagerListener listener) {
        String url = Apis.URL_PROFILE;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_USER_ID, id);

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        }
                    }
                });
    }
    public static void getOnline(final Context context, final ModelManagerListener listener) {
        String url = Apis.URL_ONLINE;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        new VolleyGet(context, false, true).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        }
                    }
                });
    }

    public static void activateDeal(Context context, String dealId, String mode,
                                    final ModelManagerListener listener) {
        String url = Apis.URL_ACTIVATE_DEAL;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_DEAL_ID, dealId);
        builder.appendQueryParameter(PARAM_MODE, mode);

        new VolleyGet(context, true, false).getJSONObject(builder, Volley.newRequestQueue(context),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void updateDurationOfDeal(Context ctx, String dealId, String duration,
                                            final ModelManagerListener listener) {
//        Toast.makeText(ctx, "This feature is under development", Toast.LENGTH_SHORT).show();

        String url = Apis.URL_DEAL_ACTION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_DEAL_ID, dealId);
        builder.appendQueryParameter(PARAM_DURATION, duration);
        builder.appendQueryParameter(PARAM_ACTION, PARAM_ACTION_ONLINE);

        new VolleyGet(ctx, true, false).getJSONObject(builder, Volley.newRequestQueue(ctx),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void getContacts(Context ctx, boolean isProgress, final ModelManagerListener listener) {
        String url = Apis.URL_GET_CONTACTS;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());

        new VolleyGet(ctx, isProgress, true).getJSONObject(builder, Volley.newRequestQueue(ctx),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void manualContacts(Context ctx, String action, String friendId, boolean showProgress,
                                      final ModelManagerListener listener) {
        String url = Apis.URL_CONTACTS_ACTION;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_TOKEN, DataStoreManager.getUser().getToken());
        builder.appendQueryParameter(PARAM_ACTION, action);
        builder.appendQueryParameter(PARAM_FRIEND_ID, friendId);

        new VolleyGet(ctx, showProgress, true).getJSONObject(builder, Volley.newRequestQueue(ctx),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }

    public static void getAddressByLatlng(Context ctx, LatLng latLng, final ModelManagerListener listener) {
        String url = Apis.URL_GET_ADDRESS_BY_LATLNG;

        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter(PARAM_LATLNG, latLng.latitude + "," + latLng.longitude);
        builder.appendQueryParameter(PARAM_SENSOR, "true");

        new VolleyGet(ctx, false, true).getJSONObject(builder, Volley.newRequestQueue(ctx),
                new IResponse() {
                    @Override
                    public void onResponse(Object response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                });
    }
}
