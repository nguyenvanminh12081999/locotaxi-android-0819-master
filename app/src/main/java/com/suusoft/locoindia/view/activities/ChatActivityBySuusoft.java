package com.suusoft.locoindia.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.animation.AniChangeSizeView;
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
import com.suusoft.locoindia.objects.DealCateObj;
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
import com.suusoft.locoindia.quickblox.conversation.utils.DialogUtil;
import com.suusoft.locoindia.utils.AppUtil;
import com.suusoft.locoindia.utils.NetworkUtility;
import com.suusoft.locoindia.utils.StringUtil;
import com.suusoft.locoindia.view.adapters.ChatAdapter;
import com.suusoft.locoindia.view.fragments.FragmentChidChatAboutDeal;
import com.suusoft.locoindia.view.fragments.FragmentChidChatActionDeal;
import com.suusoft.locoindia.view.fragments.FragmentChidChatActionReviewDeal;
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

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static android.view.View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS;

/**
 * Created by Suusoft on 11/20/2017.
 */

public class ChatActivityBySuusoft extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, FragmentChidChatActionDeal.IActionDeal, FragmentChidChatActionReviewDeal.IActionReview {

        private static final String TAG = ChatActivityBySuusoft.class.getSimpleName();

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
        private TextViewRegular mLblRemindProUser, tv_tb_user_action;

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
        private LinearLayout llNodata,llNoConnection ;
        private RelativeLayout rlMain;

        //transport
        private TransportDealObj mTransportDealObj;

        //reskin
        private DealObj mDealObj;
        private RelativeLayout rlCustomer;
        private LinearLayout rlAuthor, llHeader;
        private ViewPager viewPager;
        private CircleIndicator indicator;
        private CircleImageView imgCar;
        private TextView tvDealName, tvAddress,tvNameDealCate, lblSalePercent, lblDealPrice, lblPriceOld, lblRateQuantity;
        private RatingBar ratingBar;
        private String  nameCateDeal;
        private int categoryId, percent;
        private ImageView btnDeal, btnNoDeal, btnShowHideAction, btnTrackDeal, btnCompleteDeal;
        private LinearLayout llAction, llBtnAction;
        private int widthAction, widthInput;
        private boolean isNegotiated = false;

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

        //create by reskin
        private void showViewAuthor(){
           setViewAnimationDownsize();
        }

        private void showViewCustomer(){
            setViewAnimationUpsize();

        }

    private void setViewAnimationUpsize() {
        AniChangeSizeView aniUpSize = new AniChangeSizeView(llBtnAction,btnDeal, btnNoDeal, btnTrackDeal, btnCompleteDeal,
                (int) (AppUtil.getScreenWidth(this)*0.4));
        aniUpSize.setDuration(1000);
        llBtnAction.clearAnimation();
        llBtnAction.setAnimation(aniUpSize);
    }

    private void setViewAnimationDownsize() {
        AniChangeSizeView aniDowmSize = new AniChangeSizeView(llBtnAction,btnDeal, btnNoDeal, 0);
        aniDowmSize.setDuration(1000);
        llBtnAction.clearAnimation();
        llBtnAction.setAnimation(aniDowmSize);
    }

    private void hideViewAboutDeal(){
        llHeader.setVisibility(View.GONE);
        llAction.setVisibility(View.GONE);

    }

    private void initViewReskin() {

        rlCustomer = (RelativeLayout) findViewById(R.id.rl_customer);
        rlAuthor  = (LinearLayout) findViewById(R.id.rl_author);
        llHeader  = (LinearLayout) findViewById(R.id.llheader);
        indicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        AdapterViewPagger adapter = new AdapterViewPagger(getSupportFragmentManager(), mDealObj , this , this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        indicator.setViewPager(viewPager);

        btnDeal = (ImageView) findViewById(R.id.btn_deal);
        btnNoDeal = (ImageView) findViewById(R.id.btn_no_deal);
        btnTrackDeal = (ImageView) findViewById(R.id.btn_track_deal);
        btnCompleteDeal = (ImageView) findViewById(R.id.btn_complete_deal);
        btnShowHideAction = (ImageView) findViewById(R.id.btn_show_hide_action);
        llAction = (LinearLayout) findViewById(R.id.ll_action_deal);
        llBtnAction = (LinearLayout) findViewById(R.id.ll_btn_action);

        Log.e("width", "width =  " + llAction.getLayoutParams().width);

        tvDealName = (TextView) findViewById(R.id.tv_name_car);
        imgCar = (CircleImageView) findViewById(R.id.img_car);
        tvAddress  = (TextView) findViewById(R.id.lbl_address);
        tvNameDealCate = (TextView) findViewById(R.id.lbl_deal_name_chat);
        lblSalePercent = (TextView) findViewById(R.id.tv_sale_percent);
        lblDealPrice = (TextView) findViewById(R.id.lbl_price);
        lblPriceOld = (TextView) findViewById(R.id.lbl_price_old);
        lblPriceOld.setPaintFlags(lblPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        lblRateQuantity = (TextView) findViewById(R.id.lbl_rate_quantity);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        ((LayerDrawable) ratingBar.getProgressDrawable()).getDrawable(2)
                .setColorFilter(ratingBar.getContext().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        widthAction = (int) (((AppUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.dimen_10px)))*0.3);
        widthInput = (int) (AppUtil.getScreenWidth(this) - getResources().getDimension(R.dimen.dimen_10px));

        setOnclick();
        setData();
    }

    private void setOnclick() {
        btnNoDeal.setOnClickListener(this);
        btnDeal.setOnClickListener(this);
        btnShowHideAction.setOnClickListener(this);
        btnTrackDeal.setOnClickListener(this);
        btnCompleteDeal.setOnClickListener(this);
    }

    public int percentPriceOldAndPriceNew(double priceOld, double priceNew){
            int i = (int) (((priceOld - priceNew) / priceOld ) * 100);
            return i;
        }

        public int percentPriceOldAndPriceSale(double priceOld, double priceSale){
            int i = (int) ( (priceSale / priceOld ) * 100);
            return i;
        }

        private void setData() {
            if (mTransportDealObj!=null){
                tvDealName.setText(mTransportDealObj.getDriverName());
                checkShowBtnTrackDeal();
//                tvDealName.setText(mTransportDealObj.getDriverName());
//                tvNameDealCate.setText(nameCateDeal);
//                tvAddress.setText(mTransportDealObj.getDriverEmail());
//
//                if (mDealObj.getDiscount_type() == null || mDealObj.getCategory_id() == Integer.parseInt(DealCateObj.LABOR)||mDealObj.getDiscount_type() != null && mDealObj.getDiscount_type().isEmpty()) {
//                    lblDealPrice.setText(String.format(self.getString(R.string.dollar_value), StringUtil.convertNumberToString(mDealObj.getPrice(), 1)));
//                    lblPriceOld.setVisibility(View.GONE);
//                    lblSalePercent.setVisibility(View.GONE);
//                } else if (mDealObj.getDiscount_type() != null && mDealObj.getDiscount_type().equals(Constants.AMOUNT)) {
//
//                    if (mDealObj.getDiscount_price() != 0) {
//                        lblDealPrice.setText(String.format(self.getString(R.string.dollar_value),
//                                StringUtil.convertNumberToString(mDealObj.getPrice() - mDealObj.getDiscount_price(), 1)));
//                        lblPriceOld.setText(String.format(self.getString(R.string.dollar_value),
//                                StringUtil.convertNumberToString(mDealObj.getPrice(), 1)));
//                        percent = percentPriceOldAndPriceSale(mDealObj.getPrice(), mDealObj.getDiscount_price() );
//                        lblSalePercent.setText(percent + " %");
//                        lblPriceOld.setVisibility(View.VISIBLE);
//                        lblSalePercent.setVisibility(View.VISIBLE);
//
//                    } else {
//                        lblDealPrice.setText(String.format(self.getString(R.string.dollar_value), StringUtil.convertNumberToString(mDealObj.getPrice(), 1)));
//                        lblPriceOld.setVisibility(View.GONE);
//                        lblSalePercent.setVisibility(View.GONE);
//
//                    }
//                } else if (mDealObj.getDiscount_type() != null && mDealObj.getDiscount_type().equals(Constants.PERCENT)) {
//                    if (mDealObj.getDiscount_rate() > 0) {
//                        lblDealPrice.setText(String.format(self.getString(R.string.dollar_value),
//                                StringUtil.convertNumberToString(mDealObj.getSale_price(), 1)));
//                        lblPriceOld.setText(String.format(self.getString(R.string.dollar_value),
//                                StringUtil.convertNumberToString(mDealObj.getPrice(), 1)));
//                        percent = percentPriceOldAndPriceNew(mDealObj.getPrice(), mDealObj.getSale_price());
//                        lblSalePercent.setText(percent + " %");
//                        lblPriceOld.setVisibility(View.VISIBLE);
//                        lblSalePercent.setVisibility(View.VISIBLE);
//                    } else {
//                        lblDealPrice.setText(String.format(self.getString(R.string.dollar_value), StringUtil.convertNumberToString(mDealObj.getPrice(), 1)));
//                        lblPriceOld.setVisibility(View.GONE);
//                        lblSalePercent.setVisibility(View.GONE);
//                    }
//                }
//
                if (mTransportDealObj.getRateQuantity() > 0) {
                    rlAuthor.setVisibility(View.VISIBLE);
                    //lblRateQuantity.setVisibility(View.VISIBLE);

                    ratingBar.setRating(mTransportDealObj.getRateOfDriver());
                    lblRateQuantity.setText(String.valueOf(mTransportDealObj.getRateQuantity()));
                } else {
                    rlAuthor.setVisibility(View.GONE);
                    //lblRateQuantity.setVisibility(View.GONE);
                }
            }else hideViewAboutDeal();
        }

        private String getNameCate(){
            try {
                if (mRecentChatObj.getDealObj()!=null){
                    Log.e(TAG, "mDealObj " + new Gson().toJson(mRecentChatObj.getDealObj()));
                    categoryId = mRecentChatObj.getDealObj().getCategory_id();
                    if (categoryId==Integer.parseInt(DealCateObj.FOOD_AND_BEVERAGES))
                        return getString(R.string.food_and_beverages);
                    else if (categoryId==Integer.parseInt(DealCateObj.LABOR))
                        return getString(R.string.labor);
                    else if (categoryId ==Integer.parseInt(DealCateObj.TRAVEL))
                        return getString(R.string.travel_hotel);
                    else if (categoryId ==Integer.parseInt(DealCateObj.SHOPPING))
                        return getString(R.string.shopping);
                    else
                        return getString(R.string.other_deals);
                }
                return " ";
            }catch (Exception e){
                e.printStackTrace();
                return " ";
            }

        }
        //end reskin

        @Override
        void inflateLayout() {
            setContentView(R.layout.activity_chat_reskin2);
        }

        @Override
        void initUI() {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            initViewReskin();

            // Show as up button
            try {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            llNodata = (LinearLayout) findViewById(R.id.ll_no_data);
            llNoConnection = (LinearLayout) findViewById(R.id.ll_no_connection);
            rlMain = (RelativeLayout) findViewById(R.id.rlt_main);
            mLsvMessage = (StickyListHeadersListView) findViewById(R.id.list_chat_messages);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mLsvMessage.setImportantForAutofill(IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            }

            mTxtMessage = (EditText) findViewById(R.id.txt_message);
            mImgSend = (ImageView) findViewById(R.id.img_send);
            mImgReviews = (ImageView) findViewById(R.id.img_reviews);
            mImgDeal = (ImageView) findViewById(R.id.img_deal);
            mImgNoDeal = (ImageView) findViewById(R.id.img_no_deal);
            mRltAction = (RelativeLayout) findViewById(R.id.rlt_action);
            mLblRemindProUser = (TextViewRegular) findViewById(R.id.lbl_tell_user);
            tv_tb_user_action = (TextViewRegular) findViewById(R.id.tv_tb_user_action);

            // Hide action buttons at bottom
            if (mRecentChatObj.justForChatting()) {
                mRltAction.setVisibility(View.GONE);
            }

            // Hide Deal, No Deal buttons if he's driver
            if (mRecentChatObj.getTransportDealObj() != null) {
                if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                    mImgDeal.setVisibility(View.GONE);
                    mImgNoDeal.setVisibility(View.GONE);
                    showViewAuthor();
                }else showViewCustomer();

                if (mRecentChatObj.getQbUser() == null) {
                    mLblRemindProUser.setVisibility(View.VISIBLE);
                    mRltAction.setVisibility(View.GONE);

                    mLblRemindProUser.setText(String.format(getString(R.string.msg_tell_user_about_chat_screen),
                            getString(R.string.transport)));
                }

                mEstimateFare = mRecentChatObj.getTransportDealObj().getEstimateFare();
                Log.e("estimate","mEstimateFare = "+ mEstimateFare );
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
                        mImgSend.setImageResource(R.drawable.ic_send_red);
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
                    if (mRecentChatObj!=null){
                        Log.e(TAG, "mRecentChatObj "+ new Gson().toJson(mRecentChatObj) );
                        mDealObj = mRecentChatObj.getDealObj();
                        mTransportDealObj = mRecentChatObj.getTransportDealObj();

                        nameCateDeal = getNameCate();
                    }
                }
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_chat, menu);
            mMenu = menu;

            if (mRecentChatObj.getQbUser() == null) {
                Log.e("ChatActivityReskin2"," onCreateOptionsMenu mRecentChatObj.getQbUser() == null");
                // Hide other menus
                menu.findItem(R.id.action_call).setVisible(false);
                menu.findItem(R.id.action_voice_call).setVisible(false);
//            menu.findItem(R.id.action_add_to_contact).setVisible(false);

                if (mRecentChatObj.getDealObj() != null) {
                    if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                        showViewAuthor();
                        if (mRecentChatObj.getDealObj().isOnline()) {
                            menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                        } else {
                            menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                        }
                    } else showViewCustomer();
                } else if (mRecentChatObj.getTransportDealObj() != null) {
                    if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                        showViewAuthor();
                        if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                            menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                        } else {
                            menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                        }
                    } else showViewCustomer();
                }
            } else if (mRecentChatObj.justForChatting()) {
                Log.e("ChatActivityReskin2"," onCreateOptionsMenu mRecentChatObj.justForChatting()");
                menu.findItem(R.id.action_activate).setVisible(false);
//            menu.findItem(R.id.action_add_to_contact).setVisible(false);
            } else {
                Log.e("ChatActivityReskin2"," onCreateOptionsMenu else");
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
                        showViewAuthor();
                        // Hide other menus
//                    menu.findItem(R.id.action_add_to_contact).setVisible(false);

                        // Set text for 'activate' menu item
                        if (mRecentChatObj.getDealObj().isOnline()) {
                            menu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                        } else {
                            menu.findItem(R.id.action_activate).setTitle(R.string.activate);
                        }
                    } else {
                        showViewCustomer();
                        menu.findItem(R.id.action_activate).setVisible(false);

                        // Hide 'Add to contacts' option if he's friend
                        if (GlobalFunctions.isFriend(mRecentChatObj.getDealObj().getSeller_id())) {
//                        menu.findItem(R.id.action_add_to_contact).setVisible(false);
                        }
                    }
                }

//            updateOptionsMenu();
            }

            updateOptionsMenu();
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
               cancelTrip();
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
                qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(Args.QB_DIALOG_ID));
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            AppController.onResume();
            ChatHelper.getInstance().addConnectionListener(chatConnectionListener);

            // Keep conversation status
            DataStoreManager.saveConversationStatus(true);

            // This registers mMessageReceiver to receive messages.
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter(Constants.INTENT_ACTION_UPDATE_MENU));
        }

        @Override
        protected void onPause() {
            AppController.onPause();
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

                actionDeal();

            } else if (view == mImgNoDeal) {
                cancelTrip();
            } else if (view == mImgReviews) {
                viewReviews();
            } else if (view==btnDeal){
                actionDeal();
            } else if (view ==btnNoDeal){
                cancelTrip();
            } else if (view ==btnTrackDeal){
                trackDeal();
            } else if (view ==btnShowHideAction){
                if ( llBtnAction.getLayoutParams().width ==0){
                    showButtonAction();
                } else {
                    hideButtonAction();
                }
            }
        }

    private void hideButtonAction() {
//        llBtnAction.setVisibility(View.GONE);
        setViewAnimationDownsize();
        btnShowHideAction.setImageResource(R.drawable.ic_show_action);
    }

    private void showButtonAction() {
//        llBtnAction.setVisibility(View.VISIBLE);
        setViewAnimationUpsize();
        btnShowHideAction.setImageResource(R.drawable.ic_hide_action);
    }

    @Override
        public void onReview() {
            viewReviews();
        }

        @Override
        public void onDeal() {
            actionDeal();
        }

        private void cancelTrip() {

            String action = ReservationObj.ACTION_DENY;
            if (mRecentChatObj.getTransportDealObj() != null) {
                action = TransportDealObj.ACTION_CANCEL;
            }
            String reservationId = "";
            if (mReservationObj != null) {
                reservationId = String.valueOf(mReservationObj.getId());
            }
            deal(action, reservationId);

//            if (DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
//
//                deal(TransportDealObj.ACTION_DENY, "");
//
//            } else {
//                AppUtil.showToast(self, R.string.msg_deal_has_not_been_negotiated);
//            }
            /* Mới thêm đoạn code này ngày 21/3/18 : sửa hàm canceltrip*/
//            if (NetworkUtility.isNetworkAvailable()) {
//
//                GlobalFunctions.showConfirmationDialog(self, getString(R.string.msg_confirm_cancel_trip),
//                        getString(R.string.yes), getString(R.string.no_thank), true, new IConfirmation() {
//                            @Override
//                            public void onPositive() {
//                                Log.e("cancelTrip", "mTransportDealObj = " +new Gson().toJson(mTransportDealObj));
//                                ModelManager.manualTrip(self, TransportDealObj.ACTION_CANCEL, mTransportDealObj, "", "",
//                                        0, "", new ModelManagerListener() {
//                                            @Override
//                                            public void onSuccess(Object object) {
//                                                JSONObject jsonObject = (JSONObject) object;
//                                                if (JSONParser.responseIsSuccess(jsonObject)) {
//                                                    // Send notification to opponent
//                                                    String msg = String.format(getString(R.string.user_canceled_deal), DataStoreManager.getUser().getName());
//                                                    createEvent(mTransportDealObj, msg);
//                                                    RecentChatObj recentChatObj = new RecentChatObj(mTransportDealObj, null, null);
//                                                    DataStoreManager.saveDealNegotiation(recentChatObj, false);
//                                                    updateOptionsMenu();
//
//                                                } else {
//                                                    Toast.makeText(self, JSONParser.getMessage(jsonObject), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError() {
//                                            }
//                                        });
//                            }
//
//                            @Override
//                            public void onNegative() {
//                            }
//                        });
//            } else {
//                Toast.makeText(self, R.string.msg_no_network, Toast.LENGTH_SHORT).show();
//            }
        }


    private void createEvent(TransportDealObj transportDealObj, String msg) {
        StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
        if (transportDealObj.getDriverId().equals(DataStoreManager.getUser().getId())) {
            userIds.add(transportDealObj.getPassengerQBId());
        } else {
            userIds.add(transportDealObj.getDriverQBId());
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

            RecentChatObj obj = new RecentChatObj(transportDealObj, null, qbUser);
            jsonObject.put(Args.RECENT_CHAT_OBJ, new Gson().toJson(obj));
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
    /* Mới thêm đoạn code này ngày 21/3/18 : sửa hàm canceltrip*/

    private void actionDeal() {
        // Just send deal message if the deal is not negotiagted
        if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
            if (mRecentChatObj.getTransportDealObj() != null) {
                showPriceConfirmationDialog();
//                } else {
//                    String msg = (String.format(getString(R.string.msg_notify_opponent_agreed_deal), DataStoreManager.getUser().getName()))
//                            + getTagNameDeal() ;
//
//                    sendChatMessage(msg, null);
//
//                    AppUtil.showToast(self, R.string.msg_waiting_for_confirming);
            }
        } else {
            AppUtil.showToast(self, R.string.msg_deal_was_negotiated_already);
        }
    }


        @Override
        public void onNoDeal() {
            cancelTrip();
        }

        @Override
        public void onSessionCreated() {
            initChat();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.e("resultCode", "resultCode = "+resultCode);
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
                            AppUtil.showToast(self, R.string.msg_remind_user_grants_permissions);
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
                            AppUtil.showToast(self, R.string.msg_remind_user_grants_permissions);
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
            AppUtil.showToast(self, "This function is under development");
        }

        private void callPhone() {
            String phoneNumber = "";
            if (mRecentChatObj.getTransportDealObj() != null) {
                if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                    showViewAuthor();
                    phoneNumber = mRecentChatObj.getQbUser().getPhone();
                } else {
                    showViewCustomer();
                    phoneNumber = mRecentChatObj.getTransportDealObj().getDriverPhone();
                }
            } else if (mRecentChatObj.getDealObj() != null) {
                if (mRecentChatObj.getDealObj().getSeller_id().equals(DataStoreManager.getUser().getId())) {
                    showViewAuthor();
                    phoneNumber = mRecentChatObj.getQbUser().getPhone();
                } else {
                    showViewCustomer();
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
                                AppUtil.showToast(self, JSONParser.getMessage(jsonObject));
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
                }
            } else {
                showNotconnection();
                AppUtil.showToast(self, R.string.msg_no_network);
            }
        }

        private void showNotconnection() {
            rlMain.setVisibility(View.GONE);
            llNoConnection.setVisibility(View.VISIBLE);
            llNodata.setVisibility(View.GONE);
        }

        private void initChatDialog() {
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
                                    Log.e(TAG, "loadChatHistory initChatConnectionListener");
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
                    Log.e(TAG, "loadDialogUsers joinGroupChat");
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
                    Log.e("title", "loadDialogUsers " + QbDialogUtils.getDialogName(qbChatDialog));
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
                    Log.e(TAG, "loadChatHistory loadDialogUsers");
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
                                    Log.e(TAG, "loadDialogUsers showErrorSnackbar");
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
                    AppUtil.showToast(self, "You're still joining a group chat, please wait a bit");
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
                    AppUtil.showToast(self, "Can't send a message, You are not connected to chat");
                }
            } else {
                AppUtil.showToast(self, R.string.msg_can_not_send_message_to_yourself);
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
                    Log.e(TAG, "loadDialogUsers initChat");
                    break;

                default:
                    AppUtil.showToast(self, String.format("%s %s", getString(R.string.chat_unsupported_type), qbChatDialog.getType().name())
                    );
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
                        chatAdapter = new ChatAdapter(self, qbChatDialog, messages);
                        chatAdapter.setPaginationHistoryListener(new PaginationHistoryListener() {
                            @Override
                            public void downloadMore() {
                                loadChatHistory();
                                Log.e(TAG, "loadChatHistory loadChatHistory");
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
                                Log.e(TAG, "driverConfirmsDeal mRecentChatObj.getLastMessage() != null ");
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
                Log.e(TAG, "driverConfirmsDeal processMessage");
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
            Intent intent = new Intent(activity, ChatActivityBySuusoft.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Args.QB_DIALOG_ID, dialogId);
            intent.putExtra(Args.RECENT_CHAT_OBJ, obj);
            activity.startActivity(intent);
        }

        public static void startForResult(Activity activity, String dialogId, RecentChatObj obj, int reqCode) {
            Intent intent = new Intent(activity, ChatActivityBySuusoft.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                    showViewCustomer();
                    bundle.putString(Args.NAME_DRIVER, mRecentChatObj.getDealObj().getProData().getname());
                } else {
                    showViewAuthor();
                    bundle.putString(Args.NAME_DRIVER, mRecentChatObj.getDealObj().getBuyerName());
                }
            }
            GlobalFunctions.startActivityWithoutAnimation(self, ViewReviewsActivity.class, bundle);
        }

        private void deal(final String action, final String reservationId) {
            if (NetworkUtility.isNetworkAvailable()) {
                    if (mRecentChatObj.getTransportDealObj() != null) {
                    if (action.equals(TransportDealObj.ACTION_CREATE) || action.equals(TransportDealObj.ACTION_CANCEL)) {
                        Log.e("manualTrip", "getTransportDealObj " + new Gson().toJson(mRecentChatObj.getTransportDealObj()));
                        ModelManager.manualTrip(self, action, mRecentChatObj.getTransportDealObj(),
                                "", "", 0, "", new ModelManagerListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        Log.e("manualTrip", "onSuccess " + object.toString());
                                        JSONObject jsonObject = (JSONObject) object;
                                        if (JSONParser.responseIsSuccess(jsonObject)) {
                                            try {
                                                String tripId = jsonObject.getJSONObject("data").optString("id");
                                                long time = jsonObject.getJSONObject("data").optLong("time");
                                                String phone = jsonObject.getJSONObject("data").optString("driver_phone");
                                                mRecentChatObj.getTransportDealObj().setId(tripId);
                                                mRecentChatObj.getTransportDealObj().setTime(time);
                                                mRecentChatObj.getTransportDealObj().setDriverPhone(phone);
                                                Log.e("manualTrip", "responseIsSuccess " +new Gson().toJson(mRecentChatObj));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.e("manualTrip", "JSONException " + e.toString());
                                            }

                                            // Send message via QB to opponent
                                            String name = DataStoreManager.getUser().getName();
                                            String msg = "";
                                            if (action.equals(TransportDealObj.ACTION_CREATE)) {
                                                msg = String.format(getString(R.string.msg_notify_opponent_agreed_deal_with_id),
                                                        name,
                                                        mRecentChatObj.getTransportDealObj().getDestination(),
                                                        mRecentChatObj.getTransportDealObj().getId());

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
                                                Log.e("manualTrip", "trackDeal ACTION_CANCEL ");
                                            }

                                            updateOptionsMenu();

                                            Log.e("manualTrip", "content msg= " + msg);
                                            sendChatMessage(msg, null);

                                            dealIsUpdated = true;
                                        } else {
                                            Log.e("manualTrip", "showToast " +  JSONParser.getMessage(jsonObject));
                                            AppUtil.showToast(self, JSONParser.getMessage(jsonObject));
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("manualTrip", "onError ");
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
                AppUtil.showToast(self, R.string.msg_no_network);
            }
        }

        private String getTagNameDeal(){
            return   Constants.STR_TAG_OPEN + mRecentChatObj.getDealObj().getName() + Constants.STR_TAG_CLOSE;
        }

    private String getTagDrive(){
        return   Constants.STR_TAG_OPEN + mRecentChatObj.getTransportDealObj().getDriverName() + Constants.STR_TAG_CLOSE;
    }

        private void switchDealStatus() {
            if (NetworkUtility.isNetworkAvailable()) {
                if (mRecentChatObj.getTransportDealObj() != null) {
                    if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
                        String mode = DataStoreManager.getUser().getDriverData().isAvailable() ? Constants.OFF : Constants.ON;
                        if (mode.equals(Constants.ON)) {
                            changeDriverMode(mode, 0);
                        } else if (mode.equals(Constants.OFF)) {
                            confirmDeactivatingDeal(getString(R.string.msg_confirm_deactivating_transport_deal));
                        }
                    } else {
                        AppUtil.showToast(self, R.string.msg_remind_driver_finish_trip);
                    }
                }
            } else {
                AppUtil.showToast(self, R.string.msg_no_network);
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

                            AppUtil.showToast(self, R.string.msg_deactivate_success);
                        } else {
                            mRecentChatObj.getDealObj().setIs_online(DealObj.DEAL_ACTIVE);

                            mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);

                            AppUtil.showToast(self, R.string.msg_activate_success);
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

                        AppUtil.showToast(self, R.string.msg_activate_success);
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
                                AppUtil.showToast(self, R.string.msg_deactivate_success);
                            } else {
                                // Set driver is available
                                UserObj userObj = DataStoreManager.getUser();
                                userObj.getDriverData().setAvailable(DriverObj.DRIVER_AVAILABLE);
                                DataStoreManager.saveUser(userObj);

                                mMenu.findItem(R.id.action_activate).setTitle(R.string.deactivate);
                                AppUtil.showToast(self, R.string.msg_activate_success);
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
                AppUtil.showToast(self, R.string.msg_no_network);
            }
        }

        private void showDurationBuyingDialog(int msgId) {
            final Dialog dialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_buying_duration, false);

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
                        AppUtil.showToast(self, R.string.msg_duration_must_gt_zero);
                    } else {
                        if (DataStoreManager.getUser().getBalance() < duration) {
                            AppUtil.showToast(self, String.format(getString(R.string.msg_balance_is_not_enough),
                                    String.valueOf(duration)));
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
            if (!message.contains(String.format(getString(R.string.msg_notify_opponent_disagreed_deal), "").trim())) {
                Log.e(TAG, " !message.contains ");

//                if (message.contains(String.format(getString(R.string.msg_check_notify_opponent_agreed_deal), "", "").trim())
//                        || message.contains(String.format(getString(R.string.msg_check_notify_opponent_agreed_deal_with_id), "", "").trim())) {
                 if (message.contains(getString(R.string.msg_check_notify_opponent_agreed_deal))
                                        || message.contains(getString(R.string.msg_check_notify_opponent_agreed_deal_with_id))) {
                     Log.e(TAG, " message.contains ");

                    String proUser = "";
                    if (mRecentChatObj.getDealObj() != null) {
                        proUser = mRecentChatObj.getDealObj().getSeller_id();
                    } else if (mRecentChatObj.getTransportDealObj() != null) {
                        proUser = mRecentChatObj.getTransportDealObj().getDriverId();
                    }
                    Log.e(TAG,"proUser =  " + proUser);
                    Log.e(TAG,"getUser().getId() =  "+  DataStoreManager.getUser().getId());
                    if (proUser.equals(DataStoreManager.getUser().getId())) {
                        Log.e(TAG, "getUser = " + new Gson().toJson(DataStoreManager.getUser()));

                        if (mRecentChatObj.getTransportDealObj() != null) {
                            if (DataStoreManager.getUser().getDriverData().isAvailable()) {
                                if (!DataStoreManager.dealIsNegotiated(mRecentChatObj)) {
                                    showDealConfirmationDialog();
                                    Log.e(TAG, " getTransportDealObj() != null ");
                                }
                            } else {
                                AppUtil.showToast(ChatActivityBySuusoft.this, R.string.msg_remind_driver_online);
                            }
                        }
                    } else {
                        DataStoreManager.saveDealNegotiation(mRecentChatObj, true);
                        updateOptionsMenu();

                        //chỗ này để lấy id TransObj
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
                            Log.e(TAG, "trackDeal mRecentChatObj.getTransportDealObj() != null ");
                        }
                    }
                } else if (message.contains(String.format(getString(R.string.user_canceled_deal), "").trim())) {
                    DataStoreManager.saveDealNegotiation(mRecentChatObj, false);
                    updateOptionsMenu();

                    // Ask driver to activate again when passenger cancels trip
                    if (DataStoreManager.getUser().getDriverData() != null) {
                        if (!DataStoreManager.getUser().getDriverData().isAvailable()) {
                            if (mRecentChatObj.getTransportDealObj() != null) {
                                if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                                    confirmDriverActivateAgainWhilePassangerCancelTrip();
                                }
                            }
                        }
                    }
                }
            }
            //insert by reskin
            else {
                DataStoreManager.saveDealNegotiation(mRecentChatObj, false);
                updateOptionsMenu();
                Log.e(TAG, "!message.contains" + String.format(getString(R.string.msg_notify_opponent_disagreed_deal), "").trim() );
            }
        }

        private void showDealConfirmationDialog() {
            final Dialog mDealConfirmationDialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_confirmation, false);

            TextViewRegular lblMsg = (TextViewRegular) mDealConfirmationDialog.findViewById(R.id.lbl_msg);
            TextViewBold lblNegative = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_negative);
            TextViewBold lblPositive = (TextViewBold) mDealConfirmationDialog.findViewById(R.id.lbl_positive);

            lblMsg.setText(String.format(getString(R.string.msg_confirm_pro_agrees_deal) , mRecentChatObj.getTransportDealObj().getDestination() ));
            //Log.e("obj", "mrecent " + new Gson().toJson(mRecentChatObj.getTransportDealObj()));
            lblNegative.setText(R.string.disagree);
            lblPositive.setText(R.string.agree);

            lblNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDealConfirmationDialog.dismiss();

                        if (mRecentChatObj.getTransportDealObj() != null) {
                            Log.e("manualTrip", "ACTION_DENY ");
                        deal(TransportDealObj.ACTION_DENY, "");
                    }
                }
            });

            lblPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDealConfirmationDialog.dismiss();


                        if (mRecentChatObj.getTransportDealObj() != null) {
                            Log.e("manualTrip", "ACTION_CREATE ");
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
                                    Log.e(TAG, "trackDeal getReservationDetail ");
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
                mPriceConfirmationDialog = DialogUtil.setDialogCustomView(self, R.layout.dialog_price_confirmation, false);

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
                                AppUtil.showToast(self, R.string.msg_price_is_zero);
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

                        String msg = String.format(getString(R.string.msg_notify_opponent_agreed_deal),
                                DataStoreManager.getUser().getName(), mRecentChatObj.getTransportDealObj().getDestination() );
                        sendChatMessage(msg, null);

                        AppUtil.showToast(self, R.string.msg_waiting_for_confirming);
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
            mTransportDealObj=mRecentChatObj.getTransportDealObj();
//        Log.e(TAG, "trackDeal mRecentChatObj " + new Gson().toJson(mRecentChatObj) );
//        if (mRecentChatObj.getDealObj() != null) {
//            try{
//                mReservationObj = DataStoreManager.getNegotiatedReservation(mRecentChatObj.getDealObj());
//                Log.e("DataStoreManager", "getNegotiatedReservation json = " + new Gson().toJson(mReservationObj) );
//            }catch (Exception e){
//                Log.e(TAG, e.getMessage());
//            }
//        }
            Log.e(TAG, "mReservationObj "  + new Gson().toJson(mReservationObj));
            if (mReservationObj != null) {
                Log.e(TAG, "mReservationObj "  + new Gson().toJson(mReservationObj));
                FindUsOnMapActivity.startForResult(self, null, mReservationObj, RC_VIEW_DEAL_ON_MAP);
            } else if (mRecentChatObj.getTransportDealObj() != null) {
                Log.e(TAG, "mReservationObj getTransportDealObj "  + new Gson().toJson(mTransportDealObj));
                TripTrackingActivity.startForResult(self, mTransportDealObj, RC_TRACK_TRIP);
            }
        }

        public void updateOptionsMenu() {
             isNegotiated = DataStoreManager.dealIsNegotiated(mRecentChatObj);

             checkShowBtnTrackDeal();

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
                AppUtil.showToast(self, "Deal is null");
            }
        }

        private void checkShowBtnTrackDeal(){
            if (isNegotiated)
                onNegotiated();
            else onNoNegotiated();
        }

    private void onNegotiated(){
        llAction.setVisibility(View.VISIBLE);
        if (mRecentChatObj.getTransportDealObj() != null) {
            if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                //Người bán hàng
                if (btnDeal!=null)
                    btnDeal.setVisibility(View.GONE);
                if (btnTrackDeal!=null)
                    btnTrackDeal.setVisibility(View.GONE);
                if (btnNoDeal!=null)
                    btnNoDeal.setVisibility(View.VISIBLE);
            }else {
                //người mua hàng
                if (btnDeal!=null)
                    btnDeal.setVisibility(View.GONE);
                if (btnTrackDeal!=null)
                    btnTrackDeal.setVisibility(View.VISIBLE);
                if (btnNoDeal!=null)
                    btnNoDeal.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onNoNegotiated(){

        if (mRecentChatObj.getTransportDealObj() != null) {
            if (mRecentChatObj.getTransportDealObj().getDriverId().equals(DataStoreManager.getUser().getId())) {
                //Người bán hàng
               llAction.setVisibility(View.GONE);
//                if (btnDeal!=null)
//                    btnDeal.setVisibility(View.GONE);
//                if (btnTrackDeal!=null)
//                    btnTrackDeal.setVisibility(View.GONE);
//                if (btnNoDeal!=null)
//                    btnNoDeal.setVisibility(View.GONE);
            }else {
                llAction.setVisibility(View.VISIBLE);
                //người mua hàng
                if (btnDeal!=null)
                    btnDeal.setVisibility(View.VISIBLE);
                if (btnTrackDeal!=null)
                    btnTrackDeal.setVisibility(View.GONE);
                if (btnNoDeal!=null)
                    btnNoDeal.setVisibility(View.GONE);
            }
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
                            AppUtil.showToast(self, R.string.msg_remind_driver_activate_again);
                        }
                    });
        }


    private void confirmDriverActivateAgainWhilePassangerCancelTrip() {
        GlobalFunctions.showConfirmationDialog(self,
                String.format(getString(R.string.msg_ask_the_customer_named_Truong_has_canceled_trip_driver_activate_again), getTitle()) ,
                getString(R.string.yes), getString(R.string.no), false, new IConfirmation() {
                    @Override
                    public void onPositive() {
                        changeDriverMode(Constants.ON, 0);
                    }

                    @Override
                    public void onNegative() {
                        AppUtil.showToast(self, R.string.msg_remind_driver_activate_again);
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

        private class AdapterViewPagger extends FragmentPagerAdapter {


            private DealObj mDealObj;
            private FragmentChidChatActionDeal.IActionDeal iActionDeal;
            private FragmentChidChatActionReviewDeal.IActionReview iActionReview;

            public AdapterViewPagger(FragmentManager fm, DealObj dealObj, FragmentChidChatActionDeal.IActionDeal i,
                                     FragmentChidChatActionReviewDeal.IActionReview iActionReview) {
                super(fm);
                this.mDealObj = dealObj;
                this.iActionDeal = i;
                this.iActionReview = iActionReview;
            }


            @Override
            public Fragment getItem(int position) {
                Log.e(TAG, "getItem ");
                switch (position) {
                    case 0:
                        return FragmentChidChatAboutDeal.newInstance(mDealObj);

                    case 1:
                        return FragmentChidChatActionDeal.newInstance(mRecentChatObj , iActionDeal );

                    case 2:
                        return FragmentChidChatActionReviewDeal.newInstance(mRecentChatObj , iActionReview );

                }

                return null;

            }

            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 3;
            }

        }


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }





}
