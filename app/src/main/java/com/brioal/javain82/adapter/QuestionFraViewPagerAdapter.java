package com.brioal.javain82.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.brioal.javain82.entity.QuestionEntity;
import com.brioal.javain82.fragment.QuestionTitleFragment;

import java.util.List;

/**滑动问题列表的适配器
 * Created by Brioal on 2016/7/12.
 */

public class QuestionFraViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<QuestionEntity> mList;
    private Context mContext;

    public QuestionFraViewPagerAdapter(Context mContext, List<QuestionEntity> mList, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public Fragment getItem(int position) {
        return QuestionTitleFragment.newInstance(mList.get(position).getTitle());
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
