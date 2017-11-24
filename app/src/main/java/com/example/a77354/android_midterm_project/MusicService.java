package com.example.a77354.android_midterm_project;

import android.app.Service;

/**
 * Created by ZhangZM on 2017/11/24.
 */
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public class MusicService extends Service {
    public  MediaPlayer mp = new MediaPlayer();
    private IBinder iBinder = new MyBinder();


    public MusicService(){
        try {
            mp=MediaPlayer.create(this, R.raw.sanguo_01);
            mp.setLooping(true);
            mp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void play(){
        if (mp != null)
            if (mp.isPlaying())
                mp.stop();
            else
                mp.start();
    }


    public class MyBinder extends Binder{
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flag) throws RemoteException{
            switch (code) {
                case R.id.music:
                    //播放
                    play();
                    break;
            }
            return super.onTransact(code, data, reply, flag);
        }

        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return iBinder;
    }

    public void onDestroy() {
        super.onDestroy();
        mp.stop();
    }

}
