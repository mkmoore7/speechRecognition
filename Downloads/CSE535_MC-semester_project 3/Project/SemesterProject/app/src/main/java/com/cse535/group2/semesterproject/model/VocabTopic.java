package com.cse535.group2.semesterproject.model;

/**
 * Created by Jose on 11/11/2016.
 */

public class VocabTopic {
    private int id;
    private String topicName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return "VocabTopic{" +
                "id=" + id +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}
