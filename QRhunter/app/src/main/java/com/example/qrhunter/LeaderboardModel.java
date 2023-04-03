package com.example.qrhunter;

/**
 * Abstraction class used for comparing between in LeaderBoardCompare
 * and used in local userlists inside LeaderBoardActivity
 * @author X
 */
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
