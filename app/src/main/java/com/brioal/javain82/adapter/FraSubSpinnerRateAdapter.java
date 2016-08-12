package com.brioal.javain82.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.brioal.brioallib.view.CircleHead;
import com.brioal.javain82.R;
import com.brioal.javain82.entity.RateEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专题界面难度选择Spinner的Adapter
 * Created by Brioal on 2016/7/10.
 */

public class FraSubSpinnerRateAdapter implements SpinnerAdapter {
    private Context mContext;
    private List<RateEntity> mList;

    public FraSubSpinnerRateAdapter(Context context, List<RateEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_spinner, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RateEntity entity = mList.get(position);
        holder.mIcon.setmText(entity.getHead());
        holder.mTitle.setText(entity.getTitle());
        convertView.setBackgroundResource(R.drawable.home_fra_btn_center);
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_spinner, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RateEntity entity = mList.get(position);
        holder.mIcon.setmText(entity.getHead());
        holder.mTitle.setText(entity.getTitle());
        convertView.setBackgroundResource(R.drawable.home_fra_btn_center);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {

        return false;
    }

    class ViewHolder {
        @Bind(R.id.item_spinner_icon)
        CircleHead mIcon;
        @Bind(R.id.item_spinner_title)
        TextView mTitle;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
