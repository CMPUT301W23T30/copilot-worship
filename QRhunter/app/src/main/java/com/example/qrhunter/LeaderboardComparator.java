package com.example.qrhunter;

import java.util.Comparator;

/**
 * Custom comparator for Leaderboard models
 */
public class LeaderboardComparator implements Comparator<LeaderboardModel>
{
    /**
     * Compares users by scores
     * @param leaderboardModel
     * @param t1
     * @return 0 if equal, < 0 if t1 < leaderboard mdoel, gt 0 if t1 > leaderboard model
     * */
    @Override
    public int compare(LeaderboardModel leaderboardModel, LeaderboardModel t1) {
        return t1.getTotalScore().compareTo(leaderboardModel.getTotalScore());
    }
}
