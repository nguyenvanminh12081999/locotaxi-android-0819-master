package com.suusoft.locoindia.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.view.activities.ChangePassWordActivity;
import com.suusoft.locoindia.view.activities.MainActivity;
import com.suusoft.locoindia.view.activities.PhoneCountryListActivity;
import com.suusoft.locoindia.view.activities.ViewReviewsActivity;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.interfaces.IObserver;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.objects.DataPart;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.ImageUtil;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.utils.map.MapsUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SuuSoft.com on 01/12/2016.
 */

public class MyInfoAccFragment extends BaseFragment implements View.OnClickListener, IObserver {
    private static final String TAG = MyInfoAccFragment.class.getName();
    private static final int RC_ADDRESS = 134;
    private static final int RQ_CHANGE_PASS = 234;

    private EditText edtBusinessName, edtPhoneNumber, edtAddress, edtEmail;
    private CircleImageView imgAvatar;
    private ImageView imgEditAvatar;
    private ImageView imgSymbolAccount;
    private FrameLayout frDivider;
    private TextViewRegular btnEdit;
    private TextViewRegular tvChangePassword, tvNumRate, btnViewReviews;
    private TextView tvPhoneCode;
    private TextViewBold btnSave;
    private RatingBar rating_bar;
    private boolean isEdit = false;

    private View.OnFocusChangeListener listenerFocusAddress = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean isFocus) {
            if (isFocus) {
                MapsUtil.getAutoCompletePlaces(MyInfoAccFragment.this, RC_ADDRESS);
            }
        }
    };

    public static MyInfoAccFragment newInstance() {
        Bundle args = new Bundle();
        MyInfoAccFragment fragment = new MyInfoAccFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    View inflateLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_info_acc, container, false);
    }

    @Override
    void initUI(View view) {
        imgAvatar = (CircleImageView) view.findViewById(R.id.img_avatar);
        imgEditAvatar = (ImageView) view.findViewById(R.id.btn_edit_avatar);
        imgSymbolAccount = (ImageView) view.findViewById(R.id.img_symbol_account);
        tvNumRate = (TextViewRegular) view.findViewById(R.id.tv_num_rate);
        rating_bar = (RatingBar) view.findViewById(R.id.rating_bar);
        ((LayerDrawable) rating_bar.getProgressDrawable()).getDrawable(2)
                .setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        btnViewReviews = (TextViewRegular) view.findViewById(R.id.btn_view_reviews);

        edtBusinessName = (EditText) view.findViewById(R.id.edt_bussiness_name);
        edtPhoneNumber = (EditText) view.findViewById(R.id.edt_phone_number);
        edtAddress = (EditText) view.findViewById(R.id.edt_address);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);

        btnEdit = (TextViewRegular) view.findViewById(R.id.btn_edit_infomation);
        tvChangePassword = (TextViewRegular) view.findViewById(R.id.tv_change_password);
        btnSave = (TextViewBold) view.findViewById(R.id.btn_save);

        frDivider = (FrameLayout) view.findViewById(R.id.fr_divider);

        tvPhoneCode = (TextView) view.findViewById(R.id.tv_phone_code);
        getProfile();
    }

    @Override
    void initControl() {
        enableEdit(false);

        imgEditAvatar.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        btnViewReviews.setOnClickListener(this);
        btnViewReviews.setVisibility(View.GONE);
        btnSave.setOnClickListener(this);
        tvPhoneCode.setOnClickListener(this);
        edtAddress.setTag(edtAddress.getKeyListener());
        edtAddress.setKeyListener(null);
//        edtAddress.setOnClickListener(this);
//        edtAddress.setOnFocusChangeListener(listenerFocusAddress);

        frDivider.setVisibility(View.VISIBLE);


//        setData(DataStoreManager.getUser());
        edtEmail.setEnabled(false);
        edtBusinessName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    edtEmail.setFocusable(false);
                    edtPhoneNumber.requestFocus();
                }
                return false;
            }
        });
        if (com.suusoft.locoindia.network1.NetworkUtility.getInstance(getActivity()).isNetworkAvailable()) {
            getProfile();
        } else {
            AppUtil.showToast(getActivity(), R.string.msg_network_not_available);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnEdit) {
            isEdit = !isEdit;
            if (isEdit) {
                imgEditAvatar.setVisibility(View.VISIBLE);
                edtAddress.setOnFocusChangeListener(listenerFocusAddress);
                btnSave.setVisibility(View.VISIBLE);
                btnEdit.setBackgroundResource(R.drawable.bg_pressed_radius_accent);
                edtBusinessName.requestFocus();
            } else {
                setData(DataStoreManager.getUser());
                imgEditAvatar.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                btnEdit.setBackgroundResource(R.drawable.bg_pressed_radius_grey);
                edtAddress.setOnFocusChangeListener(null);
            }
            enableEdit(isEdit);
        } else if (view == btnSave) {
            if (com.suusoft.locoindia.network1.NetworkUtility.getInstance(getActivity()).isNetworkAvailable()) {
                if (isEdit) {
                    if (isValid()) {
                        updateProfile();
                    }
                } else {
                    AppUtil.showToast(getActivity(), R.string.msg_enable_edit);
                }

            } else {
                AppUtil.showToast(getActivity(), R.string.msg_no_network);
            }

        } else if (view == tvChangePassword) {
            Intent intent = new Intent(getActivity(), ChangePassWordActivity.class);
            startActivityForResult(intent, RQ_CHANGE_PASS);
        } else if (view == imgEditAvatar) {
            AppUtil.pickImage(this, AppUtil.PICK_IMAGE_REQUEST_CODE);
        } else if (view == btnViewReviews) {
            AppUtil.startActivity(getActivity(), ViewReviewsActivity.class);
        } else if (view == tvPhoneCode) {
            Intent intent = new Intent(getActivity(), PhoneCountryListActivity.class);
            startActivityForResult(intent, Args.RQ_GET_PHONE_CODE);
        } else if (view == edtAddress) {
            MapsUtil.getAutoCompletePlaces(this, RC_ADDRESS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtil.PICK_IMAGE_REQUEST_CODE) {
            Log.e("resultCode", "resultCode = " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                AppUtil.setImageFromUri(imgAvatar, data.getData());
            }
        } else if (requestCode == Args.RQ_GET_PHONE_CODE && resultCode == Activity.RESULT_OK) {
            String countryCodeSelected = data.getExtras().getString(Args.KEY_PHONE_CODE);
            tvPhoneCode.setText(countryCodeSelected);
        }
        if (requestCode == RC_ADDRESS) {
            if (resultCode == -1) {
                Place place = PlaceAutocomplete.getPlace(self, data);
                AppUtil.fillAddress(getActivity(), edtAddress, place);
                edtPhoneNumber.requestFocus();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(self, data);
                Log.e("DealNewFragment", status.getStatusMessage());
                edtPhoneNumber.requestFocus();
            } else if (resultCode == 0) {
                edtPhoneNumber.requestFocus();
                // The user canceled the operation.
            }
        }
        if (requestCode == RQ_CHANGE_PASS) {
            if (resultCode == ChangePassWordActivity.RC_CREATE_PASS) {
                tvChangePassword.setText(getString(R.string.change_pass));
            }
        }

    }

    private boolean isValid() {
        String bussinessName = edtBusinessName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhoneNumber.getText().toString().trim();
        if (StringUtil.isEmpty(bussinessName)) {
            AppUtil.showToast(getActivity(), R.string.msg_fill_full_name);
            edtBusinessName.requestFocus();
            return false;
        }
        if (StringUtil.isEmpty(phone)) {
            AppUtil.showToast(getActivity(), R.string.msg_phone_is_required);
            edtPhoneNumber.requestFocus();
            return false;
        }
        if (StringUtil.isEmpty(address)) {
            AppUtil.showToast(getActivity(), R.string.msg_address_is_required);
            edtAddress.requestFocus();
            return false;
        }
        return true;
    }

    private void enableEdit(boolean isEdit) {
        edtBusinessName.setEnabled(isEdit);
        edtAddress.setEnabled(isEdit);
        edtPhoneNumber.setEnabled(isEdit);
        tvPhoneCode.setEnabled(isEdit);
    }

    private void setData(UserObj userObj) {
        if (userObj.getProData() == null) {
            imgSymbolAccount.setImageResource(R.drawable.ic_member);
        } else {
            imgSymbolAccount.setImageResource(R.drawable.ic_pro_member);
        }
        imgSymbolAccount.setVisibility(View.VISIBLE);
        tvNumRate.setText(String.valueOf(userObj.getTotal_rate_count()));
        rating_bar.setRating(userObj.getAvg_rate());
        ImageUtil.setImage(getActivity(), imgAvatar, userObj.getAvatar(), 600, 600);
        edtBusinessName.setText(userObj.getName());
        edtPhoneNumber.setText(userObj.getPhoneNumber());
        if (userObj.getPhoneCode().isEmpty()) {
            getCountryCode();
        } else {
            tvPhoneCode.setText(userObj.getPhoneCode());
        }
        edtAddress.setText(userObj.getAddress());
        edtEmail.setText(userObj.getEmail());
        if (userObj.isSecured()) {
            tvChangePassword.setText(getString(R.string.change_pass));
        } else {
            tvChangePassword.setText(getString(R.string.create_pass));
        }
    }

    private void getCountryCode() {
        String[] rl = getResources().getStringArray(R.array.CountryCodes);
        int curPosition = AppUtil.getCurentPositionCountryCode(getActivity());
        String phoneCode = rl[curPosition].split(",")[0];
        tvPhoneCode.setText(phoneCode);
    }

    private void updateProfile() {
        String bussinessName = edtBusinessName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String phoneCode = tvPhoneCode.getText().toString().trim();
        String phone = phoneCode + " " + phoneNumber;
        DataPart avatar = null;
        if (imgAvatar.getDrawable() != null) {
            avatar = new DataPart("avatar.png", AppUtil.getFileDataFromDrawable(getActivity(), imgAvatar.getDrawable()), DataPart.TYPE_IMAGE);
        }

        ModelManager.updateProfileNormal(getActivity(), avatar, bussinessName, phone, address, new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    ApiResponse response = new ApiResponse(jsonObject);
                    if (!response.isError()) {
                        UserObj userObj = response.getDataObject(UserObj.class);
                        userObj.setToken(DataStoreManager.getUser().getToken());
                        userObj.setRememberMe(DataStoreManager.getUser().isRememberMe());
                        DataStoreManager.saveUser(userObj);
                        AppController.getInstance().setUserUpdated(true);
                        enableEdit(false);
                        btnEdit.setBackgroundResource(R.drawable.bg_pressed_radius_grey);
                        isEdit = false;
                        btnSave.setVisibility(View.GONE);
                        imgEditAvatar.setVisibility(View.GONE);
                        AppUtil.showToast(getActivity(), R.string.msg_update_success);
                        setData(userObj);
                        ((MainActivity) getActivity()).setTitle(userObj.getName());
                    } else {
                        Toast.makeText(self, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {
                Log.e(TAG, "ERROR: update profile!");
            }
        });
    }

    private void getProfile() {
        ModelManager.getProfile(getActivity(), DataStoreManager.getUser().getId(), new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    ApiResponse response = new ApiResponse(jsonObject);
                    if (!response.isError()) {
                        UserObj userObj = response.getDataObject(UserObj.class);
                        userObj.setToken(DataStoreManager.getUser().getToken());
                        userObj.setRememberMe(DataStoreManager.getUser().isRememberMe());
                        DataStoreManager.saveUser(userObj);
                        AppController.getInstance().setUserUpdated(true);
                        setData(userObj);
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


    @Override
    public void update() {
        setData(DataStoreManager.getUser());
    }
}
