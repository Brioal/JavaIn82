package com.brioal.javain82.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Brioal on 2016/7/10.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final String table_language = "create table Language ( mTitle primary key , mSubjectCount integer )";
    private final String table_subject = "create table Subject ( mTitle primary key , mDesc , mRate integer ,mQuestionCount integer ,   mMineDoneCount integer , mMineDoneRightCount integer ,  mLanguageTitle , long mTime , subjectId )";
    private final String table_question = "create table Question ( mTitle primary key , mAnswerA , mAnswerB , mAnswerC , mAnswerD , mRightAnswer , mTip , mRate float , mLanguage , mSubject , isCollect integer ,  mType integer ) ";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table_language);
        db.execSQL(table_subject);
        db.execSQL(table_question);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
