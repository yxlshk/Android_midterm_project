package com.example.a77354.android_midterm_project;

import android.widget.ImageView;

/**
 * Created by kunzhai on 2017/11/25.
 */

//因为CommonAdapter模板不接受int类型（int没有equal()方法？），所以要封装一下。
public class ImageInfomation {
    public int imageId;
    public ImageInfomation(int imageId) {
        this.imageId = imageId;
    }
}
