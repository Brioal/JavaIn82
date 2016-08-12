package com.brioal.javain82.entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 用户实体类
 * Created by Brioal on 2016/7/9.
 */

public class User extends BmobUser {
    //网络该有的数据
    private BmobFile mUserHead; //用户头像
    private long mTimeCount; //用户耗费的时间

    //本地存储需要的数据
    private int mDoneCount; //用户做过题目的数量
    private int mDoneRightCount; //用户做对的题目数量

    private String mHeadUrl; //用户头像链接


    public User() {
    }

    public User(BmobFile userHead, long timeCount, int doneCount, int doneRightCount, String headUrl) {
        mUserHead = userHead;
        mTimeCount = timeCount;
        mDoneCount = doneCount;
        mDoneRightCount = doneRightCount;
        mHeadUrl = headUrl;
    }


    public BmobFile getUserHead() {
        return mUserHead;
    }

    public void setUserHead(BmobFile userHead) {
        mUserHead = userHead;
    }

    public long getTimeCount() {
        return mTimeCount;
    }

    public void setTimeCount(long timeCount) {
        mTimeCount = timeCount;
    }

    public int getDoneCount() {
        return mDoneCount;
    }

    public void setDoneCount(int doneCount) {
        mDoneCount = doneCount;
    }

    public int getDoneRightCount() {
        return mDoneRightCount;
    }

    public void setDoneRightCount(int doneRightCount) {
        mDoneRightCount = doneRightCount;
    }

    public String getHeadUrl() {
        return mHeadUrl == null ? (mUserHead == null ? null : mUserHead.getFileUrl()) : mHeadUrl;
    }

    public void setHeadUrl(String headUrl) {
        mHeadUrl = headUrl;
    }
}
