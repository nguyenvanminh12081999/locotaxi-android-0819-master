package com.suusoft.locoindia.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.model.QBUser;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.quickblox.conversation.utils.DialogUtil;
import com.suusoft.locoindia.view.adapters.ChatAdapter;
import com.suusoft.locoindia.base.ApiResponse;
import com.suusoft.locoindia.configs.ChatConfigs;
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.globals.Args;
import com.suusoft.locoindia.globals.Constants;
import com.suusoft.locoindia.globals.GlobalFunctions;
import com.suusoft.locoindia.interfaces.IConfirmation;
import com.suusoft.locoindia.modelmanager.ModelManager;
import com.suusoft.locoindia.modelmanager.ModelManagerListener;
import com.suusoft.locoindia.network1.MyProgressDialog;
import com.suusoft.locoindia.objects.ContactObj;
import com.suusoft.locoindia.objects.DealObj;
import com.suusoft.locoindia.objects.DriverObj;
import com.suusoft.locoindia.objects.RecentChatObj;
import com.suusoft.locoindia.objects.ReservationObj;
import com.suusoft.locoindia.objects.SettingsObj;
import com.suusoft.locoindia.objects.TransportDealObj;
import com.suusoft.locoindia.objects.UserObj;
import com.suusoft.locoindia.parsers.JSONParser;
import com.suusoft.locoindia.quickblox.PaginationHistoryListener;
import com.suusoft.locoindia.quickblox.QbChatDialogMessageListenerImp;
import com.suusoft.locoindia.quickblox.QbDialogHolder;
import com.suusoft.locoindia.quickblox.QbDialogUtils;
import com.suusoft.locoindia.quickblox.SharedPreferencesUtil;
import com.suusoft.locoindia.quickblox.VerboseQbChatConnectionListener;
import com.suusoft.locoindia.quickblox.chat.ChatHelper;
import com.suusoft.locoindia.utils.NetworkUtility;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.widgets.textview.TextViewBold;
import com.suusoft.locoindia.widgets.textview.TextViewRegular;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private static final int RC_PHONE_CALL = 1;
    private static final int RC_PERMISSIONS = 2;
    private static final int RC_VIEW_DEAL_ON_MAP = 1000;
    private static final int RC_TRACK_TRIP = 1001;

    private static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    private MyProgressDialog myProgressDialog;
    private StickyListHeadersListView mLsvMessage;
    private EditText mTxtMessage;
    private RelativeLayout mRltAction;
    private ImageView mImgSend, mImgReviews, mImgDeal, mImgNoDeal;
    private TextViewRegular mLblRemindProUser;

    private Snackbar snackbar;

    private ChatAdapter chatAdapter;
    private ConnectionListener chatConnectionListener;

    private QBChatDialog qbChatDialog;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
    private ChatMessageListener chatMessageListener;

    private RecentChatObj mRecentChatObj;

    private Menu mMenu;

    private ReservationObj mReservationObj;
    private Dialog mPriceConfirmationDialog;
    private float mEstimateFare = 0;

    private boolean dealIsUpdated;

    private String mPassengerName = ""; // keep passenger name - temporary solution

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init quickblox
        if (mRecentChatObj != null && mRecentChatObj.getQbUser() != null) {
            if (DataStoreManager.getUser() != null && (DataStoreManager.getUser().getToken() != null
                    && !DataStoreManager.getUser().getToken().equals(""))) {
                initSession(savedInstanceState);
                initDialogsListener();
                initPushManager();
            }

            if (myProgressDialog == null) {
                myProgressDialog = new MyProgressDialog(self);

                if (!myProgressDialog.isShowing()) {
                    myProgressDialog.show();
                    myProgressDialog.setMessage(getString(R.string.loading_data));
                }
            }

            initChatDialog();
            initChatConnectionListener();
        }
    }

    @Override
    void inflateLayout() {
        setContentView(R.layout.activity_chat);
    }

    @Override
    void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show as up button
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mLsvMessage = (StickyListHeadersListView) findViewById(R.id.list_chat_messages);
        mTxtMessage = (EditText) findViewById(R.id.txt_message);
        mImgSend = (ImageView) findViewById(R.id.img_send);
        mImgReviews = (ImageView) findViewById(R.id.img_reviews);
        mImgDeal = (ImageView) findViewById(R.id.img_deal);
        mImgNoDeal = (ImageView) findViewById(R.id.img_no_deal);
        mRltAction = (RelativeLayout) findViewById(R.id.rlt_action);
        mLblRemindProUser = (TextViewRegular) findViewById(R.id.lbl_tell_user);

        // Hide action buttons at bottom
        if (mRecentChatObj.justForChatting()) {
            mRltAction.setVisibility(View.GONE);
        }

        // Hide Deal, No Deal buttons if he's driver/seller
        if (mRecentChatObj.getDealObj() != null) {
            if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                mImgDeal.setVisibility(View.GONE);
                mImgNoDeal.setVisibility(View.GONE);
            }

            if (mRecentChatObj.getQbUser() == null) {
                mLblRemindProUser.setVisibility(View.VISIBLE);
                mRltAction.setVisibility(View.GONE);

                mLblRemindProUser.setText(String.format(getString(R.string.msg_tell_user_about_chat_screen),
                        mRecentChatObj.getDealObj().getName()));
            }
        } else if (mRecentChatObj.getTransportDealObj() != null) {
            if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                mImgDeal.setVisibility(View.GONE);
                mImgNoDeal.setVisibility(View.GONE);
            }

            if (mRecentChatObj.getQbUser() == null) {
                mLblRemindProUser.setVisibility(View.VISIBLE);
                mRltAction.setVisibility(View.GONE);

                mLblRemindProUser.setText(String.format(getString(R.string.msg_tell_user_about_chat_screen),
                        getString(R.string.transport)));
            }

            mEstimateFare = mRecentChatObj.getTransportDealObj().getEstimateFare();
        }
    }

    @Override
    void initControl() {
        mImgSend.setOnClickListener(this);
        mImgReviews.setOnClickListener(this);
        mImgDeal.setOnClickListener(this);
        mImgNoDeal.setOnClickListener(this);
        closeKeyboardWhenClickOutside(mRltAction);
        closeKeyboardWhenClickOutside(mLsvMessage);

        mTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() == 0) {
                    mImgSend.setImageResource(R.drawable.ic_send_grey);
                } else {
                    mImgSend.setImageResource(R.drawable.ic_send_accent);
                }
            }
        });
    }

    @Override
    protected void getExtraValues() {
        super.getExtraValues();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Args.RECENT_CHAT_OBJ)) {
                mRecentChatObj = bundle.getParcelable(Args.RECENT_CHAT_OBJ);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        mMenu = menu;

        if (mRecentChatObj.getQbUser() == null) {
            // Hide other menus
            menu.findItem(R.id.action_call).setVisible(false);
            menu.findItem(R.id.action_voice_call).setVisible(false);
//            menu.findItem(R.id.action_add_to_contact).setVisible(false);

            if (mRecentChatObj.getDealObj() != null) {
                if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                    if (mRecentChatObj.getDealObj().isOnline()) {
                        menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                    } else {
                        menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                    }
                }
            } else if (mRecentChatObj.getTransportDealObj() != null) {
                if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                    if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                        menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                    } else {
                        menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                    }
                }
            }
        } else if (mRecentChatObj.justForChatting()) {
            menu.findItem(R.id.action_activate).setVisible(false);
//            menu.findItem(R.id.action_add_to_contact).setVisible(false);
        } else {
            if (mRecentChatObj.getTransportDealObj() != null) {
                if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                    // Hide other menus
//                    menu.findItem(R.id.action_add_to_contact).setVisible(false);

                    // Set text for 'activate' menu item
                    if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                        menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                    } else {
                        menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                    }
                } else {
                    menu.findItem(R.id.action_activate).setVisible(false);

                    // Hide 'Add to contacts' option if he's friend
                    if (GlobalFunctions.isFriend(mRecentChatObj.getTransportDealObj().getDriverId())) {
//                        menu.findItem(R.id.action_add_to_contact).setVisible(false);
                    }
                }
            } else if (mRecentChatObj.getDealObj() != null) {
                if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                    // Hide other menus
//                    menu.findItem(R.id.action_add_to_contact).setVisible(false);

                    // Set text for 'activate' menu item
                    if (mRecentChatObj.getDealObj().isOnline()) {
                        menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                    } else {
                        menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                    }
                } else {
                    menu.findItem(R.id.action_activate).setVisible(false);

                    // Hide 'Add to contacts' option if he's friend
                    if (GlobalFunctions.isFriend(mRecentChatObj.getDealObj().getSeller_id())) {
//                        menu.findItem(R.id.action_add_to_contact).setVisible(false);
                    }
                }
            }

            updateOptionsMenu();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_activate) {
            switchDealStatus();
        } else if (id == R.id.action_call) {
            if (GlobalFunctions.callPhoneIsGranted(self, RC_PHONE_CALL, null)) {
                callPhone();
            }
        } else if (id == R.id.action_voice_call) {
//            if (GlobalFunctions.isGranted(self, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS,
//                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSIONS, null)) {
//                voipCall(false);
//            }
            voipCall(false);
        } else if (id == R.id.action_add_to_contact) {
            addToContacts();
        } else if (id == R.id.action_picked_up) {
            pickedUp();
        } else if (id == R.id.action_track_deal) {
            trackDeal();
        } else if (id == R.id.action_cancel_deal) {
            String action = ReservationObj.ACTION_DENY;
            if (mRecentChatObj.getTransportDealObj() != null) {
                action = TransportDealObj.ACTION_CANCEL;
            }
            String reservationId = "";
            if (mReservationObj != null) {
                reservationId = String.valueOf(mReservationObj.getId());
            }
            deal(action, reservationId);
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbChatDialog != null) {
            outState.putString(Args.QB_DIALOG_ID, qbChatDialog.getDialogId());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbChatDialog == null) {
            Log.e("getChatDialogById", "ChatActivity onRestoreInstanceState");
            qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(Args.QB_DIALOG_ID));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);

        // Keep conversation status
        DataStoreManager.saveConversationStatus(true);

        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.INTENT_ACTION_UPDATE_MENU));
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
        ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);

        // Keep conversation status
        DataStoreManager.saveConversationStatus(false);
    }

    @Override
    public void onBackPressed() {
        releaseChat();

        // Clear current chat
        DataStoreManager.clearCurrentChat();

        // Return result for 'DealAboutFragment'
        if (mRecentChatObj.getDealObj() != null && mRecentChatObj.getQbUser() == null) {
            Intent intent = new Intent();
            intent.putExtra(Args.IS_ACTIVATED_DEAL, mRecentChatObj.getDealObj().isOnline());
            setResult(RESULT_OK, intent);
        }

        // Return recentChatObj to 'RecentChat' screen to update
        if ((mRecentChatObj.getDealObj() != null || mRecentChatObj.getTransportDealObj() != null)
                && dealIsUpdated) {
            Intent intent = new Intent();
            intent.putExtra(Args.RECENT_CHAT_OBJ, mRecentChatObj);
            setResult(RESULT_OK, intent);
        }

        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view == mImgSend) {
            if (mTxtMessage.getText().toString().trim().length() > 0) {
                sendChatMessage(mTxtMessage.getText().toString().trim(), null);
            }
        } else if (view == mImgDeal) {
            // Just send deal message if the deal is not negotiagted
            if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
                if (mRecentChatObj.getTransportDealObj() != null) {
                    showPriceConfirmationDialog();
                } else {
                    String msg = String.format(getString(R.string.msg_notify_opponent_agreed_deal), DataStoreManager.getUser().getName());
                    sendChatMessage(msg, null);

                    Toast.makeText(self, R.string.msg_waiting_for_confirming, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(self, R.string.msg_deal_was_negotiated_already, Toast.LENGTH_SHORT).show();
            }
        } else if (view == mImgNoDeal) {
            deal(ReservationObj.ACTION_DENY, "");
        } else if (view == mImgReviews) {
            viewReviews();
        }
    }

    @Override
    public void onSessionCreated() {
        initChat();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_VIEW_DEAL_ON_MAP || requestCode == RC_TRACK_TRIP) {
            if (resultCode == RESULT_OK) {
                DataStoreManager.saveDealNegotiation(mRecentChatObj, false);
                updateOptionsMenu();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RC_PHONE_CALL: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        callPhone();
                    } else {
                        Toast.makeText(self, R.string.msg_remind_user_grants_permissions, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case RC_PERMISSIONS: {
                if (grantResults.length > 0) {
                    boolean isGranted = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isGranted = false;
                            break;
                        }
                    }

                    if (isGranted) {
                        voipCall(false);
                    } else {
                        Toast.makeText(self, R.string.msg_remind_user_grants_permissions, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Clear current chat
        DataStoreManager.clearCurrentChat();
    }

    private void voipCall(boolean isVideoCall) {
        /**List<Integer> opponentsList = qbChatDialog.getOccupants();
         for (int i = 0; i < opponentsList.size(); i++) {
         if (opponentsList.get(i).equals(SharedPreferencesUtil.getQbUser().getId())) {
         opponentsList.remove(i);
         break;
         }
         }

         QBRTCTypes.QBConferenceType conferenceType = isVideoCall
         ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
         : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

         QBRTCClient qbrtcClient = QBRTCClient.getInstance(getApplicationContext());

         QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

         WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);

         PushNotificationSender.sendPushMessage(self, opponentsList);

         CallActivity.start(this, false);
         Log.d(TAG, "conferenceType = " + conferenceType);**/
        Toast.makeText(self, "This function is under development", Toast.LENGTH_SHORT).show();
    }

    private void callPhone() {
        String phoneNumber = "";
        if (mRecentChatObj.getTransportDealObj() != null) {
            if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                phoneNumber = mRecentChatObj.getQbUser().getPhone();
            } else {
                phoneNumber = mRecentChatObj.getTransportDealObj().getDriverPhone();
            }
        } else if (mRecentChatObj.getDealObj() != null) {
            if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                phoneNumber = mRecentChatObj.getQbUser().getPhone();
            } else {
                phoneNumber = mRecentChatObj.getDealObj().getProData().getBusiness_phone();
            }
        } else {
            phoneNumber = mRecentChatObj.getQbUser().getPhone();
        }

        GlobalFunctions.call(self, phoneNumber);
    }

    private void addToContacts() {
        if (NetworkUtility.isNetworkAvailable()) {
            String friendId = "";
            if (mRecentChatObj.getDealObj() != null) {
                friendId = mRecentChatObj.getDealObj().getSeller_id();
                if (DataStoreManager.getUser().getId().equals(mRecentChatObj.getDealObj().getSeller_id())) {
                    friendId = mRecentChatObj.getDealObj().getBuyerId();
                }
            } else if (mRecentChatObj.getTransportDealObj() != null) {
                friendId = mRecentChatObj.getTransportDealObj().getDriverId();
                if (DataStoreManager.getUser().getId().equals(mRecentChatObj.getTransportDealObj().getDriverId())) {
                    friendId = mRecentChatObj.getTransportDealObj().getPassengerId();
                }
            }
            if (!GlobalFunctions.isFriend(friendId)) {
                final String friendIdFinal = friendId;
                ModelManager.manualContacts(self, Constants.ACTION_ADD, friendId, false, new ModelManagerListener() {
                    @Override
                    public void onSuccess(Object object) {
                        JSONObject jsonObject = (JSONObject) object;

                        if (JSONParser.responseIsSuccess(jsonObject)) {
//                        Toast.makeText(self, getString(R.string.added_to_your_contacts), Toast.LENGTH_SHORT).show();

                            // Update contacts preference
                            ArrayList<ContactObj> contactObjs = DataStoreManager.getContactsList();
                            ContactObj contactObj = new ContactObj("", friendIdFinal, mRecentChatObj.getQbUser());
                            contactObjs.add(contactObj);
                            DataStoreManager.saveContactsList(contactObjs);

                            // Refresh options menu
//                        mMenu.findItem(R.id.action_add_to_contact).setVisible(false);
                        } else {
                            Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void initChatDialog() {
        Log.e("getChatDialogById", "ChatActivity initChatDialog");
        qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(
                getIntent().getStringExtra(Args.QB_DIALOG_ID));
        chatMessageListener = new ChatMessageListener();

        if (qbChatDialog != null) {
            qbChatDialog.setType(QBDialogType.PRIVATE);
            qbChatDialog.addMessageListener(chatMessageListener);
        }
    }

    private void initChatConnectionListener() {
        chatConnectionListener = new VerboseQbChatConnectionListener(mRltAction) {
            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                skipPagination = 0;
                chatAdapter = null;
                switch (qbChatDialog.getType()) {
                    case PRIVATE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadChatHistory();
                            }
                        });
                        break;
                    case GROUP:
                        // Join active room if we're in Group Chat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                joinGroupChat();
                            }
                        });
                        break;
                }
            }
        };
    }

    private void joinGroupChat() {
        ChatHelper.getInstance().join(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle b) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
                loadDialogUsers();
            }

            @Override
            public void onError(QBResponseException e) {
                if (myProgressDialog.isShowing()) {
                    myProgressDialog.dismiss();
                }
                snackbar = showErrorSnackbar(R.string.connection_error, mRltAction, e, null);
            }
        });
    }

    private void loadDialogUsers() {
        ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                // Set dialog's name
                setTitle(QbDialogUtils.getDialogName(qbChatDialog));
//                if (mRecentChatObj.getQbUser() != null) {
//                    if (mRecentChatObj.getTransportDealObj() != null) {
//                        if (!DataStoreManager.getUser().getId().equals(mRecentChatObj.getTransportDealObj().getDriverId())) {
//                            setTitle(mRecentChatObj.getTransportDealObj().getDriverName());
//                        } else {
//                            setTitle(mRecentChatObj.getQbUser().getFullName());
//                        }
//                    }
//                } else if(mRecentChatObj.getDealObj()!=null){
//                    if (!DataStoreManager.getUser().getId().equals(mRecentChatObj.getDealObj().getSeller_id())) {
//                        setTitle(mRecentChatObj.getDealObj().getSe());
//                    } else {
//                        setTitle(mRecentChatObj.getQbUser().getFullName());
//                    }
//                }

                // Load chat history
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException e) {
                if (myProgressDialog.isShowing()) {
                    myProgressDialog.dismiss();
                }

                showErrorSnackbar(R.string.chat_load_users_error, mRltAction, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDialogUsers();
                            }
                        });
            }
        });
    }

    private void sendChatMessage(String text, QBAttachment attachment) {
        if (mRecentChatObj.getQbUser() != null) {
            createEvent(text);

            QBChatMessage chatMessage = new QBChatMessage();
            if (attachment != null) {
                chatMessage.addAttachment(attachment);
            } else {
                // Append message if it's the first message of transport deal
                if (mRecentChatObj.getTransportDealObj() != null) {
                    if (!mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                        if (GlobalFunctions.isTheFirstMessage(mRecentChatObj.getTransportDealObj())) {
                            String str = String.format(getString(R.string.from_value), mRecentChatObj.getTransportDealObj().getPickup())
                                    + "\n" + String.format(getString(R.string.to_value), mRecentChatObj.getTransportDealObj().getDestination())
                                    + "\n" + String.format(getString(R.string.passengers_value),
                                    String.valueOf(mRecentChatObj.getTransportDealObj().getPassengerQuantity()))
                                    + "\n" + String.format(getString(R.string.approximate_price_value),
                                    StringUtil.convertNumberToString(mRecentChatObj.getTransportDealObj().getEstimateFare(), 1));

                            text = str + "\n\n" + text;

                            // Save the transport as the first
                            DataStoreManager.saveTransport(mRecentChatObj.getTransportDealObj());
                        }
                    }
                }

                chatMessage.setBody(text);
            }
            chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
            chatMessage.setProperty(Args.RECENT_CHAT_OBJ, new Gson().toJson(mRecentChatObj));
            chatMessage.setDateSent(System.currentTimeMillis() / 1000);
            chatMessage.setMarkable(true);
            Log.e(TAG, "Msg: " + new Gson().toJson(mRecentChatObj));

            if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()) {
                Toast.makeText(self, "You're still joining a group chat, please wait a bit", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                qbChatDialog.sendMessage(chatMessage);

                if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                    showMessage(chatMessage);
                }

                mTxtMessage.setText("");
                mImgSend.setImageResource(R.drawable.ic_send_grey);
            } catch (SmackException.NotConnectedException e) {
                Log.w(TAG, e);
                Toast.makeText(self, "Can't send a message, You are not connected to chat", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(self, R.string.msg_can_not_send_message_to_yourself, Toast.LENGTH_SHORT).show();
        }
    }

    private void initChat() {
        switch (qbChatDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                joinGroupChat();
                break;

            case PRIVATE:
                loadDialogUsers();
                break;

            default:
                Toast.makeText(self, String.format("%s %s", getString(R.string.chat_unsupported_type), qbChatDialog.getType().name())
                        , Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void releaseChat() {
        if (qbChatDialog != null) {
            qbChatDialog.removeMessageListrener(chatMessageListener);
            if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                leaveGroupDialog();
            }
        }
    }

    private void leaveGroupDialog() {
        try {
            ChatHelper.getInstance().leaveChatDialog(qbChatDialog);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.w(TAG, e);
        }
    }

    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                // The newest messages should be in the end of list,
                // so we need to reverse list to show messages in the right order


                Collections.reverse(messages);
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(ChatActivity.this, qbChatDialog, messages);
                    chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                        @Override
                        public void downloadMore() {
                            loadChatHistory();
                        }
                    });

                    if (unShownMessages != null && !unShownMessages.isEmpty()) {
                        List<QBChatMessage> chatList = chatAdapter.getList();
                        for (QBChatMessage message : unShownMessages) {
                            if (!chatList.contains(message)) {
                                chatAdapter.add(message);
                            }
                        }
                    }
                    mLsvMessage.setAdapter(chatAdapter);
                    mLsvMessage.setAreHeadersSticky(false);
                    mLsvMessage.setDivider(null);
                    scrollMessageListDown();

                    if (myProgressDialog.isShowing()) {
                        myProgressDialog.dismiss();
                    }

                    // Check message if it's 'agree' message
                    if (mRecentChatObj != null) {
                        if (mRecentChatObj.getLastMessage() != null) {
                            driverConfirmsDeal(mRecentChatObj.getLastMessage());
                        }
                    }
                } else {
//                    chatAdapter.addList(messages);
//                    mLsvMessage.setSelection(messages.size());
                }

                // Add to contacts automatically
                if (mRecentChatObj != null && !mRecentChatObj.justForChatting()) {
                    addToContacts();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                if (myProgressDialog.isShowing()) {
                    myProgressDialog.dismiss();
                }

                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                snackbar = showErrorSnackbar(R.string.connection_error, mRltAction, e, null);
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
    }

    public class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            showMessage(qbChatMessage);

            mRecentChatObj = new Gson().fromJson(qbChatMessage.getProperty(Args.RECENT_CHAT_OBJ).toString(), RecentChatObj.class);

            // Check message if it's 'agree' message
            driverConfirmsDeal(qbChatMessage.getBody());
        }
    }

    private void showMessage(QBChatMessage message) {
        if (chatAdapter != null) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            if (unShownMessages == null) {
                unShownMessages = new ArrayList<>();
            }
            unShownMessages.add(message);
        }
    }

    private void scrollMessageListDown() {
        mLsvMessage.setSelection(mLsvMessage.getCount() - 1);
    }

    public static void start(Activity activity, String dialogId, RecentChatObj obj) {
        Intent intent = new Intent(activity, ChatActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Args.QB_DIALOG_ID, dialogId);
        intent.putExtra(Args.RECENT_CHAT_OBJ, obj);
        activity.startActivity(intent);
    }

    public static void startForResult(Activity activity, String dialogId, RecentChatObj obj, int reqCode) {
        Intent intent = new Intent(activity, ChatActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Args.QB_DIALOG_ID, dialogId);
        intent.putExtra(Args.RECENT_CHAT_OBJ, obj);
        activity.startActivityForResult(intent, reqCode);
    }

    private void createEvent(String msg) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        List<Integer> occupants = qbChatDialog.getOccupants();
        for (int i = 0; i < occupants.size(); i++) {
            if (!occupants.get(i).equals(SharedPreferencesUtil.getQbUser().getId())) {
                userIds.add(occupants.get(i));
            }
        }

        QBEvent event = new QBEvent();
        event.setUserIds(userIds);
        event.setEnvironment(QBEnvironment.DEVELOPMENT);
        event.setNotificationType(QBNotificationType.PUSH);

        JSONObject jsonObject = new JSONObject();
        try {
            QBUser qbUser = SharedPreferencesUtil.getQbUser();
            qbUser.setPhone(DataStoreManager.getUser().getPhone());

            jsonObject.put(Args.MESSAGE, qbUser.getFullName() + ": " + msg);
            jsonObject.put(Args.NOTIFICATION_TYPE, ChatConfigs.QUICKBLOX_MESSAGE);

            RecentChatObj obj = new RecentChatObj(mRecentChatObj.getTransportDealObj(), mRecentChatObj.getDealObj(), qbUser);
            jsonObject.put(Args.RECENT_CHAT_OBJ, new Gson().toJson(obj));
            Log.e(TAG, "Event: " + new Gson().toJson(obj));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        event.setMessage(jsonObject.toString());

        QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });
    }

    private void viewReviews() {
        Bundle bundle = new Bundle();
        String userId = "";
        String iAm = "";
        if (mRecentChatObj.getDealObj() != null) {
            if (!DataStoreManager.getUser().getId().equals(mRecentChatObj.getDealObj().getSeller_id())) {
                userId = mRecentChatObj.getDealObj().getSeller_id();

                iAm = Constants.BUYER;
            } else {
                userId = mRecentChatObj.getDealObj().getBuyerId();

                iAm = Constants.SELLER;
            }
        } else if (mRecentChatObj.getTransportDealObj() != null) {
            if (!DataStoreManager.getUser().getId().equals(mRecentChatObj.getTransportDealObj().getDriverId())) {
                userId = mRecentChatObj.getTransportDealObj().getDriverId();

                iAm = Constants.PASSENGER;
            } else {
                userId = mRecentChatObj.getTransportDealObj().getPassengerId();

                iAm = Constants.DRIVER;
            }
        }

        bundle.putString(Args.USER_ID, userId);
        bundle.putString(Args.I_AM, iAm);

        if (mRecentChatObj.getTransportDealObj() != null) {
            if (!DataStoreManager.getUser().getId().equals(mRecentChatObj.getTransportDealObj().getDriverId())) {
                bundle.putString(Args.NAME_DRIVER, mRecentChatObj.getTransportDealObj().getDriverName());
            } else {
                if (mPassengerName.equals("")) {
                    mPassengerName = mRecentChatObj.getQbUser().getFullName();
                }
                bundle.putString(Args.NAME_DRIVER, mPassengerName);
            }
        } else if (mRecentChatObj.getDealObj() != null) {
            if (!DataStoreManager.getUser().getId().equals(mRecentChatObj.getDealObj().getSeller_id())) {
                bundle.putString(Args.NAME_DRIVER, mRecentChatObj.getDealObj().getProData().getname());
            } else {
                bundle.putString(Args.NAME_DRIVER, mRecentChatObj.getDealObj().getBuyerName());
            }
        }
        GlobalFunctions.startActivityWithoutAnimation(self, ViewReviewsActivity.class, bundle);
    }

    private void deal(final String action, final String reservationId) {
        if (NetworkUtility.isNetworkAvailable()) {
//            if (mRecentChatObj.getDealObj() != null) {
//                final String buyerId = mRecentChatObj.getDealObj().getBuyerId();
//                final String buyerName = mRecentChatObj.getDealObj().getBuyerName();
//                ModelManager.manualReservation(self, mRecentChatObj.getDealObj().getId(), action,
//                        mRecentChatObj.getDealObj().getName(), buyerId, buyerName, reservationId, "", "", 0, "", new ModelManagerListener() {
//                            @Override
//                            public void onSuccess(Object object) {
//                                JSONObject jsonObject = (JSONObject) object;
//                                if (JSONParser.responseIsSuccess(jsonObject)) {
//                                    // Send message via QB to opponent
//                                    String name = DataStoreManager.getUser().getName();
//                                    String msg = "";
//                                    if (action.equalsIgnoreCase(ReservationObj.ACTION_DEAL)) {
//                                        // Get reservation object to transfer to FindUsOnMap activity
//                                        ApiResponse apiResponse = new ApiResponse(jsonObject);
//                                        mReservationObj = apiResponse.getDataObject(ReservationObj.class);
//
//                                        msg = String.format(getString(R.string.msg_notify_opponent_agreed_deal_with_id), name,
//                                                String.valueOf(mReservationObj.getId()));
//
//                                        DataStoreManager.saveDealNegotiation(mRecentChatObj, true);
//
//                                        mReservationObj.getDeal().setBuyerId(buyerId);
//                                        DataStoreManager.saveNegotiatedReservation(mReservationObj, false);
//                                    } else if (action.equalsIgnoreCase(ReservationObj.ACTION_DENY)) {
//                                        if (reservationId.equals("")) {
//                                            // User clicks 'No deal'
//                                            msg = String.format(getString(R.string.msg_notify_opponent_disagreed_deal), name);
//                                        } else {
//                                            // User clicks 'Cancel deal'
//                                            msg = String.format(getString(R.string.user_canceled_deal), name);
//
//                                            DataStoreManager.saveDealNegotiation(mRecentChatObj, false);
//                                        }
//                                    }
//
//                                    updateOptionsMenu();
//
//                                    sendChatMessage(msg, null);
//
//                                    dealIsUpdated = true;
//                                } else {
//                                    Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onError() {
//                            }
//                        });
//            } else
                if (mRecentChatObj.getTransportDealObj() != null) {
                    if (action.equals(TransportDealObj.ACTION_CREATE) || action.equals(TransportDealObj.ACTION_CANCEL)) {
                    ModelManager.manualTrip(self, action, mRecentChatObj.getTransportDealObj(),
                            "", "", 0, "", new ModelManagerListener() {
                                @Override
                                public void onSuccess(Object object) {
                                    JSONObject jsonObject = (JSONObject) object;
                                    if (JSONParser.responseIsSuccess(jsonObject)) {
                                        try {
                                            String tripId = jsonObject.getJSONObject("data").optString("id");
                                            long time = jsonObject.getJSONObject("data").optLong("time");
                                            String phone = jsonObject.getJSONObject("data").optString("driver_phone");
                                            mRecentChatObj.getTransportDealObj().setId(tripId);
                                            mRecentChatObj.getTransportDealObj().setTime(time);
                                            mRecentChatObj.getTransportDealObj().setDriverPhone(phone);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        // Send message via QB to opponent
                                        String name = DataStoreManager.getUser().getName();
                                        String msg = "";
                                        if (action.equals(TransportDealObj.ACTION_CREATE)) {
                                            msg = String.format(getString(R.string.msg_notify_opponent_agreed_deal_with_id), name, mRecentChatObj.getTransportDealObj().getId());

                                            DataStoreManager.saveDealNegotiation(mRecentChatObj, true);

                                            // Driver goes to offline
                                            changeDriverMode(Constants.OFF, 0);

                                            // Open 'Trip tracking' activity for driver
                                            trackDeal();
                                        } else if (action.equals(TransportDealObj.ACTION_CANCEL)) {
                                            msg = String.format(getString(R.string.user_canceled_deal), name);

                                            DataStoreManager.saveDealNegotiation(mRecentChatObj, false);

                                            // Ask driver to activate again when driver cancels trip
                                            if (DataStoreManager.getUser().getDriverData() != null) {
                                                if (!DataStoreManager.getUser().getDriverData().isAvailable()) {
                                                    if (mRecentChatObj.getTransportDealObj() != null) {
                                                        if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                                                            confirmDriverActivateAgain();
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        updateOptionsMenu();

                                        sendChatMessage(msg, null);

                                        dealIsUpdated = true;
                                    } else {
                                        Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError() {
                                }
                            });
                } else {
                    // Send message via QB to opponent
                    String name = DataStoreManager.getUser().getName();
                    String msg = String.format(getString(R.string.msg_notify_opponent_disagreed_deal), name);
                    sendChatMessage(msg, null);
                }
            }
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void switchDealStatus() {
        if (NetworkUtility.isNetworkAvailable()) {
            if (mRecentChatObj.getDealObj() != null) {
                String mode = mRecentChatObj.getDealObj().isOnline() ? Constants.OFF : Constants.ON;
                if (mode.equals(Constants.ON)) {
                    activateDeal(mRecentChatObj.getDealObj().getId(), mode);
                } else if (mode.equals(Constants.OFF)) {
                    confirmDeactivatingDeal(getString(R.string.msg_confirm_deactivating_deal));
                }
            } else if (mRecentChatObj.getTransportDealObj() != null) {
                if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
                    String mode = DataStoreManager.getUser().getDriverData().isAvailable() ? Constants.OFF : Constants.ON;
                    if (mode.equals(Constants.ON)) {
                        changeDriverMode(mode, 0);
                    } else if (mode.equals(Constants.OFF)) {
                        confirmDeactivatingDeal(getString(R.string.msg_confirm_deactivating_transport_deal));
                    }
                } else {
                    Toast.makeText(self, R.string.msg_remind_driver_finish_trip, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void activateDeal(String dealId, final String mode) {
        ModelManager.activateDeal(self, dealId, mode, new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                JSONObject jsonObject = (JSONObject) object;
                if (JSONParser.responseIsSuccess(jsonObject)) {
                    if (mode.equals(Constants.OFF)) {
                        mRecentChatObj.getDealObj().setIs_online(DealObj.DEAL_INACTIVE);

                        mMenu.findItem(R.id.action_activate).setTitle(R.string.activate);

                        Toast.makeText(self, R.string.msg_deactivate_success, Toast.LENGTH_SHORT).show();
                    } else {
                        mRecentChatObj.getDealObj().setIs_online(DealObj.DEAL_ACTIVE);

                        mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);

                        Toast.makeText(self, R.string.msg_activate_success, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (mode.equals(Constants.ON)) {
                        showDurationBuyingDialog(R.string.msg_enter_deal_duration_to_be_available);
                    }
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    private void updateDurationOfDeal(String dealId, int duration, final int creditsFee) {
        ModelManager.updateDurationOfDeal(self, dealId, String.valueOf(duration), new ModelManagerListener() {
            @Override
            public void onSuccess(Object object) {
                JSONObject jsonObject = (JSONObject) object;
                if (JSONParser.responseIsSuccess(jsonObject)) {
                    mRecentChatObj.getDealObj().setIs_online(DealObj.DEAL_ACTIVE);

                    mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);

                    UserObj userObj = DataStoreManager.getUser();
                    userObj.setBalance(userObj.getBalance() - creditsFee);
                    DataStoreManager.saveUser(userObj);

                    Toast.makeText(self, R.string.msg_activate_success, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
            }
        });
    }

    private void changeDriverMode(final String mode, int duration) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.activateDriverMode(self, mode, duration, new ModelManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    JSONObject jsonObject = (JSONObject) object;

                    if (JSONParser.responseIsSuccess(jsonObject)) {
                        if (mode.equals(Constants.OFF)) {
                            // Set driver is unavailable
                            UserObj userObj = DataStoreManager.getUser();
                            userObj.getDriverData().setAvailable(DriverObj.DRIVER_UNAVAILABLE);
                            DataStoreManager.saveUser(userObj);

                            mMenu.findItem(R.id.action_activate).setTitle(R.string.activate);
                            Toast.makeText(self, R.string.msg_deactivate_success, Toast.LENGTH_SHORT).show();
                        } else {
                            // Set driver is available
                            UserObj userObj = DataStoreManager.getUser();
                            userObj.getDriverData().setAvailable(DriverObj.DRIVER_AVAILABLE);
                            DataStoreManager.saveUser(userObj);

                            mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                            Toast.makeText(self, R.string.msg_activate_success, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (mode.equals(Constants.ON)) {
                            showDurationBuyingDialog(R.string.msg_enter_duration_to_be_available);
                        }
                    }
                }

                @Override
                public void onError() {
                }
            });
        } else {
            Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDurationBuyingDialog(int msgId) {
        final Dialog dialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_buying_duration, true ) ;

        final EditText txtDuration = (EditText) dialog.findViewById(R.id.txt_duration);
        TextViewRegular lblMsg = (TextViewRegular) dialog.findViewById(R.id.lbl_msg);
        final TextViewRegular lblFee = (TextViewRegular) dialog.findViewById(R.id.lbl_msg_fee);
        TextViewBold lblBuyCredits = (TextViewBold) dialog.findViewById(R.id.lbl_buy_credits);
        TextViewBold lblAvailable = (TextViewBold) dialog.findViewById(R.id.lbl_available);

        lblMsg.setText(msgId);
        lblFee.setText(String.format(getString(R.string.msg_subtract_credits), "0"));

        SettingsObj settingsObj = DataStoreManager.getSettingUtility();
        final int feePerHour = (settingsObj != null && settingsObj.getDeal_online_rate() != null
                && !settingsObj.getDeal_online_rate().equals("")) ?
                Integer.parseInt(settingsObj.getDeal_online_rate()) : 1;

        txtDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String duration = editable.toString().trim().isEmpty() ? "0" : editable.toString().trim();

                lblFee.setText(String.format(getString(R.string.msg_subtract_credits), String.valueOf((Integer.parseInt(duration) * feePerHour))));
            }
        });

        lblBuyCredits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                GlobalFunctions.startActivityWithoutAnimation(self, BuyCreditsActivity.class);
            }
        });

        lblAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int duration = 0;
                if (txtDuration.getText().toString().trim().length() > 0) {
                    duration = Integer.parseInt(txtDuration.getText().toString().trim());
                }

                if (duration == 0 || duration > 24) {
                    Toast.makeText(self, R.string.msg_duration_must_gt_zero, Toast.LENGTH_LONG).show();
                } else {
                    if (DataStoreManager.getUser().getBalance() < duration) {
                        Toast.makeText(self, String.format(getString(R.string.msg_balance_is_not_enough),
                                String.valueOf(duration)), Toast.LENGTH_LONG).show();
                    } else {
                        dialog.dismiss();

                        if (mRecentChatObj.getDealObj() != null) {
                            updateDurationOfDeal(mRecentChatObj.getDealObj().getId(), duration, (duration * feePerHour));
                        } else if (mRecentChatObj.getTransportDealObj() != null) {
                            changeDriverMode(Constants.DURATION_BUYING, duration);
                        }
                    }
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void confirmDeactivatingDeal(String msg) {
        GlobalFunctions.showConfirmationDialog(self, msg, getString(R.string.confirm), getString(R.string.no),
                true, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        if (mRecentChatObj.getDealObj() != null) {
                            activateDeal(mRecentChatObj.getDealObj().getId(), Constants.OFF);
                        } else if (mRecentChatObj.getTransportDealObj() != null) {
                            changeDriverMode(Constants.OFF, 0);
                        }
                    }

                    @Override
                    public void onNegative() {
                    }
                });
    }

    private void driverConfirmsDeal(String message) {
        if (!message.endsWith(String.format(getString(R.string.msg_notify_opponent_disagreed_deal), "").trim())) {
            if (message.endsWith(String.format(getString(R.string.msg_notify_opponent_agreed_deal), "").trim())
                    || message.contains(String.format(getString(R.string.msg_notify_opponent_agreed_deal_with_id), "", "").trim())) {
                String proUser = "";
                if (mRecentChatObj.getDealObj() != null) {
                    proUser = mRecentChatObj.getDealObj().getSeller_id();
                } else if (mRecentChatObj.getTransportDealObj() != null) {
                    proUser = mRecentChatObj.getTransportDealObj().getDriverId();
                }

                if (proUser.equals(DataStoreManager.getUser().getId())) {
                    if (mRecentChatObj.getDealObj() != null) {
                        if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
                            showDealConfirmationDialog();
                        }
                    } else if (mRecentChatObj.getTransportDealObj() != null) {
                        if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                            if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
                                showDealConfirmationDialog();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, R.string.msg_remind_driver_online, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    DataStoreManager.saveDealNegotiation(mRecentChatObj, true);
                    updateOptionsMenu();

                    String id = "";
                    if (message.contains("#")) {
                        id = message.substring((message.lastIndexOf("#") + 1));
                    }
                    if (mRecentChatObj.getDealObj() != null) {
                        getReservationDetail(id);
                    } else if (mRecentChatObj.getTransportDealObj() != null) {
                        // Open 'Trip tracking' activity for passenger
                        mRecentChatObj.getTransportDealObj().setId(id);
                        trackDeal();
                    }
                }
            } else if (message.endsWith(String.format(getString(R.string.user_canceled_deal), "").trim())) {
                DataStoreManager.saveDealNegotiation(mRecentChatObj, false);
                updateOptionsMenu();

                // Ask driver to activate again when passenger cancels trip
                if (DataStoreManager.getUser().getDriverData() != null) {
                    if (!DataStoreManager.getUser().getDriverData().isAvailable()) {
                        if (mRecentChatObj.getTransportDealObj() != null) {
                            if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                                confirmDriverActivateAgain();
                            }
                        }
                    }
                }
            }
        }
    }

    private void showDealConfirmationDialog() {
        final Dialog mDealConfirmationDialog = DialogUtil.setDialogCustomView(self,R.layout.dialog_confirmation, true) ;

        TextViewRegular lblMsg = (TextViewRegular) mDealConfirmationDialog.findViewById(R.id.lbl_msg);
        TextViewBold lblNegative = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_negative);
        TextViewBold lblPositive = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_positive);

        lblMsg.setText(R.string.msg_confirm_pro_agrees_deal);
        lblNegative.setText(R.string.disagree);
        lblPositive.setText(R.string.agree);

        lblNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDealConfirmationDialog.dismiss();

//                if (mRecentChatObj.getDealObj() != null) {
//                    deal(ReservationObj.ACTION_DENY, "");
//                } else
                    if (mRecentChatObj.getTransportDealObj() != null) {
                    deal(TransportDealObj.ACTION_DENY, "");
                }
            }
        });

        lblPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDealConfirmationDialog.dismiss();

//                if (mRecentChatObj.getDealObj() != null) {
//                    deal(ReservationObj.ACTION_DEAL, "");
//                } else
                    if (mRecentChatObj.getTransportDealObj() != null) {
                    deal(TransportDealObj.ACTION_CREATE, "");
                }
            }
        });

        mDealConfirmationDialog.setCancelable(false);

        try {
            if (!mDealConfirmationDialog.isShowing()) {
                mDealConfirmationDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getReservationDetail(String reservationId) {
        if (NetworkUtility.isNetworkAvailable()) {
            ModelManager.manualReservation(self, "", ReservationObj.ACTION_DETAIL, "", "", "", reservationId, "",
                    "", 0, "", new ModelManagerListener() {
                        @Override
                        public void onSuccess(Object object) {
                            JSONObject jsonObject = (JSONObject) object;
                            if (JSONParser.responseIsSuccess(jsonObject)) {
                                // Get reservation object to transfer to FindUsOnMap activity
                                ApiResponse apiResponse = new ApiResponse(jsonObject);
                                mReservationObj = apiResponse.getDataObject(ReservationObj.class);

                                String buyerId = jsonObject.optString("buyer_id");
                                mReservationObj.getDeal().setBuyerId(buyerId);
                                DataStoreManager.saveNegotiatedReservation(mReservationObj, false);

                                // Open deal's map for buyer
                                trackDeal();
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
    }

    private void showPriceConfirmationDialog() {
        if (mPriceConfirmationDialog == null) {
            mPriceConfirmationDialog = DialogUtil.setDialogCustomView(self,R.layout.dialog_price_confirmation, true );

            TextViewBold lblNegative = (TextViewBold) mPriceConfirmationDialog.findViewById(R.id.lbl_negative);
            TextViewBold lblPositive = (TextViewBold) mPriceConfirmationDialog.findViewById(R.id.lbl_positive);
            final EditText txtPrice = (EditText) mPriceConfirmationDialog.findViewById(R.id.txt_price);
            final RadioButton radAgreedPrice = (RadioButton) mPriceConfirmationDialog.findViewById(R.id.rad_agreed_price);
            final RadioButton radOtherPrice = (RadioButton) mPriceConfirmationDialog.findViewById(R.id.rad_other_price);

            mRecentChatObj.getTransportDealObj().setEstimateFare(mEstimateFare);
            txtPrice.setText(StringUtil.convertNumberToString(mEstimateFare, 1));

            radAgreedPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    txtPrice.setEnabled(isChecked);
                }
            });

            lblNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPriceConfirmationDialog.dismiss();
                }
            });

            lblPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (radAgreedPrice.isChecked()) {
                        String price = txtPrice.getText().toString().trim();
                        if (price.length() == 0
                                || StringUtil.convertStringToNumber(price) == 0) {
                            Toast.makeText(self, R.string.msg_price_is_zero, Toast.LENGTH_SHORT).show();
                            txtPrice.requestFocus();

                            return;
                        }

                        if (price.contains(",")) {
                            price = price.replace(",", "");
                        }
                        mRecentChatObj.getTransportDealObj().setEstimateFare(StringUtil.convertStringToNumber(price));
                    } else if (radOtherPrice.isChecked()) {
                        mRecentChatObj.getTransportDealObj().setEstimateFare(0);
                    }

                    mPriceConfirmationDialog.dismiss();

                    String msg = String.format(getString(R.string.msg_notify_opponent_agreed_deal), DataStoreManager.getUser().getName());
                    sendChatMessage(msg, null);

                    Toast.makeText(self, R.string.msg_waiting_for_confirming, Toast.LENGTH_LONG).show();
                }
            });
        }

        if (!mPriceConfirmationDialog.isShowing()) {
            mPriceConfirmationDialog.show();
        }
    }

    private void pickedUp() {
        String msg = String.format(getString(R.string.driver_picked_up), DataStoreManager.getUser().getName());
        sendChatMessage(msg, null);
    }

    private void trackDeal() {
        if (mReservationObj != null) {
            FindUsOnMapActivity.startForResult(self, null, mReservationObj, RC_VIEW_DEAL_ON_MAP);
        } else if (mRecentChatObj.getTransportDealObj() != null) {
            TripTrackingActivity.startForResult(self, mRecentChatObj.getTransportDealObj(), RC_TRACK_TRIP);
        }
    }

    public void updateOptionsMenu() {
        boolean isNegotiated = DataStoreManager.dealIsNegotiated(mRecentChatObj);

        if (mRecentChatObj.getTransportDealObj() != null) {
            if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                if (isNegotiated) {
                    mMenu.findItem(R.id.action_picked_up).setVisible(true);
                } else {
                    mMenu.findItem(R.id.action_picked_up).setVisible(false);
                }

                if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                    mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                } else {
                    mMenu.findItem(R.id.action_activate).setTitle(R.string.activate);
                }
            }

            if (isNegotiated) {
                mMenu.findItem(R.id.action_track_deal).setVisible(true);
                mMenu.findItem(R.id.action_cancel_deal).setVisible(true);
            } else {
                mMenu.findItem(R.id.action_track_deal).setVisible(false);
                mMenu.findItem(R.id.action_cancel_deal).setVisible(false);
            }
        } else if (mRecentChatObj.getDealObj() != null) {
            if (mReservationObj == null) {
                mReservationObj = DataStoreManager.getNegotiatedReservation(mRecentChatObj.getDealObj());
            }

            if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                if (isNegotiated) {
                    mMenu.findItem(R.id.action_cancel_deal).setVisible(true);
                } else {
                    mMenu.findItem(R.id.action_cancel_deal).setVisible(false);
                }

                if (mRecentChatObj.getDealObj().isOnline()) {
                    mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                } else {
                    mMenu.findItem(R.id.action_activate).setTitle(R.string.activate);
                }
            } else {
                if (isNegotiated) {
                    mMenu.findItem(R.id.action_track_deal).setVisible(true);
                    mMenu.findItem(R.id.action_cancel_deal).setVisible(true);
                } else {
                    mMenu.findItem(R.id.action_track_deal).setVisible(false);
                    mMenu.findItem(R.id.action_cancel_deal).setVisible(false);
                }
            }
        } else if (mRecentChatObj.getDealObj() == null) {
            Toast.makeText(self, "Deal is null", Toast.LENGTH_SHORT).show();
        }
    }

    // This method allow closing keyboard when users click out-side
    private void closeKeyboardWhenClickOutside(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                GlobalFunctions.closeKeyboard(self);
                return false;
            }
        });

        //If a layout container, iterate over children and seed recursion.
        if (view == mLsvMessage) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                closeKeyboardWhenClickOutside(innerView);
            }
        }
    }

    private void confirmDriverActivateAgain() {
        GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_ask_driver_activate_again),
                getString(R.string.yes), getString(R.string.no), false, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        changeDriverMode(Constants.ON, 0);
                    }

                    @Override
                    public void onNegative() {
                        Toast.makeText(self, R.string.msg_remind_driver_activate_again, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Handling the received Intents for the "updateMenu" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            boolean negotiated = intent.getBooleanExtra(Args.NEGOTIATED, false);

            updateOptionsMenu();
        }
    };

}
