package com.example.a77354.android_midterm_project;

import java.io.Serializable;

/**
 * Created by kunzhai on 2017/11/23.
 */

public class HeroInfomation implements Serializable {
    public int imageId;
    public String name, nickName, sex, bornDiedDate, homeTown, loyalTo, detailInfo;
    //依次为名  字   性别  生卒日期    籍贯  主效势力    人物详细信息
    public HeroInfomation() {}
    public HeroInfomation(int id, String name, String nickName, String sex, String bornDiedDate, String loyalTo,String homeTown, String detailInfo) {
        this.imageId = id;
        this.name = name;
        this.nickName = nickName;
        this.sex = sex;
        this.bornDiedDate = bornDiedDate;
        this.homeTown = homeTown;
        this.loyalTo = loyalTo;
        this.detailInfo = detailInfo;
    }
    public void setHeroInfomation(HeroInfomation heroInfo) {
        this.imageId = heroInfo.imageId;
        this.name = heroInfo.name;
        this.nickName = heroInfo.nickName;
        this.sex = heroInfo.sex;
        this.bornDiedDate = heroInfo.bornDiedDate;
        this.homeTown = heroInfo.homeTown;
        this.loyalTo = heroInfo.loyalTo;
        this.detailInfo = heroInfo.detailInfo;
    }
}
