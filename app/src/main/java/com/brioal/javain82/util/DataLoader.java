package com.brioal.javain82.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.brioal.brioallib.klog.KLog;
import com.brioal.brioallib.util.NetWorkUtil;
import com.brioal.javain82.entity.LanguageEntity;
import com.brioal.javain82.entity.QuestionEntity;
import com.brioal.javain82.entity.RateEntity;
import com.brioal.javain82.entity.SortEntity;
import com.brioal.javain82.entity.SubjectEntity;
import com.brioal.javain82.entity.User;
import com.brioal.javain82.interfaces.onListDoneListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Brioal on 2016/7/9.
 */

public class DataLoader {
    private Context mContext;
    private User mUser;

    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;

    public DataLoader(Context context) {
        mContext = context;
    }

    public User getUser() {
        if (mUser == null) {
            try {
                mUser = getUserLocal();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mUser;
    }

    private DBHelper getHelper() {
        if (mHelper == null) {
            mHelper = new DBHelper(mContext, "Interview.db3", null, 1);
        }
        return mHelper;
    }

    private SQLiteDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = getHelper().getReadableDatabase();
        }
        return mDatabase;
    }

    //保存用户信息到本地
    public void saveUserLocal(User user) throws IOException {
        float startTime = System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(user);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        KLog.i("serial", "serialize str =" + serStr);
        float endTime = System.currentTimeMillis();
        KLog.i("serial", "序列化耗时为:" + (endTime - startTime));
        SharedPreferences sp = mContext.getSharedPreferences("person", 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("person", serStr);
        edit.commit();
    }

    //读取本地的用户信息
    public User getUserLocal() throws IOException,
            ClassNotFoundException {
        SharedPreferences sp = mContext.getSharedPreferences("person", 0);
        String content = sp.getString("person", null);
        if (content == null || content.isEmpty()) {
            return null;
        } else {
            float startTime = System.currentTimeMillis();
            String redStr = java.net.URLDecoder.decode(content, "UTF-8");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    redStr.getBytes("ISO-8859-1"));
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            User person = (User) objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
            float endTime = System.currentTimeMillis();
            KLog.i("serial", "反序列化耗时为:" + (endTime - startTime));
            return person;
        }

    }

    //根据用户ID从网络获取用户数据
    public void getUserNet(String onjectId, QueryListener<BmobUser> listener) {
        if (NetWorkUtil.isNetworkConnected(mContext)) {
            BmobQuery<BmobUser> query = new BmobQuery<>();
            query.getObject(onjectId, listener);
        }
    }

    //清除登录信息
    public void deleteUserLocal() {
        SharedPreferences sp = mContext.getSharedPreferences("person", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    //读取本地的语言分类
    public List<LanguageEntity> getLanguageLocal() {
        Cursor cursor = null;
        int subCount = 0;
        List<LanguageEntity> list = new ArrayList<>();
        try {
            removeLanguageLocal();
            cursor = getDatabase().rawQuery("select * from Language", null);
            while (cursor.moveToNext()) {
                LanguageEntity entity = new LanguageEntity(cursor.getString(0), cursor.getInt(1));
                subCount += entity.getSubjectCount();
                list.add(entity);
            }
            list.add(0, new LanguageEntity("全部", subCount));
            KLog.i("加载本地语言分类成功,大小为:" + list.size());
        } catch (Exception e) {
            KLog.i("加载本地语言分类数据失败" + e.toString());
            e.printStackTrace();
            KLog.e(e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    //保存语言分类数据到本地
    public void saveLanguageLocal(List<LanguageEntity> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                LanguageEntity entity = list.get(i);
                getDatabase().execSQL("insert into Language values ( ? , ?  )", new Object[]{
                        entity.getTitle(),
                        entity.getSubjectCount(),
                });
            }
            KLog.i("保存" + list.size() + "条数据成功");
        } catch (Exception e) {
            KLog.i("保存语言数据到本地失败" + e.toString());
            e.printStackTrace();
        }
    }

    //删除本地的分类数据
    public void removeLanguageLocal() {
        try {
            getDatabase().execSQL("delete from Language");
        } catch (Exception e) {
            KLog.e("删除本地语言分类数据失败" + e.toString());
            e.printStackTrace();
        }
    }


    //更具语言分类获取本地的专题列表
    public List<SubjectEntity> getSubjectListLocal(String languageID) {
        List<SubjectEntity> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (languageID.equals("全部")) {
                cursor = getDatabase().rawQuery("select * from Subject ", null);

            } else {
                cursor = getDatabase().rawQuery("select * from Subject where mLanguage = '" + languageID + "'", null);
            }
            while (cursor.moveToNext()) {
                SubjectEntity entity = new SubjectEntity(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getLong(7), cursor.getString(8));
                list.add(entity);
            }
            KLog.i("根据语言ID:" + languageID + "获取专题数据成功，大小为：" + list.size());
        } catch (Exception e) {
            KLog.e("根据语言ID:" + languageID + "获取专题数据失败：" + e.toString());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }


    //保存制定语言的专题数据到本地
    public void saveSubjectLocal(List<SubjectEntity> list) {
        try {
            removeSubjectLocal(list.get(0).getLanguageTitle());
            for (int i = 0; i < list.size(); i++) {
                SubjectEntity entity = list.get(i);
                getDatabase().execSQL("insert into Subject values ( ? , ? , ? , ? , ? , ? , ? , ? , ? )", new Object[]{
                        entity.getTitle(),
                        entity.getDesc(),
                        entity.getRate(),
                        entity.getQuestionCount(),
                        entity.getMineDoneCount(),
                        entity.getMineDoneRightCount(),
                        entity.getLanguageTitle(),
                        entity.getTimer(),
                        entity.getObjectId()

                });

            }
            KLog.e("保存" + list.size() + "个专题数据到本地成功");
        } catch (Exception e) {
            KLog.e("保存" + list.size() + "个专题数据到本地失败:" + e.toString());
            e.printStackTrace();
        }
    }

    //删除指定语言的专题数据
    public void removeSubjectLocal(String languageId) {
        try {
            getDatabase().execSQL("delete from Subject where mLanguageTitle = '" + languageId + "'");
            KLog.i("删除指定语言ID:" + languageId + "下的专题数据成功");
        } catch (Exception e) {
            KLog.e("删除指定语言ID:" + languageId + "下的专题数据失败:" + e.toString());
            e.printStackTrace();
        }
    }




    private List<RateEntity> mRateEntityList;

    //获取难度分类列表
    public List<RateEntity> getRateLocal() {
        if (mRateEntityList == null) {
            mRateEntityList = new ArrayList<>();
            mRateEntityList.add(new RateEntity("全", "全部", 0, 11));
            mRateEntityList.add(new RateEntity("初", "初级", 0, 4));
            mRateEntityList.add(new RateEntity("中", "中级", 3, 7));
            mRateEntityList.add(new RateEntity("进", "进阶", 6, 9));
            mRateEntityList.add(new RateEntity("高", "高级", 8, 11));
        }

        return mRateEntityList;
    }

    private List<SortEntity> mSortEntityList;

    //获取排序规则列表
    public List<SortEntity> getSortLocal() {
        if (mSortEntityList == null) {
            mSortEntityList = new ArrayList<>();
            mSortEntityList.add(new SortEntity(0, "↓", " 热度 "));
            mSortEntityList.add(new SortEntity(1, "↑", " 热度 "));
            mSortEntityList.add(new SortEntity(2, "↓", " 时间 "));
            mSortEntityList.add(new SortEntity(3, "↑", " 时间 "));
            mSortEntityList.add(new SortEntity(4, "↓", " 难度 "));
            mSortEntityList.add(new SortEntity(4, "↑", " 难度 "));
            mSortEntityList.add(new SortEntity(6, "↓", " 数量 "));
            mSortEntityList.add(new SortEntity(7, "↑", " 数量 "));
        }
        return mSortEntityList;
    }


    //查询所有语言及其包含的专题数量
    public void getLanguageNetWithCount(final onListDoneListener listener) {
        try {
            BmobQuery<LanguageEntity> query = new BmobQuery<>();
            query.order("createdAt");
            query.findObjects(new FindListener<LanguageEntity>() {
                @Override
                public void done(List<LanguageEntity> list, BmobException e) {
                    if (list != null && list.size() != 0) {
                        listener.success(list);

                    }
                }
            });
        } catch (Exception e) {

        }

    }

    //根据条件查询专题数据  语言分类 , 难度分类 , 排序方式 跳过的条数记录
    public void getSunListAll(String language, RateEntity rate, SortEntity sort, int skip, final onListDoneListener listener) {
        try {
            int sortType = sort.getType();
            String sortConidtion = "";
            switch (sortType) {
                case 0://热度降序
                    sortConidtion = "-mDoneTimeCount";
                    break;
                case 1://热度升序
                    sortConidtion = "mDoneTimeCount";
                    break;
                case 2: //时间降序
                    sortConidtion = "-createdAt";
                    break;
                case 3://时间升序
                    sortConidtion = "createdAt";
                    break;
                case 4: //难度降序
                    sortConidtion = "-mRate";
                    break;
                case 5: //难度升序
                    sortConidtion = "mRate";
                    break;
                case 6: // 数量降序
                    sortConidtion = "-mQuestionCount";
                    break;
                case 7: //数量升序
                    sortConidtion = "mQuestionCount";
                    break;
            }
            BmobQuery<SubjectEntity> query = new BmobQuery<>();
            query.setSkip(skip); //分页大小
            if (!language.equals("全部")) {
                query.addWhereEqualTo("mLanguageTitle", language); //指定语言
            }
            query.include("mLanguage"); //添加语言显示
            query.addWhereGreaterThan("mRate", rate.getStartRate()); //难度范围
            query.addWhereLessThan("mRate", rate.getEndRate());
            query.order(sortConidtion); //排序方式
            query.findObjects(new FindListener<SubjectEntity>() {
                @Override
                public void done(List<SubjectEntity> list, BmobException e) {
                    if (e != null) {
                        KLog.i("加载专题数据失败:" + e.toString());
                        return;
                    }
                    listener.success(list);
                }
            });
        } catch (Exception e) {

        }

    }


    //加载指定专题的问题列表
    public void getQuestionListNet(String entity, final onListDoneListener listener) {
        try {
            BmobQuery<QuestionEntity> query = new BmobQuery<>();
            query.addWhereEqualTo("mSubjectId", entity);
            query.findObjects(new FindListener<QuestionEntity>() {
                @Override
                public void done(List<QuestionEntity> list, BmobException e) {
                    if (e != null || list == null || list.size() == 0) {
                        if (e != null) {
                            KLog.i("加赞网络问题数据失败:" + e.toString());
                            listener.failed(e.toString());
                        }
                        if (list == null) {
                            listener.failed("查询不到数据");
                        } else {
                            listener.failed("无记录");
                        }

                        return;
                    }
                    listener.success(list);
                }
            });
        } catch (Exception e) {

        }

    }


    //保存问题数据到本地数据库
    public void saveQuestionListLocal(List<QuestionEntity> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                QuestionEntity entity = list.get(i);
                getDatabase().execSQL("insert into Question values ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? ,　? , ? )", new Object[]{
                        entity.getTitle(),
                        entity.getRightAnswer(),
                        entity.getTip(),
                        entity.getRate(),
                        entity.getLanguage(),
                        entity.getSubject(),
                        entity.isCollect() == true ? 0 : 1,
                        entity.getType()
                });
            }
            KLog.i("保存问题数据到本地成功");
        } catch (Exception e) {
            KLog.i("保存问题数据到本地失败:" + e.toString());
            e.printStackTrace();
        }
    }

    //读取本地所有的专题数据,挑选最近访问的并按访问时间降序 ,只要十个
    public List<SubjectEntity> getRecentSubListLocal() {
        try {
            List<SubjectEntity> list = getSubjectListLocal("全部");
            List<SubjectEntity> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTimer() != 0) {
                    result.add(list.get(i));
                }
            }
            Collections.sort(result, new SubComparator());

            return result;
        } catch (Exception e) {

        }
        return new ArrayList<>();
    }

    class SubComparator implements Comparator<SubjectEntity> {

        @Override
        public int compare(SubjectEntity lhs, SubjectEntity rhs) {

            return (int) (rhs.getTimer() - lhs.getTimer());
        }
    }
}
