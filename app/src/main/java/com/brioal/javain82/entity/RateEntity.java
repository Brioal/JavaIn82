package com.brioal.javain82.entity;

/**
 * 难度分类实体类
 * Created by Brioal on 2016/7/10.
 */

public class RateEntity {
    private String mHead ; //难度的Icon
    private String mTitle; //难度名称
    private int startRate ; //开始的难度
    private int endRate ; //结束的难度

    public RateEntity(String head, String title, int startRate, int endRate) {
        mHead = head;
        mTitle = title;
        this.startRate = startRate;
        this.endRate = endRate;
    }

    public int getStartRate() {
        return startRate;
    }

    public void setStartRate(int startRate) {
        this.startRate = startRate;
    }

    public int getEndRate() {
        return endRate;
    }

    public void setEndRate(int endRate) {
        this.endRate = endRate;
    }

    public String getHead() {
        return mHead;
    }

    public void setHead(String head) {
        mHead = head;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
