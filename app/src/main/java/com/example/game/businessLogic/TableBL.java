package com.example.game.businessLogic;

import com.example.game.model.ClubStats;
import com.example.game.model.Game;
import com.example.game.model.League;
import com.example.game.repository.AppDatabase;

import java.util.ArrayList;

public class TableBL {

    private AppDatabase appDatabase;

    private ArrayList<Game> gamesArrayList ;
    private ArrayList<String> distinctHome ;
    private ArrayList<String> distinctAway ;
    private ArrayList<String> distinctClub ;

    public TableBL(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public void SeedTableDB( int leagueId, int season){
        appDatabase.seedClubStats( leagueId, season);
    }
}
