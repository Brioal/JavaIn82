package com.brioal.javain82.entity;

/**
 * 排序方式实体类
 * Created by Brioal on 2016/7/10.
 */

public class SortEntity {
    private int mType ;//排序方式标号
    private String mIcon ; //排序方式ICon
    private String mTitle ; //排序方式类别 0.热度降序 , 1.热度升序 , 2.时间降序 , 3.时间升序 , 4.难度降序 , 5.难度升序 , 6.数量降序 , 7.数量升序

    public SortEntity(int type, String icon, String title) {
        mType = type;
        mIcon = icon;
        mTitle = title;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
