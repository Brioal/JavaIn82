package com.brioal.javain82.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseFragment;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.NetWorkUtil;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.adapter.FraSubSpinnerLanguageAdapter;
import com.brioal.javain82.adapter.FraSubSpinnerRateAdapter;
import com.brioal.javain82.adapter.FraSubSpinnerSortAdapter;
import com.brioal.javain82.adapter.SubjectRecyclerViewAdapter;
import com.brioal.javain82.entity.LanguageEntity;
import com.brioal.javain82.entity.RateEntity;
import com.brioal.javain82.entity.SortEntity;
import com.brioal.javain82.entity.SubjectEntity;
import com.brioal.javain82.interfaces.onListDoneListener;
import com.brioal.javain82.util.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;

/**
 * 专题显示列表
 * Created by Brioal on 2016/7/9.
 */

public class SubjectFragment extends BaseFragment {
    private static SubjectFragment mFragment;
    @Bind(R.id.fra_sub_spinnerlanguage)
    AppCompatSpinner mSpinnerLanguage;
    @Bind(R.id.fra_sub_spinnerrate)
    AppCompatSpinner mSpinnerRate;
    @Bind(R.id.fra_sub_spinnersort)
    AppCompatSpinner mSpinnerSort;
    @Bind(R.id.fra_sub_recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.fra_sub_tv_refesh)
    TextView mTvRefesh;
    @Bind(R.id.fra_sub_refreshlayout)
    SwipeRefreshLayout mRefreshlayout;

    private FraSubSpinnerLanguageAdapter mLanguageAdapter;
    private FraSubSpinnerRateAdapter mRateAdapter;
    private FraSubSpinnerSortAdapter mSortAdapter;
    private SubjectRecyclerViewAdapter mRecyclerViewAdapter;


    private List<LanguageEntity> mLanguageEntityList;
    private List<RateEntity> mRateEntityList;
    private List<SortEntity> mSortEntityList;
    private List<SubjectEntity> mSubjectEntityList;

    private String mLanguage = null; //所选语言
    private RateEntity mRateEntity = null; //难度
    private SortEntity mSortEntity = null; //排序方式

    private int mItemDefault = 15;
    private int mItemLoadMore = 10;

    BmobQuery<SubjectEntity> query;

    public static SubjectFragment newInstance() {
        if (mFragment == null) {
            mFragment = new SubjectFragment();
        }
        return mFragment;
    }

    private final int Handle_type_init = 0;
    private final int Handle_type_change = 1;
    private boolean isInited = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Handle_type_init) {
                setView();
            } else if (msg.what == Handle_type_change) {
                reSetView();
            }
        }
    };

    //更新界面
    private void reSetView() {
        if (mSubjectEntityList.size() == 0) {
            mRecyclerview.setVisibility(View.GONE);
            mTvRefesh.setVisibility(View.VISIBLE);
            mTvRefesh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reLoadData();
                }
            });
        } else {
            mRecyclerview.setVisibility(View.VISIBLE);
            mTvRefesh.setVisibility(View.GONE);
        }
        mRecyclerViewAdapter.notifyDataSetChanged();
        mLanguageAdapter.notifiedDataChange(mLanguageEntityList);
        if (mRefreshlayout.isRefreshing()) {
            mRefreshlayout.setRefreshing(false);
        }
    }

    @Override
    public void initData() {
        super.initData();
        isInited = false;
        mLanguageEntityList = Constants.getDataLoader(mContext).getLanguageLocal(); //获取本地存储的语言分类
        mRateEntityList = Constants.getDataLoader(mContext).getRateLocal(); //获取难度分类
        mSortEntityList = Constants.getDataLoader(mContext).getSortLocal(); //获取排序方式分类
        mSubjectEntityList = Constants.getDataLoader(mContext).getSubjectListLocal("全部");
        mLanguage = mLanguageEntityList.get(0).getTitle();
        mRateEntity = mRateEntityList.get(0);
        mSortEntity = mSortEntityList.get(0);
    }

    @Override
    public void initView() {
        super.initView();
        mRootView = inflater.inflate(R.layout.fra_subject, container, false);
        ButterKnife.bind(this, mRootView);
        //点击事件设置
        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mLanguageEntityList.get(position).getTitle().equals(mLanguage)) {
                    KLog.i(mLanguageEntityList.get(position).getTitle());
                    mLanguage = mLanguageEntityList.get(position).getTitle();
                    reLoadData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                KLog.i("Noting SelectEd");
            }
        });
        mSpinnerRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mRateEntityList.get(position).getTitle().equals(mRateEntity.getTitle())) {
                    mRateEntity = mRateEntityList.get(position);
                    reLoadData();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSortEntityList.get(position).getType() != mSortEntity.getType()) {
                    mSortEntity = mSortEntityList.get(position);
                    reLoadData();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                KLog.i();
            }
        });
        mRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reLoadData();
            }
        });
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        if (isInited) {
            mHandler.sendEmptyMessage(Handle_type_change);
        } else {
            mHandler.sendEmptyMessage(Handle_type_init);
        }
        //获取网络的专题分类
        Constants.getDataLoader(mContext).getLanguageNetWithCount(new onListDoneListener() {
            @Override
            public void success(List list) {
                mLanguageEntityList.clear();
                for (int i = 0; i < list.size(); i++) {
                    mLanguageEntityList.add((LanguageEntity) list.get(i));
                }
                mHandler.sendEmptyMessage(Handle_type_init);
                getDataNet();//更新网络的语言数据之后更新一下专题列表
            }

            @Override
            public void failed(String message) {
            }
        });

    }

    public void getDataNet() {
        //获取网络的专题数据
        Constants.getDataLoader(mContext).getSunListAll(mLanguage, mRateEntity, mSortEntity, 0, new onListDoneListener() {
            @Override
            public void success(List list) {
                mSubjectEntityList.clear();
                for (int i = 0; i < list.size(); i++) {
                    mSubjectEntityList.add((SubjectEntity) list.get(i));
                }
                KLog.i("查询指定专题列表成功:" + list.size());
                if (isInited) {
                    mHandler.sendEmptyMessage(Handle_type_change);
                } else {
                    mHandler.sendEmptyMessage(Handle_type_init);
                }
            }

            @Override
            public void failed(String message) {
                //获取失败
                KLog.e("查询专题列表失败" + message);
                ToastUtils.showToast(mContext, message, ExtraToast.LENGTH_LONG);
            }
        });
    }


    @Override
    public void setView() {
        super.setView();
        //adapter初始化
        mLanguageAdapter = new FraSubSpinnerLanguageAdapter(mContext, mLanguageEntityList);
        mRateAdapter = new FraSubSpinnerRateAdapter(mContext, mRateEntityList);
        mSortAdapter = new FraSubSpinnerSortAdapter(mContext, mSortEntityList);
        mRecyclerViewAdapter = new SubjectRecyclerViewAdapter(mContext, mSubjectEntityList);
        //adapter设置
        mSpinnerLanguage.setAdapter(mLanguageAdapter);
        mSpinnerRate.setAdapter(mRateAdapter);
        mSpinnerSort.setAdapter(mSortAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.setAdapter(mRecyclerViewAdapter);
        isInited = true;
        if (mRefreshlayout.isRefreshing()) {
            mRefreshlayout.setRefreshing(false);
        }
    }

    //Spinner选择之后重新加载数据
    public void reLoadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetWorkUtil.isNetworkConnected(mContext)) {
                    getDataNet();
                } else {
                    loadDataLocal();
                }
            }
        }).start();

    }

    //加载更多数据
    public void loadMoreData() {
        query.setSkip(mSubjectEntityList.size());
        loadDataNet();
    }

    @Override
    public void saveDataLocal() {
        super.saveDataLocal();
        Constants.getDataLoader(mContext).saveLanguageLocal(mLanguageEntityList);
        Constants.getDataLoader(mContext).saveSubjectLocal(mSubjectEntityList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        saveDataLocal();
    }

}
