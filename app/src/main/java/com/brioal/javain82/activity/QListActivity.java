package com.brioal.javain82.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.NetWorkUtil;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.adapter.QueRecyclerAdapter;
import com.brioal.javain82.entity.QuestionEntity;
import com.brioal.javain82.entity.SubjectDoneEntity;
import com.brioal.javain82.entity.SubjectEntity;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.interfaces.onListDoneListener;
import com.brioal.javain82.util.Constants;
import com.brioal.javain82.view.PercentProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 专题所包含的问题列表
 */
public class QListActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.act_questionlist_title)
    TextView mTitle;
    @Bind(R.id.act_questionlist_desc)
    TextView mDesc;
    @Bind(R.id.act_questionlist_collect)
    CheckBox mCbCheck;
    @Bind(R.id.act_questionlist_collectcount)
    TextView mTvCollectCount;
    @Bind(R.id.act_questionlist_rate)
    AppCompatRatingBar mRate;
    @Bind(R.id.act_questionlist_language)
    TextView mLanguage;
    @Bind(R.id.act_questionlist_time)
    TextView mTime;
    @Bind(R.id.act_questionlist_progressbar)
    PercentProgressBar mProgressbar;
    @Bind(R.id.act_q_allcount)
    TextView mTvAllcount;
    @Bind(R.id.act_questionlist_donecount)
    TextView mTvDonecount;
    @Bind(R.id.act_questionlist_rightcount)
    TextView mTvDoneRightCount;
    @Bind(R.id.act_questionlist_errorcount)
    TextView mTvErrorcount;
    @Bind(R.id.act_questionlist_recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.act_questionlist_start)
    Button mBtnStart;
    @Bind(R.id.act_q_donecount_net)
    TextView mTvDoneCountNet;
    @Bind(R.id.act_questionlist_back)
    ImageButton mBtnBack;
    @Bind(R.id.q_list_network_data_layout)
    LinearLayout mNetDataLayout;

    static {
        TAG = "QuestionListAct";
    }

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @Bind(R.id.act_q_donerightcount_net)
    TextView mTvDonerightcountNet;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.act_q_list_btn_refresh)
    TextView mTvRefresh;

    private SubjectEntity mSubjectEntity;
    private boolean isLogined = false;
    private User mUser;
    private String mSubObjectId;
    private ArrayList<QuestionEntity> mQuestionEntityList;
    private QueRecyclerAdapter mRecyclerViewAdapter;

    @Override
    public void initData() {
        super.initData();
        try {
            mSubjectEntity = (SubjectEntity) getIntent().getSerializableExtra("SubjectEntity");
            mSubObjectId = mSubjectEntity.getObjectId();
            mUser = Constants.getDataLoader(mContext).getUser();
            if (mUser == null) {
                isLogined = false;
            } else {
                isLogined = true;
            }
        } catch (Exception e) {
            KLog.i(TAG, "获取专题数据失败:" + e.toString());
            ToastUtils.showToast(mContext, "获取专题数据失败,请退出重试", ExtraToast.LENGTH_LONG);
        }

    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_q_list);
        ButterKnife.bind(this);
        showLocal();

    }

    //显示基础数据
    public void showLocal() {
        isNetWork = NetWorkUtil.isNetworkConnected(mContext);
        try {
            //基础数据显示

            //如果没网
            if (!isNetWork) {
                //隐藏收藏 , 隐藏收藏人数
                mCbCheck.setVisibility(View.GONE);
                mTvCollectCount.setVisibility(View.GONE);
                //隐藏网络做题数量 , 隐藏网络正确了
                mNetDataLayout.setVisibility(View.GONE);
            } else { //如果有网
                //初始化收藏 ,网络数据
                mCbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (Constants.getDataLoader(mContext).getUser() == null) {
                            UserOperatorActivity.startUserOperator(mContext, true,1);
                        }
                    }
                });
                mTvCollectCount.setText(0 + "");
                mTvDoneCountNet.setText(0 + "人做过");
                mTvDonerightcountNet.setText("正确率:" + 0);
                try {
                    //加载做过的人数 ,正确率 收藏的人数
                    BmobQuery<SubjectDoneEntity> query = new BmobQuery<>();
                    query.addWhereEqualTo("mSubject", mSubjectEntity);
                    query.findObjects(new FindListener<SubjectDoneEntity>() {
                        @Override
                        public void done(List<SubjectDoneEntity> list, BmobException e) {
                            if (list != null && list.size() != 0) {
                                mTvDoneCountNet.setText(list.size() + "人做过");
                                int collectcount = 0;
                                int donecont = 0;
                                int doneright = 0;
                                for (int i = 0; i < list.size(); i++) {
                                    if (list.get(i).isCollect()) {
                                        collectcount++;
                                    }
                                    donecont += list.get(i).getDoneCount();
                                    mDoneRightCount += list.get(i).getDoneRightCount();
                                }
                                if (donecont == 0) {
                                    mTvDonerightcountNet.setText("正确率:0%");
                                } else {
                                    mTvDonerightcountNet.setText("正确率:" + (doneright / donecont) + "%");
                                }
                                mTvCollectCount.setText(collectcount + "");
                            }
                        }
                    });

                    //如果已登录,查询是否收藏 ,查询做题记录
                    if (mUser != null) {
                        BmobQuery<SubjectDoneEntity> doneQuery = new BmobQuery<>();
                        doneQuery.addWhereEqualTo("mSubject", mSubjectEntity);
                        doneQuery.addWhereEqualTo("mUser", mUser);
                        doneQuery.findObjects(new FindListener<SubjectDoneEntity>() {
                            @Override
                            public void done(List<SubjectDoneEntity> list, BmobException e) {
                                if (list != null && list.size() != 0) {
                                    KLog.i("查询用户收藏信息成功");
                                    mCbCheck.setChecked(list.get(0).isCollect());
                                    mTvDonecount.setText("已做:" + list.get(0).getDoneCount());
                                    mTvDoneRightCount.setText("正确:" + list.get(0).getDoneRightCount());
                                    mTvErrorcount.setText("错误:" + (list.get(0).getDoneCount() - list.get(0).getDoneRightCount()));
                                    if (list.get(0).getDoneCount() != 0) {
                                        mProgressbar.setData(mQuestionEntityList.size(), list.get(0).getDoneRightCount(), (list.get(0).getDoneCount() - list.get(0).getDoneRightCount()));
                                    }
                                } else {
                                    KLog.i("查询用户做题信息失败");
                                    if (e != null) {
                                        KLog.i(e.toString());
                                    }
                                }
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mTitle.setText(mSubjectEntity.getTitle()); //标题
            mDesc.setText(mSubjectEntity.getDesc());//描述
            mRate.setRating(mSubjectEntity.getRate()); //难度
            mLanguage.setText(mSubjectEntity.getLanguageTitle());
            mTime.setText(mSubjectEntity.getCreatedAt());
            mBtnBack.setOnClickListener(this);
            mBtnStart.setOnClickListener(this);
        } catch (Exception e) {
            KLog.i(TAG, "专题数据获取失败:" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
        if (!NetWorkUtil.isNetworkConnected(mContext)) { //没网的时候加载本地数据
            // TODO: 2016/7/13 加载本地列表
        }
    }

    private boolean isNetWork = false; //是否有网


    @Override
    public void loadDataNet() {
        super.loadDataNet();
        if (mQuestionEntityList == null) {
            mQuestionEntityList = new ArrayList<>();
        }
        //加载网络的问题列表
        Constants.getDataLoader(mContext).getQuestionListNet(mSubjectEntity.getObjectId(), new onListDoneListener() {
            @Override
            public void success(List list) {
                mQuestionEntityList.clear();
                for (int i = 0; i < list.size(); i++) {
                    mQuestionEntityList.add((QuestionEntity) list.get(i));
                }
                KLog.i(TAG, "加载问题数据成功:" + list.size());
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void failed(String message) {
                KLog.i(TAG, "加载问题数据失败" + message);
                mHandler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public void setView() {
        super.setView();
        if (mQuestionEntityList.size() == 0) {
            mRecyclerview.setVisibility(View.GONE);
            mTvRefresh.setVisibility(View.VISIBLE);
            mBtnStart.setVisibility(View.GONE);
            mTvRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadDataNet();
                }
            });
            return;
        }
        mRecyclerview.setVisibility(View.VISIBLE);
        mTvRefresh.setVisibility(View.GONE);
        mBtnStart.setVisibility(View.VISIBLE);
        if (mRecyclerViewAdapter == null) {
            mRecyclerViewAdapter = new QueRecyclerAdapter(mContext, mQuestionEntityList);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerview.setAdapter(mRecyclerViewAdapter);
        } else {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void saveDataLocal() {
        super.saveDataLocal();

    }


    private int mDoneCount;
    private int mDoneRightCount;

    //上传专题信息到网络
    public void updateDoneDataNet() {
        mDoneCount = 0;
        mDoneRightCount = 0;
        for (int i = 0; i < mQuestionEntityList.size(); i++) {
            if (mQuestionEntityList.get(i).getType() != 0) {
                mDoneCount++;
                if (mQuestionEntityList.get(i).getType() == 1) {
                    mDoneRightCount++;
                }
            }
        }
        mTvDonecount.setText("已做:" + mDoneCount);
        mTvDoneRightCount.setText("正确:" + mDoneRightCount);
        mTvErrorcount.setText("错误:" + (mDoneCount - mDoneRightCount));
        if (mDoneCount != 0) {
            mProgressbar.setVisibility(View.VISIBLE);
            mProgressbar.setData(mQuestionEntityList.size(), mDoneRightCount, mDoneCount - mDoneRightCount);
        }
        //保存数据到网络
        //如果用户为空 ,直接添加一条游客记录
        if (mUser == null) {
            SubjectDoneEntity entity = new SubjectDoneEntity();
            entity.setSubject(mSubjectEntity);
            entity.setUser(mUser);
            entity.setDoneCount(mDoneCount);
            entity.setDoneRightCount(mDoneRightCount);
            entity.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        KLog.i("保存一条游客专题记录到网络成功");
                    } else {
                        KLog.i(e.toString());
                    }
                }
            });
        } else {
            //如果用户不为空,查找是否存在记录
            BmobQuery<SubjectDoneEntity> query = new BmobQuery<>();
            query.addWhereEqualTo("mSubject", mSubjectEntity);
            query.addWhereEqualTo("mUser", mUser);
            final int finalDoneRightCount = mDoneRightCount;
            final int finalDoneCount = mDoneCount;
            query.findObjects(new FindListener<SubjectDoneEntity>() {
                @Override
                public void done(List<SubjectDoneEntity> list, BmobException e) {
                    if (list != null && list.size() != 0) {
                        //存在记录
                        //更新操作
                        SubjectDoneEntity entity = list.get(0);
                        entity.setCollect(mCbCheck.isChecked());
                        entity.setDoneCount(finalDoneCount);
                        entity.setDoneRightCount(finalDoneRightCount);
                        entity.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    KLog.i("更新用户专题操作成功");
                                } else {
                                    KLog.i(e.toString());
                                }
                            }
                        });
                    } else if (list.size() == 0) {
                        SubjectDoneEntity entity = new SubjectDoneEntity();
                        entity.setSubject(mSubjectEntity);
                        entity.setUser(mUser);
                        entity.setCollect(mCbCheck.isChecked());
                        entity.setDoneCount(mDoneCount);
                        entity.setDoneRightCount(mDoneRightCount);
                        entity.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    KLog.i("保存一条游客专题记录到网络成功");
                                } else {
                                    KLog.i(e.toString());
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {

            mQuestionEntityList = (ArrayList<QuestionEntity>) data.getSerializableExtra("Questions");
            if (mQuestionEntityList != null && mQuestionEntityList.size() != 0) {
                updateDoneDataNet();
            }
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mUser = Constants.getDataLoader(mContext).getUser();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataLocal();
        if (mQuestionEntityList != null && mQuestionEntityList.size() != 0) {
            updateDoneDataNet();
        }
    }

    public static void enterQuestionList(Context context, SubjectEntity entity) {
        Intent intent = new Intent(context, QListActivity.class);
        intent.putExtra("SubjectEntity", entity);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_questionlist_start:
                //开始做题
                QDetailActivity.enterQuestionDetail(mContext, mQuestionEntityList, 0);
                break;

            case R.id.act_questionlist_back:
                //退出
                finish();
                break;
        }
    }


}
