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
import com.brioal.javain82.adapter.QueRecyclerAdapter;
import com.brioal.javain82.entity.QuestionDoneEntity;
import com.brioal.javain82.entity.QuestionEntity;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.view.View.GONE;

public class QueCollectActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.act_que_collect_btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.act_que_collect_tv_tip)
    TextView mTvTip;
    @Bind(R.id.act_que_collect_recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.act_que_collect_layout_refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.act_que_collect_layout_title)
    TextView mTitle ;

    private User mUser;
    private ArrayList<QuestionEntity> mList;
    private QueRecyclerAdapter mAdapter;
    private boolean isError;

    @Override
    public void initData() {
        super.initData();
        try {
            mUser = Constants.getDataLoader(mContext).getUser();
            isError = getIntent().getBooleanExtra("isError", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_que_collect);
        ButterKnife.bind(this);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataNet();
            }
        });
        mBtnBack.setOnClickListener(this);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorHalfBlack));
        if (isError) { //错题
            mTvTip.setText("暂无错题");
            mTitle.setText("错题集");
        } else {
            mTvTip.setText("暂无收藏,快去浏览一些吧~");
            mTitle.setText("我的收藏");
        }
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (mList == null) mList = new ArrayList<>();
        BmobQuery<QuestionDoneEntity> query = new BmobQuery<>();
        query.addWhereEqualTo("mUser", mUser);
        if (isError) { //错题
            query.addWhereEqualTo("mType", 2);
        } else { //收藏题目
            query.addWhereEqualTo("isCollect", true);
        }
        query.order("-createdAt");
        query.include("mQuestion");
        query.findObjects(new FindListener<QuestionDoneEntity>() {
            @Override
            public void done(List<QuestionDoneEntity> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    KLog.i("查询当前用户的收藏问题成功:" + list.size());
                    mList.clear();
                    for (int i = 0; i < list.size(); i++) {
                        if (!mList.contains(list.get(i).getQuestion())) {
                            mList.add(list.get(i).getQuestion());
                        }
                    }
                    mHandler.sendEmptyMessage(0);
                } else {
                    KLog.i("加载用户的收藏问题失败");
                    if (e != null) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }
        });
    }

    @Override
    public void setView() {
        super.setView();
        if (mList.size() == 0) {
            mTvTip.setVisibility(View.VISIBLE);
            mRecyclerview.setVisibility(GONE);
            return;
        }
        if (mAdapter == null) {
            mAdapter = new QueRecyclerAdapter(mContext, mList);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerview.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        if (mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_que_collect_btn_back:
                finish();
                break;
        }
    }

    public static void enterQueCollectActivity(Context context, boolean isError) {
        Intent intent = new Intent(context, QueCollectActivity.class);
        intent.putExtra("isError", isError);
        context.startActivity(intent);
    }
}
