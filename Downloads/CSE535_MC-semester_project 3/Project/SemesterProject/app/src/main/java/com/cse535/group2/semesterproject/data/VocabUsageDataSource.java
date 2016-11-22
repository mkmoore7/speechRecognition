package com.cse535.group2.semesterproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cse535.group2.semesterproject.model.VocabUsage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jose on 11/11/2016.
 */

public class VocabUsageDataSource {
    private static final String LOG_TAG = "VocabUsageDataSource";

    private SQLiteDatabase database;
    private ProjectDatabaseHelper projectDatabaseHelper;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ProjectDatabaseHelper.DATE_TIME_FORMAT);

    private static double LAT_LON_BUFFER = 0.05;

    private static final String VOCAB_USE_SELECT_ALL = "SELECT"
            + " " + ProjectDatabaseHelper.VOCAB_USE_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP
            + " FROM " + ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME + ";";

    //SELECT * FROM VocabUsage u, Vocabulary v, VocabTopic t WHERE u.latitude > lat - CONST AND u.latitude < lat + CONST
    //AND u.longitude > lon - CONST AND u.longitude < lon + CONST AND u.vocabularyUsedId = v._id AND t._id = ID;
    private static final String VOCAB_USE_SELECT_BY_LOCATION_AND_TOPIC_ID = "SELECT"
            + " u." + ProjectDatabaseHelper.VOCAB_USE_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP
            + " FROM " + ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME + " u"
            + ", " + ProjectDatabaseHelper.VOCABULARY_TABLE_NAME + " v"
            + ", " + ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME + " t"
            + " WHERE u." + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE + " > ? - " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE + " < ? + " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE + " > ? - " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE + " < ? + " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + " = v." + ProjectDatabaseHelper.VOCABULARY_COL_TABLE_ID
            + " AND v." + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID
            + " = t." + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TABLE_ID
            + " AND t. " + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TABLE_ID + " = ?"
            + ";";

    //SELECT * FROM VocabUsage u, Vocabulary v, VocabTopic t WHERE u.latitude > lat - CONST AND u.latitude < lat + CONST
    //AND u.longitude > lon - CONST AND u.longitude < lon + CONST AND u.vocabularyUsedId = v._id AND t.topicName = TOPIC;
    private static final String VOCAB_USE_SELECT_BY_LOCATION_AND_TOPIC_NAME = "SELECT"
            + " u." + ProjectDatabaseHelper.VOCAB_USE_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP
            + " FROM " + ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME + " u"
            + ", " + ProjectDatabaseHelper.VOCABULARY_TABLE_NAME + " v"
            + ", " + ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME + " t"
            + " WHERE u." + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE + " > ? - " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE + " < ? + " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE + " > ? - " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE + " < ? + " + LAT_LON_BUFFER
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + " = v." + ProjectDatabaseHelper.VOCABULARY_COL_TABLE_ID
            + " AND t. " + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TOPIC_NAME + " = ?"
            + ";";

    private static final String VOCAB_USE_SELECT_BY_SIMILAR_TIME_AND_TOPIC_ID = "SELECT"
            + " u." + ProjectDatabaseHelper.VOCAB_USE_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP
            + " FROM " + ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME + " u"
            + ", " + ProjectDatabaseHelper.VOCABULARY_TABLE_NAME + " v"
            + ", " + ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME + " t"
            + " WHERE STRFTIME('%H:%M', u." + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP + ") < ?"
            + " AND STRFTIME('%H:%M', u." + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP + ") > ?"
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + " = v." + ProjectDatabaseHelper.VOCABULARY_COL_TABLE_ID
            + " AND v." + ProjectDatabaseHelper.VOCABULARY_COL_VOCABULARY_TOPIC_ID
            + " = t." + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TABLE_ID
            + " AND t. " + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TABLE_ID + " = ?"
            + ";";

    private static final String VOCAB_USE_SELECT_BY_SIMILAR_TIME_AND_TOPIC_NAME = "SELECT"
            + " u." + ProjectDatabaseHelper.VOCAB_USE_COL_TABLE_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE
            + ", " + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP
            + " FROM " + ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME + " u"
            + ", " + ProjectDatabaseHelper.VOCABULARY_TABLE_NAME + " v"
            + ", " + ProjectDatabaseHelper.VOCAB_TOPIC_TABLE_NAME + " t"
            + " WHERE STRFTIME('%H:%M', u." + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP + ") < ?"
            + " AND STRFTIME('%H:%M', u." + ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP + ") > ?"
            + " AND u." + ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID
            + " = v." + ProjectDatabaseHelper.VOCABULARY_COL_TABLE_ID
            + " AND t. " + ProjectDatabaseHelper.VOCAB_TOPIC_COL_TOPIC_NAME + " = ?"
            + ";";

    public VocabUsageDataSource(Context context){
        projectDatabaseHelper = new ProjectDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = projectDatabaseHelper.getWritableDatabase();
    }

    public void close(){
        projectDatabaseHelper.close();
    }

    public boolean addVocabUsage(int vocabularyUsedId, double latitude, double longitude){
        open();
        boolean addScuccess = false;

        ContentValues values = new ContentValues();

        String dateString = simpleDateFormat.format(new Date());

        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID, vocabularyUsedId);
        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE, latitude);
        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE, longitude);
        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP, dateString);

        database.beginTransaction();
        addScuccess =  database.insert(ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME, null, values) > 0;
        database.setTransactionSuccessful();
        database.endTransaction();

        close();

        return addScuccess;
    }

    public boolean addVocabUsage(int vocabularyUsedId, double latitude, double longitude, Date date){
        open();
        boolean addScuccess = false;

        ContentValues values = new ContentValues();

        String dateString = simpleDateFormat.format(date);

        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_VOCABULARY_USED_ID, vocabularyUsedId);
        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_LATITUDE, latitude);
        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_LONGITUDE, longitude);
        values.put(ProjectDatabaseHelper.VOCAB_USE_COL_TIMESTAMP, dateString);

        database.beginTransaction();
        addScuccess =  database.insert(ProjectDatabaseHelper.VOCAB_USE_TABLE_NAME, null, values) > 0;
        database.setTransactionSuccessful();
        database.endTransaction();

        close();

        return addScuccess;
    }

    public List<VocabUsage> getAllResults(){
        open();
        database = projectDatabaseHelper.getWritableDatabase();
        ArrayList<VocabUsage> dataList = new ArrayList<>();
        Cursor cursor = database.rawQuery(VOCAB_USE_SELECT_ALL, null);

        try{
            while (cursor.moveToNext()) {
                VocabUsage data = new VocabUsage();
                data.setId(cursor.getInt(0));
                data.setVocabularyUsedId(cursor.getInt(1));
                data.setLatitude(cursor.getFloat(2));
                data.setLongitude(cursor.getFloat(3));
                data.setTimestamp(simpleDateFormat.parse(cursor.getString(4)));
                dataList.add(data);
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }

    //SELECT * FROM VocabUsage u, Vocabulary v, VocabTopic t WHERE u.latitude > lat - CONST AND u.latitude < lat + CONST
    //AND u.longitude > lon - CONST AND u.longitude < lon + CONST AND u.vocabularyUsedId = v._id AND t.topicName = TOPIC;
    public List<VocabUsage> getResultsByTopicNameAndLocation(String topic, double latitude, double longitude){
        ArrayList<VocabUsage> dataList = new ArrayList<>();

        String[] parameters = new String[5];
        parameters[0] = "" + latitude;
        parameters[1] = "" + latitude;
        parameters[2] = "" + longitude;
        parameters[3] = "" + longitude;
        parameters[4] = topic;

        open();
        database = projectDatabaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(VOCAB_USE_SELECT_BY_LOCATION_AND_TOPIC_NAME, parameters);

        try{
            while (cursor.moveToNext()) {
                VocabUsage data = new VocabUsage();
                data.setId(cursor.getInt(0));
                data.setVocabularyUsedId(cursor.getInt(1));
                data.setLatitude(cursor.getFloat(2));
                data.setLongitude(cursor.getFloat(3));
                data.setTimestamp(simpleDateFormat.parse(cursor.getString(4)));
                dataList.add(data);
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }

    //SELECT * FROM VocabUsage u, Vocabulary v, VocabTopic t WHERE u.latitude > lat - CONST AND u.latitude < lat + CONST
    //AND u.longitude > lon - CONST AND u.longitude < lon + CONST AND u.vocabularyUsedId = v._id AND t._id = ID;
    public List<VocabUsage> getResultsByTopicIdAndLocation(int topicId, double latitude, double longitude){
        ArrayList<VocabUsage> dataList = new ArrayList<>();

        String[] parameters = new String[5];
        parameters[0] = "" + latitude;
        parameters[1] = "" + latitude;
        parameters[2] = "" + longitude;
        parameters[3] = "" + longitude;
        parameters[4] = "" + topicId;

        open();
        database = projectDatabaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(VOCAB_USE_SELECT_BY_LOCATION_AND_TOPIC_ID, parameters);

        try{
            while (cursor.moveToNext()) {
                VocabUsage data = new VocabUsage();
                data.setId(cursor.getInt(0));
                data.setVocabularyUsedId(cursor.getInt(1));
                data.setLatitude(cursor.getFloat(2));
                data.setLongitude(cursor.getFloat(3));
                data.setTimestamp(simpleDateFormat.parse(cursor.getString(4)));
                dataList.add(data);
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }

    public List<VocabUsage> getResultsBySimilarTimeOfDay(int topicId, Date date){
        ArrayList<VocabUsage> dataList = new ArrayList<>();

        Log.d(LOG_TAG, "full time: " + simpleDateFormat.format(date));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 30);
        String nextHalfHour = timeFormat.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, -60);
        String lastHalfHour = timeFormat.format(calendar.getTime());

        Log.d(LOG_TAG, "next half hour: " + nextHalfHour);
        Log.d(LOG_TAG, "last half hour: " + lastHalfHour);

        String[] parameters = new String[3];
        parameters[0] = nextHalfHour;
        parameters[1] = lastHalfHour;
        parameters[2] = "" + topicId;

        open();
        database = projectDatabaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(VOCAB_USE_SELECT_BY_SIMILAR_TIME_AND_TOPIC_ID, parameters);

        try{
            while (cursor.moveToNext()) {
                VocabUsage data = new VocabUsage();
                data.setId(cursor.getInt(0));
                data.setVocabularyUsedId(cursor.getInt(1));
                data.setLatitude(cursor.getFloat(2));
                data.setLongitude(cursor.getFloat(3));
                data.setTimestamp(simpleDateFormat.parse(cursor.getString(4)));
                dataList.add(data);
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }

    public List<VocabUsage> getResultsBySimilarTimeOfDay(String topicName, Date date){
        ArrayList<VocabUsage> dataList = new ArrayList<>();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 30);
        String nextHalfHour = timeFormat.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, -60);
        String lastHalfHour = timeFormat.format(calendar.getTime());

        String[] parameters = new String[3];
        parameters[0] = nextHalfHour;
        parameters[1] = lastHalfHour;
        parameters[2] = topicName;

        open();
        database = projectDatabaseHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(VOCAB_USE_SELECT_BY_SIMILAR_TIME_AND_TOPIC_NAME, parameters);

        try{
            while (cursor.moveToNext()) {
                VocabUsage data = new VocabUsage();
                data.setId(cursor.getInt(0));
                data.setVocabularyUsedId(cursor.getInt(1));
                data.setLatitude(cursor.getFloat(2));
                data.setLongitude(cursor.getFloat(3));
                data.setTimestamp(simpleDateFormat.parse(cursor.getString(4)));
                dataList.add(data);
            }
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally{
            cursor.close();
            close();
        }

        return dataList;
    }
}
