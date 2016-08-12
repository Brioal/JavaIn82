package com.brioal.javain82.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brioal.brioallib.base.BaseActivity;
import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.ExtraToast;
import com.brioal.brioallib.util.NetWorkUtil;
import com.brioal.brioallib.util.ToastUtils;
import com.brioal.javain82.R;
import com.brioal.javain82.adapter.QuestionFraViewPagerAdapter;
import com.brioal.javain82.entity.QuestionDoneEntity;
import com.brioal.javain82.entity.QuestionEntity;
import com.brioal.javain82.entity.User;
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

public class QDetailActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.question_detail_progressbar)
    PercentProgressBar mProgressbar;
    @Bind(R.id.question_detail_all)
    TextView mTvAllCount;
    @Bind(R.id.question_detail_hasdone)
    TextView mTvHasdoneCount;
    @Bind(R.id.question_detail_doneright)
    TextView mTvDonerightCount;
    @Bind(R.id.question_detail_doneerror)
    TextView mTvDoneerror;
    @Bind(R.id.question_detail_done_average)
    TextView mTvDoneCountNet;
    @Bind(R.id.question_detail_doneright_average)
    TextView mTvDoneRightNet;
    @Bind(R.id.question_detail_ask_btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.question_detail_btn_expand)
    CheckBox mBtnExpand;
    @Bind(R.id.question_detail_tv_currentindex)
    TextView mTvCurrent;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.question_detail_viewpager)
    ViewPager mViewpager;
    @Bind(R.id.question_detail_answer_layoutA)
    RelativeLayout mLayoutA;
    @Bind(R.id.question_detail_answer_layoutB)
    RelativeLayout mLayoutB;
    @Bind(R.id.question_detail_answer_layoutC)
    RelativeLayout mLayoutC;
    @Bind(R.id.question_detail_answer_layoutD)
    RelativeLayout mLayoutD;
    @Bind(R.id.question_detail_parise)
    CheckBox mCbParise;
    @Bind(R.id.question_detail_commit)
    TextView mBtnCommit;
    @Bind(R.id.question_detail_collect)
    CheckBox mCbCollect;
    @Bind(R.id.question_detail_choose_layout)
    LinearLayout mChooseLayout;
    @Bind(R.id.question_detail_btn_tip)
    ImageButton mBtnShowTip;
    @Bind(R.id.act_q_detail_netdata_layout)
    LinearLayout mNetDataLayout;
    @Bind(R.id.question_detail_rate)
    AppCompatRatingBar mRateBar;

    private boolean isChoose = true; //默认选择题
    private int mDoneCount; //做了得题目数量
    private int mDoneRight; //作对了的题目数量
    private int mDoneError; //做错了的题目数量


    private ArrayList<QuestionEntity> mQuestions; //问题实体类
    private int mCurrentIndex; //当前下标
    private QuestionEntity mCurrentQuestion; //当前问题
    private QuestionFraViewPagerAdapter mViewPagerAdapter;

    @Override   //获取传入的数据
    public void initData() {
        super.initData();
        try {
            mQuestions = (ArrayList<QuestionEntity>) getIntent().getSerializableExtra("QuestionList");
            mCurrentIndex = getIntent().getIntExtra("Index", 0);
            mUser = Constants.getDataLoader(mContext).getUser();
            mViewPagerAdapter = new QuestionFraViewPagerAdapter(mContext, mQuestions, getSupportFragmentManager());
        } catch (Exception e) {
            KLog.i(TAG, e.toString());
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setContentView(R.layout.act_q_detail);
        ButterKnife.bind(this);
        //设置适配器
        mViewpager.setAdapter(mViewPagerAdapter);
        mViewpager.setCurrentItem(mCurrentIndex);
        //问题滚动时更新当前问题数据
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //清楚当前的选项
                setData();
                resert();
                mCurrentIndex = position;
                initHasData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initHasData();
        mAppBar.setExpanded(false);
    }

    private boolean isChanged = false;

    //清除界面
    public void resert() {
        //隐藏Tip
        mBtnShowTip.setVisibility(View.GONE);
        //提交按钮复位
        mBtnCommit.setText("提交答案");
        mBtnCommit.setTextColor(Color.WHITE);
        mBtnCommit.setBackgroundResource(R.drawable.item_sub_bg);
        //选项复位
        mLayoutA.setBackgroundResource(R.drawable.item_sub_bg);
        mLayoutB.setBackgroundResource(R.drawable.item_sub_bg);
        mLayoutC.setBackgroundResource(R.drawable.item_sub_bg);
        mLayoutD.setBackgroundResource(R.drawable.item_sub_bg);
        //选项可点击
        mLayoutA.setEnabled(true);
        mLayoutB.setEnabled(true);
        mLayoutC.setEnabled(true);
        mLayoutD.setEnabled(true);
        //当前题目状态复位
        mType = 0;
        mDoneCount = 0;
        mDoneRight = 0;
        mCurrentAnswer = "";
        mDoneCountAverage = 0;
        mDoneRightCountAverage = 0;
        //点赞,收藏信息复位
        isParise = false;
        isCollect = false;
        //是否更改信息复位
        isChanged = false;

    }

    //保存数据到网络
    public void setData() {
        if (isChanged) {
            mQuestions.get(mCurrentIndex).setType(mType); // 设置进度信息
            uploadQuestionDoneEntity(); //上传进度信息
        }
    }

    private boolean isNetWork = false; //是否有网络
    private boolean isParise = false;
    private boolean isCollect = false;

    //将已有的数据加载显示到界面
    private void initHasData() {

        isNetWork = NetWorkUtil.isNetworkConnected(mContext);
        mCurrentQuestion = mQuestions.get(mCurrentIndex); //当前题目
        mRightAnswer = mQuestions.get(mCurrentIndex).getRightAnswer(); //正确答案

        //显示答案 ,如果选项A为空,则是简答题
        if (mRightAnswer != null && mRightAnswer.length() > 4) { //问答题
            isChoose = false;
            //隐藏选择区域
            mChooseLayout.setVisibility(View.GONE);
            //提交按钮改成显示答案按钮
            mBtnCommit.setText("显示答案");
            mBtnCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChanged = true;
                    mType = 1; //问答题直接算作对
                    TipShowActivity.showTipActivity(mContext, mCurrentQuestion.getRightAnswer());
                    //设置按钮状态
                    mDoneCount++;
                    mDoneRight++;
                    mTvHasdoneCount.setText("已做:" + mDoneCount);
                    mTvDonerightCount.setText("正确:" + mDoneRight);
                    mTvDoneerror.setText("错误:" + (mDoneCount - mDoneRight));
                    if (mDoneCount != 0) {
                        mProgressbar.setData(mQuestions.size(), mDoneRight, (mDoneCount - mDoneRight));
                    }
                }
            });
        } else { //选择题
            isChoose = true;
            //显示选择区域
            mChooseLayout.setVisibility(View.VISIBLE);
            //隐藏显示答案按钮
            mBtnCommit.setText("提交答案");
            mBtnCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChanged = true;
                    if (mCurrentAnswer.isEmpty()) { //未选择答案
                        startShake(mBtnCommit);
                        ToastUtils.showToast(mContext, "答案未选择", ExtraToast.LENGTH_SHORT);
                    } else {
                        //判断是否完全正确
                        if (mRightAnswer.equals(mCurrentAnswer)) {
                            mBtnCommit.setText("答案正确");
                            mBtnCommit.setBackgroundResource(R.drawable.ic_answer_right);
                            //设置题目标示
                            mType = 1;
                            mDoneCount++;
                            mDoneRight++;
                            mTvHasdoneCount.setText("已做:" + mDoneCount);
                            mTvDonerightCount.setText("正确:" + mDoneRight);
                            mTvDoneerror.setText("错误:" + (mDoneCount - mDoneRight));
                            if (mDoneCount != 0) {
                                mProgressbar.setData(mQuestions.size(), mDoneRight, (mDoneCount - mDoneRight));
                            }

                        } else {
                            //显示正确的答案
                            showRightChooseAnswer();
                            //更新按钮状态
                            mBtnCommit.setText("答案错误");
                            mDoneCount++;
                            mTvHasdoneCount.setText("已做:" + mDoneCount);
                            mTvDonerightCount.setText("正确:" + mDoneRight);
                            mTvDoneerror.setText("错误:" + (mDoneCount - mDoneRight));
                            if (mDoneCount != 0) {
                                mProgressbar.setData(mQuestions.size(), mDoneRight, (mDoneCount - mDoneRight));
                            }
                            mBtnCommit.setBackgroundResource(R.drawable.ic_answer_error);
                            //设置题目标示
                            mType = 2;
                        }
                        //显示提示按钮
                        if (mCurrentQuestion.getTip() != null) { //存在提示
                            mBtnShowTip.setVisibility(View.VISIBLE);
                            mBtnShowTip.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TipShowActivity.showTipActivity(mContext, mCurrentQuestion.getTip());
                                }
                            });
                        }

                    }
                }
            });
            //选项添加点击事件
            mLayoutA.setOnClickListener(this);
            mLayoutB.setOnClickListener(this);
            mLayoutC.setOnClickListener(this);
            mLayoutD.setOnClickListener(this);
        }
        //显示难度
        mRateBar.setRating(mCurrentQuestion.getRate());
        //显示题目总数
        mTvAllCount.setText("题目总数:" + mQuestions.size());
        //显示已做
        mDoneCount = 0;
        for (int i = 0; i < mQuestions.size(); i++) {
            QuestionEntity question = mQuestions.get(i);
            if (question.getType() != 0) {  //0没做  1 ,做对 , 2 , 作错
                mDoneCount++;
                if (question.getType() == 1) {
                    mDoneRight++;
                }
            }

        }
        mDoneError = mDoneCount - mDoneRight;
        mTvHasdoneCount.setText("已做:" + mDoneCount);
        //显示作对
        mTvDonerightCount.setText("正确:" + mDoneRight);
        //显示做错
        mTvDoneerror.setText("错误:" + (mDoneCount - mDoneRight));
        //进度条显示
        if (mDoneCount != 0) {
            mProgressbar.setData(mQuestions.size(), mDoneRight, mDoneError);
        }
        //显示当前问题的序号
        mTvCurrent.setText((mCurrentIndex + 1) + " / " + mQuestions.size());
        //前进后退点击事件添加
        mBtnExpand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAppBar.setExpanded(isChecked, true);
            }
        });
        mBtnBack.setOnClickListener(this);

        //如果没网,隐藏收藏,点赞
        if (!isNetWork) {
            //隐藏收藏点赞
            mCbParise.setVisibility(View.GONE);
            mCbCollect.setVisibility(View.GONE);

            //隐藏网络数据
            mNetDataLayout.setVisibility(View.GONE);
        } else {
            //初始化收藏点赞
            mCbParise.setChecked(false);
            mCbCollect.setChecked(false);
            mCbParise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (Constants.getDataLoader(mContext).getUser() == null) {
                        UserOperatorActivity.startUserOperator(mContext, true,0);
                    }
                    isParise = isChecked;
                    isChanged = true;
                }
            });

            mCbCollect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (Constants.getDataLoader(mContext).getUser() == null) {
                        UserOperatorActivity.startUserOperator(mContext, true,0);
                    }
                    isCollect = isChecked;
                    isChanged = true;
                }
            });
            //初始化网络数据
            mTvDoneCountNet.setText("做过人数:0");
            mTvDoneRightNet.setText("正确率:0%");
            //如果当前已登录 ,从网络加载是否收藏或者点赞
            if (mUser != null) {
                BmobQuery<QuestionDoneEntity> collectQuery = new BmobQuery<>();
                collectQuery.addWhereEqualTo("mQuestion", mCurrentQuestion);
                collectQuery.addWhereEqualTo("mUser", mUser);
                collectQuery.order("-createdAt");
                collectQuery.findObjects(new FindListener<QuestionDoneEntity>() {
                    @Override
                    public void done(List<QuestionDoneEntity> list, BmobException e) {
                        if (list != null && list.size() != 0) {
                            QuestionDoneEntity doneEntity = list.get(0);
                            mCbParise.setChecked(doneEntity.isPraise()); //是否点赞
                            isCollect = doneEntity.isCollect();
                            isParise = doneEntity.isPraise();
                            mCbCollect.setChecked(doneEntity.isCollect()); //是否收藏
                        } else {
                            KLog.i("加载当前用的的收藏数据失败");
                            if (e != null) {
                                KLog.i(e.toString());
                            }
                        }
                    }
                });
            }
            //查询做过的人数与网络正确率
            BmobQuery<QuestionDoneEntity> query = new BmobQuery<>();
            query.addWhereEqualTo("mQuestion", mCurrentQuestion);
            query.findObjects(new FindListener<QuestionDoneEntity>() {
                @Override
                public void done(List<QuestionDoneEntity> list, BmobException e) {
                    if (list != null && list.size() != 0) {
                        //存在网络做题记录
                        mDoneCountAverage = list.size();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getType() == 1) {
                                mDoneRightCountAverage++;
                            }
                        }
                        mTvDoneCountNet.setText("做过人数:" + mDoneCountAverage);
                        mTvDoneRightNet.setText("正确率:" + (mDoneRightCountAverage * 100 / mDoneCountAverage) + "%");
                        KLog.i("加载当前问题的网络数据成功");
                    } else {
                        if (e != null) {

                            KLog.i(e.toString());
                        }
                        KLog.i("加载当前问题的网络数据失败");
                    }
                }
            });
        }


    }


    @Override
    public void loadDataLocal() {
        super.loadDataLocal();
    }

    //组建抖动
    public void startShake(View view) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        view.startAnimation(animation);
    }

    //显示网络数据
    @Override
    public void loadDataNet() {
        super.loadDataNet();
        //查找网络做题数据
        BmobQuery<QuestionDoneEntity> query = new BmobQuery<>();
        QuestionEntity question = new QuestionEntity();
        question.setObjectId(mCurrentQuestion.getObjectId());
        query.addWhereEqualTo("mQuestion", query);
        query.findObjects(new FindListener<QuestionDoneEntity>() {
            @Override
            public void done(List<QuestionDoneEntity> list, BmobException e) {
                if (e == null && list != null) {
                    mDoneCountAverage = list.size();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getType() == 1) {
                            mDoneRightCountAverage++;
                        }
                    }
                    mTvDoneCountNet.setText("做过人数:" + mDoneCountAverage);
                    mTvDoneRightNet.setText("正确率:" + (mDoneRightCountAverage * 100 / mDoneRightCountAverage) + "%");
                }
            }
        });
    }

    @Override
    public void setView() {
        super.setView();
    }

    @Override
    public void saveDataLocal() {
        super.saveDataLocal();
    }

    public static void enterQuestionDetail(Context context, ArrayList<QuestionEntity> list, int currentIndex) {
        Intent intent = new Intent(context, QDetailActivity.class);
        intent.putExtra("QuestionList", list);
        intent.putExtra("Index", currentIndex);
        ((Activity) context).startActivityForResult(intent, 0);
    }

    private String mRightAnswer; //正确答案
    private String mCurrentAnswer = ""; //当前答案
    private int mDoneCountAverage = 0; //网络上总共做过的次数
    private int mDoneRightCountAverage = 0; //网络上作对的次数

    //显示正确选项
    public void showRightChooseAnswer() {
        char[] userChoose = mCurrentAnswer.toCharArray();
        //所选选项进行正确与否的判断
        for (int i = 0; i < userChoose.length; i++) {
            if (mRightAnswer.contains(userChoose[i] + "")) {
                judgeAndSet(userChoose[i] + "", true);
            } else {
                judgeAndSet(userChoose[i] + "", false);
            }
        }
        char[] rightChoose = mRightAnswer.toCharArray();
        //标示所有正确选项
        for (int i = 0; i < rightChoose.length; i++) {
            judgeAndSet(rightChoose[i] + "", true);
        }
        //所有选项不可点击
        mLayoutA.setEnabled(false);
        mLayoutB.setEnabled(false);
        mLayoutC.setEnabled(false);
        mLayoutD.setEnabled(false);

    }

    public void judgeAndSet(String answer, boolean right) {
        switch (answer) {
            case "A":
                if (right) {
                    mLayoutA.setBackgroundResource(R.drawable.ic_answer_right);
                } else {
                    mLayoutA.setBackgroundResource(R.drawable.ic_answer_error);
                }
                break;
            case "B":
                if (right) {
                    mLayoutB.setBackgroundResource(R.drawable.ic_answer_right);
                } else {
                    mLayoutB.setBackgroundResource(R.drawable.ic_answer_error);
                }
                break;
            case "C":
                if (right) {
                    mLayoutC.setBackgroundResource(R.drawable.ic_answer_right);
                } else {
                    mLayoutC.setBackgroundResource(R.drawable.ic_answer_error);
                }
                break;
            case "D":
                if (right) {
                    mLayoutD.setBackgroundResource(R.drawable.ic_answer_right);
                } else {
                    mLayoutD.setBackgroundResource(R.drawable.ic_answer_error);
                }
                break;
        }
    }

    //点击答案之后的处理
    public void answerDeal(String choose) {
        RelativeLayout layout = null;
        switch (choose) {
            case "A":
                layout = mLayoutA;
                break;
            case "B":
                layout = mLayoutB;
                break;
            case "C":
                layout = mLayoutC;
                break;
            case "D":
                layout = mLayoutD;
                break;
        }
        //设置当前选项为选中
        if (mCurrentAnswer.contains(choose)) { //已选中
            //取消选择
            layout.setBackgroundResource(R.drawable.item_sub_bg);
            //从答案删除
            mCurrentAnswer.replace(choose, "");

        } else { //未选中
            //添加选中
            layout.setBackgroundResource(R.drawable.ic_answer_select);
            //添加到答案
            mCurrentAnswer += choose;
        }
    }


    private int mType = 0; //未做 , 1 作对 , 2 做错

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.question_detail_answer_layoutA: //点击A
                answerDeal("A");
                break;
            case R.id.question_detail_answer_layoutB: //点击B
                answerDeal("B");
                break;
            case R.id.question_detail_answer_layoutC: //点击C
                answerDeal("C");
                break;
            case R.id.question_detail_answer_layoutD: //点击D
                answerDeal("D");
                break;
            case R.id.question_detail_ask_btn_back: // 下一题:
                backResult();
                break;

        }
    }

    //返回结果
    public void backResult() {
        Intent intent = new Intent();
        intent.putExtra("Questions", mQuestions);
        setResult(RESULT_OK, intent);
        finish();
    }

    //保存当前题目的DOneEntity信息到网络
    public void uploadQuestionDoneEntity() {
        //如果当前用户为空,直接上传一个用户为空的记录仪
        mObjectid = mCurrentQuestion.getObjectId();
        final boolean parise = isParise;
        final boolean collect = isCollect;
        final int type = mType;
        if (mUser == null) {
            QuestionDoneEntity entity = new QuestionDoneEntity();
            QuestionEntity question = new QuestionEntity();
            question.setObjectId(mObjectid);
            entity.setQuestion(question);
            entity.setUser(mUser);
            entity.setType(type);
            entity.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e != null) {
                        KLog.i(e.toString());
                    } else {
                        KLog.i("添加游客问题记录成功");
                    }
                }
            });
            return;
        }
        //删除原纪录
        final BmobQuery<QuestionDoneEntity> query = new BmobQuery<>();
        QuestionEntity question = new QuestionEntity();
        question.setObjectId(mObjectid);
        query.addWhereEqualTo("mQuestion", question);
        query.addWhereEqualTo("mUser", mUser);
        query.findObjects(new FindListener<QuestionDoneEntity>() {
            @Override
            public void done(List<QuestionDoneEntity> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    //存在纪录
                    //更新纪录

                    QuestionDoneEntity entity = new QuestionDoneEntity();
                    entity.setObjectId(list.get(0).getObjectId());
                    QuestionEntity question = new QuestionEntity();
                    question.setObjectId(mObjectid);
                    entity.setQuestion(question);
                    entity.setUser(mUser);
                    entity.setType(type);
                    entity.setCollect(collect);
                    entity.setPraise(parise);
                    entity.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null) {
                                KLog.i(e.toString());
                            } else {
                                KLog.i("更新问题记录成功");
                            }
                        }
                    });
                } else if (list != null && list.size() == 0) {
                    //不存在记录 ,添加纪录
                    QuestionDoneEntity entity = new QuestionDoneEntity();
                    QuestionEntity question = new QuestionEntity();
                    question.setObjectId(mObjectid);
                    entity.setQuestion(question);
                    entity.setUser(mUser);
                    entity.setType(type);
                    entity.setCollect(collect);
                    entity.setPraise(parise);
                    entity.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e != null) {
                                KLog.i(e.toString());
                            } else {
                                KLog.i("添加问题记录成功");
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isExpand = false;


    @Override
    public void onBackPressed() {
        if (mBtnExpand.isChecked()) { //说明已扩展
            mBtnExpand.setChecked(false);
        } else {
            backResult();
            super.onBackPressed();
        }
    }

    private User mUser;
    private String mObjectid;
}
