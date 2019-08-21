package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.view.fragments.ReviewAccFragment;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.objects.UserObj;

/**
 * Created by SuuSoft.com on 02/12/2016.
 */

public class ViewReviewsActivity extends com.suusoft.locoindia.base.BaseActivity {
    public static final String TAG = ViewReviewsActivity.class.getSimpleName();
    public static final int RESULT_CODE_VIEW_REVIEWS = 1234;

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_view_reviews;
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


    }

    @Override
    protected void onViewCreated() {
        Bundle bundle = getIntent().getExtras();
        String id = null;
        String name = null;
        String i_am = "";
        if (bundle != null) {
            Log.e(TAG, "bundle != null");
            if (bundle.containsKey(Args.USER_ID)) {
                Log.e(TAG, "bundle.containsKey(Args.USER_ID)");
                id = bundle.getString(Args.USER_ID);
                name = bundle.getString(Args.NAME_DRIVER);
                i_am = bundle.getString(Args.I_AM);
                if (i_am.equals(Constants.BUYER) || i_am.equals(Constants.PASSENGER)) {
                    if (i_am.equals(Constants.BUYER)) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.ll_parent_content, ReviewAccFragment.newInstance(id, Constants.PRO, "")).commit();
                    } else if (i_am.equals(Constants.PASSENGER)){
                        getSupportFragmentManager().beginTransaction().replace(R.id.ll_parent_content, ReviewAccFragment.newInstance(id, Constants.PRO, "")).commit();
                    }

                } else if (i_am.equals(Constants.SELLER) || i_am.equals(Constants.DRIVER)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.ll_parent_content, ReviewAccFragment.newInstance(id, Constants.BASIC, "")).commit();
                }

            }
        } else {
            Log.e(TAG, "bundle = null");
            UserObj userObj = DataStoreManager.getUser();
            id = userObj.getId();
            name = userObj.getName();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ll_parent_content, ReviewAccFragment.newInstance(id, Constants.ALL_REVIEWS, "")).commit();
        }


        setToolbarTitle(name);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CODE_VIEW_REVIEWS);
    }
}
