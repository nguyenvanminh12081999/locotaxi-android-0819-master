package com.suusoft.locoindia.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.suusoft.locoindia.AppController;

public class ResourceUtils {

    public static String getString(@StringRes int stringId) {
        return AppController.getInstance().getString(stringId);
    }

    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return ContextCompat.getDrawable(AppController.getInstance(), drawableId);
    }

    public static int getColor(@ColorRes int colorId) {
        return ContextCompat.getColor(AppController.getInstance(), colorId);
    }

    public static int getDimen(@DimenRes int dimenId) {
        return (int) AppController.getInstance().getResources().getDimension(dimenId);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}
