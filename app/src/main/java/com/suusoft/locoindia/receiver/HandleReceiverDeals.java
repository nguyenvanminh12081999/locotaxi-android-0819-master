package com.suusoft.locoindia.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.quickblox.conversation.utils.DialogUtil;
import com.suusoft.locoindia.view.activities.SplashLoginActivity;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;


/**
 * Created by SuuSoft.com on 03/26/2018.
 */

public class HandleReceiverDeals {

    private static final String TAG  = HandleReceiverDeals.class.getSimpleName();
    private Context context;
    private ReceiverDeals receiverDeals;
    private RecentChatObj mRecentChatObj;

    //hàm khởi tạo trong activity
    public HandleReceiverDeals(Context context) {
        this.context = context;
    }

    //register trong on resume
    public void registerReceiver() {
        receiverDeals = new ReceiverDeals();
        IntentFilter filter = new IntentFilter(Action.ACTION_PUSH_DEAL);
        context.registerReceiver(receiverDeals, filter);
    }

    //unregisterReceiver trong on pause
    public void unregisterReceiver(){
        context.unregisterReceiver(receiverDeals);
    }

    private class ReceiverDeals extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Action.ACTION_PUSH_DEAL)){
                Log.e(TAG, "received push deal");
                mRecentChatObj = (RecentChatObj) intent.getExtras().get(Args.RECENT_CHAT_OBJ);
                showDialogNotif(intent.getStringExtra(Action.ACTION_PUSH_DEAL),
                        mRecentChatObj,
                        intent.getStringExtra(Args.NOTIFICATION_TYPE));
            }
        }
    }

    private void showDialogNotif(String msg, final RecentChatObj mRecentChatObj, final String type) {
        final Dialog mDealConfirmationDialog = DialogUtil.setDialogCustomView(context, R.layout.dialog_confirmation, false);

        TextViewRegular lblMsg = (TextViewRegular) mDealConfirmationDialog.findViewById(R.id.lbl_msg);
        TextViewBold lblNegative = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_negative);
        TextViewBold lblPositive = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_positive);

        lblMsg.setText(msg);

        lblNegative.setText(R.string.close);
        lblPositive.setText(R.string.view_deals);

        lblNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDealConfirmationDialog.dismiss();
                Log.e(TAG, "close");
//                Intent intent = new Intent(context, SplashLoginActivity.class);
//                intent.putExtra(Args.RECENT_CHAT_OBJ, mRecentChatObj);
//                intent.putExtra(Args.NOTIFICATION_TYPE, type);
//                context.startActivity(intent);
            }
        });

        lblPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDealConfirmationDialog.dismiss();
                Log.e(TAG, "view_deals");
                Intent intent = new Intent(context, SplashLoginActivity.class);
                intent.putExtra(Args.RECENT_CHAT_OBJ, mRecentChatObj);
                intent.putExtra(Args.NOTIFICATION_TYPE, type);
                context.startActivity(intent);

            }
        });
        mDealConfirmationDialog.show();

    }

}
