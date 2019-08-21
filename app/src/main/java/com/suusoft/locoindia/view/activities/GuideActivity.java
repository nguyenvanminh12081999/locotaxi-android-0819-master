package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.view.fragments.GuideFragment;
import com.suusoft.locoindia.widgets.textview.TextViewBold;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by SuuSoft.com on 24/12/2016.
 */

public class GuideActivity extends com.suusoft.locoindia.base.BaseActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private TextViewBold tvPrevious, tvNextAndDone;
    private CircleIndicator indicator;
    private int mPosition;
    private int mLastPosition;

    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NONE;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_guide;
    }

    @Override
    protected void getExtraData(Intent intent) {

    }

    @Override
    protected void initilize() {

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
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pagger);
        tvPrevious = (TextViewBold) findViewById(R.id.tv_previous);
        tvNextAndDone = (TextViewBold) findViewById(R.id.tv_next);
        indicator = (CircleIndicator) findViewById(R.id.circle_indicator);

    }

    @Override
    protected void onViewCreated() {
        setUpViewPager(viewPager);
        tvPrevious.setOnClickListener(this);
        tvNextAndDone.setOnClickListener(this);
        tvPrevious.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if (view == tvPrevious) {
            if (viewPager.getCurrentItem() > 0) {
                mPosition--;
                tvPrevious.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(mPosition);
                tvNextAndDone.setText(getString(R.string.next));
            }
        } else if (view == tvNextAndDone) {
            if (viewPager.getCurrentItem() < mLastPosition) {
                tvNextAndDone.setText(getString(R.string.next));
                mPosition++;
                viewPager.setCurrentItem(mPosition);
                if (mPosition == mLastPosition) {
                    tvNextAndDone.setText(getString(R.string.done));
                }
                return;
            }
            if (tvNextAndDone.getText().toString().equals(getString(R.string.done))) {
                finish();
            }
        }
    }

    private void setUpViewPager(ViewPager viewPager) {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(GuideFragment.newInstance(getString(R.string.find_taxi), getString(R.string.description_find_taxi)));
        adapter.addFragment(GuideFragment.newInstance(getString(R.string.chat), getString(R.string.description_chat)));
        adapter.addFragment(GuideFragment.newInstance(getString(R.string.vip), getString(R.string.description_pro)));

        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if (position == 0) {
                    tvPrevious.setVisibility(View.GONE);
                } else if (position > 0) {
                    tvPrevious.setVisibility(View.VISIBLE);
                }
                if (position == mLastPosition) {
                    tvNextAndDone.setText(getString(R.string.done));
                } else if (position >= 0) {
                    tvNextAndDone.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mLastPosition = adapter.getCount() - 1;
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> listFragments;
        private ArrayList<String> listTabs;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            listFragments = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }

        public void addFragment(Fragment fragment) {
            listFragments.add(fragment);
        }

    }
}
