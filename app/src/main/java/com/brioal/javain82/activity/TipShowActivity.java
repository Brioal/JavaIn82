package com.brioal.javain82.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.javain82.R;
import com.brioal.javain82.fragment.QuestionTitleFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TipShowActivity extends BaseActivity {
    private String mContent;
    @Bind(R.id.ben_close)
     Button mBtnClose;

    @Override
    public void initData() {
        super.initData();
        try {
            mContent = getIntent().getStringExtra("Content");
        } catch (Exception e) {
            mContent = "SB";
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_tip_show);
        ButterKnife.bind(this);
        WindowManager m = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        getWindow().setLayout(screenWidth-10, screenHeight-100);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportFragmentManager().beginTransaction().add(R.id.act_tip_show, QuestionTitleFragment.newInstance(mContent)).commit();
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static void showTipActivity(Context context, String tip) {
        Intent intent = new Intent(context, TipShowActivity.class);
        intent.putExtra("Content", tip);
        context.startActivity(intent);
    }
}
