package com.suusoft.locoindia.view.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.PacketUtility;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.configs.ChatConfigs;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.quickblox.conversation.utils.DialogUtil;
import com.suusoft.locoindia.receiver.Action;
import com.suusoft.locoindia.receiver.HandleReceiverDeals;
import com.suusoft.locoindia.utils.map.LocationService1;
import com.suusoft.locoindia.view.fragments.ContactFragment;
import com.suusoft.locoindia.view.fragments.MyAccountFragment;
import com.suusoft.locoindia.view.fragments.PayFragment;
import com.suusoft.locoindia.view.fragments.SettingsFragment;
import com.suusoft.locoindia.view.fragments.FindTaxiFragment;
import com.suusoft.locoindia.view.fragments.WebViewFragment;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IChangeTitle;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.MyProgressDialog;
import com.suusoft.locoindia.network1.NetworkUtility;
import com.suusoft.locoindia.objects.ContactObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.objects.SettingsObj;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.quickblox.QbAuthUtils;
import com.suusoft.locoindia.quickblox.QbDialogHolder;
import com.suusoft.locoindia.quickblox.SharedPreferencesUtil;
import com.suusoft.locoindia.quickblox.chat.ChatHelper;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.ImageUtil;
import com.suusoft.locoindia.utils.map.IMaps;
import com.suusoft.locoindia.utils.map.LocationService;
import com.suusoft.locoindia.utils.map.MapsUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.suusoft.locoindia.globals.Args.RC_TURN_ON_LOCATION;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,
        IChangeTitle{

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_LOCATION_PERMISSION_TO_UPDATE_DRIVER_LOCATION = 1;
    private static final int RC_TURN_ON_LOCATION_TO_UPDATE_DRIVER_LOCATION = 2;
    private static final int RC_LOCATION_PERMISSION_TO_UPDATE_USER_LOCATION = 3;
    private static final int RC_TURN_ON_LOCATION_TO_UPDATE_USER_LOCATION = 4;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;

    private GoogleMap mMap;
    private SettingsFragment mSettingsFragment;
    private MyAccountFragment myAccountFragment;
    private FindTaxiFragment mTransportFragment;
    private ContactFragment mContactFragment;
    private PayFragment mPayFragment;
    private WebViewFragment mWebViewFragment;
    private LatLng mylatLng;

    private GoogleApiClient mGoogleApiClient;
    private int mSelectedNav;
    private boolean userLocationIsUpdated;

    //show diaglog while nhận push deals
    private HandleReceiverDeals handleReceiverDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleApiClient();
        initEnableLocation();
        if (DataStoreManager.getUser() != null && (DataStoreManager.getUser().getToken() != null
                && !DataStoreManager.getUser().getToken().equals(""))) {
            initSession(savedInstanceState);
            initDialogsListener();
            initPushManager();
        }
        handleReceiverDeals = new HandleReceiverDeals(self);
        //initReceiver();

        // Start location service in some cases(driver closes app without deactivating)
        updateDriverLocation();
        updateUserLocation();

        // Get contacts from scratch
        getContacts();
        setSelectedOnNavigation(0);
    }

    @Override
    void inflateLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                AppUtil.hideSoftKeyboard(MainActivity.this);
                updateMenuHeader();
            }
        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // Set the first fragment
        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));

        // set updating header menu
        AppController.getInstance().setUserUpdated(true);
        updateMenuHeader();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RC_LOCATION_PERMISSION_TO_UPDATE_USER_LOCATION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        updateUserLocation();
                    } else {
                        showPermissionsReminder(RC_LOCATION_PERMISSION_TO_UPDATE_USER_LOCATION, true);
                    }
                }
                break;
            case RC_LOCATION_PERMISSION_TO_UPDATE_DRIVER_LOCATION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (MapsUtil.locationIsEnable(self)) {
                            updateDriverLocation();
                        } else {
                            turnOnLocationReminder(RC_TURN_ON_LOCATION_TO_UPDATE_DRIVER_LOCATION, false);
                        }
                    } else {
                        showPermissionsReminder(RC_LOCATION_PERMISSION_TO_UPDATE_DRIVER_LOCATION, true);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    private void updateMenuHeader() {
        if (AppController.getInstance().isUserUpdated()) {
            View view = mNavigationView.getHeaderView(0);
            ImageView imageView = (ImageView) view.findViewById(R.id.civ_avatar);
            TextView tvName = (TextView) view.findViewById(R.id.lbl_name);
            TextView tvEmail = (TextView) view.findViewById(R.id.lbl_email);
            UserObj userObj = DataStoreManager.getUser();
            if (userObj != null) {
                tvName.setText(userObj.getName());
                tvEmail.setText(userObj.getEmail());
                ImageUtil.setImage(self, imageView, userObj.getAvatar());
            }

            AppController.getInstance().setUserUpdated(false);
        }
    }

    @Override
    void initControl() {
        mNavigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void getExtraValues() {
        super.getExtraValues();
        Log.e("EE", "EE: MAIN");
        getExtra(getIntent());
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("EE", "EE: NEWINTENT MAIN");
        getExtra(intent);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setFragment(id);

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.onResume();
        DataStoreManager.saveScreenMain(true);
        handleReceiverDeals.registerReceiver();
        Log.e(TAG,"onResume Activity");
    }

    @Override
    protected void onDestroy() {
        // Reset recent chat list
        DataStoreManager.clearRecentChat();

        // Keep conversation status
        DataStoreManager.saveConversationStatus(false);

        super.onDestroy();
    }

    private void setFragment(final int navId) {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (navId == R.id.nav_transport_deals) {
            setSelectedOnNavigation(0);
            if (mSelectedNav != navId) {

                if (mTransportFragment == null) {
                    mTransportFragment = FindTaxiFragment.newInstance(this);
                }
                fragmentTransaction.replace(R.id.frl_main, mTransportFragment).commit();
                setTitle(R.string.find_taxi);
                mSelectedNav = navId;
            }
        } else if (navId == R.id.nav_chat) {
                setSelectedOnNavigation(1);
            if (mSelectedNav != navId) {
                if (mContactFragment == null) {
                    mContactFragment = ContactFragment.newInstance();
                }
                fragmentTransaction.replace(R.id.frl_main, mContactFragment).commit();
                setTitle(R.string.taxi_chat);
                mSelectedNav = navId;
            }
        } else if (navId == R.id.nav_pay) {
                setSelectedOnNavigation(2);
            if (mSelectedNav != navId) {
                if (mPayFragment == null) {
                    mPayFragment = PayFragment.newInstance();
                }
                fragmentTransaction.replace(R.id.frl_main, mPayFragment).commit();

                setTitle(R.string.pay);
                mSelectedNav = navId;
            }
        } else if (navId == R.id.nav_profile) {
                setSelectedOnNavigation(3);
            if (mSelectedNav != navId) {
                if (myAccountFragment == null) {
                    myAccountFragment = MyAccountFragment.newInstance();
                }
                fragmentTransaction.replace(R.id.frl_main, myAccountFragment).commit();

                setTitle(R.string.my_account);

                mSelectedNav = navId;
            }
        } else if (navId == R.id.nav_share) {
                setSelectedOnNavigation(4);
            AppUtil.share(this, "http://play.google.com/store/apps/details?id=" + new PacketUtility().getPacketName());
        } else if (navId == R.id.nav_help) {
            onOpenScreenHelp(navId, fragmentTransaction);

        } else if (navId == R.id.nav_settings) {
            if (mSelectedNav != navId) {
                if (mSettingsFragment == null) {
                    mSettingsFragment = SettingsFragment.newInstance();
                }
                fragmentTransaction.replace(R.id.frl_main, mSettingsFragment).commit();

                setTitle(R.string.settings);

                mSelectedNav = navId;
            }
                setSelectedOnNavigation(7);
        } else if (navId == R.id.nav_about_us) {

            onOpenScreenAboutUs(navId, fragmentTransaction);

        } else if (navId == R.id.nav_log_out) {
            logout();
        } else if (navId == R.id.nav_faqs) {

            onOpenScreenFAQs(navId, fragmentTransaction);

        }
    }

    private void onOpenScreenFAQs(final int navId, final FragmentTransaction fragmentTransaction) {
        if (mSelectedNav != navId) {
            SettingsObj setting = DataStoreManager.getSettingUtility();
            if (setting == null) {
                if (NetworkUtility.getInstance(this).isNetworkAvailable()) {
                    ModelManager.getSettingUtility(this, new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            org.json.JSONObject jsonObject = (JSONObject) object;
                            ApiResponse apiResponse = new ApiResponse(jsonObject);
                            if (!apiResponse.isError()) {
                                DataStoreManager.saveSettingUtility(jsonObject.toString());
                                SettingsObj utitlityObj = apiResponse.getDataObject(SettingsObj.class);
//                                    openWebView(getString(R.string.faq), utitlityObj.getFaq());

                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.KEY_URL, utitlityObj.getFaq());
                                mWebViewFragment = WebViewFragment.newInstance(bundle);

                                fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                                setTitle(getString(R.string.faq));

                                mSelectedNav = navId;
//                                    openWebView(getString(R.string.about_us), DataStoreManager.getSettingUtility().getAbout());
                            }

                        }

                        @Override
                        public void onError() {
                        }
                    });
                } else {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
                }
            } else {

                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_URL, DataStoreManager.getSettingUtility().getFaq());
                mWebViewFragment = WebViewFragment.newInstance(bundle);

                fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                setTitle(getString(R.string.faq));

                mSelectedNav = navId;
//                    openWebView(getString(R.string.faq), DataStoreManager.getSettingUtility().getFaq());
            }
        }
        setSelectedOnNavigation(6);
    }

    private void onOpenScreenAboutUs(final int navId, final FragmentTransaction fragmentTransaction) {
        if (mSelectedNav != navId) {
            SettingsObj setting = DataStoreManager.getSettingUtility();
            if (setting == null) {
                if (NetworkUtility.getInstance(this).isNetworkAvailable()) {
                    ModelManager.getSettingUtility(this, new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            org.json.JSONObject jsonObject = (JSONObject) object;
                            ApiResponse apiResponse = new ApiResponse(jsonObject);
                            if (!apiResponse.isError()) {
                                DataStoreManager.saveSettingUtility(jsonObject.toString());
                                SettingsObj utitlityObj = apiResponse.getDataObject(SettingsObj.class);
//                                    openWebView(getString(R.string.about_us), utitlityObj.getAbout());

                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.KEY_URL, utitlityObj.getAbout());
                                mWebViewFragment = WebViewFragment.newInstance(bundle);

                                fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                                setTitle(getString(R.string.about_us));

                                mSelectedNav = navId;
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
                } else {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
                }
            } else {

                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_URL, DataStoreManager.getSettingUtility().getAbout());
                mWebViewFragment = WebViewFragment.newInstance(bundle);

                fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                setTitle(getString(R.string.about_us));
                mSelectedNav = navId;
//                    openWebView(getString(R.string.about_us), DataStoreManager.getSettingUtility().getAbout());
            }
        }
        setSelectedOnNavigation(5);
    }

    private void onOpenScreenHelp(final int navId,final FragmentTransaction fragmentTransaction) {
        if (mSelectedNav != navId) {
            SettingsObj setting = DataStoreManager.getSettingUtility();
            if (setting == null) {
                if (NetworkUtility.getInstance(this).isNetworkAvailable()) {
                    ModelManager.getSettingUtility(this, new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            org.json.JSONObject jsonObject = (JSONObject) object;
                            ApiResponse apiResponse = new ApiResponse(jsonObject);
                            if (!apiResponse.isError()) {
                                DataStoreManager.saveSettingUtility(jsonObject.toString());
                                SettingsObj utitlityObj = apiResponse.getDataObject(SettingsObj.class);
                                //openWebView(getString(R.string.help), utitlityObj.getHelp());


                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.KEY_URL, utitlityObj.getHelp());
                                mWebViewFragment = WebViewFragment.newInstance(bundle);

                                fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                                setTitle(getString(R.string.help));

                                mSelectedNav = navId;
                            } else {
                                AppUtil.showToast(getApplicationContext(), apiResponse.getMessage());
                            }
                        }

                        @Override
                        public void onError() {
                            AppUtil.showToast(getApplicationContext(), "Error!");
                        }
                    });
                } else {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
                }

            } else {

                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_URL, DataStoreManager.getSettingUtility().getHelp());
                mWebViewFragment = WebViewFragment.newInstance(bundle);

                fragmentTransaction.replace(R.id.frl_main, mWebViewFragment).commit();

                setTitle(getString(R.string.help));

                mSelectedNav = navId;
                //openWebView(getString(R.string.help), DataStoreManager.getSettingUtility().getHelp());
            }
        }
        setSelectedOnNavigation(8);
    }


    private void setSelectedOnNavigation(int i) {
        mNavigationView.getMenu().getItem(i).setCheckable(true);
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        new FindTaxiFragment().onActivityResult(requestCode,resultCode,data);

        if (requestCode == RC_TURN_ON_LOCATION) {
            if (MapsUtil.locationIsEnable(self)) {
                initEnableLocation();
                    Log.e("EE","4");
            } else {
                turnOnLocationReminder(RC_TURN_ON_LOCATION, true);
            }
        } else if (requestCode == RC_TURN_ON_LOCATION_TO_UPDATE_DRIVER_LOCATION) {
            if (MapsUtil.locationIsEnable(self)) {
                updateDriverLocation();
            } else {
                turnOnLocationReminder(RC_TURN_ON_LOCATION_TO_UPDATE_DRIVER_LOCATION, false);
            }
        } else if (requestCode == RC_TURN_ON_LOCATION_TO_UPDATE_USER_LOCATION) {
            if (MapsUtil.locationIsEnable(self)) {
                updateUserLocation();
            } else {
                turnOnLocationReminder(RC_TURN_ON_LOCATION_TO_UPDATE_USER_LOCATION, true);
            }
        }
    }

    private void getMyLocation() {
        if (MapsUtil.locationIsEnable(self)) {
            MapsUtil.getMyLocation(mGoogleApiClient, new IMaps() {
                @Override
                public void processFinished(Object obj) {
                    AppController.getInstance().setMyLocation((Location) obj);
                }
            });
        } else {
            MapsUtil.displayLocationSettingsRequest(this, RC_TURN_ON_LOCATION);
        }
    }

    private void initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(self)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        MapsUtil.getMyLocation(mGoogleApiClient, new IMaps() {
            @Override
            public void processFinished(Object obj) {
                Location location = (Location) obj;
                AppController.getInstance().setMyLocation(location);
            }
        });

//        updateUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void getExtra(Intent intent) {
        Bundle bundle = intent.getExtras();
        //processNotificationNewDeal(bundle);
        if (bundle != null) {
            Log.e(TAG, "bundle != null");
            RecentChatObj recentChatObj = null;
            String notificationType = "";
            if (bundle.containsKey(Args.NOTIFICATION_TYPE)) {
                notificationType = bundle.getString(Args.NOTIFICATION_TYPE);
                if (notificationType != null) {
                    Log.e(TAG, "type is " + notificationType);
                    if (notificationType.equalsIgnoreCase(ChatConfigs.QUICKBLOX_MESSAGE)) {
                        if (bundle.containsKey(Args.RECENT_CHAT_OBJ)) {
                            Log.e(TAG, "have recentChatObj");
                            recentChatObj = bundle.getParcelable(Args.RECENT_CHAT_OBJ);
                            if (recentChatObj != null) {
                                RecentChatsActivity.start(self, recentChatObj);
                            }
                        } else {
                            Log.e(TAG, "have no recentChatObj");
                        }
                    } else if (notificationType.equalsIgnoreCase(ChatConfigs.NOTIFICATION_LOCATION_PERMISSION)) {
                        if (GlobalFunctions.locationIsGranted(self, RC_LOCATION_PERMISSION_TO_UPDATE_DRIVER_LOCATION, "")) {
                            updateDriverLocation();
                        }
                    } else if (notificationType.equalsIgnoreCase(ChatConfigs.NOTIFICATION_TURN_ON_LOCATION)) {
                        if (MapsUtil.locationIsEnable(self)) {
                            updateDriverLocation();
                        } else {
                            MapsUtil.displayLocationSettingsRequest(self, RC_TURN_ON_LOCATION_TO_UPDATE_DRIVER_LOCATION);
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "bundle is null");
        }
    }

    private void openWebView(String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_TITLE, title);
        bundle.putString(Constants.KEY_URL, url);
        AppUtil.startActivity(MainActivity.this, WebViewActivity.class, bundle);
    }

    private void logout() {
        if (NetworkUtility.getInstance(self).isNetworkAvailable()) {
            final MyProgressDialog progressDialog = new MyProgressDialog(self);
            progressDialog.show();

            if (QbAuthUtils.isSessionActive()) {
                ChatHelper.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        processBeforeLoggingOut(progressDialog);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Log.e(TAG, "Log out - onError: " + e.getMessage());
                    }
                });
            } else {
                ChatHelper.getInstance().login(SharedPreferencesUtil.getQbUser(), new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        processBeforeLoggingOut(progressDialog);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(self, "Fail to logout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void processBeforeLoggingOut(MyProgressDialog progressDialog) {
        if (DataStoreManager.getUser() != null && DataStoreManager.getUser().getDriverData() != null) {
            if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                LocationService.start(self, LocationService.STOP_REQUESTING_LOCATION);

                // Deactivate driver's mode before logging out
                ModelManager.activateDriverMode(self, Constants.OFF, 0, new ModelManagerListener() {
                    @Override
                    public void onSuccess(Object object) {
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        SubscribeService.unSubscribeFromPushes(self);
        QBChatService.getInstance().destroy();
        SharedPreferencesUtil.removeQbUser();
        QbDialogHolder.getInstance().clear();

        DataStoreManager.clearUserToken();
        AppController.getInstance().setUserUpdated(true);
        AppUtil.startActivity(self, SplashLoginActivity.class);
        finish();
    }

    public void updateDriverLocation() {
        if (DataStoreManager.getUser() != null && DataStoreManager.getUser().getDriverData() != null) {
            if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                LocationService1.start(self, LocationService1.REQUEST_LOCATION);
//                Log.e("PP",)
//                ModelManager.updateLocation(self,mylatLng);
            }
        }
    }


    private void updateUserLocation() {
        if (NetworkUtility.getInstance(self).isNetworkAvailable()) {
            if (!userLocationIsUpdated) {
                if (DataStoreManager.getUser() != null) {
                    if ((DataStoreManager.getUser().getDriverData() != null
                            && !DataStoreManager.getUser().getDriverData().isAvailable())
                            || DataStoreManager.getUser().getDriverData() == null) {
                        if (GlobalFunctions.locationIsGranted(self, RC_LOCATION_PERMISSION_TO_UPDATE_USER_LOCATION, null)) {
                            if (MapsUtil.locationIsEnable(self)) {
//                                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                                    MapsUtil.getMyLocation(mGoogleApiClient, new IMaps() {
//                                        @Override
//                                        public void processFinished(Object obj) {
//                                            Location location = (Location) obj;
//
//                                            ModelManager.updateLocation(self, new LatLng(location.getLatitude(), location.getLongitude()));
//                                            userLocationIsUpdated = true;
//                                        }
//                                    });
//                                }
                                initEnableLocation();
                            } else {
//                                MapsUtil.displayLocationSettingsRequest(self, RC_TURN_ON_LOCATION_TO_UPDATE_USER_LOCATION);
                            }
                        }
                    }
                }
            }
        }
    }

    private void initEnableLocation() {
        if (MapsUtil.locationIsEnable(self)) {
            initGetLocation();
            updateDriverLocation();
        } else {
            Log.e("II","LL");
            MapsUtil.displayLocationSettingsRequest(self, RC_TURN_ON_LOCATION);
        }
    }

    private FusedLocationProviderClient mFusedLocationClient;

    private void initGetLocation() {
        Log.e(TAG, "initGetLocation");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(self);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener((MainActivity) self, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.

                        Log.e(TAG, "addOnSuccessListener onSuccess ");
                        if (location != null) {
                            Log.d("YYY", location + "");
                            // Logic to handle location object
                            mylatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                            AppController.setMyLatLng(mylatLng);
                            Log.e("location", "lat " + location.getLatitude() + " long " + location.getLongitude());
                            //MapsUtil.moveCameraTo(mMap, mylatLng  );
//                            mLblFrom.setText(MapsUtil.getAddressFromLatLng(self, mylatLng));
//                            mMyLocMarker = addMarker(mMyLocMarker, mylatLng, R.drawable.ic_my_location, false);
//                            mFromLatLng=mylatLng;
//                            Log.e("PP1",mFromLatLng.toString());
////                            if (mFromLatLng == null) {
////                                fillAddressByLatLng(mLblFrom, mylatLng);
////                            }else {
//                            ()MapsUtil.moveCameraTo(mMap, mylatLng);
                            if(mTransportFragment !=null) {
                                mTransportFragment.initEnableLocation();
                            }


////                            }
//
//                            getNearbyDrivers();
                            // Determine whether a Geocoder is available.
                            if (!Geocoder.isPresent()) {
                                Toast.makeText(self, R.string.service_not_available, Toast.LENGTH_LONG).show();
                                return;
                            }
//
//                            if (mDepartureRequested) {
//                                fillAddressByLatLng(mLblFrom, mylatLng);
//                            } else if (mDestinationRequested) {
//                                fillAddressByLatLng(mLblTo, mylatLng);
//                            }
                        } else {
                            getLastLocation();
                        }
                    }
                });
    }

//    private void processNotificationNewDeal(Bundle args) {
//        if (args != null) {
//            if (args.containsKey(Args.NOTIFICATION_TYPE)) {
//                String type = args.getString(Args.NOTIFICATION_TYPE);
//                if (type != null && type.equalsIgnoreCase(ChatConfigs.TYPE_DEAL)) {
//                    GlobalFunctions.startActivityWithoutAnimation(this, DealDetailActivity.class, args);
//                }
//            }
//        }
//
//    }

    private void getContacts() {
        if (NetworkUtility.getInstance(self).isNetworkAvailable()) {
            ModelManager.getContacts(self, false, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;
                    if (JSONParser.responseIsSuccess(jsonObject)) {
                        ArrayList<ContactObj> contactObjs = JSONParser.parseContacts(jsonObject);
                        if (contactObjs.size() > 0) {
                            // Save contacts into preference
                            DataStoreManager.saveContactsList(contactObjs);
                        }
                    }
                }

                @Override
                public void onError() {
                }
            });
        }
    }

    @Override
    public void onChangTitle(String title) {
        setTitle(title);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(receiverDeals);
        AppController.onPause();
        DataStoreManager.saveScreenMain(false);
        handleReceiverDeals.unregisterReceiver();
    }

    /**
     *  show dialog thông báo cho user biết người đối thoại đã hủy deal
     **/

    private class ReceiverDeals extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_PUSH_DEAL)){
                RecentChatObj obj = (RecentChatObj) intent.getExtras().get(Args.RECENT_CHAT_OBJ);
                showDialogNotif(intent.getStringExtra(Action.ACTION_PUSH_DEAL),
                        obj,
                        intent.getStringExtra(Args.NOTIFICATION_TYPE));
            }
        }
    }

    private void showDialogNotif(String msg, RecentChatObj obj, String type) {
        final Dialog mDealConfirmationDialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_confirmation, false);

        TextViewRegular lblMsg = (TextViewRegular) mDealConfirmationDialog.findViewById(R.id.lbl_msg);
        TextViewBold lblNegative = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_negative);
        TextViewBold lblPositive = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_positive);

        lblMsg.setText(msg);

        lblNegative.setText(R.string.disagree);
        lblPositive.setText(R.string.agree);

        lblNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDealConfirmationDialog.dismiss();

            }
        });

        lblPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDealConfirmationDialog.dismiss();

            }
        });
    }

    private ReceiverDeals receiverDeals;
    private void initReceiver() {
        receiverDeals = new ReceiverDeals();
        IntentFilter filter = new IntentFilter(Action.ACTION_PUSH_DEAL);
        registerReceiver(receiverDeals, filter);
    }
}
