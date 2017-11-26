package com.example.a77354.android_midterm_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class detailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getSerializable("heroInfo") != null)
            initLayout(bundle);
        setClickEvent();
    }
    private void initLayout(Bundle bundle) {
        HeroInfomation heroInfo = (HeroInfomation) bundle.getSerializable("heroInfo");
        TextView heroIntroduction = (TextView)findViewById(R.id.introd_detail);
        ImageView imageView = (ImageView)findViewById(R.id.avatar);
        TextView nameView = (TextView)findViewById(R.id.realName);
        TextView sexView = (TextView)findViewById(R.id.realGender);
        TextView bornDiedDateView = (TextView)findViewById(R.id.realLife);
        TextView homwTownView = (TextView)findViewById(R.id.realPlace);
        TextView loyalToView = (TextView)findViewById(R.id.realLeague);

        heroIntroduction.setText(heroInfo.detailInfo);
        imageView.setImageResource(heroInfo.imageId);
        nameView.setText(heroInfo.name);
        sexView.setText(heroInfo.sex);
        bornDiedDateView.setText(heroInfo.bornDiedDate);
        homwTownView.setText(heroInfo.homeTown);
        loyalToView.setText(heroInfo.loyalTo);
    }

    private void setClickEvent() {
        Button editButton = (Button) findViewById(R.id.editButton);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(detailActivity.this, editActivity.class);
                Bundle bundle = getIntent().getExtras();
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
