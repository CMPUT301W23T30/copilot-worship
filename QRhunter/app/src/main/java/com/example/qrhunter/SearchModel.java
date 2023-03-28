package com.example.qrhunter;

/**
 * Abstraction for interfacing with database document objects for SearchPlayer
 * @author X
 */
public class SearchModel {

    String username;

    SearchModel(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
