package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.view.adapters.HistoryAdapter;
import com.suusoft.locoindia.base.*;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IConfirmation;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.NetworkUtility;
import com.suusoft.locoindia.objects.HistoryObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.widgets.recyclerview.EndlessRecyclerOnScrollListener;
import com.suusoft.locoindia.widgets.textview.TextViewBold;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 15/12/2016.
 */

public class HistoryActivity extends com.suusoft.locoindia.base.BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rcvData;
    private ArrayList<HistoryObj> listHistory;
    private HistoryAdapter adapter;
    private TextViewBold btnDeleteAll;
    private LinearLayout llNodata, llNoConection;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private EndlessRecyclerOnScrollListener onScrollListener;

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_history;
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
        listHistory = new ArrayList<>();
        adapter = new HistoryAdapter(this, listHistory);
    }

    @Override
    protected void initView() {
        setToolbarTitle(R.string.history);
        rcvData = (RecyclerView) findViewById(R.id.rcv_data);
        btnDeleteAll = (TextViewBold) findViewById(R.id.btn_delete_all);
        llNodata = (LinearLayout) findViewById(R.id.ll_no_data);
        llNoConection = (LinearLayout) findViewById(R.id.ll_no_connection);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
    }

    private void setUpRecyclerView() {
        rcvData.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        rcvData.setAdapter(adapter);
        onScrollListener = new EndlessRecyclerOnScrollListener(new EndlessRecyclerOnScrollListener.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page) {
                getHistory(page);
            }
        }, linearLayoutManager);
        rcvData.addOnScrollListener(onScrollListener);
    }

    @Override
    protected void onViewCreated() {
        swipeRefreshLayout.setOnRefreshListener(this);
        setUpRecyclerView();
        getHistory(page);
        btnDeleteAll.setOnClickListener(this);
    }

    private void getHistory(final int page) {
        swipeRefreshLayout.setRefreshing(true);
        if (com.suusoft.locoindia.utils.NetworkUtility.isNetworkAvailable()) {
            ModelManager.getHistory(this, String.valueOf(page), new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    org.json.JSONObject jsonObject = (JSONObject) object;
                    ApiResponse apiResponse = new ApiResponse(jsonObject);
                    if (!apiResponse.isError()) {
                        listHistory.addAll(apiResponse.getDataList(HistoryObj.class));
                        adapter.notifyDataSetChanged();
                        onScrollListener.onLoadMoreComplete();
                        onScrollListener.setEnded(JSONParser.isEnded(apiResponse, page));
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        AppUtil.showToast(getApplicationContext(), R.string.msg_have_some_errors);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (listHistory.isEmpty()) {
                        llNodata.setVisibility(View.VISIBLE);
                    } else {
                        llNodata.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError() {
                    swipeRefreshLayout.setRefreshing(false);
                    if (listHistory.isEmpty()) {
                        llNodata.setVisibility(View.VISIBLE);
                    } else {
                        llNodata.setVisibility(View.GONE);
                    }
                    AppUtil.showToast(getApplicationContext(), R.string.msg_have_some_errors);
                }
            });
        }else {
            llNoConection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnDeleteAll) {
            if (NetworkUtility.getInstance(getApplicationContext()).isNetworkAvailable()) {
                if (!listHistory.isEmpty()) {
                    GlobalFunctions.showConfirmationDialog(this, getString(R.string.msg_confirm_delete_history),
                            getString(R.string.yes), getString(R.string.no), true, new IConfirmation() {
                        @Override
                        public void onPositive() {
                            deleteAllHistory();
                        }

                        @Override
                        public void onNegative() {

                        }
                    });

                } else {
                    AppUtil.showToast(getApplicationContext(), R.string.msg_no_history);
                }


            } else {
                AppUtil.showToast(getApplicationContext(), R.string.msg_network_not_available);
            }

        }
    }

    private void deleteAllHistory() {
        ModelManager.deleteHistory(this, "", new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                org.json.JSONObject jsonObject = (JSONObject) object;
                ApiResponse apiResponse = new ApiResponse(jsonObject);
                if (!apiResponse.isError()) {
                    listHistory.clear();
                    adapter.notifyDataSetChanged();
                    AppUtil.showToast(getApplicationContext(), R.string.msg_delete_history_success);
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void onRefresh() {
        listHistory.clear();
        adapter.notifyDataSetChanged();
        onScrollListener.setEnded(false);
        onScrollListener.setCurrentPage(page);
        getHistory(page);

    }
}
