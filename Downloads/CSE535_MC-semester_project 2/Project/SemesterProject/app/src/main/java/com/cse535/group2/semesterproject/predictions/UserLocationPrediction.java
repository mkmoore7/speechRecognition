package com.cse535.group2.semesterproject.predictions;

import android.content.Context;

import com.cse535.group2.semesterproject.controller.UserLocation;
import com.cse535.group2.semesterproject.data.VocabUsageDataSource;
import com.cse535.group2.semesterproject.model.VocabUsage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jose on 11/13/16.
 */

public class UserLocationPrediction {
    private Context mContext;

    public UserLocationPrediction(Context context){
        mContext = context;
    }

    //return: Map<Integer, Integer> -> vocabularyUsedId, Count
    public Map<Integer, Integer> getLocationPredictionCountsByTopicId(int topicId, double latitude, double longitude){
        Map<Integer, Integer> vocabUsedCountMap = new HashMap<>();
        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(mContext);
        List<VocabUsage> results = vocabUsageDataSource.getResultsByTopicIdAndLocation(topicId, latitude, longitude);

        for(VocabUsage vocabUsage : results){
            Integer currentCount = vocabUsedCountMap.get(vocabUsage.getVocabularyUsedId());
            if(currentCount == null){
                currentCount = 1;
            }
            else{
                currentCount += 1;
            }
            vocabUsedCountMap.put(vocabUsage.getVocabularyUsedId(), currentCount);
        }

        return vocabUsedCountMap;
    }

    //return: Map<String, Integer> -> vocabularyUsedId, Count
    public Map<Integer, Integer> getLocationPredictionCountsByTopicName(String topicName, double latitude, double longitude){
        Map<Integer, Integer> vocabUsedCountMap = new HashMap<>();
        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(mContext);
        List<VocabUsage> results = vocabUsageDataSource.getResultsByTopicNameAndLocation(topicName, latitude, longitude);

        for(VocabUsage vocabUsage : results){
            Integer currentCount = vocabUsedCountMap.get(vocabUsage.getVocabularyUsedId());
            if(currentCount == null){
                currentCount = 1;
            }
            else{
                currentCount += 1;
            }
            vocabUsedCountMap.put(vocabUsage.getVocabularyUsedId(), currentCount);
        }

        return vocabUsedCountMap;
    }
}
