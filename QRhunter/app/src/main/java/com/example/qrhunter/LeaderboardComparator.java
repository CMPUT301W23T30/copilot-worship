package com.example.qrhunter;

import java.util.Comparator;

public class LeaderboardComparator implements Comparator<LeaderboardModel>
{
    @Override
    public int compare(LeaderboardModel leaderboardModel, LeaderboardModel t1) {
        return t1.getTotalScore().compareTo(leaderboardModel.getTotalScore());
    }
}
