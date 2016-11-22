package com.cse535.group2.semesterproject.predictions;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.cse535.group2.semesterproject.controller.UserLocation;
import com.cse535.group2.semesterproject.data.VocabularyDataSource;
import com.cse535.group2.semesterproject.model.Vocabulary;
import com.cse535.group2.semesterproject.model.VocabularyRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jose on 11/13/16.
 */

public class PredictionsUtility {
    private static final String LOG_TAG = "PredictionsUtility";

    private Context mContext;

    public PredictionsUtility(Context context){
        mContext = context;
    }

    public void testLocationPrediction(){
        UserLocationPrediction userLocationPrediction = new UserLocationPrediction(mContext);

        Map<Integer, Integer> locationPredictionCounts =  userLocationPrediction.getLocationPredictionCountsByTopicId(4, 1.0, 1.0);
        Log.d(LOG_TAG, "location: " + locationPredictionCounts.toString());
        locationPredictionCounts =  userLocationPrediction.getLocationPredictionCountsByTopicName("restaurant", 1.0, 1.0);
        Log.d(LOG_TAG, "location: " + locationPredictionCounts.toString());
    }

    public void testTimePrediction(){
        TimePrediction timePrediction = new TimePrediction(mContext);
        Map<Integer, Integer> timePredictionCounts =  timePrediction.getSimilarTimePrediction(4);
        Log.d(LOG_TAG, "time: " + timePredictionCounts.toString());
        timePredictionCounts =  timePrediction.getSimilarTimePrediction("restaurant");
        Log.d(LOG_TAG, "time: " + timePredictionCounts.toString());
    }

    /**
     * Get the rankings for all predictions
     */
    public List<VocabularyRanking> getAllPredictionRanks(int topicId){
        Map<Integer, Integer> combinedPredictionCounts = combinePredictionCounts(topicId);

        VocabularyDataSource vocabularyDataSource = new VocabularyDataSource(mContext);
        final List<Vocabulary> vocabularies = vocabularyDataSource.getAllResultsByTopicId(topicId);
        List<VocabularyRanking> vocabularyRankings = new ArrayList<>();

        for(Vocabulary vocabulary : vocabularies){
            int id = vocabulary.getId();
            if(combinedPredictionCounts.get(id) != null){
                vocabularyRankings.add(new VocabularyRanking(id, combinedPredictionCounts.get(id)));
            }
            else{
                vocabularyRankings.add(new VocabularyRanking(id, 0));
            }
        }

        Collections.sort(vocabularyRankings);
        Collections.reverse(vocabularyRankings);

        Log.d(LOG_TAG, "vocabulary rankings: " + vocabularyRankings);

        return vocabularyRankings;
    }

    public Map<Integer, Integer> combinePredictionCounts(int topicId){
        Log.d(LOG_TAG, "combinePredictionCounts");
        UserLocation userLocation = UserLocation.getInstance();
        Location location = userLocation.getLocation();

        UserLocationPrediction userLocationPrediction = new UserLocationPrediction(mContext);
        TimePrediction timePrediction = new TimePrediction(mContext);
        Map<Integer, Integer> locationPredictionCounts =  userLocationPrediction.getLocationPredictionCountsByTopicId(topicId,
                //for testing with test data
//                1.0, 1.0);
                location.getLongitude(), location.getLatitude());
        Map<Integer, Integer> timePredictionCounts =  timePrediction.getSimilarTimePrediction(topicId);

        Map<Integer, Integer> combinedPredictionCounts = new HashMap<>();

        for (Integer key : locationPredictionCounts.keySet()) {
            if(timePredictionCounts.get(key) != null){
                combinedPredictionCounts.put(key, locationPredictionCounts.get(key) + timePredictionCounts.get(key));
            }
            else{
                combinedPredictionCounts.put(key, locationPredictionCounts.get(key));

            }
        }

        for (Integer key : timePredictionCounts.keySet()) {
            if(combinedPredictionCounts.get(key) == null){
                combinedPredictionCounts.put(key, timePredictionCounts.get(key));
            }
        }

        Log.d(LOG_TAG, "combined prediction counts: " + combinedPredictionCounts.toString());

        return combinedPredictionCounts;
    }
}
