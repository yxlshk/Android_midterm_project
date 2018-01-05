package com.example.a77354.android_midterm_project;

/**
 * Created by kunzhai on 2017/11/23.
 */

public  class MessageEvent {
    public final HeroInfomation heroInfo;
    public final boolean isChangingHeroInfo;     //true表示当前是添加英雄信息，false表示是修改英雄信息。
    public MessageEvent(HeroInfomation heroInfo, boolean isChangingHeroInfo) {
        this.heroInfo = new HeroInfomation();
        this.heroInfo.setHeroInfomation(heroInfo);
        this.isChangingHeroInfo = isChangingHeroInfo;
    }
}
