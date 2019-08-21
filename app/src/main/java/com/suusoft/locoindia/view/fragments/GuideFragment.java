package com.suusoft.locoindia.view.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

/**
 * Created by SuuSoft.com on 24/12/2016.
 */

public class GuideFragment extends com.suusoft.locoindia.base.BaseFragment {
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private TextViewBold tvTitle;
    private TextViewRegular tvDescriptions;
    private RelativeLayout rltParent;


    public static GuideFragment newInstance(String title, String description) {

        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESCRIPTION, description);
        GuideFragment fragment = new GuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_guide;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initView(View view) {
        tvTitle = (TextViewBold) view.findViewById(R.id.tv_title);
        tvDescriptions = (TextViewRegular) view.findViewById(R.id.tv_description);
        rltParent = (RelativeLayout) view.findViewById(R.id.rlt_parent);
    }

    @Override
    protected void getData() {
        Bundle bundle = getArguments();
        String title = bundle.getString(TITLE);

        if (title.equals(getString(R.string.find_taxi))) {
            rltParent.setBackgroundResource(R.drawable.background_find_taxi);
        } else if (title.equals(getString(R.string.chat))) {
            rltParent.setBackgroundResource(R.drawable.background_chat);
        } else if (title.equals(getString(R.string.vip))) {
            rltParent.setBackgroundResource(R.drawable.background_vip);
        }

        tvTitle.setText(title);
        tvDescriptions.setText(bundle.getString(DESCRIPTION));

    }


}
