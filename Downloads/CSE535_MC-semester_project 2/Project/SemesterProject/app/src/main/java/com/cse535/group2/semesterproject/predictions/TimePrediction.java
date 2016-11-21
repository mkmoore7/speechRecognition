package com.cse535.group2.semesterproject.predictions;

import android.content.Context;

import com.cse535.group2.semesterproject.data.VocabUsageDataSource;
import com.cse535.group2.semesterproject.model.VocabUsage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jose on 11/13/16.
 */

public class TimePrediction {
    private Context mContext;

    public TimePrediction(Context context){
        mContext = context;
    }

    public Map<Integer, Integer> getSimilarTimePrediction(int topicId){
        Map<Integer, Integer> vocabUsedCountMap = new HashMap<>();
        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(mContext);
        List<VocabUsage> results = vocabUsageDataSource.getResultsBySimilarTimeOfDay(topicId, new Date());

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

    public Map<Integer, Integer> getSimilarTimePrediction(String topicName){
        Map<Integer, Integer> vocabUsedCountMap = new HashMap<>();
        VocabUsageDataSource vocabUsageDataSource = new VocabUsageDataSource(mContext);

        List<VocabUsage> results = vocabUsageDataSource.getResultsBySimilarTimeOfDay(topicName, new Date());

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
