package com.suusoft.locoindia.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.view.activities.BuyCreditsActivity;
import com.suusoft.locoindia.view.activities.HistoryActivity;
import com.suusoft.locoindia.view.activities.RedeemActivity;
import com.suusoft.locoindia.view.activities.TransferActivity;
import com.suusoft.locoindia.utils.AppUtil;

/**
 * Created by SuuSoft.com on 30/11/2016.
 */

public class PayFragment extends BaseFragment implements View.OnClickListener {
    private View btnBuyCredits, btnRedeem, btnTranfer, btnHistories;

    public static PayFragment newInstance() {

        Bundle args = new Bundle();

        PayFragment fragment = new PayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    View inflateLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pay, container, false);
    }

    @Override
    void initUI(View view) {

        btnBuyCredits = (View) view.findViewById(R.id.btn_buy_credits);
        btnHistories = (View) view.findViewById(R.id.btn_history);
        btnRedeem = (View) view.findViewById(R.id.btn_functions);
        btnTranfer = (View) view.findViewById(R.id.btn_transfer);
    }

    @Override
    void initControl() {
        btnTranfer.setOnClickListener(this);
        btnRedeem.setOnClickListener(this);
        btnHistories.setOnClickListener(this);
        btnBuyCredits.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnBuyCredits) {
            AppUtil.startActivity(getActivity(), BuyCreditsActivity.class);
        } else if (view == btnHistories) {
            AppUtil.startActivity(getActivity(), HistoryActivity.class);
        } else if (view == btnRedeem) {
            AppUtil.startActivity(getActivity(), RedeemActivity.class);
        } else if (view == btnTranfer) {
            AppUtil.startActivity(getActivity(), TransferActivity.class);
        }
    }
}
