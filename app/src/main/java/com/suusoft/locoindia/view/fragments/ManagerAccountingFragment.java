package com.suusoft.locoindia.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.suusoft.locoindia.R;

/**
 * Created by SuuSoft.com on 30/11/2016.
 */

public class ManagerAccountingFragment extends BaseFragment {

    public static ManagerAccountingFragment newInstance() {
        return new ManagerAccountingFragment();
    }

    @Override
    View inflateLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager_tablayout, container, false);
    }

    @Override
    void initUI(View view) {
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);

        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        tabs.setupWithViewPager(pager);
    }

    @Override
    void initControl() {
    }

    class PagerAdapter extends FragmentPagerAdapter {

        private String[] TITLES;

        public PagerAdapter(FragmentManager fm) {
            super(fm);

            TITLES = new String[]{getString(R.string.my_trip), getString(R.string.accounting)};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            try {
                return TITLES.length;
            } catch (NullPointerException e) {
                return 0;
            }
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return TripHistoryFragment.newInstance();
            } else if (position == 1) {
                return AccountingFragment.newInstance(AccountingFragment.TYPE_TRIP);
            }

            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }
}
