package com.brioal.javain82.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.adapter.MainViewPagerAdapter;
import com.brioal.javain82.entity.QuestionDoneEntity;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;
import com.brioal.javain82.view.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.home_tv_name)
    TextView mTvName;
    @Bind(R.id.home_head_image)
    CircleImageView mHeadImage;
    @Bind(R.id.home_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.home_viewpager)
    ViewPager mViewpager;
    @Bind(R.id.home_head_donecount)
    TextView mTvDonecount;
    @Bind(R.id.home_head_rightcount)
    TextView mTvRightcount;
    @Bind(R.id.home_head_layout)
    RelativeLayout mHomeHeadLayout;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @Bind(R.id.main_app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.main_user_data_layout)
    LinearLayout mUserDataLayout;


    private User mUser;
    private MainViewPagerAdapter mAdapter;


    @Override
    public void initData() {
        super.initData();
        overridePendingTransition( R.anim.zoom_in,R.anim.zoom_out);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        initAction();
        initUser();
        mAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUser();
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
    }

    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (mUser != null) {
            Constants.getDataLoader(mContext).getUserNet(mUser.getObjectId(), new QueryListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (bmobUser != null) {
                        initUser();
                    }
                }
            });
        }

    }

    int doneCount = 0;
    int rightCount = 0;
    long timeCount = 0;

    @Override
    public void saveDataLocal() {
        super.saveDataLocal();
    }


    private void initUser() {
        try {
            mUser = Constants.getDataLoader(mContext).getUserLocal();
            //判断是否登录
            if (mUser == null) {
                //未登录
                //显示默认头像 ,显示点击登录 设置点击事件
                //隐藏用户数据区
            }


            //如果已登录
            //加载本地存储的用户数据

            //如果有网,从网络更新数据并存储到本地
            String headUrl = "";
            String name = "";
            if (mUser == null) {
                name = "点击登录";
                mUserDataLayout.setVisibility(View.GONE);
            } else {
                mUserDataLayout.setVisibility(View.VISIBLE);
                name = mUser.getUsername();
                headUrl = mUser.getHeadUrl();
                doneCount = mUser.getDoneCount();
                rightCount = mUser.getDoneRightCount();
                timeCount = mUser.getTimeCount();
                mTvDonecount.setText("已做题目:" + doneCount);
                mTvRightcount.setText("正确率:" + (doneCount == 0 ? 0 : (rightCount * 100 / doneCount)) + "%");
                //查询网络的做题数据并更新结果 保存本地
                BmobQuery<QuestionDoneEntity> queQuery = new BmobQuery<>();
                queQuery.addWhereEqualTo("mUser", mUser);
                queQuery.findObjects(new FindListener<QuestionDoneEntity>() {
                    @Override
                    public void done(List<QuestionDoneEntity> list, BmobException e) {
                        if (list != null && list.size() != 0) {
                            KLog.i("加载用户做题数据成功");
                            doneCount = 0;
                            timeCount = 0;
                            rightCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getType() != 0) {
                                    doneCount++;
                                    if (list.get(i).getType() == 1) {
                                        rightCount++;
                                    }
                                }
                            }
                            mTvDonecount.setText("已做题目:" + doneCount);
                            mTvRightcount.setText("正确率:" + (doneCount == 0 ? 0 : (rightCount * 100 / doneCount)) + "%");
                        } else {
                            if (e != null) {
                                KLog.i(e.toString());
                            }
                        }
                    }
                });
            }
            Glide.with(mContext).load(headUrl).error(R.mipmap.ic_default_head).into(mHeadImage);
            mTvName.setText(name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAction() {
        mHeadImage.setOnClickListener(this);
        mTvName.setOnClickListener(this);
    }

    public static final void startMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

        ((Activity) context).finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_head_image:
                if (mUser == null) {
                    UserOperatorActivity.startUserOperator(mContext, false,0);
                } else {
                    //个人数据中心
                    UserInfoCompleteActivity.enterUserCompleteActivity(mContext, mUser);
                }
                break;
            case R.id.home_tv_name:
                if (mUser == null) {
                    UserOperatorActivity.startUserOperator(mContext, false,0);
                } else {
                    //个人数据中心
                    UserInfoCompleteActivity.enterUserCompleteActivity(mContext, mUser);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) { //登录成功
            initUser();
        }
    }

    private long mLastClickTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastClickTime < 2000) {
            super.onBackPressed();
        } else {
            mLastClickTime = System.currentTimeMillis();
            ToastUtils.showToast(mContext, "再按一次返回键退出", ExtraToast.LENGTH_LONG);
        }
    }
}
