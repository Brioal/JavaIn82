package com.brioal.javain82.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.javain82.R;
import com.brioal.javain82.util.BrioalUtil;
import com.brioal.javain82.util.Constants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * 启动界面
 */
public class LauncherActivity extends BaseActivity {

    @Bind(R.id.act_launcher_image)
    ImageView mImage;

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_launcher);
        ButterKnife.bind(this);
        Glide.with(mContext).load(R.mipmap.ic_launcher_back).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
                setTheme(R.style.AppTheme_NoActionBar);
                mImage.setImageDrawable(glideDrawable);
                initTimer();
            }
        });
        initSdk();
        initFullScreen();
    }

    private void initTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                finish();
            }
        }, 800);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //设置全屏
    private void initFullScreen() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initSdk() {
        BrioalUtil.init(this);
        BmobConfig config = new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId(Constants.APPID)
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }

}
