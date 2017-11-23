package com.example.a77354.android_midterm_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

public class editActivity extends AppCompatActivity {
    boolean isChangingHeroInfo = false; //true表示是修改模式，false表示是新增英雄信息模式。
    int imageIdOfHero = -1;               //记录传来的英雄头像id。如果是新增英雄信息模式，则为-1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_details_layout);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getSerializable("heroInfo") != null)
            initLayout(bundle);
        setClickEvent();
    }
    private void initLayout(Bundle bundle) {
        isChangingHeroInfo = true;  //设置为修改模式
        HeroInfomation heroInfo = (HeroInfomation) bundle.getSerializable("heroInfo");
        imageIdOfHero = heroInfo.imageId;   //保存英雄头像id
        ImageView imageView = (ImageView)findViewById(R.id.avatar);
        EditText nameView = (EditText)findViewById(R.id.realName);
        EditText bornDiedDateView = (EditText)findViewById(R.id.realLife);
        EditText homwTownView = (EditText)findViewById(R.id.realPlace);
        RadioGroup sexGroupView = (RadioGroup)findViewById(R.id.realGender);
        RadioGroup loyalToGroupView = (RadioGroup)findViewById(R.id.realLeague);

        imageView.setImageResource(heroInfo.imageId);
        nameView.setText(heroInfo.name);
        bornDiedDateView.setText(heroInfo.bornDiedDate);
        homwTownView.setText(heroInfo.homeTown);
        nameView.setEnabled(false);                     //修改模式下不可修改姓名
        switch (heroInfo.sex) {
            case "男":
                sexGroupView.check(R.id.r_button1);
                break;
            case "女":
                sexGroupView.check(R.id.r_button2);
                break;
            default:
                break;
        }
        switch (heroInfo.loyalTo) {
            case "魏":
                loyalToGroupView.check(R.id.realLeague_button1);
                break;
            case "蜀":
                loyalToGroupView.check(R.id.realLeague_button2);
                break;
            case "吴":
                loyalToGroupView.check(R.id.realLeague_button3);
                break;
            case "群雄":
                loyalToGroupView.check(R.id.realLeague_button4);
                break;
            default:
                break;
        }
    }

    private void setClickEvent() {
        Button cancelButton = (Button) findViewById(R.id.cancelBtn);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        Button okButton = (Button) findViewById(R.id.okBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageId = imageIdOfHero == -1? R.drawable.unknown: imageIdOfHero;
                String name = ((EditText)findViewById(R.id.realName)).getText().toString();
                String bornDiedDate = ((EditText)findViewById(R.id.realLife)).getText().toString();
                String hometown = ((EditText)findViewById(R.id.realPlace)).getText().toString();
                RadioButton checkedSexButton = (RadioButton) findViewById( ((RadioGroup)findViewById(R.id.realGender)).getCheckedRadioButtonId() );
                RadioButton checkedLoyalToButton = (RadioButton) findViewById( ((RadioGroup)findViewById(R.id.realLeague)).getCheckedRadioButtonId() );
                String sex = checkedSexButton.getText().toString();
                String loyalTo = checkedLoyalToButton.getText().toString();

                HeroInfomation heroInfo = new HeroInfomation(imageId, name, "", sex, bornDiedDate, loyalTo, hometown, "");
                MessageEvent messageEvent = new MessageEvent(heroInfo, isChangingHeroInfo);
                EventBus.getDefault().post(messageEvent);
                finish();
            }
        });
    }
}
