package com.suusoft.locoindia.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.core.utils.Quarter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.suke.widget.SwitchButton;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.quickblox.conversation.utils.DialogUtil;
import com.suusoft.locoindia.retrofit.APIService;
import com.suusoft.locoindia.retrofit.ApiUtils;
import com.suusoft.locoindia.retrofit.response.RespondGetOnline;
import com.suusoft.locoindia.utils.NetworkUtility;
import com.suusoft.locoindia.view.activities.BecomeAProActivity;
import com.suusoft.locoindia.view.activities.BuyCreditsActivity;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IConfirmation;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.objects.DriverObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.objects.SettingsObj;
import com.suusoft.locoindia.objects.TransportDealObj;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.DateTimeUtil;
import com.suusoft.locoindia.utils.map.LocationService;
import com.suusoft.locoindia.view.activities.ChatActivityBySuusoft;
import com.suusoft.locoindia.view.activities.TripManagerActivity;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SuuSoft.com on 10/06/2017.
 */

public class TaxiNewFragment extends com.suusoft.locoindia.base.BaseFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_CAMERA_PERMISSION = 1;
    public static final int REQUEST_CODE_CAPTURE_IMAGE = 1323;
    private static final int TIME_ACTIVE = 12;
    private static final int REQUEST_CODE_BECOME_PRO = 14;
    private static final String TAG = "TaxiNew";
    private String token = "";
    private int updateDuration = 0;

    private TextViewBold btnNewTaxi;
    private SettingsObj settingUtitlityObj;
    private SwitchButton switchButton;
    private TextView btnBuy,btnTripmanager,tvIAFD,hour;
    private TextView hourAvaiable;
    private View v;
    private boolean running = false;

    public static TaxiNewFragment newInstance() {
        Bundle args = new Bundle();
        TaxiNewFragment fragment = new TaxiNewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_taxi_new;
    }

    @Override
    protected void init() {
        setSavedViewState(true);
    }

    @Override
    protected void initView(View view) {
        btnNewTaxi = (TextViewBold) view.findViewById(R.id.btn_create_new);
        switchButton = view.findViewById(R.id.switchDriving);
        btnBuy = view.findViewById(R.id.btnDurationBuying);
        btnTripmanager = view.findViewById(R.id.btnTripmanager);
        hourAvaiable = view.findViewById(R.id.tvHourAvaiable);
        tvIAFD = view.findViewById(R.id.tvIAFD);
        v = view.findViewById(R.id.view2);
        hour = view.findViewById(R.id.tvlabelHourAvaiable);


        initControl();
        setAvaiable();
        checkDataOnline();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_CAMERA_PERMISSION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AppUtil.captureImage(TaxiNewFragment.this, REQUEST_CODE_CAPTURE_IMAGE);
                } else {
                    showPermissionReminder();
                }
            }
        }
    }

    private void showPermissionReminder() {
        GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_remind_user_grant_camera_permission),
                getString(R.string.allow), getString(R.string.no), true, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, RC_CAMERA_PERMISSION);
                    }

                    @Override
                    public void onNegative() {
                    }
                });
    }

    private void initControl() {
        btnNewTaxi.setOnClickListener(this);
        checkUseIsDriver();
        btnTripmanager.setOnClickListener(this);
        btnBuy.setOnClickListener(this);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void getData() {

    }


   private void setAvaiable() {
        if(DataStoreManager.getUser().getDriverData() != null){

            btnNewTaxi.setVisibility(View.GONE);

        }else {

            btnNewTaxi.setVisibility(View.VISIBLE);
            switchButton.setVisibility(View.GONE);
            btnBuy.setVisibility(View.GONE);
            btnTripmanager.setVisibility(View.GONE);
            tvIAFD.setVisibility(View.GONE);
            v.setVisibility(View.GONE);
            hour.setVisibility(View.GONE);
            hourAvaiable.setVisibility(View.GONE);

        }
    }
    private void checkDataOnline(){
        if(DataStoreManager.isOnline()){
            switchButton.setChecked(true);
        }else {
            switchButton.setChecked(false);
        }
    }

        private void processADriverSelected() {
        GlobalFunctions.showConfirmationDialog(getActivity(), getString(R.string.do_you_want_to_start_driving_or_delivering_now),
                getString(R.string.yes), getString(R.string.no), true, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        GlobalFunctions.showConfirmationDialog(getActivity(), getString(R.string.once_you_click_the_confirm_button_your_deal_will_be_active_for_12_hours),
                                getString(R.string.confirm), getString(R.string.canceled), true, new IConfirmation() {
                                    @Override
                                    public void onPositive() {
                                        settingUtitlityObj = DataStoreManager.getSettingUtility();
                                        if (settingUtitlityObj == null) {
                                            ModelManager.getSettingUtility(getActivity(), new ModelManagerListener() {
                                                @Override
                                                public void onSuccess(Object object) {
                                                    org.json.JSONObject jsonObject = (JSONObject) object;
                                                    ApiResponse apiResponse = new ApiResponse(jsonObject);
                                                    if (!apiResponse.isError()) {
                                                        settingUtitlityObj = apiResponse.getDataObject(SettingsObj.class);
                                                        if (settingUtitlityObj != null) {

                                                            submit1();

                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
                                        }
                                        else {

                                            submit1();

                                        }
                                    }

                                    @Override
                                    public void onNegative() {
                                    }
                                });
                    }

                    @Override
                    public void onNegative() {
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_BECOME_PRO) {
            if (resultCode == BecomeAProActivity.RESULT_CODE_BECOME_PRO) {
                if (DataStoreManager.getUser().getDriverData() != null) {
                    processADriverSelected();
                }

            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view==btnNewTaxi){
            UserObj user = DataStoreManager.getUser();
            if (user.getDriverData() == null ) {
                startActivityForResult(new Intent(getActivity(), BecomeAProActivity.class), REQUEST_CODE_BECOME_PRO);

            } else {
                processADriverSelected();
            }
        }else if(view == btnBuy){
             buyDura(Constants.DURATION_BUYING);
        }else if(view == btnTripmanager){
            startActivity(new Intent(self, TripManagerActivity.class));
        }
    }

    private void buyDura(String mode){
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

        Log.e("freePerHour", "feePerHour " + feePerHour);
        Log.e("Setting", "setting " + new Gson().toJson(settingsObj));

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
    private void changeDriverMode(final String mode, final int duration) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.activateDriverMode(self, mode, duration, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;

                    //Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();

                    if (!JSONParser.responseIsSuccess(jsonObject)) {
                        Log.d(TAG, "onSuccess: ");
                    } else {
                        if (mode.equals(Constants.OFF)) {
                            // Set driver is unavailable
                            UserObj userObj = DataStoreManager.getUser();
                            userObj.getDriverData().setAvailable(DriverObj.DRIVER_UNAVAILABLE);
                            DataStoreManager.saveUser(userObj);

                            switchButton.setChecked(false);

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
    private void buyDuration() {
        Log.e("QQ", token);
        APIService apiService = ApiUtils.getAPIService();
        Call<RespondGetOnline> callBack = apiService.getOnline(token);
        callBack.enqueue(new Callback<RespondGetOnline>() {
            @Override
            public void onResponse(Call<RespondGetOnline> call, Response<RespondGetOnline> response) {
                if (response != null) {
                    String dudu = response.body().data;
                    if(dudu != null) {
                        Log.e("duration","duration"+ dudu);
                        updateDuration = Integer.parseInt(dudu);
                        if (DataStoreManager.isOnline()) {
                            Log.e("DURATION", updateDuration + "");
                            UserObj userObj = DataStoreManager.getUser();
                            userObj.getDriverData().setAvailable(DriverObj.DRIVER_AVAILABLE);
                            DataStoreManager.saveUser(userObj);

                            if (updateDuration > 0) {
                                Log.d("updateduration1", "onResponse: "+updateDuration);
                                hourAvaiable.setText("(" + updateDuration + "h"+")");
                                AppUtil.showToast(self, R.string.you_are_online);
                                DataStoreManager.saveOnline(true);
                            } else {
                                Log.d("updateduration2", "onResponse: "+updateDuration);
                                hourAvaiable.setText(" (" + 0 + "h) ");
                                AppUtil.showToast(self, R.string.you_are_online);
                                DataStoreManager.saveOnline(true);
                            }
                        }
                    }else {
                        Toast.makeText(self, dudu, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespondGetOnline> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
    private void submit1() {
        if (settingUtitlityObj != null) {
            int price = Integer.parseInt(settingUtitlityObj.getDriver_online_rate());
            if (price * TIME_ACTIVE < DataStoreManager.getUser().getBalance()) {
                ModelManager.activateDriverMode(getActivity(), Constants.DURATION_BUYING, TIME_ACTIVE, new ModelManagerListener() {
                    @Override
                    public void onSuccess(Object object) {
                        org.json.JSONObject jsonObject = (JSONObject) object;
                        ApiResponse apiResponse = new ApiResponse(jsonObject);
                        if (!apiResponse.isError()) {
                            LocationService.start(getActivity(), LocationService.REQUEST_LOCATION);

                            String notify = String.format(getString(R.string.msg_deal_create_success), DateTimeUtil.getEndAtFromNow(TIME_ACTIVE));
                            Toast.makeText(self, notify, Toast.LENGTH_LONG).show();

                            UserObj userObj = DataStoreManager.getUser();
                            userObj.getDriverData().setAvailable(DriverObj.DRIVER_AVAILABLE);
                            DataStoreManager.saveUser(userObj);
                            gotoChat("", "");

                        } else {
                            Toast.makeText(self, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            } else {
                showDialogBuyCredit();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppUtil.hideSoftKeyboard((Activity) self);
    }

    private void gotoChat(String dealId, String dealName) {
        RecentChatObj recentChatObj;
        UserObj userObj = DataStoreManager.getUser();
        userObj.getDriverData().setAvailable(DriverObj.DRIVER_AVAILABLE);
        DataStoreManager.saveUser(userObj);
        TransportDealObj transportDealObj = new TransportDealObj();
        transportDealObj.setDriverId(DataStoreManager.getUser().getId());
        recentChatObj = new RecentChatObj(transportDealObj, null, null);
        ChatActivityBySuusoft.start(getActivity(), null, recentChatObj);
    }

    private void showDialogBuyCredit() {
        GlobalFunctions.showConfirmationDialog(getActivity(), getString(R.string.msg_not_enought_credits),
                getString(R.string.button_buy_credits), getString(R.string.button_promotions), true, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Args.IS_NEED_FINISH, true);
                        GlobalFunctions.startActivityForResult(TaxiNewFragment.this, BuyCreditsActivity.class, Args.RQ_BUY_CREDIT, bundle);
                    }

                    @Override
                    public void onNegative() {
                        AppUtil.showToast(self, R.string.msg_promotion_is_developping);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUseIsDriver();
    }

    private void checkUseIsDriver() {
        if (DataStoreManager.getUser().getDriverData() == null) {
            btnNewTaxi.setText(getResources().getString(R.string.create_new_taxi));
        } else {
            btnNewTaxi.setText(getResources().getString(R.string.start_driver));
        }
    }

}