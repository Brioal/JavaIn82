package com.brioal.javain82.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseFragment;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * 登陆Fragment
 * Created by Brioal on 2016/7/9.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private static LoginFragment mFragment;
    @Bind(R.id.login_et_phone)
    EditText mEtUserName;
    @Bind(R.id.login_et_pass)
    EditText mEtPass;
    @Bind(R.id.login_btn_login)
    Button mBtnLogin;

    private Timer mTimer;
    private User mUser;
    private float mTimerCount = 9000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTimerCount -= 1000;
            if (mTimerCount == 0) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                showNoticeDialog("登陆超时", "请检查网络之后重试");
            }

        }
    };

    public static LoginFragment newInstance() {
        if (mFragment == null) {
            mFragment = new LoginFragment();
        }
        return mFragment;
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fra_login, container, false);
        ButterKnife.bind(this, mRootView);
        mBtnLogin.setOnClickListener(this);
        mEtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return true;
            }
        });
    }

    //登陆
    public void login() {
        String phone = mEtUserName.getText().toString();
        String pass = mEtPass.getText().toString();
        if (phone.isEmpty() || pass.isEmpty()) {
            if (phone.isEmpty()) {
                mEtUserName.setError("用户手机号不能为空");
            }
            if (pass.isEmpty()) {
                mEtPass.setError("用户密码不能为空");
            }
            return;
        }
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        }, 100, 1000);
        showProgressDialog("稍等", "正在登录中");
        BmobUser.loginByAccount(mEtUserName.getText().toString(), mEtPass.getText().toString(), new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    mUser = user;
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    //保存用户信息
                    try {
                        Constants.getDataLoader(mContext).saveUserLocal(user);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    ToastUtils.showToast(mContext, "登录成功,正在跳转", ExtraToast.LENGTH_SHORT);
                    mBtnLogin.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    }, 1000);
                } else {
                    KLog.i("done: 登陆失败");
                    showNoticeDialog("登录失败,请重试", e.toString());
                }
            }
        });
    }

    private ProgressDialog mProgressDialog;

    //显示进度
    private void showProgressDialog(String title, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
        }
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;

    //显示警告dialog
    public void showNoticeDialog(String title, String msg) {
        if (mBuilder == null) {
            mBuilder = new AlertDialog.Builder(mContext);
        }
        mAlertDialog = mBuilder.setTitle(title).setMessage(msg).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        mAlertDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_login:
                login();
                break;
        }
    }
}
