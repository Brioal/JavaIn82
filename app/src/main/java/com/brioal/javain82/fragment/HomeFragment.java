package com.brioal.javain82.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseFragment;
import com.brioal.javain82.R;
import com.brioal.javain82.activity.QueCollectActivity;
import com.brioal.javain82.activity.SubCollectActivity;
import com.brioal.javain82.activity.UserOperatorActivity;
import com.brioal.javain82.adapter.SubjectRecyclerViewAdapter;
import com.brioal.javain82.entity.SubjectEntity;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页Fra
 * Created by Brioal on 2016/7/9.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static HomeFragment mFragment;
    @Bind(R.id.fra_home_sub_collect_layout)
    LinearLayout mSubCollectLayout;
    @Bind(R.id.fra_home_que_collect_layout)
    LinearLayout mQueCollectLayout;
    @Bind(R.id.fra_home_que_error_layout)
    LinearLayout mQueErrorLayout;
    @Bind(R.id.fra_home_recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.fra_home_nodata_tip)
    TextView mNodataTip;

    private List<SubjectEntity> mList;
    private SubjectRecyclerViewAdapter mAdapter;
    private User mUser;

    public static HomeFragment newInstance() {
        if (mFragment == null) {
            mFragment = new HomeFragment();
        }
        return mFragment;
    }

    @Override
    public void initData() {
        super.initData();
        mUser = Constants.getDataLoader(mContext).getUser();

    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fra_home, container, false);
        ButterKnife.bind(this, mRootView);
        mQueCollectLayout.setOnClickListener(this);
        mSubCollectLayout.setOnClickListener(this);
        mQueErrorLayout.setOnClickListener(this);
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        //加载本地保存的十个数据
        mList = Constants.getDataLoader(mContext).getRecentSubListLocal();
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
    }

    @Override
    public void setView() {
        super.setView();
        if (mList.size() == 0) {
            mNodataTip.setVisibility(View.VISIBLE);
            mNodataTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDataLocal();
                }
            });
        } else {
            mNodataTip.setVisibility(View.GONE);
            mAdapter = new SubjectRecyclerViewAdapter(mContext, mList);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerview.setAdapter(mAdapter);
        }

    }

    @Override
    public void saveDataLocal() {
        super.saveDataLocal();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        mUser = Constants.getDataLoader(mContext).getUser();
        switch (v.getId()) {
            case R.id.fra_home_sub_collect_layout: //专题收藏按钮
                if (mUser == null) {
                    UserOperatorActivity.startUserOperator(mContext, true, 0);
                    return;
                }
                SubCollectActivity.enterCollectActivity(mContext);
                break;
            case R.id.fra_home_que_collect_layout://问题收藏按钮

                if (mUser == null) {
                    UserOperatorActivity.startUserOperator(mContext, true, 0);
                    return;
                }
                QueCollectActivity.enterQueCollectActivity(mContext, false);
                break;
            case R.id.fra_home_que_error_layout://错题按钮
                if (mUser == null) {
                    UserOperatorActivity.startUserOperator(mContext, true, 0);
                    return;
                }
                QueCollectActivity.enterQueCollectActivity(mContext, true);
                break;
        }
    }
}
