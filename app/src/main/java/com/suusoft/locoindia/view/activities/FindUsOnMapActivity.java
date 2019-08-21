package com.suusoft.locoindia.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.objects.DealObj;
import com.suusoft.locoindia.objects.ReservationObj;
import com.suusoft.locoindia.utils.map.MapsUtil;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

/**
 * Created by SuuSoft.com on 12/10/2016.
 */

public class FindUsOnMapActivity extends com.suusoft.locoindia.base.BaseActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private static final int RC_DEAL_COMPLETED = 999;

    private GoogleMap mMap;
    private DealObj item;
    private ReservationObj reservationObj;

    private TextViewRegular mLblDealCompleted;

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.map;
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
    protected void getExtraData(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Args.KEY_DEAL_OBJECT)) {
                item = bundle.getParcelable(Args.KEY_DEAL_OBJECT);
            } else if (bundle.containsKey(Args.RESERVATION_DEAL_OBJ)) {
                reservationObj = bundle.getParcelable(Args.RESERVATION_DEAL_OBJ);
                item = reservationObj.getDeal();
            }
        }
    }

    @Override
    protected void initilize() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void initView() {
        setToolbarTitle(item.getName());
        mLblDealCompleted = (TextViewRegular) findViewById(R.id.lbl_completed);

        if (reservationObj != null) {
            mLblDealCompleted.setVisibility(View.VISIBLE);
            mLblDealCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TripFinishingActivity.startForResult(FindUsOnMapActivity.this, null, reservationObj, RC_DEAL_COMPLETED);
                }
            });
        }
    }

    @Override
    protected void onViewCreated() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_DEAL_COMPLETED) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                onBackPressed();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        initMarker();

        LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.animateCamera(cameraUpdate);
    }

    private void initMarker() {
        LatLng latLng = new LatLng(item.getLatitude(), item.getLongitude());
        addMarker(null, latLng, R.drawable.ic_location_on_white, false);
    }


    private Marker addMarker(Marker marker, LatLng latLng, int icon, boolean draggable) {
        // Clear old marker if it's
        if (marker != null) {
            marker.remove();
        }

        return MapsUtil.addMarker(mMap, latLng, "", icon, draggable);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        ViewGroup window = (ViewGroup) LayoutInflater.from(self).inflate(R.layout.layout_windowinfo_marker_deal, null);

        TextView lblName = (TextViewRegular) window.findViewById(R.id.lbl_driver_name);
        TextView lblRateQuantity = (TextViewRegular) window.findViewById(R.id.lbl_rate_quantity);
        RatingBar ratingBar = (RatingBar) window.findViewById(R.id.rating);
        TextView lblPrice = (TextView) window.findViewById(R.id.lbl_price);

        lblPrice.setVisibility(View.GONE);
        if (item.getRateQuantity() > 0) {
            lblRateQuantity.setVisibility(View.VISIBLE);
            lblRateQuantity.setText(String.valueOf(item.getRateQuantity()));
        } else {
            lblRateQuantity.setVisibility(View.GONE);
        }

        lblName.setText(item.getAddress());
        ratingBar.setRating(item.getRate());
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public static void start(Activity activity, DealObj dealObj, ReservationObj reservationObj) {
        Bundle bundle = new Bundle();
        if (dealObj != null) {
            bundle.putParcelable(Args.KEY_DEAL_OBJECT, dealObj);
        } else if (reservationObj != null) {
            bundle.putParcelable(Args.RESERVATION_DEAL_OBJ, reservationObj);
        }
        GlobalFunctions.startActivityWithoutAnimation(activity, FindUsOnMapActivity.class, bundle);
    }

    public static void startForResult(Activity activity, DealObj dealObj, ReservationObj reservationObj, int reqCode) {
        Bundle bundle = new Bundle();
        if (dealObj != null) {
            bundle.putParcelable(Args.KEY_DEAL_OBJECT, dealObj);
        } else if (reservationObj != null) {
            bundle.putParcelable(Args.RESERVATION_DEAL_OBJ, reservationObj);
        }
        Intent intent = new Intent(activity, FindUsOnMapActivity.class);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, reqCode);
    }
}
