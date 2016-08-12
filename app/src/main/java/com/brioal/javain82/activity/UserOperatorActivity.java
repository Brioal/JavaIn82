package com.brioal.javain82.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.StatusBarUtils;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.adapter.UserViewPagerAdapter;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 登录注册Activity
 */
public class UserOperatorActivity extends BaseActivity {


    @Bind(R.id.act_user_image)
    ImageView mImage;
    @Bind(R.id.act_user_tab)
    TabLayout mTab;
    @Bind(R.id.act_user_viewpager)
    ViewPager mViewpager;
    @Bind(R.id.act_user_back)
    ImageButton mBtnBack;
    private UserViewPagerAdapter mAdapter;


    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_user);
        ButterKnife.bind(this);
        StatusBarUtils.setTranslucent(UserOperatorActivity.this);
        mAdapter = new UserViewPagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mTab.setupWithViewPager(mViewpager);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static final void startUserOperator(final Context context, boolean showTip , final int code) {
        final Intent intent = new Intent(context, UserOperatorActivity.class);
        if (showTip) {
            ToastUtils.showToast(context, "未登录,正在跳转登录接界面", ExtraToast.LENGTH_LONG);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) context).startActivityForResult(intent, code);
                }
            }, 1000);

            return;
        }
        ((Activity) context).startActivityForResult(intent, code);
    }


}
