package com.suusoft.locoindia.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IConfirmation;
import com.suusoft.locoindia.utils.map.MapsUtil;

/**
 * Created by SuuSoft.com on 6/17/2016.
 */
public abstract class BaseActivity extends AbstractActivity {

    protected abstract ToolbarType getToolbarType();
    protected abstract int getLayoutInflate();
    protected abstract void getExtraData(Intent intent);
    protected abstract void initilize();
    protected abstract void initView();
    protected abstract void onViewCreated();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(getLayoutInflate(),contentLayout);
        initilize();
        initView();
        onViewCreated();
    }

    public void showPermissionsReminder(final int reqCode, final boolean flag) {
        GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_remind_user_grants_permissions),
                getString(R.string.allow), getString(R.string.no_thank), false, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        GlobalFunctions.isGranted(BaseActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION}, reqCode, null);
                    }

                    @Override
                    public void onNegative() {
                        if (flag) {
                            finish();
                        }
                    }
                });
    }

    public void turnOnLocationReminder(final int reqCode, final boolean flag) {
        GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_remind_user_turn_on_location),
                getString(R.string.turn_on), getString(R.string.no_thank), false, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        MapsUtil.displayLocationSettingsRequest(BaseActivity.this, reqCode);
                    }

                    @Override
                    public void onNegative() {
                        if (flag) {
                            finish();
                        }
                    }
                });
    }

}
