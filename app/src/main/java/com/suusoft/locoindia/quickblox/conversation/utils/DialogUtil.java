package com.suusoft.locoindia.quickblox.conversation.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Toast;

/**
 * QuickBlox team
 */
public class DialogUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static  void showToast(Context context, int messageId) {
        Toast.makeText(context, context.getString(messageId), Toast.LENGTH_LONG).show();
    }

    public static Dialog setDialogCustomView(Context context, int layout, boolean isCancelable){
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(isCancelable);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        return dialog;
    }
}
