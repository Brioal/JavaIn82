package com.brioal.javain82.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.StatusBarUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.adapter.SubjectRecyclerViewAdapter;
import com.brioal.javain82.entity.SubjectDoneEntity;
import com.brioal.javain82.entity.SubjectEntity;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SubCollectActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.act_sub_collect_btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.act_sub_collect_tv_tip)
    TextView mTvTip;
    @Bind(R.id.act_sub_collect_recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.act_sub_collect_layout_refresh)
    SwipeRefreshLayout mLayoutRefresh;

    private User mUser;
    private SubjectRecyclerViewAdapter mAdapter;
    private List<SubjectEntity> mList;

    @Override
    public void initData() {
        super.initData();
        try {
            mUser = Constants.getDataLoader(mContext).getUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_sub_collect);
        ButterKnife.bind(this);
        mLayoutRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataNet();
            }
        });
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorHalfBlack));
        mBtnBack.setOnClickListener(this);
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (mList == null) mList = new ArrayList<SubjectEntity>();
        BmobQuery<SubjectDoneEntity> query = new BmobQuery<>();
        query.addWhereEqualTo("mUser", mUser);
        query.addWhereEqualTo("isCollect", true);
        query.include("mSubject");
        query.findObjects(new FindListener<SubjectDoneEntity>() {
            @Override
            public void done(List<SubjectDoneEntity> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    KLog.i("加载当前用的的收藏列表成功:" + list.size());
                    mList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        mList.add(list.get(i).getSubject());
                    }
                    mHandler.sendEmptyMessage(0);
                } else {
                    KLog.i("加载当前用户的收藏列表失败");
                    mHandler.sendEmptyMessage(0);
                    if (e != null) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void setView() {
        super.setView();
        if (mList.size() == 0) {
            mTvTip.setVisibility(View.VISIBLE);
            mRecyclerview.setVisibility(View.GONE);
            return;
        }
        if (mAdapter == null) {
            mAdapter = new SubjectRecyclerViewAdapter(mContext, mList);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerview.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        if (mLayoutRefresh.isRefreshing()) {
            mLayoutRefresh.setRefreshing(false);
        }
    }


    public static void enterCollectActivity(Context context) {
        Intent intent = new Intent(context, SubCollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_sub_collect_btn_back:
                finish();
                break;
        }
    }
}
