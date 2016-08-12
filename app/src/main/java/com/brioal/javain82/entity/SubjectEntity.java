package com.brioal.javain82.entity;

import cn.bmob.v3.BmobObject;

/**
 * 专题实体类
 * Created by Brioal on 2016/7/9.
 */

public class SubjectEntity extends BmobObject {
    //网络数据本身
    private String mTitle;//专题题目
    private String mDesc; //专题描述
    private int mRate; //难度
    private int mQuestionCount; //问题总数
    //本地存储需要的数据
    private int mMineDoneCount; //我做的数量
    private int mMineDoneRightCount; //我做对的数量
    private String mLanguageTitle; //所属的语言
    private long mTimer; //浏览的时间

    public SubjectEntity() {
    }

    public SubjectEntity(String title, String desc, int rate, int questionCount, int mineDoneCount, int mineDoneRightCount, String languageTitle, long mTimer, String subjectid) {
        mTitle = title;
        mDesc = desc;
        mRate = rate;
        mQuestionCount = questionCount;
        mMineDoneCount = mineDoneCount;
        mMineDoneRightCount = mineDoneRightCount;
        mLanguageTitle = languageTitle;
        this.mTimer = mTimer;
        this.setObjectId(subjectid);
    }

    public long getTimer() {
        return mTimer;
    }

    public void setTimer(long timer) {
        mTimer = timer;
    }

    public int getMineDoneCount() {
        return mMineDoneCount;
    }

    public int getMineDoneRightCount() {
        return mMineDoneRightCount;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public int getRate() {
        return mRate;
    }

    public void setRate(int rate) {
        mRate = rate;
    }

    public int getQuestionCount() {
        return mQuestionCount;
    }

    public void setQuestionCount(int questionCount) {
        mQuestionCount = questionCount;
    }


    public void setMineDoneCount(int mineDoneCount) {
        mMineDoneCount = mineDoneCount;
    }

    public void setMineDoneRightCount(int mineDoneRightCount) {
        mMineDoneRightCount = mineDoneRightCount;
    }

    public String getLanguageTitle() {
        return mLanguageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        mLanguageTitle = languageTitle;
    }
}
