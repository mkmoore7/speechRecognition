package com.cse535.group2.semesterproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cse535.group2.semesterproject.model.VocabTopic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 11/11/2016.
 */

public class VocabTopicDataSource {
    private static final String LOG_TAG = "VocabTopicDataSource";

    private SQLiteDatabase database;
    private ProjectDatabaseHelper projectDatabaseHelper;

    private static final String VOCAB_TOPIC_SELECT_ALL = "SELECT"
            + " " + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TOPIC_NAME
            + " FROM " + ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME + ";";

    public VocabTopicDataSource(Context context){
        projectDatabaseHelper = new ProjectDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = projectDatabaseHelper.getWritableDatabase();
    }

    public void close(){
        projectDatabaseHelper.close();
    }

    public boolean addVocabTopic(String vocabularyTopic){
        open();
        boolean addScuccess = false;

        ContentValues values = new ContentValues();

        values.put(ProjectDatabaseHelper.VOCAB_TOPIC_COL_TOPIC_NAME, vocabularyTopic);

        database.beginTransaction();
        addScuccess =  database.insert(ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME, null, values) > 0;
        database.setTransactionSuccessful();
        database.endTransaction();

        close();

        return addScuccess;
    }

    public List<VocabTopic> getAllResults(){
        open();
        database = projectDatabaseHelper.getWritableDatabase();
        ArrayList<VocabTopic> dataList = new ArrayList<>();
        Cursor cursor = database.rawQuery(VOCAB_TOPIC_SELECT_ALL, null);

        try{
            while (cursor.moveToNext()) {
                VocabTopic data = new VocabTopic();
                data.setId(cursor.getInt(0));
                data.setTopicName(cursor.getString(1));
                dataList.add(data);
            }
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }
}
