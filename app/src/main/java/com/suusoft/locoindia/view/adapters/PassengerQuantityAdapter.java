package com.suusoft.locoindia.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.interfaces.IPassenger;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

/**
 * Created by SuuSoft.com on 10/15/2015.
 */
public class PassengerQuantityAdapter extends RecyclerView.Adapter<PassengerQuantityAdapter.ViewHolder> {

    private static final int SIZE = 9;

    private IPassenger iPassenger;
    private Context mContext;

    public PassengerQuantityAdapter(Context context, IPassenger iPassenger) {
        this.iPassenger = iPassenger;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_passenger_quantity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItemCount() > 0) {
            final String quantity = String.valueOf(position + 1);
            holder.lblPassengerQuantity.setText(quantity);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iPassenger.onQuantitySelected(quantity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        try {
            return SIZE;
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView lblPassengerQuantity;

        public ViewHolder(View view) {
            super(view);

            lblPassengerQuantity = (TextViewRegular) view.findViewById(R.id.lbl_quantity);
        }
    }
}
