package com.suusoft.locoindia.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.NetworkUtility;
import com.suusoft.locoindia.view.adapters.DealReviewAdapter;
import com.suusoft.locoindia.base.*;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.objects.DealReviewObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.widgets.recyclerview.EndlessRecyclerOnScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuuSoft.com on 11/24/2016.
 */

public class ReviewAccFragment extends com.suusoft.locoindia.base.BaseFragment implements View.OnClickListener, EndlessRecyclerOnScrollListener.OnLoadMoreListener {

    private static final String KEY_OJECT_ID = "OJECT_ID";
    private static final String KEY_OBJECT_TYPE = "OBJECT_TYPE";
    private static final String KEY_SELLER_ID = "SELLER_ID";

    private RecyclerView rcvData;
    private LinearLayout mLlNoData, mLlNoConnection;
    private DealReviewAdapter mAdapter;
    private List<DealReviewObj> mDatas;

    private String objectId;
    private String objectType;
    private int page = 1;

    private LinearLayoutManager manager;

    private EndlessRecyclerOnScrollListener onScrollListener;

    public static ReviewAccFragment newInstance(String objectId, String objectType, String sellerId) {

        Bundle args = new Bundle();
        args.putString(KEY_OJECT_ID, objectId);
        args.putString(KEY_OBJECT_TYPE, objectType);
        args.putString(KEY_SELLER_ID, sellerId);
        ReviewAccFragment fragment = new ReviewAccFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_review_acc;
    }

    @Override
    protected void init() {
        objectId = getArguments().getString(KEY_OJECT_ID);
        objectType = getArguments().getString(KEY_OBJECT_TYPE);

    }

    @Override
    protected void initView(View view) {
        rcvData = (RecyclerView) view.findViewById(R.id.rcv_data);
        manager = new LinearLayoutManager(self);
        rcvData.setLayoutManager(manager);
        mLlNoData = (LinearLayout) view.findViewById(R.id.ll_no_data);
        mLlNoConnection = (LinearLayout) view.findViewById(R.id.ll_no_connection);
        /*if (objectType.equals(Constants.PRO)) {
            btnRate.setVisibility(View.GONE);
        }*/
        initAdapter();
    }

    private void initAdapter() {
        mDatas = new ArrayList<>();
        mAdapter = new DealReviewAdapter(self, mDatas);
        rcvData.setAdapter(mAdapter);
        onScrollListener = new EndlessRecyclerOnScrollListener(this, manager);
        rcvData.addOnScrollListener(onScrollListener);
    }

    @Override
    protected void getData() {
        if (NetworkUtility.isNetworkAvailable()) {

            ModelManager.getReview(self, objectId, objectType, page, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;
                    ApiResponse response = new ApiResponse(jsonObject);
                    if (!response.isError()) {
                        mDatas.addAll(response.getDataList(DealReviewObj.class));
                        mAdapter.notifyDataSetChanged();
                        onScrollListener.onLoadMoreComplete();
                        onScrollListener.setEnded(JSONParser.isEnded(response, page));
                        if (mDatas.isEmpty()) {
                            showNoData();
                        } else {
                            mLlNoData.setVisibility(View.GONE);
                            mLlNoConnection.setVisibility(View.GONE);
                            rcvData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(self, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(self, R.string.msg_have_some_errors, Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            showNoConection();

            AppUtil.showToast(self, R.string.msg_no_network);
        }
    }

    private void showNoData() {
        mLlNoData.setVisibility(View.VISIBLE);
        mLlNoConnection.setVisibility(View.GONE);
        rcvData.setVisibility(View.GONE);
    }

    private void showNoConection() {
        mLlNoConnection.setVisibility(View.VISIBLE);
        mLlNoData.setVisibility(View.GONE);
        rcvData.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLoadMore(int page) {
        this.page = page;
        getData();
    }
}
