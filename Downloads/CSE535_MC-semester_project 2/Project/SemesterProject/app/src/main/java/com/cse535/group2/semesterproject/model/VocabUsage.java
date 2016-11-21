package com.cse535.group2.semesterproject.model;

import java.util.Date;

/**
 * Created by Jose on 11/11/2016.
 */

public class VocabUsage {
    private int id;
    private int vocabularyUsedId;
    private double latitude;
    private double longitude;
    private Date timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVocabularyUsedId() {
        return vocabularyUsedId;
    }

    public void setVocabularyUsedId(int vocabularyUsedId) {
        this.vocabularyUsedId = vocabularyUsedId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "VocabUsage{" +
                "id=" + id +
                ", vocabularyUsedId=" + vocabularyUsedId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}
