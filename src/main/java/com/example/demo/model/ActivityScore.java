package com.example.demo.model;

public class ActivityScore {
    private String activityName;
    private int score;

    public ActivityScore(String activityName, int score) {
        this.activityName = activityName;
        this.score = score;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getScore() {
        return score;
    }
}
