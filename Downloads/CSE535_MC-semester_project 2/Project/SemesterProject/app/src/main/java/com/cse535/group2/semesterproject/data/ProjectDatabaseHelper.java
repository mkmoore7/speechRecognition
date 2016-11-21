package com.cse535.group2.semesterproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cse535.group2.semesterproject.MainActivity;

import java.io.File;

/**
 * Created by Jose on 11/11/2016.
 */

public class ProjectDatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "ProjectDatabaseHelper";

    public static final String DATABASE_LOCATION = "/data/data/" + MainActivity.class.getPackage().getName() + "/databases/";
    private static final String FULL_DATABASE_PATH = ProjectDatabaseHelper.DATABASE_LOCATION + ProjectDatabaseHelper.DATABASE_NAME;
    public static final String DATABASE_NAME = "Project.db";
    private static final int DATABASE_VERSION = 2;

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //Topics for vocabulary like school, work, etc.
    public static final String VOCAB_TOPIC_TABLE_NAME = "VocabTopic";
    public static final String VOCAB_TOPIC_COL_TABLE_ID = "_id";
    public static final String VOCAB_TOPIC_COL_TOPIC_NAME = "topicName";
    private static final String VOCAB_TOPIC_SQL_CREATE_TABLE = "create table " + VOCAB_TOPIC_TABLE_NAME + " ("
            + " " + VOCAB_TOPIC_COL_TABLE_ID + " integer PRIMARY KEY autoincrement "
            + ", " + VOCAB_TOPIC_COL_TOPIC_NAME + " text"
            + " );";

    //Specific vocabulary terms
    public static final String VOCABULARY_TABLE_NAME = "Vocabulary";
    public static final String VOCABULARY_COL_TABLE_ID = "_id";
    public static final String VOCABULARY_COL_VOCABULARY_NAME = "vocabularyName";
    //Key from VocabTopic table
    public static final String VOCABULARY_COL_VOCABULARY_TOPIC_ID = "vocabularyTopicId";
    private static final String VOCABULARY_SQL_CREATE_TABLE = "create table " + VOCABULARY_TABLE_NAME + " ("
            + " " + VOCABULARY_COL_TABLE_ID + " integer PRIMARY KEY autoincrement"
            + ", " + VOCABULARY_COL_VOCABULARY_NAME + " text"
            + ", " + VOCABULARY_COL_VOCABULARY_TOPIC_ID + " integer"
            + ", FOREIGN KEY (" + VOCABULARY_COL_VOCABULARY_TOPIC_ID + ") REFERENCES " + VOCAB_TOPIC_TABLE_NAME + "(" + VOCAB_TOPIC_COL_TABLE_ID + ")"
            + " );";

    //Usage of vocabulary
    public static final String VOCAB_USE_TABLE_NAME = "VocabUse";
    public static final String VOCAB_USE_COL_TABLE_ID = "_id";
    public static final String VOCAB_USE_COL_VOCABULARY_USED_ID = "vocabularyUsedId";
    public static final String VOCAB_USE_COL_LATITUDE = "latitude";
    public static final String VOCAB_USE_COL_LONGITUDE = "longitude";
    public static final String VOCAB_USE_COL_TIMESTAMP = "timestamp";
    private static final String VOCAB_USE_SQL_CREATE_TABLE = "create table " + VOCAB_USE_TABLE_NAME + " ("
            + " " + VOCAB_USE_COL_TABLE_ID + " integer PRIMARY KEY autoincrement"
            + ", " + VOCAB_USE_COL_VOCABULARY_USED_ID + " integer"
            + ", " + VOCAB_USE_COL_LATITUDE + " real"
            + ", " + VOCAB_USE_COL_LONGITUDE + " real"
            + ", " + VOCAB_USE_COL_TIMESTAMP + " datetime"
            + ", FOREIGN KEY (" + VOCAB_USE_COL_VOCABULARY_USED_ID + ") REFERENCES " + VOCABULARY_TABLE_NAME + "(" + VOCABULARY_COL_TABLE_ID + ")"
            + " );";

//    public static final String ACCEL_TABLE_NAME = "AccelerometerData";
//
//    public static final String ACCEL_COL_TABLE_ID = "_id";
//    public static final String ACCEL_COL_TIME_STAMP = "timestamp";
//    public static final String ACCEL_COL_X_VALUE = "xValue";
//    public static final String ACCEL_COL_Y_VALUE = "yValue";
//    public static final String ACCEL_COL_Z_VALUE = "zValue";
//
//    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
//
//    private static final String ACCEL_SQL_CREATE_TABLE = "create table " + ACCEL_TABLE_NAME + " ("
//            + " " + ACCEL_COL_TABLE_ID + " integer PRIMARY KEY autoincrement, "
//            + " " + ACCEL_COL_TIME_STAMP + " datetime, "
//            + " " + ACCEL_COL_X_VALUE + " real, "
//            + " " + ACCEL_COL_Y_VALUE + " real, "
//            + " " + ACCEL_COL_Z_VALUE + " real ); ";

    public ProjectDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.beginTransaction();
            database.execSQL(VOCABULARY_SQL_CREATE_TABLE);
            database.execSQL(VOCAB_TOPIC_SQL_CREATE_TABLE);
            database.execSQL(VOCAB_USE_SQL_CREATE_TABLE);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        System.out.println("Upgrading database...");
        database.beginTransaction();
        database.execSQL("DROP TABLE IF EXISTS " + VOCABULARY_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + VOCAB_TOPIC_TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + VOCAB_USE_TABLE_NAME);
        database.setTransactionSuccessful();
        database.endTransaction();
        onCreate(database);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public static void deleteDatabase(){
        File database = new File(FULL_DATABASE_PATH);

        if(database.exists()){
            Log.d(LOG_TAG, "Database file found, deleting.");
            database.delete();
        }
        else{
            Log.d(LOG_TAG, "Database file not found.");
        }
    }
}
