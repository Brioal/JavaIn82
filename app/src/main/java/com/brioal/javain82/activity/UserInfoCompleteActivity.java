package com.brioal.javain82.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.ImageTools;
import com.brioal.brioallib.util.StatusBarUtils;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.brioallib.view.CircleImageView;
import com.brioal.javain82.R;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.util.Constants;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 个人信息完善界面
 */
public class UserInfoCompleteActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.user_edit_btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.user_edit_head)
    CircleImageView mHead;
    @Bind(R.id.user_edit_ben_change_gead)
    Button mBtnChangeHead;
    @Bind(R.id.user_edit_id)
    EditText mEtId;
    @Bind(R.id.user_edit_btn_commit)
    Button mBtnCommit;
    @Bind(R.id.user_edit_btn_out)
    Button mBtnOut;
    private User user;
    String HeadUrl;
    String Name;
    private String headPath;
    private AlertDialog.Builder builder;
    private int GETPIC = 2;
    private boolean hasChenged = false;


    @Override
    public void initData() {
        super.initData();
        user = (User) getIntent().getSerializableExtra("User");
    }


    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_user_info_complete);
        ButterKnife.bind(this);
        initUser();
        initActions();
        StatusBarUtils.setColor(mContext, getResources().getColor(R.color.colorHalfBlack));
    }

    private void initActions() {
        mBtnChangeHead.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
        mBtnOut.setOnClickListener(this);
    }


    //显示加载Dialog
    public void showProgressDialog(String title, String message, boolean isValue) {

        mProgressDialog = new ProgressDialog(mContext);
        if (isValue) {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        } else {
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    //显示错误Dialog
    public void showNoticeDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        mNoticeDialog = builder.setTitle(title).setMessage(message).setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        mNoticeDialog.show();
    }

    public void initUser() {
        HeadUrl = user.getHeadUrl();
        Name = user.getUsername() == null ? user.getMobilePhoneNumber() : user.getUsername();
        if (HeadUrl != null) {
            Glide.with(mContext).load(HeadUrl).into(mHead);
        } else {
            mHead.setImageResource(R.mipmap.ic_default_head);
        }
        if (Name != null) {
            mEtId.setText(Name);
        }
    }

    private AlertDialog mNoticeDialog;
    private ProgressDialog mProgressDialog;


    //显示图片选择
    public void showPicturePicker(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Uri imageUri = null;
                        String fileName = null;
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //删除上一次截图的临时文件
                        SharedPreferences sharedPreferences = activity.getSharedPreferences("temp", Context.MODE_WORLD_WRITEABLE);
                        ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(), sharedPreferences.getString("tempName", ""));

                        //保存本次截图临时文件名字
                        fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tempName", fileName);
                        editor.commit();

                        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), fileName));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activity.startActivityForResult(openCameraIntent, GETPIC);
                        break;

                    case 1:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        activity.startActivityForResult(openAlbumIntent, GETPIC);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GETPIC) {
                Bitmap photo = null;
                Uri photoUri = data.getData();
                if (photoUri != null) {
                    photo = BitmapFactory.decodeFile(photoUri.getPath());
                }
                if (photo == null) {
                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        photo = (Bitmap) extra.get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    }
                }
                mHead.setImageBitmap(photo);
                File file = Environment.getExternalStorageDirectory();
                try {
                    headPath = file.getCanonicalFile().toString();
                    ImageTools.savePhotoToSDCard(photo, headPath, "head");
                    upLoadHead();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //上传头像
    public void upLoadHead() {
        final BmobFile bmobFile = new BmobFile(new File(headPath + "/head.png"));
        KLog.i("upLoadHead: " + headPath + "/head.png");
        showProgressDialog("请稍等", "正在上传头像", true);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //上传成功
                    mProgressDialog.setMessage("头像上传成功,正在更新信息，请稍等");
                    User myUser = new User();
                    myUser.setUserHead(bmobFile);
                    myUser.update(user.getObjectId(), new UpdateListener() {

                        @Override
                        public void done(BmobException e1) {
                            if (e1 == null) {
                                //更新信息成功
                                if (mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                                KLog.i("onSuccess: 更新成功");
                                ToastUtils.showToast(mContext, "头像更新成功", ExtraToast.LENGTH_SHORT);
                                user.setHeadUrl(bmobFile.getFileUrl());
                                try {
                                    Constants.getDataLoader(mContext).saveUserLocal(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                                new File(headPath + "head.png").delete();
                            } else {
                                //更新信息失败
                                if (mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                }
                                showNoticeDialog("错误", e1.toString());
                                KLog.i("onFailure:更新失败 " + e1.toString());
                            }
                        }
                    });
                } else {
                    //上传失败
                    KLog.i("onFailure: 上传头像失败" + e.toString());
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    showNoticeDialog("错误", e.toString());
                }
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);

            }


        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_edit_ben_change_gead: //头像更改
                showPicturePicker(this);
                break;
            case R.id.user_edit_btn_commit://提交修改:
                if (user.getUsername().equals(mEtId.getText().toString())) {
                    ToastUtils.showToast(mContext, "数据更新成功", ExtraToast.LENGTH_SHORT);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                user.setUsername(mEtId.getText().toString());
                                Constants.getDataLoader(mContext).saveUserLocal(user);
                                setResult(RESULT_OK);
                                finish();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }, 1000);
                    return;
                }
                User s = new User();
                s.setUsername(mEtId.getText().toString());
                s.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            KLog.i("更新用户数据成功");
                            ToastUtils.showToast(mContext, "数据更新成功", ExtraToast.LENGTH_SHORT);
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        user.setUsername(mEtId.getText().toString());
                                        Constants.getDataLoader(mContext).saveUserLocal(user);
                                        setResult(RESULT_OK);
                                        finish();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }, 1000);
                        } else {
                            KLog.i("更新用户数据失败");
                            showNoticeDialog("失败", e.toString());
                        }
                    }
                });
                break;
            case R.id.user_edit_btn_out: //退出登录
                Constants.getDataLoader(mContext).deleteUserLocal();
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    public static void enterUserCompleteActivity(Context context, User user) {
        Intent intent = new Intent(context, UserInfoCompleteActivity.class);
        intent.putExtra("User", user);
        ((Activity) context).startActivityForResult(intent, 0);
    }

}
