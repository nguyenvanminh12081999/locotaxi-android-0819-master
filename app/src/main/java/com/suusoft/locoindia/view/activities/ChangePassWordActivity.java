package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.base.*;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.NetworkUtility;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SuuSoft.com on 08/12/2016.
 */

public class ChangePassWordActivity extends com.suusoft.locoindia.base.BaseActivity implements View.OnClickListener {
    private static final String TAG = ChangePassWordActivity.class.getName();
    public static final int RC_CREATE_PASS = 123;
    private EditText edtOldPass, edtPass, edtConfirmPass;
    private TextViewBold btnChangePassWord;
    private EditText edtEmail;

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_change_pass_word;
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

    }

    @Override
    protected void initView() {

        edtOldPass = (EditText) findViewById(R.id.edt_current_pass);
        edtPass = (EditText) findViewById(R.id.edt_password);
        edtConfirmPass = (EditText) findViewById(R.id.edt_password_confirm);
        btnChangePassWord = (TextViewBold) findViewById(R.id.btn_change_password);
        edtEmail = (EditText) findViewById(R.id.edt_email);
    }

    @Override
    protected void onViewCreated() {
        btnChangePassWord.setOnClickListener(this);
        UserObj userObj = DataStoreManager.getUser();
        edtEmail.setTag(edtEmail.getKeyListener());
        edtEmail.setKeyListener(null);
        if (userObj.isSecured()) {
            setToolbarTitle(R.string.change_pass);
            edtEmail.setVisibility(View.GONE);
            edtOldPass.setVisibility(View.VISIBLE);
            btnChangePassWord.setText(getString(R.string.button_change_pass));
        } else {
            setToolbarTitle(R.string.create_pass);
            edtEmail.setText(userObj.getEmail());
            edtEmail.setVisibility(View.VISIBLE);
            edtOldPass.setVisibility(View.GONE);
            btnChangePassWord.setText(getString(R.string.button_create_pass));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnChangePassWord) {
            if (NetworkUtility.getInstance(getApplicationContext()).isNetworkAvailable()) {

                if (isValid()) {
                    String currentPass = edtOldPass.getText().toString().trim();
                    String pass = edtPass.getText().toString().trim();
                    if (DataStoreManager.getUser().isSecured()) {
                        ModelManager.changePassword(this, pass, currentPass, new ModelManagerListener() {
                            @Override
                            public void onSuccess(Object object) {
                                try {
                                    JSONObject jsonObject = new JSONObject(object.toString());
                                    ApiResponse response = new ApiResponse(jsonObject);
                                    if (!response.isError()) {
                                        AppUtil.showToast(getApplicationContext(), R.string.msg_success);
                                        finish();
                                    } else {
                                        Toast.makeText(self, response.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError() {
                                Log.e(TAG, "");
                            }
                        });
                    } else {
                        ModelManager.createPassword(this, pass, new ModelManagerListener() {
                            @Override
                            public void onSuccess(Object object) {
                                try {
                                    JSONObject jsonObject = new JSONObject(object.toString());
                                    ApiResponse response = new ApiResponse(jsonObject);
                                    if (!response.isError()) {
                                        UserObj userObj = DataStoreManager.getUser();
                                        userObj.setIs_secured(1);
                                        DataStoreManager.saveUser(userObj);
                                        AppUtil.showToast(getApplicationContext(), R.string.msg_success);
                                        setResult(RC_CREATE_PASS);
                                        finish();
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
            } else {
                AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
            }
        }
    }

    private boolean isValid() {
        String oldpass = edtOldPass.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();
        if (DataStoreManager.getUser().isSecured()) {
            if (StringUtil.isEmpty(oldpass)) {
                AppUtil.showToast(this, R.string.msg_fill_old_pass);
                return false;
            }
        }

        if (StringUtil.isEmpty(pass)) {
            AppUtil.showToast(this, R.string.msg_password_is_required);
            return false;
        } else {
            if (!StringUtil.isValidatePassword(pass)) {
                AppUtil.showToast(this, R.string.msg_password_is_required);
                return false;
            }
        }
        if (StringUtil.isEmpty(confirmPass)) {
            AppUtil.showToast(this, R.string.msg_confirm_pass_is_required);
            return false;
        } else if (!confirmPass.equals(pass)) {
            AppUtil.showToast(this, R.string.msg_password_is_not_match);
            return false;
        }
        return true;
    }

}
