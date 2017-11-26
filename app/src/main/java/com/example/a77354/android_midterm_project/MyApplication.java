package com.example.a77354.android_midterm_project;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by kunzhai on 2017/11/26.
 */

public class MyApplication extends Application {
    public int count = 0;
    public boolean musicIsOn = false;       //切换到后台时，正在播放着音乐
    MusicService musicService = new MusicService();
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                count--;
                if (count == 0) {
                    Log.v("vergo", "**********切到后台**********");
                    if (musicService.mp != null){
                        if(musicService.mp.isPlaying()) {
                            musicIsOn = true;
                            musicService.mp.pause();
                        }
                        else
                            musicIsOn = false;
                    }
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (count == 0) {
                    Log.v("vergo", "**********切到前台**********");
                    if(musicService.mp != null && musicIsOn)
                        musicService.mp.start();
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
        });
    }
}