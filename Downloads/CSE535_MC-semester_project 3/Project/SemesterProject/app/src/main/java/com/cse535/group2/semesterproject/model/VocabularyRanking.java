package com.cse535.group2.semesterproject.model;

/**
 * Created by jose on 11/21/16.
 */

public class VocabularyRanking implements Comparable{
    private int vocabularyId;
    private int rankingValue;

    public VocabularyRanking(int vocabularyId, int rankingValue){
        this.vocabularyId = vocabularyId;
        this.rankingValue = rankingValue;
    }

    public int getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(int vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public int getRankingValue() {
        return rankingValue;
    }

    public void setRankingValue(int rankingValue) {
        this.rankingValue = rankingValue;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null){
            return -1;
        }
        if(o.getClass() != VocabularyRanking.class){
            return -1;
        }
        VocabularyRanking otherVocabularyRanking = (VocabularyRanking) o;

        if(this.getRankingValue() > otherVocabularyRanking.getRankingValue()){
            return 1;
        }
        else if(this.getRankingValue() < otherVocabularyRanking.getRankingValue()){
            return -1;
        }
        else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return "VocabularyRanking{" +
                "vocabularyId=" + vocabularyId +
                ", rankingValue=" + rankingValue +
                '}';
    }
}
