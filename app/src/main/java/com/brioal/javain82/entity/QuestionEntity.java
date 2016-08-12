package com.brioal.javain82.entity;

import cn.bmob.v3.BmobObject;

/**
 * 问题实体类
 * Created by Brioal on 2016/7/9.
 */

public class QuestionEntity extends BmobObject {
    //网络数据本身包含的数据
    private String mTitle; //题目
    private String mRightAnswer; //正确答案
    private String mTip; //分析
    private float mRate; //难度评分
    private LanguageEntity mLanguageEntity; //所属语言
    private String mSubjectId; //所属专题
    private User mUser; //作者ID


    //查询后获取,然后保存到本地
    private String mLanguageTitle; //所属语言
    private String mSubjectTitle; //所属专题
    private boolean isCollect; //是否收藏
    private int mType; //问题状态


    public QuestionEntity() {
    }

    public QuestionEntity(String title,  String rightAnswer, String tip, float rate, String language, String subject, boolean isCollect, int type) {
        mTitle = title;
        mRightAnswer = rightAnswer;
        mTip = tip;
        mRate = rate;
        mLanguageTitle = language;
        mSubjectTitle = subject;
        this.isCollect = isCollect;
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public String getRightAnswer() {
        return mRightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        mRightAnswer = rightAnswer;
    }

    public String getTip() {
        return mTip;
    }

    public void setTip(String tip) {
        mTip = tip;
    }

    public float getRate() {
        return mRate;
    }

    public void setRate(float rate) {
        mRate = rate;
    }

    public LanguageEntity getLanguageEntity() {
        return mLanguageEntity;
    }

    public void setLanguageEntity(LanguageEntity languageEntity) {
        mLanguageEntity = languageEntity;
    }


    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public String getLanguageTitle() {
        return mLanguageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        mLanguageTitle = languageTitle;
    }

    public String getSubjectTitle() {
        return mSubjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        mSubjectTitle = subjectTitle;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getLanguage() {
        return mLanguageTitle;
    }

    public void setLanguage(String language) {
        mLanguageTitle = language;
    }

    public String getSubject() {
        return mSubjectTitle;
    }

    public void setSubject(String subject) {
        mSubjectTitle = subject;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
