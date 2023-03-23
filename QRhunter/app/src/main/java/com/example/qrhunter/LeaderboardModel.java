package com.example.qrhunter;

public class LeaderboardModel {

    String username;
    Integer ranking;

    LeaderboardModel(String username, Integer ranking){
        this.username = username;
        this.ranking = ranking;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
}
