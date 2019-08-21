package com.suusoft.locoindia.utils.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.configs.ChatConfigs;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.utils.NetworkUtility;
import com.suusoft.locoindia.view.activities.SplashLoginActivity;

public class LocationService1 extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private String mAction;
    private static final String ACTION = "action";
    public static final String REQUEST_LOCATION = "requestLocation";
    public static final String STOP_REQUESTING_LOCATION = "stopRequestingLocation";
    private static int NOTIFICATION_LOCATION_PERMISSION = 1000;
    private static int NOTIFICATION_TURN_ON_LOCATION = 1001;
    private static final String TAG = LocationService1.class.getSimpleName();
    private LatLng mylatLng, mFromLatLng;
    private static int REQUEST_CODE = 1000;

    public LocationService1(String name) {
        super(name);
    }

    public LocationService1() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mAction = intent.getStringExtra(ACTION);

        if (mAction != null) {
            if (mAction.equals(REQUEST_LOCATION)) {
                requestLocation();
            } else if (mAction.equals(STOP_REQUESTING_LOCATION)) {
//                stopRequestingLocation();
            }
        }

        checkLocation();
    }


    private void requestLocation() {
            if (GlobalFunctions.isMarshmallow()) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (MapsUtil.locationIsEnable(this)) {
                        initEnableLocation();
                        Log.e("KK","Service");
                    } else {
//                        sendNotification(ChatConfigs.NOTIFICATION_TURN_ON_LOCATION);
                    }
                } else {
                    sendNotification(ChatConfigs.NOTIFICATION_LOCATION_PERMISSION);
                }
            } else {
                if (MapsUtil.locationIsEnable(this)) {
                   initEnableLocation();
                } else {
//                    sendNotification(ChatConfigs.NOTIFICATION_TURN_ON_LOCATION);
                }
            }
    }
    private void checkLocation() {
        if (NetworkUtility.isNetworkAvailable()) {
            if (DataStoreManager.getUser() != null && DataStoreManager.getUser().getDriverData() != null) {
                if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                    if (GlobalFunctions.isMarshmallow()) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            if (!MapsUtil.locationIsEnable(LocationService1.this)) {
                                sendNotification(ChatConfigs.NOTIFICATION_TURN_ON_LOCATION);
                                checkLocation();
                            }
                        } else {
                            sendNotification(ChatConfigs.NOTIFICATION_LOCATION_PERMISSION);
                            checkLocation();
                        }
                    } else {
                        if (!MapsUtil.locationIsEnable(LocationService1.this)) {
                            sendNotification(ChatConfigs.NOTIFICATION_TURN_ON_LOCATION);
                            checkLocation();
                        }
                    }
                }
            }
        } else {
            Toast.makeText(LocationService1.this, R.string.msg_no_network_to_update_location, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocation(LatLng mylatLng) {
        if (mylatLng != null) {
            if (NetworkUtility.isNetworkAvailable()) {
                if (GlobalFunctions.isMarshmallow()) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (MapsUtil.locationIsEnable(this)) {
                            ModelManager.updateLocation(this, mylatLng);
                        }
                    }
                } else {
                    if (MapsUtil.locationIsEnable(this)) {
                        ModelManager.updateLocation(this, mylatLng);
                    }
                }
            } else {
                Toast.makeText(this, R.string.msg_no_network_to_update_location, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendLocationToServer();
    }

    private void initEnableLocation() {
        if (MapsUtil.locationIsEnable(this)) {
            initGetLocation();
//            ((MainActivity) self).updateDriverLocation();
        } else {
//            MapsUtil.displayLocationSettingsRequest((AppCompatActivity) self, RC_TURN_ON_LOCATION);
        }
    }




    private FusedLocationProviderClient mFusedLocationClient;

    private void initGetLocation() {
        Log.e(TAG, "initGetLocation");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        Log.e(TAG, "addOnSuccessListener onSuccess ");
                        if (location != null) {
                            Log.d("YYY", location + "");
                            // Logic to handle location object
                            mylatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            Log.e("location", "lat " + location.getLatitude() + " long " + location.getLongitude());
                            updateLocation(mylatLng);
                            // Determine whether a Geocoder is available.
                            if (!Geocoder.isPresent()) {
//                                Toast.makeText(this, R.string.service_not_available, Toast.LENGTH_LONG).show();
                                return;
                            }
                        } else {
                            getLastLocation();
                        }
                    }
                });
    }

    private LocationManager mLocationManager;

    @SuppressLint("MissingPermission")
    private void sendLocationToServer() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, mLocationListener);
    }

    //update location auto
    private Location locationOld;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Log.e("location", "onLocationChanged");
            if (locationOld != null)
                Log.e("location", "locationOld " + locationOld.getLatitude() + "  " + locationOld.getLongitude());
            Log.e("location", "location " + location.getLatitude() + "  " + location.getLongitude());

            //if(isPointChanged(locationOld, location)){
            if (isPointChanged(locationOld, location)) {

                Log.e("location", "onLocationChanged true");


                updateLocation(new LatLng(location.getLatitude(),location.getLongitude()));

                locationOld = location;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            Log.e("location", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("location", "onProviderEnabled");

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("location", "onProviderDisabled");

        }
    };

    private boolean isPointChanged(Location lOld, Location lNew){
        boolean isChange = false;

        if (lOld==null)
            return true;

        if (isAxisChanged(lOld.getLatitude(), lNew.getLatitude())
                || isAxisChanged(lOld.getLongitude(), lNew.getLongitude())){
            isChange = true;
        }

        return isChange;

    }
    private boolean isAxisChanged(double toadoCu, double toadoMoi){
        boolean isChanged = false;

        if (getNumber(toadoCu)!= getNumber(toadoMoi))
            isChanged = true;

        Log.e("location", "toa do cu = "  + getNumber(toadoCu) + " toa do moi = " + getNumber(toadoMoi) );

        return isChanged;
    }
    private int getNumber (double d){
        int number =(int)(d*1000) ;
        return number;
    }

    public static void start(Context context, String action) {
        Intent intent = new Intent(context, LocationService1.class);
        intent.putExtra(ACTION, action);
        context.startService(intent);
    }

    private void sendNotification(String notificationType) {
        Intent intent = new Intent(this, SplashLoginActivity.class);
        intent.putExtra(Args.NOTIFICATION_TYPE, notificationType);

        REQUEST_CODE++;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String messageBody = getString(R.string.msg_remind_user_grant_location);
        int notificationId = NOTIFICATION_LOCATION_PERMISSION;
        if (notificationType.equals(ChatConfigs.NOTIFICATION_TURN_ON_LOCATION)) {
            notificationId = NOTIFICATION_TURN_ON_LOCATION;
            messageBody = getString(R.string.msg_remind_user_turn_on_location);
        }

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
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
