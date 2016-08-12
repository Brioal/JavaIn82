package com.brioal.javain82.fragment;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.brioal.brioallib.base.BaseFragment;
import com.brioal.brioallib.klog.KLog;
import com.brioal.javain82.R;
import com.brioal.javain82.util.CodeUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用于显示题目和选项的Gra
 * Created by Brioal on 2016/7/12.
 */

public class QuestionTitleFragment extends BaseFragment {
    @Bind(R.id.fra_question_web)
    WebView mWeb;

    private String mContent;

    public static QuestionTitleFragment newInstance(String content) {
        QuestionTitleFragment fragment = new QuestionTitleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Content", content);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initData() {
        super.initData();
        try {
            mContent = getArguments().getString("Content");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fra_question, container, false);
        KLog.i(mContent);
        ButterKnife.bind(this, mRootView);
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        showWebContent(mContent);
    }

    public void showWebContent(String s) {
        mWeb.loadDataWithBaseURL("file:///android_asset/", getFileContent("index.html").replaceAll("Brioal_is_hardingworking", CodeUtil.encode(s)), "text/html", null, null);
    }

    private String getFileContent(String file) {
        String content = "";
        try {
            // 把数据从文件读入内存
            InputStream is = getResources().getAssets().open(file);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int i = is.read(buffer, 0, buffer.length);
            while (i > 0) {
                bs.write(buffer, 0, i);
                i = is.read(buffer, 0, buffer.length);
            }

            content = new String(bs.toByteArray(), Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return content;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
