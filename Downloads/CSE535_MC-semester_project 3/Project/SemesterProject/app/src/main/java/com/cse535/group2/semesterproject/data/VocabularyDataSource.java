package com.cse535.group2.semesterproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cse535.group2.semesterproject.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 11/11/2016.
 */

public class VocabularyDataSource {
    private static final String LOG_TAG = "VocabularyDataSource";

    private SQLiteDatabase database;
    private ProjectDatabaseHelper projectDatabaseHelper;

    private static final String VOCABULARY_SELECT_ALL = "SELECT"
            + " " + ProjectDatabaseHelper.VOCABULARY_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_NAME
            + ", " + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID
            + " FROM " + ProjectDatabaseHelper.VOCABULARY_TABLE_NAME + ";";

    private static final String VOCABULARY_SELECT_ALL_BY_TOPIC = "SELECT"
            + " v." + ProjectDatabaseHelper.VOCABULARY_COL_TABLE_ID
            + ", v." + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_NAME
            + ", v." + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID
            + " FROM " + ProjectDatabaseHelper.VOCABULARY_TABLE_NAME + " v"
            + ", " + ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME + " t"
            + " WHERE v." + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID
            + " = t." + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TABLE_ID
            + " AND v." + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID
            + " = ?"
            + ";";

    public VocabularyDataSource(Context context){
        projectDatabaseHelper = new ProjectDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = projectDatabaseHelper.getWritableDatabase();
    }

    public void close(){
        projectDatabaseHelper.close();
    }

    public boolean addVocabulary(String vocabularyName, int vocabularyTopicId){
        open();
        boolean addScuccess = false;

        ContentValues values = new ContentValues();

        values.put(ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_NAME, vocabularyName);
        values.put(ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID, vocabularyTopicId);

        database.beginTransaction();
        addScuccess =  database.insert(ProjectDatabaseHelper.VOCABULARY_TABLE_NAME, null, values) > 0;
        database.setTransactionSuccessful();
        database.endTransaction();

        close();

        return addScuccess;
    }

    public List<Vocabulary> getAllResults(){
        open();
        database = projectDatabaseHelper.getWritableDatabase();
        ArrayList<Vocabulary> dataList = new ArrayList<>();
        Cursor cursor = database.rawQuery(VOCABULARY_SELECT_ALL, null);

        try{
            while (cursor.moveToNext()) {
                Vocabulary data = new Vocabulary();
                data.setId(cursor.getInt(0));
                data.setVocabularyName(cursor.getString(1));
                data.setVocabularyTopicId(cursor.getInt(2));
                dataList.add(data);
            }
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }

    public List<Vocabulary> getAllResultsByTopicId(int topicId){
        open();
        database = projectDatabaseHelper.getWritableDatabase();
        ArrayList<Vocabulary> dataList = new ArrayList<>();

        String[] parameters = new String[1];
        parameters[0] = "" + topicId;

        Cursor cursor = database.rawQuery(VOCABULARY_SELECT_ALL_BY_TOPIC, parameters);

        try{
            while (cursor.moveToNext()) {
                Vocabulary data = new Vocabulary();
                data.setId(cursor.getInt(0));
                data.setVocabularyName(cursor.getString(1));
                data.setVocabularyTopicId(cursor.getInt(2));
                dataList.add(data);
            }
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }
}
