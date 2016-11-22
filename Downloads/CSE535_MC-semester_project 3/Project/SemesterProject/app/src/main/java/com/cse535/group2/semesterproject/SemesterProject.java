package com.cse535.group2.semesterproject;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jose on 10/26/2016.
 */

public class SemesterProject extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        SemesterProject.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SemesterProject.context;
    }
}
