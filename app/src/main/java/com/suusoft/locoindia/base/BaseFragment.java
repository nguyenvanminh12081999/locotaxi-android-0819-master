package com.suusoft.locoindia.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SuuSoft.com on 6/29/2016.
 */
public abstract class BaseFragment extends Fragment {

    protected Context self;

    protected abstract int getLayoutInflate();

    protected abstract void init();

    protected abstract void initView(View view);

    protected abstract void getData();

    private boolean isSavedViewState;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = getActivity();
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isSavedViewState) {
            if (view == null) {
                view = inflater.inflate(getLayoutInflate(), container, false);
                initView(view);
            }
        }else{
            view = inflater.inflate(getLayoutInflate(), container, false);
            initView(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getData();
    }

    public boolean isSavedViewState() {
        return isSavedViewState;
    }

    public void setSavedViewState(boolean savedViewState) {
        isSavedViewState = savedViewState;
    }


}
