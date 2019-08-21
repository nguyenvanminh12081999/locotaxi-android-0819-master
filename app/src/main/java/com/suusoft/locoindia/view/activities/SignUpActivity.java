package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.NetworkUtility;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = SignUpActivity.class.getSimpleName();

    private EditText mTxtFullName, mTxtEmail, mTxtPhoneNumber, mTxtPassword, mTxtRetypePassword;
    private TextView mLblCreateAccount;
    private TextViewRegular mLblAlreadyAMember;
    private CheckBox mChkRememberMe;
    private TextView tvPhoneCode;
    private boolean mIsRegistered;

    private String countryCodeSelected="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    void inflateLayout() {
        setContentView(R.layout.activity_sign_up);
    }

    @Override
    void initUI() {
        // Hide actionbar
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mTxtFullName = (EditText) findViewById(R.id.txt_full_name);
        mTxtEmail = (EditText) findViewById(R.id.txt_email);
        mTxtPhoneNumber = (EditText) findViewById(R.id.txt_phone);
        mTxtPassword = (EditText) findViewById(R.id.txt_password);
        mTxtRetypePassword = (EditText) findViewById(R.id.txt_retype_password);
        mLblCreateAccount = (TextView) findViewById(R.id.lbl_create_account);
        mLblAlreadyAMember = (TextViewRegular) findViewById(R.id.lbl_already_a_member);
        mChkRememberMe = (CheckBox) findViewById(R.id.chk_remember_me);
        tvPhoneCode = (TextView) findViewById(R.id.tv_phone_code);

        getCountryCode();
    }

    @Override
    void initControl() {
        mLblCreateAccount.setOnClickListener(this);
        mLblAlreadyAMember.setOnClickListener(this);
        tvPhoneCode.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        if (v == mLblCreateAccount) {
            if (isValid()) {
                createAccount();
            }
        } else if (v == mLblAlreadyAMember) {
            GlobalFunctions.startActivityWithoutAnimation(self, SplashLoginActivity.class);
            finish();
        }else if (v == tvPhoneCode){
            tvPhoneCode.setBackgroundResource(R.drawable.bg_border_grey);
            GlobalFunctions.startActivityForResult(this, PhoneCountryListActivity.class,Args.RQ_GET_PHONE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Args.RQ_GET_PHONE_CODE && resultCode == RESULT_OK){
            countryCodeSelected = data.getExtras().getString(Args.KEY_PHONE_CODE);
            tvPhoneCode.setText(countryCodeSelected);
        }
    }

    @Override
    public void onBackPressed() {
        GlobalFunctions.startActivityWithoutAnimation(self, SplashLoginActivity.class);
        finish();
    }

    private void getCountryCode() {
        String[] rl = getResources().getStringArray(R.array.CountryCodes);
        int curPosition =  AppUtil.getCurentPositionCountryCode(this);
        String phoneCode = rl[curPosition].split(",")[0];
        tvPhoneCode.setText(phoneCode);
    }

    private void gotoLoginPage() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Args.IS_FROM_SIGNUP,true);
        if (mIsRegistered && mChkRememberMe.isChecked()) {
            String email = mTxtEmail.getText().toString().trim();
            String password = mTxtPassword.getText().toString().trim();

            if (!email.isEmpty()) {
                bundle.putString(Args.EMAIL, email);
            }
            if (!password.isEmpty()) {
                bundle.putString(Args.PASSWORD, password);
            }

        }

        GlobalFunctions.startActivityWithoutAnimation(self, LoginActivity.class, bundle);
        finish();
    }

    private void createAccount() {
        if (NetworkUtility.getInstance(self).isNetworkAvailable()) {
            final String fullName = mTxtFullName.getText().toString().trim();
            final String email = mTxtEmail.getText().toString().trim();
            final String phoneNumber = mTxtPhoneNumber.getText().toString().trim();
            final String password = mTxtPassword.getText().toString().trim();

            String phone = tvPhoneCode.getText().toString().trim()+" "+phoneNumber;

            ModelManager.registerNormalAccount(self, Volley.newRequestQueue(self), fullName, email,
                    phone, password, new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            JSONObject jsonObject = (JSONObject) object;
                            ApiResponse response = new ApiResponse(jsonObject);
                            if (!response.isError()) {
                                mIsRegistered = true;
                                if (mChkRememberMe.isChecked()) {
                                    UserObj userObj = new UserObj();
                                    userObj.setName(fullName);
                                    userObj.setEmail(email);
                                    userObj.setPhone(phoneNumber);
                                    userObj.setRememberMe(mChkRememberMe.isChecked());
                                    DataStoreManager.saveUser(userObj);
                                }
                                Toast.makeText(self, R.string.msg_register_success, Toast.LENGTH_LONG).show();
                                gotoLoginPage();
                            } else {
                                Toast.makeText(self, response.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(self, R.string.msg_have_some_errors, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(self, getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid() {
        String fullName = mTxtFullName.getText().toString().trim();
        String email = mTxtEmail.getText().toString().trim();
        String phoneNumber = mTxtPhoneNumber.getText().toString().trim();
        String password = mTxtPassword.getText().toString().trim();
        String retypePassword = mTxtRetypePassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(self, R.string.msg_name_is_required, Toast.LENGTH_SHORT).show();
            mTxtFullName.requestFocus();

            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(self, R.string.msg_email_is_required, Toast.LENGTH_SHORT).show();
            mTxtEmail.requestFocus();

            return false;
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(self, R.string.msg_phone_is_required, Toast.LENGTH_SHORT).show();
            mTxtPhoneNumber.requestFocus();

            return false;
        }
        if (!StringUtil.isValidatePassword(password)) {
            Toast.makeText(self, R.string.msg_password_is_required, Toast.LENGTH_LONG).show();
            mTxtPassword.requestFocus();

            return false;
        }
        if (!retypePassword.equals(password)) {
            Toast.makeText(self, R.string.msg_password_is_not_match, Toast.LENGTH_SHORT).show();
            mTxtRetypePassword.requestFocus();

            return false;
        }

        return true;
    }

    private void getListCode(){

        Map<String, String> languagesMap = new TreeMap<>();

        Locale[] locales = Locale.getAvailableLocales();

        for (Locale obj : locales) {

            if ((obj.getDisplayCountry() != null) && (!"".equals(obj.getDisplayCountry()))) {
                languagesMap.put(obj.getCountry(), obj.getLanguage());
                Log.e(TAG, "country: "+ obj.getCountry());
            }

        }


    }

}
