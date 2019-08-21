package com.suusoft.locoindia.view.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.quickblox.users.model.QBUser;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.quickblox.conversation.utils.DialogUtil;
import com.suusoft.locoindia.retrofit.APIService;
import com.suusoft.locoindia.retrofit.ApiUtils;
import com.suusoft.locoindia.retrofit.response.RespondGetOnline;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.view.activities.BecomeAProActivity;
import com.suusoft.locoindia.view.activities.BuyCreditsActivity;
import com.suusoft.locoindia.view.activities.MainActivity;
import com.suusoft.locoindia.view.activities.PassengerViewDriverActivity;
import com.suusoft.locoindia.view.activities.RecentChatsActivity;
import com.suusoft.locoindia.view.activities.TripManagerActivity;
import com.suusoft.locoindia.view.adapters.PassengerQuantityAdapter;
import com.suusoft.locoindia.view.adapters.TransportAdapter;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IChangeTitle;
import com.suusoft.locoindia.interfaces.IConfirmation;
import com.suusoft.locoindia.interfaces.IPassenger;
import com.suusoft.locoindia.interfaces.ITransport;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.objects.DriverObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.objects.SettingsObj;
import com.suusoft.locoindia.objects.TransportDealObj;
import com.suusoft.locoindia.objects.TransportObj;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.quickblox.SharedPreferencesUtil;
import com.suusoft.locoindia.utils.NetworkUtility;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.utils.map.IMaps;
import com.suusoft.locoindia.utils.map.MapsUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by SuuSoft.com on 10/05/2017.
 */

public class FindTaxiFragment extends com.suusoft.locoindia.base.BaseFragment implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener {

    private IChangeTitle iChangeTitle;

    public FindTaxiFragment() {
    }

    public static FindTaxiFragment newInstance(IChangeTitle changeTitle) {
        Bundle args = new Bundle();
        FindTaxiFragment fragment = new FindTaxiFragment();
        fragment.setArguments(args);
        fragment.iChangeTitle = changeTitle;
        return fragment;
    }

    private static final String TAG = FindTaxiFragment.class.getSimpleName();

    private static final int RC_LOCATION = 1;
    private static final int RC_TURN_ON_LOCATION = 2;
    private static final int RC_GET_DEPARTURE = 3;
    private static final int RC_GET_DESTINATION = 4;

    private GoogleMap mMap;
    private TextViewRegular mLblFrom, mLblTo, mLblIwanaride;
    private ImageView mImgLocDeparture, mImgLocDestination;
    private TextView mTvDistance;
    private int updateDuration = 0;
    private boolean check = true;
    private boolean checkBuyDuration = false;

    private LatLng mylatLng;
    private SupportMapFragment mMapFragment;
    private boolean checkClickImageLocation = true;

    // Use google api to get current location
    private GoogleApiClient mGoogleApiClient;
    private Place mMyPlace;
    private boolean mDepartureRequested, mDestinationRequested;

    private LatLng mFromLatLng, mToLatLng;
    private Marker mMyLocMarker, mFromMarker, mToMarker;

    // Select transport type and number of passengers
    private String mSelectedTransportType = TransportObj.ALL;
    private LinearLayout mLlTransport, mLlPassengerQuantity;
    private ImageView mImgTransportType;
    private TextViewRegular mLblTransportType, mLblPassengerQuantity;
    private RecyclerView mRclTransport, mRclPassengerQuantity;

    private ArrayList<TransportDealObj> mNearbyDriverDeals;
    private ArrayList<Marker> mDriverMarkers;
    private TransportDealObj mSelectedDriver;
    private RecentChatObj mRecentChatObj;
    private ArrayList<QBUser> mSelectedUsers;
    private String token = "";

    private Menu mMenu;

    private String mSelectedDriverId;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenu = menu;
        inflater.inflate(R.menu.menu_transport, menu);
        //updateMenu();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateMenu() {
        if (DataStoreManager.getUser().getDriverData() != null)
            if (DataStoreManager.getUser().getDriverData().isActive()) {
                mMenu.findItem(R.id.action_available_for_driving).setChecked(true);
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_available_for_driving) {
            checkEndSetAvaiable();

        } else if (id == R.id.action_duration_buying) {
            buyDuration(Constants.DURATION_BUYING);
        } else if (id == R.id.action_trip_manager) {
            GlobalFunctions.startActivityWithoutAnimation(self, TripManagerActivity.class);
//        } else if (id == android.R.id.home) {
//            onBackPressed();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (check) {
            initEnableLocation();
        }
        if (DataStoreManager.getUser().getDriverData() != null) {
            buyDuration();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAvaiable();
                }
            }, 2000);
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showCurrentLocation(mImgLocDeparture);
//            }
//        }, 1500);
//        initEnableLocation();
    }

    private void setAvaiable() {
        if (DataStoreManager.getUser().getDriverData() != null) {
            if (DataStoreManager.getUser().getDriverData().isActive()) {
                String mode = DataStoreManager.isOnline() ? Constants.ON : Constants.OFF;
                changeDriverMode(mode, updateDuration);
            } else {
                if (mMenu != null)
                    mMenu.findItem(R.id.action_available_for_driving).setChecked(false);

                Toast.makeText(self, R.string.msg_wait_active_driver, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkEndSetAvaiable() {
        if (DataStoreManager.getUser().getDriverData() != null) {
            if (DataStoreManager.getUser().getDriverData().isActive()) {
                String mode = mMenu.findItem(R.id.action_available_for_driving).isChecked() ? Constants.OFF : Constants.ON;
                //String mode = Constants.ON;
                changeDriverMode(mode, updateDuration);
            } else {
                if (mMenu != null)
                    mMenu.findItem(R.id.action_available_for_driving).setChecked(false);

                Toast.makeText(self, R.string.msg_wait_active_driver, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (mMenu != null)
                mMenu.findItem(R.id.action_available_for_driving).setChecked(false);

            buyDuration(Constants.DURATION_BUYING);
        }

    }

    private void buyDuration(String mode) {
        if (DataStoreManager.getUser().getDriverData() != null) {
            if (DataStoreManager.getUser().getDriverData().isActive()) {
                showDriverModeActivationDialog(mode);
            } else {
                Toast.makeText(self, R.string.msg_wait_active_driver, Toast.LENGTH_SHORT).show();
            }
        } else {
            GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_need_to_become_driver),
                    getString(R.string.yes), getString(R.string.no_thank), true, new IConfirmation() {
                        @Override
                        public void onPositive() {
                            GlobalFunctions.startActivityWithoutAnimation(self, BecomeAProActivity.class);
                        }

                        @Override
                        public void onNegative() {
                        }
                    });
        }
    }

    private void showDriverModeActivationDialog(final String mode) {
        final Dialog dialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_buying_duration, true);

        final EditText txtDuration = (EditText) dialog.findViewById(R.id.txt_duration);
        final TextViewRegular lblFee = (TextViewRegular) dialog.findViewById(R.id.lbl_msg_fee);
        TextViewBold lblBuyCredits = (TextViewBold) dialog.findViewById(R.id.lbl_buy_credits);
        TextViewBold lblAvailable = (TextViewBold) dialog.findViewById(R.id.lbl_available);

        lblFee.setText(String.format(getString(R.string.msg_subtract_credits), "0"));

        SettingsObj settingsObj = DataStoreManager.getSettingUtility();
        final int feePerHour = (settingsObj != null && settingsObj.getDriver_online_rate() != null && !settingsObj.getDriver_online_rate().equals("")) ?
                Integer.parseInt(settingsObj.getDriver_online_rate()) : 1;

        Log.e(TAG, "feePerHour " + feePerHour);
        Log.e(TAG, "setting " + new Gson().toJson(settingsObj));

        txtDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String duration = editable.toString().trim().isEmpty() ? "0" : editable.toString().trim();

                lblFee.setText(String.format(getString(R.string.msg_subtract_credits), String.valueOf((Integer.parseInt(duration) * feePerHour))));
            }
        });

        lblBuyCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                GlobalFunctions.startActivityWithoutAnimation(self, BuyCreditsActivity.class);
            }
        });

        lblAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int duration = 0;
                if (txtDuration.getText().toString().trim().length() > 0) {
                    duration = Integer.parseInt(txtDuration.getText().toString().trim());
                }

                if (duration == 0 || duration > 24) {
                    Toast.makeText(self, R.string.msg_duration_must_gt_zero, Toast.LENGTH_LONG).show();
                } else {
                    if (DataStoreManager.getUser().getBalance() < duration) {
                        Toast.makeText(self, String.format(getString(R.string.msg_balance_is_not_enough),
                                String.valueOf(duration)), Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();
//                        buyDuration();
                        changeDriverMode(mode, duration);
                    }
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                GlobalFunctions.closeKeyboard((AppCompatActivity) self);
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void buyDuration() {
        Log.e("QQ", token);
        APIService apiService = ApiUtils.getAPIService();
        Call<RespondGetOnline> callBack = apiService.getOnline(token);
        callBack.enqueue(new Callback<RespondGetOnline>() {
            @Override
            public void onResponse(Call<RespondGetOnline> call, Response<RespondGetOnline> response) {
                if (response != null) {
                    String duration = response.body().data;
                    Log.e("QQ", duration);
                        updateDuration = Integer.parseInt(duration);
                    if (DataStoreManager.isOnline()) {
                        Log.e("QQ", updateDuration + "");
                        UserObj userObj = DataStoreManager.getUser();
                        userObj.getDriverData().setAvailable(DriverObj.DRIVER_AVAILABLE);
                        DataStoreManager.saveUser(userObj);
                        mMenu.findItem(R.id.action_available_for_driving).setChecked(true);
                        if (updateDuration > 0) {
                            mMenu.findItem(R.id.action_available_for_driving).setTitle(getString(R.string.i_am_available_for_driving) + " (" + updateDuration + "h) ");
                            AppUtil.showToast(self, R.string.you_are_online);
                            DataStoreManager.saveOnline(true);
                        } else {
                            mMenu.findItem(R.id.action_available_for_driving).setTitle(getString(R.string.i_am_available_for_driving) + " (" + 0 + "h) ");
                            AppUtil.showToast(self, R.string.you_are_online);
                            DataStoreManager.saveOnline(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RespondGetOnline> call, Throwable t) {

            }
        });


    }

    private void changeDriverMode(final String mode, final int duration) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.activateDriverMode(self, mode, duration, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;

                    //Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();

                    if (!JSONParser.responseIsSuccess(jsonObject)) {
                        mMenu.findItem(R.id.action_available_for_driving).setChecked(mode.equals(Constants.OFF));
                    } else {
                        if (mode.equals(Constants.OFF)) {
                            // Set driver is unavailable
                            UserObj userObj = DataStoreManager.getUser();
                            userObj.getDriverData().setAvailable(DriverObj.DRIVER_UNAVAILABLE);
                            DataStoreManager.saveUser(userObj);

                            mMenu.findItem(R.id.action_available_for_driving).setChecked(false);
                            mMenu.findItem(R.id.action_available_for_driving).setTitle(getString(R.string.i_am_available_for_driving));
                            AppUtil.showToast(self, R.string.you_are_offline);
                            DataStoreManager.saveOnline(false);
                        } else {

                            DataStoreManager.saveOnline(true);
                            buyDuration();
                            // Set driver is unavailable
                        }
                    }
                }

                @Override
                public void onError() {
                }
            });
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        check = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = DataStoreManager.getUser().getToken();
//        initEnableLocation();
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_find_taxi;
    }

    @Override
    protected void init() {
        setHasOptionsMenu(true);
    }

    @Override
    protected void initView(View view) {
        // Show as up button
//        try {
//            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);
//
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }

        initEnableLocation();
        initControls(view);
//        initGoogleApiClient();
        initTransportTypes();
        initPassengerQuantity();
    }

    private void initControls(View view) {
//        SupportMapFragment mapFragment = (SupportMapFragment)getActivity().getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        mMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mMapFragment.getMapAsync(this);
//        mMapFragment = new SupportMapFragment() {
//            @Override
//            public void onActivityCreated(Bundle savedInstanceState) {
//                super.onActivityCreated(savedInstanceState);
//                mMap = mMapFragment.getMap();
//                if (mMap != null) {
//                    setupMap();
//                }
//            }
//        };
//        getChildFragmentManager().beginTransaction().add(R.id.framelayout_location_container, mMapFragment).commit();


        mLblIwanaride = (TextViewRegular) view.findViewById(R.id.lbl_iwanaride);
        mTvDistance = (TextView) view.findViewById(R.id.tv_distance);
        mLblFrom = (TextViewRegular) view.findViewById(R.id.lbl_from);
        mLblTo = (TextViewRegular) view.findViewById(R.id.lbl_to);
        mImgLocDeparture = (ImageView) view.findViewById(R.id.img_location_departure);
        mImgLocDestination = (ImageView) view.findViewById(R.id.img_location_destination);

        mLlTransport = (LinearLayout) view.findViewById(R.id.ll_transport_type);
        mImgTransportType = (ImageView) view.findViewById(R.id.img_transport_type);
        mLblTransportType = (TextViewRegular) view.findViewById(R.id.lbl_transport_type);
        mLlPassengerQuantity = (LinearLayout) view.findViewById(R.id.ll_passenger_quantity);
        mLblPassengerQuantity = (TextViewRegular) view.findViewById(R.id.lbl_passenger_quantity);

        mRclTransport = (RecyclerView) view.findViewById(R.id.rcl_transport_type);
        mRclTransport.setLayoutManager(new LinearLayoutManager(self, LinearLayoutManager.HORIZONTAL, false));
        mRclTransport.setHasFixedSize(true);

        mRclPassengerQuantity = (RecyclerView) view.findViewById(R.id.rcl_passenger_quantity);
        mRclPassengerQuantity.setLayoutManager(new LinearLayoutManager(self, LinearLayoutManager.HORIZONTAL, false));
        mRclPassengerQuantity.setHasFixedSize(true);

        mLblIwanaride.setOnClickListener(this);
        mLblFrom.setOnClickListener(this);
        mLblTo.setOnClickListener(this);
        mImgLocDeparture.setOnClickListener(this);
        mImgLocDestination.setOnClickListener(this);
        mLlTransport.setOnClickListener(this);
        mLlPassengerQuantity.setOnClickListener(this);

    }

    @Override
    protected void getData() {

    }

    private void initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(self)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(Places.GEO_DATA_API)
                    .enableAutoManage(getActivity(), this)
                    .build();
        }
    }

    public void initEnableLocation() {
        if (MapsUtil.locationIsEnable(self)) {
            initGetLocation();
            ((MainActivity) self).updateDriverLocation();
        } else {
//            MapsUtil.displayLocationSettingsRequest((AppCompatActivity)self, RC_TURN_ON_LOCATION);
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
                            if (checkClickImageLocation) {
                                mLblFrom.setText(MapsUtil.getAddressFromLatLng(self, mylatLng));
                            } else {
                                mLblTo.setText(MapsUtil.getAddressFromLatLng(self, mylatLng));
                            }
//                            AppController.setMyLatLng(mylatLng);
                            Log.e("location", "lat " + location.getLatitude() + " long " + location.getLongitude());
                            //MapsUtil.moveCameraTo(mMap, mylatLng  );

                            mMyLocMarker = addMarker(mMyLocMarker, mylatLng, R.drawable.ic_my_location, false);

                            mFromLatLng = mylatLng;
                            Log.e("PP1", mFromLatLng.toString());
//                            if (mFromLatLng == null) {
//                                fillAddressByLatLng(mLblFrom, mylatLng);
//                            }else {
                            MapsUtil.moveCameraTo(mMap, mylatLng);
//                            }

                            getNearbyDrivers();
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

    private void initTransportTypes() {
        ArrayList<TransportObj> transportObjs = new ArrayList<>();
        transportObjs.add(new TransportObj(TransportObj.ALL, getString(R.string.all), R.drawable.ic_wheel));
        transportObjs.add(new TransportObj(TransportObj.TAXI, getString(R.string.taxi), R.drawable.ic_taxi));
        transportObjs.add(new TransportObj(TransportObj.VIP, getString(R.string.vip), R.drawable.ic_vip));
        transportObjs.add(new TransportObj(TransportObj.LIFTS, getString(R.string.lifts), R.drawable.ic_lifts));
        transportObjs.add(new TransportObj(TransportObj.MOTORBIKE, getString(R.string.motorbike), R.drawable.ic_moto));
        transportObjs.add(new TransportObj(TransportObj.DELIVERY, getString(R.string.delivery), R.drawable.ic_delivery));
        transportObjs.add(new TransportObj(TransportObj.TUKTUK, getString(R.string.tuktuk),R.drawable.tuktuk));

        // Fix for 'iwanadelivery' in left menu
        if (mSelectedTransportType.equalsIgnoreCase(TransportObj.DELIVERY)) {
            mImgTransportType.setImageResource(R.drawable.ic_delivery);
            mLblTransportType.setText(getString(R.string.delivery));

            onChangeTitle(R.string.deal_delivery);

            mLblIwanaride.setText(R.string.deal_delivery);
        }

        TransportAdapter transportAdapter = new TransportAdapter(getContext(), transportObjs, new ITransport() {
            @Override
            public void onTransportSelected(TransportObj transportObj) {
                mSelectedTransportType = transportObj.getType();
                mImgTransportType.setImageResource(transportObj.getIcon());
                mLblTransportType.setText(transportObj.getName());

                // Filter nearby drivers by transport type
                getNearbyDrivers();

                // Change text and title
                if (mSelectedTransportType.equalsIgnoreCase(TransportObj.DELIVERY)) {
                    onChangeTitle(R.string.deal_delivery);
                    mLblIwanaride.setText(R.string.deal_delivery);
                } else {
                    onChangeTitle(R.string.deal_transport);
                    mLblIwanaride.setText(R.string.deal_transport);
                }

                mRclTransport.setVisibility(View.GONE);
                mLlTransport.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        mRclTransport.setAdapter(transportAdapter);
    }

    private void onChangeTitle(int title) {
        iChangeTitle.onChangTitle(getResources().getString(title));
    }

    private void getNearbyDrivers() {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.getNearbyDrivers(self, DataStoreManager.getUser().getToken(), mSelectedTransportType,
                    mFromLatLng, mToLatLng, new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            JSONObject jsonObject = (JSONObject) object;
                            if (JSONParser.responseIsSuccess(jsonObject)) {
                                if (mNearbyDriverDeals == null) {
                                    mNearbyDriverDeals = new ArrayList<>();
                                    mDriverMarkers = new ArrayList<>();
                                } else {
                                    mNearbyDriverDeals.clear();
                                    // Remove old markers
                                    for (int i = 0; i < mDriverMarkers.size(); i++) {
                                        mDriverMarkers.get(i).remove();
                                    }
                                }

                                mNearbyDriverDeals = JSONParser.parseDrivers(jsonObject);

                                addDriverMarkers();
                            } else {
                                Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void addDriverMarkers() {
        if (mNearbyDriverDeals != null) {
            if (mNearbyDriverDeals.size() > 0) {
                Log.e(TAG, mNearbyDriverDeals.size() + "");
                for (int i = 0; i < mNearbyDriverDeals.size(); i++) {
                    TransportDealObj transportDealObj = mNearbyDriverDeals.get(i);
                    Marker marker = addMarker(null, transportDealObj.getLatLngDriver(), getTransportIcon(transportDealObj), false);
                    marker.setTag(transportDealObj.getDriverId());
                    mDriverMarkers.add(marker);
                }
            }
        }
    }

    protected int getTransportIcon(TransportDealObj transportDealObj) {
        int icon = R.drawable.ic_taxi_type;
        if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.TAXI)) {
            if (transportDealObj.driverIsDelivery()) {
                icon = R.drawable.ic_taxi_delivery_type;
            } else {
                icon = R.drawable.ic_taxi_type;
            }
        } else if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.VIP)) {
            if (transportDealObj.driverIsDelivery()) {
                icon = R.drawable.ic_vip_delivery_type;
            } else {
                icon = R.drawable.ic_vip_type;
            }
        } else if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.LIFTS)) {
            if (transportDealObj.driverIsDelivery()) {
                icon = R.drawable.ic_lifts_delivery_type;
            } else {
                icon = R.drawable.ic_lifts_type;
            }
        } else if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.MOTORBIKE)) {
            if (transportDealObj.driverIsDelivery()) {
                icon = R.drawable.ic_moto_delivery_type;
            } else {
                icon = R.drawable.ic_moto_type;
            }
        }

        return icon;
    }

    private Marker addMarker(Marker marker, LatLng latLng, int icon, boolean draggable) {
        // Clear old marker if it's
        if (mMap != null) {
            if (marker != null) {
                marker.remove();
            }
            return MapsUtil.addMarker(mMap, latLng, "", icon, draggable);
        }
        return null;
    }

    private void initPassengerQuantity() {
        PassengerQuantityAdapter quantityAdapter = new PassengerQuantityAdapter(getContext(), new IPassenger() {
            @Override
            public void onQuantitySelected(String s) {
                mLblPassengerQuantity.setText(s);

                mRclPassengerQuantity.setVisibility(View.GONE);
                mLlPassengerQuantity.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        mRclPassengerQuantity.setAdapter(quantityAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == mLblFrom) {
            check = false;
            MapsUtil.getAutoCompletePlaces(FindTaxiFragment.this, RC_GET_DEPARTURE);
        } else if (view == mLblTo) {
            check = false;
            MapsUtil.getAutoCompletePlaces(FindTaxiFragment.this, RC_GET_DESTINATION);

        } else if (view == mImgLocDeparture) {
            checkClickImageLocation = true;
            initEnableLocation();
//            showCurrentLocation(mImgLocDeparture);
        } else if (view == mImgLocDestination) {
//            checkClickImageLocation=false;
            mLblTo.setText(MapsUtil.getAddressFromLatLng(self, mylatLng));
//            MapsUtil.moveCameraTo(mMap,mylatLng);
            mToLatLng = mylatLng;
//            initEnableLocation();
//            showCurrentLocation(mImgLocDestination);
        } else if (view == mLlTransport) {
            if (mRclTransport.getVisibility() == View.GONE) {
                mRclTransport.setVisibility(View.VISIBLE);
                mLlTransport.setBackground(getResources().getDrawable(R.drawable.bg_radius_gray));
            } else {
                mRclTransport.setVisibility(View.GONE);
                mLlTransport.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        } else if (view == mLlPassengerQuantity) {
            if (mRclPassengerQuantity.getVisibility() == View.GONE) {
                mRclPassengerQuantity.setVisibility(View.VISIBLE);
                mLlPassengerQuantity.setBackground(getResources().getDrawable(R.drawable.bg_radius_gray));
            } else {
                mRclPassengerQuantity.setVisibility(View.GONE);
                mLlPassengerQuantity.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        } else if (view == mLblIwanaride) {

            iwanaride();
        }

    }


    private void iwanaride() {
        if (mFromLatLng == null) {
            Toast.makeText(self, R.string.msg_choose_pickup, Toast.LENGTH_SHORT).show();
        } else if (mToLatLng == null) {
            Toast.makeText(self, R.string.msg_choose_destination, Toast.LENGTH_SHORT).show();
        } else if (mSelectedDriverId == null || mSelectedDriverId.equals("")) {
            Toast.makeText(self, R.string.msg_choose_driver, Toast.LENGTH_SHORT).show();
        } else {
            // Transfer trip info
            keepTripInfo();

            RecentChatsActivity.start(getActivity(), mRecentChatObj);
        }
    }

    private void keepTripInfo() {
        if (mNearbyDriverDeals != null) {
            if (mNearbyDriverDeals.size() > 0) {
                for (int i = 0; i < mNearbyDriverDeals.size(); i++) {
                    if (mSelectedDriverId.equals(mNearbyDriverDeals.get(i).getDriverId())) {
                        mSelectedDriver = mNearbyDriverDeals.get(i);
                        break;
                    }
                }
            }
        }

        mSelectedDriver.setPassengerQuantity(Integer.parseInt(mLblPassengerQuantity.getText().toString()));
        mSelectedDriver.setPickup(mLblFrom.getText().toString());
        mSelectedDriver.setDestination(mLblTo.getText().toString());
        mSelectedDriver.setLatLngPickup(mFromLatLng);
        mSelectedDriver.setLatLngDestination(mToLatLng);
        mSelectedDriver.setPassengerId(DataStoreManager.getUser().getId());
        mSelectedDriver.setPassengerQBId(SharedPreferencesUtil.getQbUser().getId());

        mSelectedUsers = new ArrayList<>();
        /*QBUser qbUser = new QBUser();
        qbUser.setId(mSelectedDriver.getDriverQBId());
        qbUser.setLogin(mSelectedDriver.getDriverEmail());
        qbUser.setFullName(mSelectedDriver.getDriverName());
        String phone = DataStoreManager.getUser().getPhone() == null ? "" : DataStoreManager.getUser().getPhone();
        qbUser.setPhone(phone);*/

        QBUser qbUser = SharedPreferencesUtil.getQbUser();
        String phone = DataStoreManager.getUser().getPhone() == null ? "" : DataStoreManager.getUser().getPhone();
        qbUser.setPhone(phone);
        mSelectedUsers.add(qbUser);

        mRecentChatObj = new RecentChatObj(mSelectedDriver, null, qbUser);
        Log.e("mRecentChatObj", "mRecentChatObj = " + new Gson().toJson(mRecentChatObj));
    }

    private void showCurrentLocation(View view) {
        if (GlobalFunctions.locationIsGranted(getActivity(), RC_LOCATION, null)) {
            if (MapsUtil.locationIsEnable(self)) {
                // Only start the service to fetch the address if GoogleApiClient is
                // connected.
                if (mGoogleApiClient.isConnected() && mMyPlace != null) {
                    if (view == mImgLocDeparture) {
                        fillAddress(mLblFrom, mMyPlace);
                    } else if (view == mImgLocDestination) {
                        fillAddress(mLblTo, mMyPlace);
                    }
                }
            } else {
                MapsUtil.displayLocationSettingsRequest((AppCompatActivity) self, RC_TURN_ON_LOCATION);
            }
        }

        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        if (view == mImgLocDeparture) {
            mDepartureRequested = true;
        } else if (view == mImgLocDestination) {
            mDestinationRequested = true;
        }
    }

    private void fillAddress(final TextViewRegular lblAddress, final LatLng latLng) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.getAddressByLatlng(getActivity(), latLng, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            JSONObject firstResult = jsonArray.getJSONObject(0);
                            String address = firstResult.optString("formatted_address");
                            if (address.contains("\n")) {
                                address = address.replace("\n", ", ");
                            }
                            lblAddress.setText(address);
                            Log.e("FragmentTransport", "Address LatLng " + address.toString());

                            // Reset boolean var to void some bugs
                            if (lblAddress == mLblFrom) {
                                mDepartureRequested = false;
                                MapsUtil.moveCameraTo(mMap, latLng);

                                mFromMarker = addMarker(mFromMarker, latLng, R.drawable.ic_place_accent, true);

                                mFromLatLng = latLng;
                            } else if (lblAddress == mLblTo) {
                                mDestinationRequested = false;

                                mToMarker = addMarker(mToMarker, latLng, R.drawable.ic_place_to, true);

                                mToLatLng = latLng;
                            }


                            // Get nearby drivers
                            getNearbyDrivers();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError() {

                }
            });
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }

//        /*MapsUtil.startAddressService(self, new AddressResultReceiver(new Handler(), new IMaps() {
//            @Override
//            public void processFinished(Object obj) {
//                String address = obj.toString();
//                if (address.contains("\n")) {
//                    address = address.replace("\n", ", ");
//                }
//                lblAddress.setText(address);
//
//                // Reset boolean var to void some bugs
//                if (lblAddress == mLblFrom) {
//                    mDepartureRequested = false;
//                    MapsUtil.moveCameraTo(mMap, latLng);
//
//                    mFromMarker = addMarker(mFromMarker, latLng, R.drawable.ic_place_accent, true);
//
//                    mFromLatLng = latLng;
//                } else if (lblAddress == mLblTo) {
//                    mDestinationRequested = false;
//
//                    mToMarker = addMarker(mToMarker, latLng, R.drawable.ic_place_to, true);
//
//                    mToLatLng = latLng;
//                }
//
//                // Get nearby drivers
//                getNearbyDrivers();
//            }
//        }), latLng);*/
    }

    private void fillAddress(final TextViewRegular lblAddress, Place place) {
        LatLng latLng = place.getLatLng();
        String address = place.getAddress().toString();
        if (address.contains("\n")) {
            address = address.replace("\n", ", ");
        }
        lblAddress.setText(address);
        Log.e("FragmentTransport", "Address " + address.toString());

        // Reset boolean var to void some bugs
        if (lblAddress == mLblFrom) {
            mDepartureRequested = false;
            MapsUtil.moveCameraTo(mMap, latLng);

            mFromMarker = addMarker(mFromMarker, latLng, R.drawable.ic_place_accent, true);

            mFromLatLng = latLng;
            Log.e("PP", mFromLatLng.toString());
        } else if (lblAddress == mLblTo) {
            mDestinationRequested = false;

            mToMarker = addMarker(mToMarker, latLng, R.drawable.ic_place_to, true);

            mToLatLng = latLng;
        }


        // Get nearby drivers
        getNearbyDrivers();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (MapsUtil.locationIsEnable(self)) {
            if (mMyPlace == null) {
                MapsUtil.getMyPlace(mGoogleApiClient, new IMaps() {
                    @Override
                    public void processFinished(Object obj) {
                        mMyPlace = (Place) obj;
                        if (mMyPlace != null) {
//                            mMyLocMarker = addMarker(mMyLocMarker, mMyPlace.getLatLng(), R.drawable.ic_my_location, false);

//                            if (mFromLatLng == null) {
//                                fillAddress(mLblFrom, mMyPlace);
//                            }

                            // Determine whether a Geocoder is available.
                            if (!Geocoder.isPresent()) {
                                Toast.makeText(self, R.string.service_not_available, Toast.LENGTH_LONG).show();
                                return;
                            }

//                            if (mDepartureRequested) {
//                                fillAddress(mLblFrom, mMyPlace);
//                            } else if (mDestinationRequested) {
//                                fillAddress(mLblTo, mMyPlace);
//                            }
                        }
                    }
                });
            }
        } else {
//            MapsUtil.displayLocationSettingsRequest((AppCompatActivity) self, RC_TURN_ON_LOCATION);
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public View getInfoWindow(Marker marker) {
        @SuppressLint("RestrictedApi") ViewGroup window = (ViewGroup) getLayoutInflater(new Bundle()).inflate(R.layout.layout_windowinfo_marker_driver, null);

        TextViewRegular lblMinutes = (TextViewRegular) window.findViewById(R.id.lbl_minutes);
        TextViewRegular lblName = (TextViewRegular) window.findViewById(R.id.lbl_driver_name);
        TextViewRegular lblTransport = (TextViewRegular) window.findViewById(R.id.lbl_type);
        TextViewRegular lblRateQuantity = (TextViewRegular) window.findViewById(R.id.lbl_rate_quantity);
        TextViewRegular lblFare = (TextViewRegular) window.findViewById(R.id.lbl_fare);
        RatingBar ratingBar = (RatingBar) window.findViewById(R.id.rating);

        if (!marker.equals(mMyLocMarker) && !marker.equals(mToMarker) && !marker.equals(mFromMarker)) {
            TransportDealObj transportDealObj = null;
            if (mNearbyDriverDeals != null) {
                if (mNearbyDriverDeals.size() > 0) {
                    for (int i = 0; i < mNearbyDriverDeals.size(); i++) {
                        transportDealObj = mNearbyDriverDeals.get(i);
                        if (marker.getTag().equals(transportDealObj.getDriverId())) {
                            break;
                        }
                    }
                }
            }

            if (transportDealObj != null) {
                String minutes = String.valueOf(transportDealObj.getDuration() / 60);
                lblMinutes.setText(String.format(getString(R.string.value_minutes_away), minutes));
                lblName.setText(transportDealObj.getDriverName());
                lblRateQuantity.setText(String.valueOf(transportDealObj.getRateQuantity()));
                ratingBar.setRating(transportDealObj.getRateOfDriver());

                if (transportDealObj.getEstimateFare() > 0) {
                    lblFare.setVisibility(View.VISIBLE);
                    lblFare.setText(String.format(getString(R.string.dollar_value), StringUtil.convertNumberToString(transportDealObj.getEstimateFare(), 1)));
                } else {
                    lblFare.setVisibility(View.INVISIBLE);
                }

                String transport = "";
                if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.TAXI)) {
                    if (transportDealObj.driverIsDelivery()) {
                        transport = getString(R.string.taxi_delivery);
                    } else {
                        transport = getString(R.string.taxi);
                    }
                } else if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.VIP)) {
                    if (transportDealObj.driverIsDelivery()) {
                        transport = getString(R.string.vip_delivery);
                    } else {
                        transport = getString(R.string.vip);
                    }
                } else if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.LIFTS)) {
                    if (transportDealObj.driverIsDelivery()) {
                        transport = getString(R.string.lift_delivery);
                    } else {
                        transport = getString(R.string.lifts);
                    }
                } else if (transportDealObj.getTransportType().equalsIgnoreCase(TransportObj.MOTORBIKE)) {
                    if (transportDealObj.driverIsDelivery()) {
                        transport = getString(R.string.motor_delivery);
                    } else {
                        transport = getString(R.string.motorbike);
                    }
                }
                lblTransport.setText(transport);
            }

            return window;
        } else {
            return null;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Transfer trip info
        keepTripInfo();

        Bundle bundle = new Bundle();
        bundle.putParcelable(Args.RECENT_CHAT_OBJ, mRecentChatObj);
        GlobalFunctions.startActivityWithoutAnimation(self, PassengerViewDriverActivity.class, bundle);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.equals(mMyLocMarker) && !marker.equals(mFromMarker) && !marker.equals(mToMarker)) {
            mSelectedDriverId = marker.getTag().toString();
        }
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (marker.equals(mFromMarker)) {
            fillAddress(mLblFrom, marker.getPosition());
        } else if (marker.equals(mToMarker)) {
            fillAddress(mLblTo, marker.getPosition());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TransportFragment", "onActivityResult");
        if (requestCode == RC_GET_DEPARTURE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(self, data);
                fillAddress(mLblFrom, place);
                Log.e("TransportFragment", "requestCode == RC_GET_DEPARTURE -- resultCode == RESULT_OK");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(self, data);
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == RC_GET_DESTINATION) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(self, data);
                fillAddress(mLblTo, place);
                Log.e("TransportFragment", "requestCode == RC_GET_DESTINATION -- resultCode == RESULT_OK");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(self, data);
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == RC_TURN_ON_LOCATION) {
            Log.e("EE", "1");
            if (MapsUtil.locationIsEnable(self)) {
//                if (resultCode == RESULT_OK) {
//                    Log.e("EE", "2");
//                if (!mDestinationRequested) {
////                    if (mMyPlace == null) {
////                        MapsUtil.getMyPlace(mGoogleApiClient, new IMaps() {
////                            @Override
////                            public void processFinished(Object obj) {
////                                mMyPlace = (Place) obj;
////                                if (mMyPlace != null) {
////                                    // Add marker
////                                    if (mFromLatLng == null) {
////                                        mMyLocMarker = addMarker(mMyLocMarker, mMyPlace.getLatLng(), R.drawable.ic_my_location, false);
////                                    }
////
////                                    fillAddress(mLblFrom, mMyPlace);
////                                }
////                            }
////                        });
////                    } else {
////                        MapsUtil.moveCameraTo(mMap, mMyPlace.getLatLng());
////                    }
////                }
                initEnableLocation();
            } else {
                Log.e("EE", "3");
                turnOnLocationReminder(RC_TURN_ON_LOCATION, true);
            }
        }
    }


    public void turnOnLocationReminder(final int reqCode, final boolean flag) {
        GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_remind_user_turn_on_location),
                getString(R.string.turn_on), getString(R.string.no_thank), false, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        MapsUtil.displayLocationSettingsRequest((AppCompatActivity) self, reqCode);
                    }

                    @Override
                    public void onNegative() {
                        if (flag) {
//                            finish();
                        }
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_LOCATION: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (MapsUtil.locationIsEnable(self)) {
                            if (mMyPlace == null) {
                                MapsUtil.getMyPlace(mGoogleApiClient, new IMaps() {
                                    @Override
                                    public void processFinished(Object obj) {
                                        mMyPlace = (Place) obj;
                                        if (mMyPlace != null) {
                                            if (mDepartureRequested) {
                                                fillAddress(mLblFrom, mMyPlace);
                                            } else {
                                                MapsUtil.moveCameraTo(mMap, mMyPlace.getLatLng());

                                                // Add marker
                                                if (mFromLatLng == null) {
                                                    mMyLocMarker = addMarker(mMyLocMarker, mMyPlace.getLatLng(), R.drawable.ic_my_location, false);
                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                if (mDepartureRequested) {
                                    fillAddress(mLblFrom, mMyPlace);
                                } else {
                                    MapsUtil.moveCameraTo(mMap, mMyPlace.getLatLng());

                                    // Add marker
                                    if (mFromLatLng == null) {
                                        mMyLocMarker = addMarker(mMyLocMarker, mMyPlace.getLatLng(), R.drawable.ic_my_location, false);
                                    }
                                }
                            }
                        } else {
                            turnOnLocationReminder(RC_TURN_ON_LOCATION, true);
                        }
                    } else {
                        showPermissionsReminder(RC_LOCATION, true);
                    }
                }
                break;
            }
            default:
                break;
        }
    }


    protected void showPermissionsReminder(final int reqCode, final boolean flag) {
        GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_remind_user_grants_permissions),
                getString(R.string.allow), getString(R.string.no_thank), false, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        GlobalFunctions.isGranted(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION}, reqCode, null);
                    }

                    @Override
                    public void onNegative() {
                        if (flag) {
//                            finish();
                        }
                    }
                });
    }

}
