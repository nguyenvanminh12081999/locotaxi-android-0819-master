package com.suusoft.locoindia.view.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

/**
 * Created by SuuSoft.com on 27/12/2016.
 */

public class GeneralInformationFragment extends com.suusoft.locoindia.base.BaseFragment {
    private EditText edtBusinessName, edtPhoneNumber, edtAddress, edtEmail;
    private TextViewRegular btnEdit;
    private TextView tvPhoneCode;

    private UserObj user;


    public static GeneralInformationFragment newInstance(Bundle args) {
        GeneralInformationFragment fragment = new GeneralInformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.layout_general_infomation;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        edtBusinessName = (EditText) view.findViewById(R.id.edt_bussiness_name);
        edtPhoneNumber = (EditText) view.findViewById(R.id.edt_phone_number);
        edtAddress = (EditText) view.findViewById(R.id.edt_address);
        edtEmail = (EditText) view.findViewById(R.id.edt_email);
        tvPhoneCode = (TextView) view.findViewById(R.id.tv_phone_code);

        btnEdit = (TextViewRegular) view.findViewById(R.id.btn_edit_infomation);
        btnEdit.setVisibility(View.GONE);
    }

    @Override
    protected void getData() {
        user = getArguments().getParcelable(UserObj.DATA_USER);
        if (user != null) {
            setData();
        }
    }

    private void setData() {
        edtBusinessName.setText(user.getName());
        edtPhoneNumber.setText(user.getPhoneNumber());
        edtAddress.setText(user.getAddress());
        edtEmail.setText(user.getEmail());
        tvPhoneCode.setText(user.getPhoneCode());
        enableEdit(false);
    }

    private void enableEdit(boolean isEdit) {
        edtBusinessName.setEnabled(isEdit);
        edtAddress.setEnabled(isEdit);
        edtPhoneNumber.setEnabled(isEdit);
        edtPhoneNumber.setEnabled(isEdit);
    }
}
