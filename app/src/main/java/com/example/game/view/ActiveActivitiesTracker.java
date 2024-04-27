package com.example.game.view;

import android.content.Context;

import com.example.game.repository.AppDatabase;
import com.example.game.repository.AppRepository;

public class ActiveActivitiesTracker {
    private static int sActiveActivities = 0;
    private static AppRepository appRepository;
    private AppDatabase appDatabase;
    public static void activityStarted(Context baseContext)
    {
//        appRepository = new AppRepository();
//        appRepository.init(baseContext);
        if( sActiveActivities == 0 )
        {

        }
        sActiveActivities++;
    }

    public static void activityStopped()
    {
        sActiveActivities--;
        if( sActiveActivities == 0 )
        {


        }
    }
}
