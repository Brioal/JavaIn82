package com.brioal.javain82.entity;

import cn.bmob.v3.BmobObject;

/**问题浏览实体类
 * Created by Brioal on 2016/7/9.
 */

public class QuestionDoneEntity extends BmobObject {
    private QuestionEntity mQuestion;
    private User mUser;
    private int mType; //0 没做 ， 1 作对 ， 2 做错
    private boolean isCollect ; //是否收藏
    private boolean isPraise ; //是否点赞

    public QuestionDoneEntity() {
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }

    public QuestionEntity getQuestion() {
        return mQuestion;
    }

    public void setQuestion(QuestionEntity question) {
        mQuestion = question;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
