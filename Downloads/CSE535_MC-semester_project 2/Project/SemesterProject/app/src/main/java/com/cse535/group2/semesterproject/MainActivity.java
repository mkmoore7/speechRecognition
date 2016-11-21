package com.cse535.group2.semesterproject;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.cse535.group2.semesterproject.activity.SettingsActivity;
import com.cse535.group2.semesterproject.activity.VocabularyActivity;
import com.cse535.group2.semesterproject.controller.UserLocation;
import com.cse535.group2.semesterproject.data.DatabaseUtility;
import com.cse535.group2.semesterproject.data.VocabTopicDataSource;
import com.cse535.group2.semesterproject.model.VocabTopic;
import com.cse535.group2.semesterproject.predictions.PredictionsUtility;
import com.cse535.group2.semesterproject.service.SpeakerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";

    private UserLocation mUserLocation;
    private Menu mMenu;

    private static final int COLUMN_COUNT = 2;

    private SpeakerService mService;
    private boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "on create");


        DatabaseUtility databaseUtility = new DatabaseUtility(this);
//        databaseUtility.testDatabase();
//        databaseUtility.deleteDatabase();
//        databaseUtility.setupLocationPredictionData();
        databaseUtility.setupBaseVocabulary();
//        databaseUtility.testLocationPredictionQueries();

        PredictionsUtility predictionsUtility = new PredictionsUtility(this);
        predictionsUtility.testLocationPrediction();
        predictionsUtility.testTimePrediction();
        predictionsUtility.getAllPredictionRanks(4);
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mService!=null) {
            mService.resumeRecording();
        }
        Intent intent = new Intent(this, SpeakerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume(){
        super.onResume();

        mUserLocation = UserLocation.getInstance();
        mUserLocation.requestLocationUpdates(this);

        setupVocabTopicButtons();
    }

    @Override
    public void onStop(){
        super.onStop();
        mService.stopRecording();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    //https://developer.android.com/training/appbar/setting-up.html#utility
    //http://www.vogella.com/tutorials/AndroidActionBar/article.html
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
            case R.id.menu_identify_speaker:
                Log.d(LOG_TAG, "menu_identify_speaker selected");
                Toast.makeText(MainActivity.this, mService.getSpeaker(), Toast.LENGTH_SHORT).show();
                promptSpeechInput();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UserLocation.PERMISSION_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mUserLocation.requestLocationUpdates(this);
                    }
                } else {
                    //Do something if the user cancelled the permissions request
                }
                return;
            }

        }
    }

    private void setupVocabTopicButtons(){
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout_main);
        VocabTopicDataSource vocabTopicDataSource = new VocabTopicDataSource(this);
        final List<VocabTopic> vocabTopics =  vocabTopicDataSource.getAllResults();

        gridLayout.setRowCount((int) Math.ceil(vocabTopics.size() / 2.0));
        gridLayout.setColumnCount(COLUMN_COUNT);

        for(int i = 0; i < vocabTopics.size(); i++){
            Button topicButton = new Button(gridLayout.getContext());
            topicButton.setText(vocabTopics.get(i).getTopicName());
            final int j = i;
            topicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToVocabulayPage(vocabTopics.get(j).getId());
                }
            });
                    gridLayout.addView(topicButton, new GridLayout.LayoutParams(
                    GridLayout.spec((int) Math.floor(i / 2), GridLayout.LEFT),
                    GridLayout.spec(i % COLUMN_COUNT, GridLayout.LEFT)
            ));
        }
    }

    private void navigateToVocabulayPage(int topicId){
        Log.d(LOG_TAG, "vocabulary topic selected: " + topicId);
        Intent vocabularyIntent = new Intent(this, VocabularyActivity.class);
        vocabularyIntent.putExtra(VocabularyActivity.BUNDLE_TOPIC_ID, topicId);
        startActivity(vocabularyIntent);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SpeakerService.LocalBinder binder = (SpeakerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void promptSpeechInput(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!");

        try{
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException e){
            Toast.makeText(MainActivity.this, "Sorry, your device does not support Speech Recognition", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent i){
        super.onActivityResult(requestCode, resultCode, i );
        switch(requestCode){
            case 100: {
                if(resultCode ==RESULT_OK && i!= null) {
                    ArrayList<String> results = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(MainActivity.this, results.toString(), Toast.LENGTH_SHORT).show();

                }
                break;
            }
        }
    }
}
