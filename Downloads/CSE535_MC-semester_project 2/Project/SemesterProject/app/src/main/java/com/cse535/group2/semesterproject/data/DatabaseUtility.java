package com.cse535.group2.semesterproject.data;

import android.content.Context;
import android.util.Log;

import com.cse535.group2.semesterproject.MainActivity;
import com.cse535.group2.semesterproject.model.VocabTopic;
import com.cse535.group2.semesterproject.model.VocabUsage;
import com.cse535.group2.semesterproject.model.Vocabulary;

import java.util.List;

/**
 * Created by jose on 11/13/16.
 */

public class DatabaseUtility {
    private static final String LOG_TAG = "DatabaseUtility";
    private Context mContext;

    public DatabaseUtility(Context context){
        mContext = context;
    }

    public void setupDatabaseTables(){

    }

    public void deleteDatabase(){
        ProjectDatabaseHelper.deleteDatabase();
    }

    public void testDatabase(){
        VocabTopicDataSource vocabTopicDataSource = new VocabTopicDataSource(mContext);
        vocabTopicDataSource.addVocabTopic("test topic");
        List<VocabTopic> vocabTopicList = vocabTopicDataSource.getAllResults();
        Log.d(LOG_TAG, vocabTopicList.toString());

        VocabularyDataSource vocabularyDataSource = new VocabularyDataSource(mContext);
        vocabularyDataSource.addVocabulary("test", 1);
        List<Vocabulary> vocabularyList = vocabularyDataSource.getAllResults();
        Log.d(LOG_TAG, vocabularyList.toString());

        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(mContext);
        vocabUsageDataSource.addVocabUsage(1, 1.0, 1.0);
        List<VocabUsage> vocabUsageList = vocabUsageDataSource.getAllResults();
        Log.d(LOG_TAG, vocabUsageList.toString());
    }

    public void setupLocationPredictionData(){
        VocabTopicDataSource vocabTopicDataSource = new VocabTopicDataSource(mContext);
        vocabTopicDataSource.addVocabTopic("general");
        vocabTopicDataSource.addVocabTopic("home");
        vocabTopicDataSource.addVocabTopic("school");
        vocabTopicDataSource.addVocabTopic("restaurant");
        List<VocabTopic> vocabTopicList = vocabTopicDataSource.getAllResults();
        Log.d(LOG_TAG, vocabTopicList.toString());

        VocabularyDataSource vocabularyDataSource = new VocabularyDataSource(mContext);
        vocabularyDataSource.addVocabulary("hamburger", 4);
        vocabularyDataSource.addVocabulary("large", 4);
        vocabularyDataSource.addVocabulary("medium", 4);
        vocabularyDataSource.addVocabulary("drink", 4);
        vocabularyDataSource.addVocabulary("pizza", 4);
        vocabularyDataSource.addVocabulary("bathroom", 4);
        List<Vocabulary> vocabularyList = vocabularyDataSource.getAllResults();
        Log.d(LOG_TAG, vocabularyList.toString());

        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(mContext);
        vocabUsageDataSource.addVocabUsage(1, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(2, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(1, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(2, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(2, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(5, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(3, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(5, 1.0, 1.0);
        vocabUsageDataSource.addVocabUsage(6, 2.0, 2.0);
        vocabUsageDataSource.addVocabUsage(6, 1.0, 1.0);
        List<VocabUsage> vocabUsageList = vocabUsageDataSource.getAllResults();
        Log.d(LOG_TAG, vocabUsageList.toString());
    }

    public void setupBaseVocabulary(){
        VocabTopicDataSource vocabTopicDataSource = new VocabTopicDataSource(mContext);
        VocabularyDataSource vocabularyDataSource = new VocabularyDataSource(mContext);
        List<VocabTopic> vocabTopics = vocabTopicDataSource.getAllResults();
        if(vocabTopics == null || vocabTopics.size() == 0){
            vocabTopicDataSource.addVocabTopic("General");
            vocabTopicDataSource.addVocabTopic("School");
            vocabTopicDataSource.addVocabTopic("Home");
            List<VocabTopic> vocabTopicList = vocabTopicDataSource.getAllResults();
            Log.d(LOG_TAG, vocabTopicList.toString());

            vocabularyDataSource.addVocabulary("Hello", 1);
            vocabularyDataSource.addVocabulary("How are you?", 1);
            vocabularyDataSource.addVocabulary("I'm good", 1);
            vocabularyDataSource.addVocabulary("I'm not doing that well", 1);
            vocabularyDataSource.addVocabulary("Thank you", 1);
            vocabularyDataSource.addVocabulary("Please", 1);
            vocabularyDataSource.addVocabulary("You", 1);
            vocabularyDataSource.addVocabulary("I/Me", 1);
            vocabularyDataSource.addVocabulary("Want", 1);
            vocabularyDataSource.addVocabulary("Today", 1);
            vocabularyDataSource.addVocabulary("Tomorrow", 1);
            vocabularyDataSource.addVocabulary("Yesterday", 1);
            vocabularyDataSource.addVocabulary("Dog", 1);
            vocabularyDataSource.addVocabulary("Cat", 1);
            vocabularyDataSource.addVocabulary("Goodbye", 1);
            vocabularyDataSource.addVocabulary("See ya later", 1);
            vocabularyDataSource.addVocabulary("Teacher", 1);
            vocabularyDataSource.addVocabulary("Friend", 1);
            vocabularyDataSource.addVocabulary("Mom", 1);
            vocabularyDataSource.addVocabulary("Dad", 1);
            vocabularyDataSource.addVocabulary("Brother", 1);
            vocabularyDataSource.addVocabulary("Sister", 1);
            vocabularyDataSource.addVocabulary("Bus Driver", 1);

            vocabularyDataSource.addVocabulary("Pen", 2);
            vocabularyDataSource.addVocabulary("Pencil", 2);
            vocabularyDataSource.addVocabulary("Scissors", 2);
            vocabularyDataSource.addVocabulary("Computer", 2);
            vocabularyDataSource.addVocabulary("iPad", 2);
            vocabularyDataSource.addVocabulary("Desk", 2);
            vocabularyDataSource.addVocabulary("Whiteboard", 2);
            vocabularyDataSource.addVocabulary("Notebook", 2);
            vocabularyDataSource.addVocabulary("Book", 2);
            vocabularyDataSource.addVocabulary("Backpack", 2);
            vocabularyDataSource.addVocabulary("Play", 2);
            vocabularyDataSource.addVocabulary("Help", 2);
            vocabularyDataSource.addVocabulary("Write", 2);
            vocabularyDataSource.addVocabulary("Learn", 2);
            vocabularyDataSource.addVocabulary("Draw", 2);
            vocabularyDataSource.addVocabulary("Erase", 2);
            vocabularyDataSource.addVocabulary("Think", 2);
            vocabularyDataSource.addVocabulary("Listen", 2);
            vocabularyDataSource.addVocabulary("Teacher", 2);
            vocabularyDataSource.addVocabulary("Friend", 2);
            vocabularyDataSource.addVocabulary("Mom", 2);
            vocabularyDataSource.addVocabulary("Dad", 2);
            vocabularyDataSource.addVocabulary("Brother", 2);
            vocabularyDataSource.addVocabulary("Sister", 2);
            vocabularyDataSource.addVocabulary("Bus Driver", 2);

            vocabularyDataSource.addVocabulary("Apple", 3);
            vocabularyDataSource.addVocabulary("Banana", 3);
            vocabularyDataSource.addVocabulary("Sandwich", 3);
            vocabularyDataSource.addVocabulary("Milk", 3);
            vocabularyDataSource.addVocabulary("Juice", 3);
            vocabularyDataSource.addVocabulary("Carrots", 3);
            vocabularyDataSource.addVocabulary("Cereal", 3);
            vocabularyDataSource.addVocabulary("Crackers", 3);
            vocabularyDataSource.addVocabulary("Chips", 3);
            vocabularyDataSource.addVocabulary("Toothbrush", 3);
            vocabularyDataSource.addVocabulary("Comb", 3);
            vocabularyDataSource.addVocabulary("Shirt", 3);
            vocabularyDataSource.addVocabulary("Desk", 3);
            vocabularyDataSource.addVocabulary("Socks", 3);
            vocabularyDataSource.addVocabulary("Pants", 3);
            vocabularyDataSource.addVocabulary("Shoes", 3);
            vocabularyDataSource.addVocabulary("Computer", 3);
            vocabularyDataSource.addVocabulary("Bed", 3);
            vocabularyDataSource.addVocabulary("Couch", 3);
            vocabularyDataSource.addVocabulary("Lights", 3);
            vocabularyDataSource.addVocabulary("Car", 3);
            vocabularyDataSource.addVocabulary("Outside", 3);
            vocabularyDataSource.addVocabulary("iPad", 3);
            vocabularyDataSource.addVocabulary("TV", 3);
            vocabularyDataSource.addVocabulary("Play", 3);
            vocabularyDataSource.addVocabulary("Eat", 3);
            vocabularyDataSource.addVocabulary("Sleep", 3);
            vocabularyDataSource.addVocabulary("Watch", 3);
            vocabularyDataSource.addVocabulary("Teacher", 3);
            vocabularyDataSource.addVocabulary("Friend", 3);
            vocabularyDataSource.addVocabulary("Mom", 3);
            vocabularyDataSource.addVocabulary("Dad", 3);
            vocabularyDataSource.addVocabulary("Brother", 3);
            vocabularyDataSource.addVocabulary("Sister", 3);
            vocabularyDataSource.addVocabulary("Bus Driver", 3);
        }

        List<Vocabulary> vocabularyList = vocabularyDataSource.getAllResults();
        Log.d(LOG_TAG, vocabularyList.toString());
    }
}
