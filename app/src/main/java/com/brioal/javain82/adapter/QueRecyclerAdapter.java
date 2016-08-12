package com.brioal.javain82.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.brioallib.view.CircleHead;
import com.brioal.javain82.R;
import com.brioal.javain82.activity.QDetailActivity;
import com.brioal.javain82.entity.QuestionEntity;
import com.brioal.javain82.util.CodeUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Brioal on 2016/7/12.
 */

public class QueRecyclerAdapter extends RecyclerView.Adapter<QueRecyclerAdapter.QuestionViewHolder> {

    private Context mContext;
    private ArrayList<QuestionEntity> mList;

    public QueRecyclerAdapter(Context context, ArrayList<QuestionEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public QuestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestionViewHolder holder, final int position) {
        QuestionEntity entity = mList.get(position);
        holder.mHead.setmText((position + 1) + "");
        holder.mTitle.setText(CodeUtil.encode(entity.getTitle().replaceAll("º", "")));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDetailActivity.enterQuestionDetail(mContext, mList, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class QuestionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_question_head)
        CircleHead mHead;
        @Bind(R.id.item_question_title)
        TextView mTitle;

        View itemView;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
