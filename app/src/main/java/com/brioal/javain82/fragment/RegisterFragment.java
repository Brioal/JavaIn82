package com.brioal.javain82.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.brioal.brioallib.base.BaseFragment;
import com.brioal.javain82.R;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Brioal on 2016/7/9.
 */

public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private static RegisterFragment sFragment;
    @Bind(R.id.register_et_phone)
    EditText mEtUserName;
    @Bind(R.id.register_et_code)
    EditText mEtEmail;
    @Bind(R.id.register_et_pass)
    EditText mEtPass;
    @Bind(R.id.register_btn_register)
    Button mBtnRegister;

    private User mUser;
    private String mUserName;
    private String mEmail;
    private String mPassword;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder mBuilder;

    public static RegisterFragment newInstance() {
        if (sFragment == null) {
            sFragment = new RegisterFragment();
        }
        return sFragment;
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fra_register, container, false);
        ButterKnife.bind(this, mRootView);
        mBtnRegister.setOnClickListener(this);
    }


    //一键注册或者登陆
    public void register() {
        mUserName = mEtUserName.getText().toString();
        mEmail = mEtEmail.getText().toString();
        mPassword = mEtPass.getText().toString();
        showProgressDialog("请稍等", "正在注册");
        mUser = new User();
        mUser.setUsername(mUserName);
        mUser.setEmail(mEmail);
        mUser.setPassword(mPassword);
        mUser.signUp(new SaveListener<User>() {
            @Override
            public void done(final User s, BmobException e) {
                dismissProgressDialog();
                if (s != null && e == null) {
                    login();
                } else {
                    showNoticeDailog("错误", e.getMessage());
                }
            }
        });
    }

    public void login() {
        showProgressDialog("请稍等", "注册成功,正在登录....");
        User bu2 = new User();
        bu2.setUsername(mUserName);
        bu2.setPassword(mPassword);
        bu2.login(new SaveListener<BmobUser>() {

            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                dismissProgressDialog();
                if (e == null) {
                    try {
                        Constants.getDataLoader(mContext).saveUserLocal(mUser);
                        mContext.setResult(Activity.RESULT_OK);
                        mContext.finish();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    showNoticeDailog("错误", e.getMessage());
                }
            }
        });
    }

    //显示加载进度条
    public void showProgressDialog(String title, String message) {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    //进度条消失
    public void dismissProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    //显示警告Dialog
    public void showNoticeDailog(String title, String message) {
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(title).setMessage(message).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_register:
                register();
                break;
        }
    }
}
