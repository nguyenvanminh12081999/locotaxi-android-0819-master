package com.suusoft.locoindia.view.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IConfirmation;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.NetworkUtility;
import com.suusoft.locoindia.objects.HistoryObj;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 15/12/2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private ArrayList<HistoryObj> listHistory;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<HistoryObj> listHistory) {
        this.listHistory = listHistory;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(listHistory.get(position));
    }

    @Override
    public int getItemCount() {
        return listHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextViewBold tvCredits, tvName;
        private TextViewRegular tvTime, tvEmail, lblEmail;
        private FrameLayout btnClose;
        private LinearLayout rlParent;
        private FrameLayout frDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCredits = (TextViewBold) itemView.findViewById(R.id.tv_credits);
            tvName = (TextViewBold) itemView.findViewById(R.id.tv_name);
            tvTime = (TextViewRegular) itemView.findViewById(R.id.tv_time);
            tvEmail = (TextViewRegular) itemView.findViewById(R.id.tv_email);
            btnClose = (FrameLayout) itemView.findViewById(R.id.btn_close);
            rlParent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
            frDivider = (FrameLayout) itemView.findViewById(R.id.divider);
            lblEmail = (TextViewRegular) itemView.findViewById(R.id.lbl_email);
            btnClose.setOnClickListener(this);
        }

        public void bindData(HistoryObj historyObj) {
            tvCredits.setText(StringUtil.convertNumberToString(Float.parseFloat(historyObj.getCredits()), 1));
            tvTime.setText(historyObj.getTime());
            tvName.setText(historyObj.getName());
            switch (historyObj.getType()) {
                case HistoryObj.CREDIT:
                    tvName.setTextColor(ContextCompat.getColor(context, R.color.green));
                    frDivider.setBackgroundResource(R.color.green);
                    rlParent.setVisibility(View.GONE);
                    break;
                case HistoryObj.REDEEM:
                    tvName.setTextColor(ContextCompat.getColor(context, R.color.red));
                    frDivider.setBackgroundResource(R.color.red);
                    rlParent.setVisibility(View.GONE);
                    break;
                case HistoryObj.TRANSFER:
                    tvName.setTextColor(ContextCompat.getColor(context, R.color.yellows));
                    frDivider.setBackgroundResource(R.color.yellows);
                    tvEmail.setText(historyObj.getEmail());
                    rlParent.setVisibility(View.VISIBLE);
                    if (historyObj.getTypeTransaction() == HistoryObj.TRANSFER_TO) {
                        lblEmail.setText("To: ");
                    } else if (historyObj.getTypeTransaction() == HistoryObj.RECEIVE) {
                        lblEmail.setText("From: ");
                    }
                    break;
            }

        }

        @Override
        public void onClick(View view) {
            if (NetworkUtility.getInstance(context).isNetworkAvailable()) {
                GlobalFunctions.showConfirmationDialog(context, context.getString(R.string.msg_delete_item_history_success),
                        context.getString(R.string.yes), context.getString(R.string.no), true, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        deleteItemHistory(getAdapterPosition());
                    }

                    @Override
                    public void onNegative() {

                    }
                });


            } else {
                AppUtil.showToast(context, R.string.msg_network_not_available);
            }

        }

        private void deleteItemHistory(int pos) {
            ModelManager.deleteHistory(context, listHistory.get(pos).getId(), new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    org.json.JSONObject jsonObject = (JSONObject) object;
                    ApiResponse apiResponse = new ApiResponse(jsonObject);
                    if (!apiResponse.isError()) {
                        listHistory.remove(listHistory.get(getAdapterPosition()));
                        notifyItemRemoved(getAdapterPosition());
                        AppUtil.showToast(context, R.string.msg_delete_history_success);
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
    }


}
