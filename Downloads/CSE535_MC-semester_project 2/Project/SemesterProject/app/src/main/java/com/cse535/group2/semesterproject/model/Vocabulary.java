package com.cse535.group2.semesterproject.model;

/**
 * Created by Jose on 11/11/2016.
 */

public class Vocabulary {
    private int id;
    private String vocabularyName;
    private int vocabularyTopicId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVocabularyName() {
        return vocabularyName;
    }

    public void setVocabularyName(String vocabularyName) {
        this.vocabularyName = vocabularyName;
    }

    public int getVocabularyTopicId() {
        return vocabularyTopicId;
    }

    public void setVocabularyTopicId(int vocabularyTopicId) {
        this.vocabularyTopicId = vocabularyTopicId;
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
                "id=" + id +
                ", vocabularyName='" + vocabularyName + '\'' +
                ", vocabularyTopicId=" + vocabularyTopicId +
                '}';
    }
}
