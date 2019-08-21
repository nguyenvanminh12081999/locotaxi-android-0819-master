package com.suusoft.locoindia.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quickblox.users.model.QBUser;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.view.activities.RecentChatsActivity;
import com.suusoft.locoindia.view.adapters.ContactAdapter;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.interfaces.IChat;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.objects.ContactObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.utils.NetworkUtility;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 14/12/2016.
 */

public class ContactFragment extends com.suusoft.locoindia.base.BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSw;
    private RecyclerView rcvData;
    private ContactAdapter adapter;
    private ArrayList<ContactObj> listContacts;

    private LinearLayout mLlNoData, mLlNoConection;

    public static ContactFragment newInstance() {
        Bundle args = new Bundle();
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void init() {
        listContacts = new ArrayList<>();
        adapter = new ContactAdapter(getActivity(), listContacts, new IChat() {
            @Override
            public void onUserClicked(Object obj) {
                QBUser qbUser = ((ContactObj) obj).getQbUser();

                RecentChatObj recentChatObj = new RecentChatObj(null, null, qbUser);
                RecentChatsActivity.start(getActivity(), recentChatObj);
            }

            @Override
            public void onActionClicked(View view, Object obj) {
                showPopupMenu((ContactObj) obj, view);
            }
        });
    }

    @Override
    protected void initView(View view) {
        mSw = (SwipeRefreshLayout) view.findViewById(R.id.sw_contacts);
        mSw.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        mSw.setOnRefreshListener(this);
        rcvData = (RecyclerView) view.findViewById(R.id.rcv_data);
        setUpRecyclerView();

        mLlNoData = (LinearLayout) view.findViewById(R.id.ll_no_data);
        mLlNoConection = (LinearLayout) view.findViewById(R.id.ll_no_connection);
    }

    @Override
    protected void getData() {
        getContacts(true);
    }

    @Override
    public void onRefresh() {
        getContacts(false);
    }

    private void setUpRecyclerView() {
        rcvData.setHasFixedSize(true);
        rcvData.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rcvData.setAdapter(adapter);
    }

    private void getContacts(boolean isProgress) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.getContacts(self, isProgress, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;
                    if (JSONParser.responseIsSuccess(jsonObject)) {
                        ArrayList<ContactObj> contactObjs = JSONParser.parseContacts(jsonObject);
                        if (contactObjs.size() > 0) {
                            if (listContacts.size() > 0) {
                                listContacts.clear();
                            }

                            listContacts.addAll(contactObjs);

                            // Refresh adapter
                            adapter.notifyDataSetChanged();

                            // Save contacts into preference
                            DataStoreManager.saveContactsList(listContacts);
                        }
                    }

                    // Show/hide 'no data' layout
                    showHideNoDataLayout();

                    mSw.setRefreshing(false);
                }

                @Override
                public void onError() {
                    mSw.setRefreshing(false);
                }
            });
        } else {
            mSw.setRefreshing(false);
            showNoConection();
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void showPopupMenu(final ContactObj contactObj, View view) {
        PopupMenu popupMenu = new PopupMenu(self, view);
        popupMenu.inflate(R.menu.menu_contact);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_remove_from_contacts:
                        removeFromContacts(contactObj);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void removeFromContacts(final ContactObj contactObj) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.manualContacts(self, Constants.ACTION_REMOVE, contactObj.getFriendId(),
                    true, new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            JSONObject jsonObject = (JSONObject) object;
                            if (JSONParser.responseIsSuccess(jsonObject)) {
                                listContacts.remove(contactObj);
                                adapter.notifyDataSetChanged();

                                showHideNoDataLayout();

                                // Remove contact from preference which just removed from server
                                ArrayList<ContactObj> contactObjs = DataStoreManager.getContactsList();
                                for (int i = 0; i < contactObjs.size(); i++) {
                                    if(contactObj.getFriendId().equals(contactObjs.get(i).getFriendId())){
                                        contactObjs.remove(i);
                                        break;
                                    }
                                }
                                DataStoreManager.saveContactsList(contactObjs);
                            } else {
                                Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            showNoConection();
            Toast.makeText(self, getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoConection() {
        mLlNoConection.setVisibility(View.VISIBLE);
        mLlNoData.setVisibility(View.GONE);
        rcvData.setVisibility(View.GONE);
    }


    private void showHideNoDataLayout() {
        if (listContacts.size() > 0) {
            rcvData.setVisibility(View.VISIBLE);
            mLlNoData.setVisibility(View.GONE);
            mLlNoConection.setVisibility(View.GONE);
        } else {
            mLlNoData.setVisibility(View.VISIBLE);
            rcvData.setVisibility(View.GONE);
            mLlNoConection.setVisibility(View.GONE);
        }
    }
}
