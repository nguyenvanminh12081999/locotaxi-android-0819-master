package com.suusoft.locoindia.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.interfaces.ITransport;
import com.suusoft.locoindia.objects.TransportObj;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 10/15/2015.
 */
public class TransportAdapter extends RecyclerView.Adapter<TransportAdapter.ViewHolder> {

    private ArrayList<TransportObj> transportObjs;
    private ITransport iTransport;
    private Context mContext;

    public TransportAdapter(Context context, ArrayList<TransportObj> transportObjs, ITransport iTransport) {
        this.transportObjs = transportObjs;
        this.iTransport = iTransport;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transport_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItemCount() > 0) {
            final TransportObj transportObj = transportObjs.get(position);
            if (transportObj != null) {
                holder.imgTransport.setImageResource(transportObj.getIcon());
                holder.lblTransportType.setText(transportObj.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iTransport.onTransportSelected(transportObj);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        try {
            return transportObjs.size();
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgTransport;
        private TextView lblTransportType;

        public ViewHolder(View view) {
            super(view);

            imgTransport = (ImageView) view.findViewById(R.id.img_type);
            lblTransportType = (TextViewRegular) view.findViewById(R.id.lbl_type);
        }
    }
}
