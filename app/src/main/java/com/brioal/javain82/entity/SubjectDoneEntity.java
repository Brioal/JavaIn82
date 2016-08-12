package com.brioal.javain82.entity;

import cn.bmob.v3.BmobObject;

/**
 * 专题浏览实体类
 * Created by Brioal on 2016/7/9.
 */

public class SubjectDoneEntity extends BmobObject {
    private SubjectEntity mSubject;
    private User mUser;
    private int mDoneCount; //做过的题目数量
    private int mDoneRightCount; //作对的题目数量
    private boolean isCollect; //是否收藏


    public SubjectDoneEntity() {

    }

    public SubjectEntity getSubject() {
        return mSubject;
    }

    public void setSubject(SubjectEntity subject) {
        mSubject = subject;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
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

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }
}
