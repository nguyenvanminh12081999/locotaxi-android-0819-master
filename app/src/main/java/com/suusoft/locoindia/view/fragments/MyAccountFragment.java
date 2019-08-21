package com.suusoft.locoindia.view.fragments;

import android.content.Intent;
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
import com.suusoft.locoindia.datastore.DataStoreManager;
import com.suusoft.locoindia.interfaces.IObserver;
import com.suusoft.locoindia.utils.AppUtil;

import java.util.ArrayList;

/**
 * Created by SuuSoft.com on 01/12/2016.
 */

public class MyAccountFragment extends BaseFragment implements View.OnClickListener {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static MyAccountFragment newInstance() {
        Bundle args = new Bundle();
        MyAccountFragment fragment = new MyAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    View inflateLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_viewpager_tablayout1, container, false);
    }

    @Override
    void initUI(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.view_pagger);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
    }

    @Override
    void initControl() {
        setUpViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppUtil.hideSoftKeyboard(getActivity());
    }

    private void setUpViewPager(final ViewPager viewPager) {
        final PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());

        adapter.addFragment(MyInfoAccFragment.newInstance(), getString(R.string.me));
        adapter.addFragment(AccountProFragment.newInstance(), getString(R.string.pro));
        adapter.addFragment(TaxiNewFragment.newInstance(), getString(R.string.taxi_));
        //adapter.addFragment(PayFragment.newInstance(), getString(R.string.pay));
        adapter.addFragment(ManagerAccountingFragment.newInstance(), getString(R.string.manager_accounting));

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                AppUtil.hideSoftKeyboard(getActivity());
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    if (DataStoreManager.getUser().getProData() != null && ((AccountProFragment) adapter.getItem(position)).isCreateView()) {
                        ((IObserver) adapter.getItem(position)).update();
                    }
                } else if (position == 0) {
                    ((IObserver) adapter.getItem(position)).update();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> listFragments;
        private ArrayList<String> listTabs;


        public PagerAdapter(FragmentManager fm) {
            super(fm);

            listFragments = new ArrayList<>();
            listTabs = new ArrayList<>();

        }

        private void addFragment(Fragment fragment, String tab) {
            listFragments.add(fragment);
            listTabs.add(tab);
        }

        private void addFragment(Fragment fragment, int pos) {
            listFragments.add(pos, fragment);
            notifyDataSetChanged();
        }

        private void removeFragment(int pos) {
            listFragments.remove(pos);
            notifyDataSetChanged();
        }


        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTabs.get(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
