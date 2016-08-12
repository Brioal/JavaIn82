package com.brioal.javain82.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.brioallib.view.CircleHead;
import com.brioal.javain82.R;
import com.brioal.javain82.activity.QListActivity;
import com.brioal.javain82.entity.SubjectEntity;
import com.brioal.javain82.view.PercentProgressBar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专题界面的专题显示RecyclerView的适配器
 * Created by Brioal on 2016/7/10.
 */

public class SubjectRecyclerViewAdapter extends RecyclerView.Adapter<SubjectRecyclerViewAdapter.SubViewHolder> {
    private Context mContext;
    private List<SubjectEntity> mList;

    public SubjectRecyclerViewAdapter(Context context, List<SubjectEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public SubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootview = LayoutInflater.from(mContext).inflate(R.layout.item_sub, parent, false);
        return new SubViewHolder(rootview);
    }

    @Override
    public void onBindViewHolder(final SubViewHolder holder, int position) {
        final SubjectEntity entity = mList.get(position);
        holder.mLanguage.setmText(entity.getLanguageTitle().toCharArray()[0] + ""); //语言
        holder.mTitle.setText(entity.getTitle()); //标题
        holder.mRate.setRating(((float) entity.getRate()) / 2); //难度

        int doneCount = entity.getMineDoneCount();
        if (doneCount != 0) { //存在本地记录
            holder.mProgressbar.setVisibility(View.VISIBLE);
            holder.mProgressbar.setData(entity.getQuestionCount(), entity.getMineDoneRightCount(), entity.getMineDoneCount() - entity.getMineDoneRightCount()); //显示进度条
            holder.mProgress.setText(doneCount + "" + entity.getQuestionCount());
        } else {
            holder.mProgress.setText("" + entity.getQuestionCount());
        }
        holder.mTime.setText(entity.getCreatedAt());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entity.setTimer(System.currentTimeMillis());
                QListActivity.enterQuestionList(mContext, entity);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SubViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_sub_language)
        CircleHead mLanguage;
        @Bind(R.id.item_sub_title)
        TextView mTitle;
        @Bind(R.id.item_sub_rate)
        AppCompatRatingBar mRate;
        @Bind(R.id.item_sub_time)
        TextView mTime;
        @Bind(R.id.item_sub_progress)
        TextView mProgress;
        @Bind(R.id.item_sub_progressbar)
        PercentProgressBar mProgressbar;

        View itemView;

        public SubViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
