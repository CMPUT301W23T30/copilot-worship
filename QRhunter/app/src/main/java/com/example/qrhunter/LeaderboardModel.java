package com.example.qrhunter;

public class LeaderboardModel {

    private String username;

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    private Integer totalScore;

    LeaderboardModel(String username, Integer totalScore){
        this.username = username;
        this.totalScore = totalScore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
