package com.example.a77354.android_midterm_project;

import android.app.Application;
import android.content.res.Resources;

/**
 * Created by kunzhai on 2017/11/25.
 */

public class MyApplication extends Application {
    protected static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Resources getMyResources() {
        return instance.getResources();
    }
}