package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.interfaces.IRedeem;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.NetworkUtility;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewLightItalic;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SuuSoft.com on 05/12/2016.
 */

public class RedeemActivity extends com.suusoft.locoindia.base.BaseActivity implements View.OnClickListener, IRedeem {
    private static final String ACTION_REDEEM = "redeem";
    private static final String TAG = RedeemActivity.class.getName();
    private TextViewRegular tvCreditsAvailable;
    private EditText edtCredits;
    private TextViewLightItalic tvInfo;
    private TextViewBold btnRedeem;
    private EditText edtDescription;
    private UserObj user;

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_redeem;
    }

    @Override
    protected void getExtraData(Intent intent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.onPause();
    }

    @Override
    protected void initilize() {
        user = DataStoreManager.getUser();
    }

    @Override
    protected void initView() {
        setToolbarTitle(R.string.iwana_pay_redeem);
        AppController.getInstance().setiRedeem(this);
        tvCreditsAvailable = (TextViewRegular) findViewById(R.id.tv_credits);
        edtCredits = (EditText) findViewById(R.id.edt_credits);
        tvInfo = (TextViewLightItalic) findViewById(R.id.tv_info);
        btnRedeem = (TextViewBold) findViewById(R.id.btn_functions);
        edtDescription = (EditText) findViewById(R.id.edt_description);

    }

    @Override
    protected void onViewCreated() {
        btnRedeem.setOnClickListener(this);
        edtCredits.requestFocus();
//        tvCreditsAvailable.setText(AppUtil.formatCurrency(user.getBalance()));
        if (NetworkUtility.getInstance(this).isNetworkAvailable()) {
            getProfile();
        } else {
            AppUtil.showToast(this, R.string.msg_network_not_available);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == btnRedeem) {
            if (NetworkUtility.getInstance(getApplicationContext()).isNetworkAvailable()) {
                if (isValid()) {
                    redeem();
                }
            } else {
                AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
            }
        }
    }

    private void redeem() {
        ModelManager.transaction(this, "", edtCredits.getText().toString(), ACTION_REDEEM, edtCredits.getText().toString(), "", "", new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                org.json.JSONObject jsonObject = (JSONObject) object;
                ApiResponse apiResponse = new ApiResponse(jsonObject);
                if (!apiResponse.isError()) {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_transaction_success);
                    finish();
                } else {
                    AppUtil.showToast(getApplicationContext(), apiResponse.getMessage());
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "REDEEM: Error!");
            }
        });
    }

    private boolean isValid() {
        String amount = edtCredits.getText().toString();
        String description = edtDescription.getText().toString();
        String numCredits = tvCreditsAvailable.getText().toString().replaceAll(",","");
//        DecimalFormat format = new DecimalFormat();
//        Number number = null;
//        try {
//            number = format.parse(numCredits);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (StringUtil.isEmpty(amount)) {
            AppUtil.showToast(getApplicationContext(), R.string.msg_amout_is_require);
            edtCredits.requestFocus();
            return false;
        } else {
            int amountCredit = Integer.parseInt(amount);
            if (amountCredit <= 0) {
                AppUtil.showToast(getApplicationContext(), R.string.msg_value_credits);
                edtCredits.requestFocus();
                return false;
            }else if (amountCredit > Double.parseDouble(numCredits)) {
                AppUtil.showToast(getApplicationContext(), R.string.msg_enought_credits);
                return false;
            }
        }
        if (StringUtil.isEmpty(description)) {
            AppUtil.showToast(getApplicationContext(), R.string.msg_description_is_require);
            edtDescription.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void updateBalace(final float balance) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCreditsAvailable.setText(StringUtil.convertNumberToString(balance, 1));
//                tvCreditsAvailable.setText(AppUtil.formatCurrency(balance));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppController.getInstance().setiRedeem(null);
    }

    private void getProfile() {
        ModelManager.getProfile(this, DataStoreManager.getUser().getId(), new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    ApiResponse response = new ApiResponse(jsonObject);
                    if (!response.isError()) {
                        UserObj userObj = response.getDataObject(UserObj.class);
                        UserObj user = DataStoreManager.getUser();
                        user.setBalance(userObj.getBalance());
                        DataStoreManager.saveUser(user);
//                        tvCreditsAvailable.setText(AppUtil.formatCurrency(user.getBalance()));
                        tvCreditsAvailable.setText(StringUtil.convertNumberToString(user.getBalance(),1));

                    } else {
                        Toast.makeText(self, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        });
    }
}
