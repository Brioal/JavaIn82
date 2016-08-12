package com.brioal.brioallib.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brioal.brioallib.interfaces.FragmentFormat;
import com.brioal.brioallib.klog.KLog;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


/**
 * Fragment基类
 * Created by mm on 2016/6/4.
 */

public class BaseFragment extends Fragment implements FragmentFormat {
    protected Activity mContext;
    protected View mRootView;
    protected LayoutInflater inflater;
    protected ViewGroup container;
    public String TAg = "BaseFragment";
    protected Bundle saveInstanceState;
    protected Runnable mRunnableLocal=new Runnable() {
        @Override
        public void run() {
            KLog.i(TAG,"Run loadDataLocal");
            loadDataLocal();
        }
    };
    protected Runnable mRunnableNet = new Runnable() {
        @Override
        public void run() {
            KLog.i(TAG,"Run loadDataNet");
            loadDataNet();      //加载数据
        }
    };

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            KLog.i(TAG,"handler Message");
            setView(); //数据显示到布局
        }
    };


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        KLog.i(TAG,"onAttach");
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.i(TAG,"onCreate");
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.i(TAG,"onResume");
    }

    @Override
    public void onDestroy() {
        KLog.i(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KLog.i(TAG,"onDestroyView");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KLog.i(TAG,"onCreateView");
        this.inflater = inflater;
        this.container = container;
        this.saveInstanceState = savedInstanceState;
        initView();
        new Thread(mRunnableLocal).start();
        new Thread(mRunnableNet).start();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        KLog.i(TAG,"onViewCreated");
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void initData() {
        KLog.i(TAG,"initData");
    }

    @Override
    public void initView() {
        KLog.i(TAG,"initView");
    }

    @Override
    public void loadDataLocal() {
        KLog.i(TAG,"loadDataLocal");
    }

    @Override
    public void loadDataNet() {
        KLog.i(TAG,"loadDataNet");
    }

    @Override
    public void setView() {
        KLog.i(TAG,"setView");
    }

    @Override
    public void saveDataLocal() {
        KLog.i(TAG,"saveDataLocal");
    }
}
