package com.cse535.group2.semesterproject.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.cse535.group2.semesterproject.R;
import com.cse535.group2.semesterproject.controller.UserLocation;
import com.cse535.group2.semesterproject.data.VocabUsageDataSource;
import com.cse535.group2.semesterproject.data.VocabularyDataSource;
import com.cse535.group2.semesterproject.model.Vocabulary;
import com.cse535.group2.semesterproject.model.VocabularyRanking;
import com.cse535.group2.semesterproject.predictions.PredictionsUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jose on 11/19/16.
 */

public class VocabularyActivity extends AppCompatActivity implements UserLocation.UserLocationReceiver{
    public static final String BUNDLE_TOPIC_ID = "topicId";
    private static final String LOG_TAG = "VocabularyActivity";
    private static final int COLUMN_COUNT = 2;

    private Menu mMenu;
    private int topicId;

    private UserLocation mUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabulary);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            topicId = extras.getInt(BUNDLE_TOPIC_ID);
        }
        else{
            Log.e(LOG_TAG, "Bundle is null!");
        }


        Log.d(LOG_TAG, "onCreate, topicId: " + topicId);
    }

    @Override
    public void onResume(){
        super.onResume();

        setupVocabularyButtons();
        mUserLocation = UserLocation.getInstance();
        mUserLocation.registerReceiver(this);
    }

    @Override
    public void onStop(){
        super.onStop();

        mUserLocation.unregisterReceiver(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, mMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Log.d(LOG_TAG, "menu_settings selected");
                Intent eventCreateIntent = new Intent(this, SettingsActivity.class);
                startActivity(eventCreateIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Map<Integer, Vocabulary> createVocabularyMap(List<Vocabulary> vocabularies){
        Map<Integer, Vocabulary> vocabularyMap = new HashMap<>();

        for(Vocabulary vocabulary : vocabularies){
            vocabularyMap.put(vocabulary.getId(), vocabulary);
        }

        return vocabularyMap;
    }

    private void setupVocabularyButtons(){
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout_vocabulary);
        gridLayout.removeAllViews();
        VocabularyDataSource vocabularyDataSource = new VocabularyDataSource(this);
        final List<Vocabulary> vocabularies = vocabularyDataSource.getAllResultsByTopicId(topicId);
        Log.d(LOG_TAG, vocabularies.toString());

        Map<Integer, Vocabulary> vocabularyMap = createVocabularyMap(vocabularies);

        gridLayout.setRowCount((int) Math.ceil(vocabularies.size() / 2.0));
        gridLayout.setColumnCount(COLUMN_COUNT);

        PredictionsUtility predictionsUtility = new PredictionsUtility(this);

        List<VocabularyRanking> vocabularyRankings = predictionsUtility.getAllPredictionRanks(topicId);

        if(vocabularyRankings.size() != vocabularies.size()){
            Log.e(LOG_TAG, "vocabularyRankings.size() != vocabularies.size()");
        }

        for(int i = 0; i < vocabularyRankings.size(); i++){
            final Vocabulary vocabulary = vocabularyMap.get(vocabularyRankings.get(i).getVocabularyId());
            Button topicButton = new Button(gridLayout.getContext());

            topicButton.setText(vocabulary.getVocabularyName());
            topicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processVocabularySelect(vocabulary.getId());
                }
            });

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(
                    GridLayout.spec((int) Math.floor(i / 2), GridLayout.CENTER),
                    GridLayout.spec(i % COLUMN_COUNT, GridLayout.CENTER)
            );

            gridLayout.addView(topicButton, layoutParams);
        }
    }

    private void processVocabularySelect(int vocabularyId){
        Log.d(LOG_TAG, "Vocabulary selected: " + vocabularyId);

        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(this);
        Location userLocation = mUserLocation.getLocation();
        Log.d(LOG_TAG, "lat: " + userLocation.getLatitude() + " lon: " + userLocation.getLongitude());
        vocabUsageDataSource.addVocabUsage(vocabularyId, userLocation.getLatitude(), userLocation.getLongitude());

        setupVocabularyButtons();
    }

    @Override
    public void receiveBroadcast() {
        setupVocabularyButtons();
    }
}
