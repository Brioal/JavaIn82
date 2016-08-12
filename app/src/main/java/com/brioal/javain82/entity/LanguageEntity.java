package com.brioal.javain82.entity;

import cn.bmob.v3.BmobObject;

/**
 * 语言分类实体类
 * Created by Brioal on 2016/7/10.
 */

public class LanguageEntity extends BmobObject {
    private String mTitle; //语言名称
    private int mSubjectCount; //专题数量

    public LanguageEntity() {
    }

    public LanguageEntity(String title, int subjectCount) {
        mTitle = title;
        mSubjectCount = subjectCount;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getSubjectCount() {
        return mSubjectCount;
    }

    public void setSubjectCount(int subjectCount) {
        mSubjectCount = subjectCount;
    }

}
