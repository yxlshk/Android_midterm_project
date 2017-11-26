package com.example.a77354.android_midterm_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class editActivity extends AppCompatActivity {
    private boolean isChangingHeroInfo = false; //true表示是修改模式，false表示是新增英雄信息模式。
    private int imageIdOfHero = R.drawable.unknown;               //记录传来的英雄头像id。如果是新增英雄信息模式，则为unknown
    private String nickName = "", detailInfo="";           //记录传来英雄的字和详细信息。
    private CommonAdapter<ImageInfomation> imageAdapter;
    private List<ImageInfomation> imageInfomationList = new ArrayList<ImageInfomation>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_details_layout);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getSerializable("heroInfo") != null)
            initLayout(bundle);
        setClickEvent();
        initImageIdList();
    }
    private void initLayout(Bundle bundle) {
        isChangingHeroInfo = true;  //设置为修改模式
        HeroInfomation heroInfo = (HeroInfomation) bundle.getSerializable("heroInfo");
        imageIdOfHero = heroInfo.imageId;   //保存英雄头像id
        nickName = heroInfo.nickName;        //保存英雄的字
        detailInfo = heroInfo.detailInfo;   //保存英雄详细介绍
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
        TextView changeAvatarButton = (TextView) findViewById(R.id.changeAvatar);
        final ImageView avatar = (ImageView) findViewById(R.id.avatar);
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
                int imageId = imageIdOfHero;
                String name = ((EditText)findViewById(R.id.realName)).getText().toString();
                String bornDiedDate = ((EditText)findViewById(R.id.realLife)).getText().toString();
                String hometown = ((EditText)findViewById(R.id.realPlace)).getText().toString();
                RadioButton checkedSexButton = (RadioButton) findViewById( ((RadioGroup)findViewById(R.id.realGender)).getCheckedRadioButtonId() );
                RadioButton checkedLoyalToButton = (RadioButton) findViewById( ((RadioGroup)findViewById(R.id.realLeague)).getCheckedRadioButtonId() );
                String sex = checkedSexButton.getText().toString();
                String loyalTo = checkedLoyalToButton.getText().toString();

                HeroInfomation heroInfo = new HeroInfomation(imageId, name, nickName, sex, bornDiedDate, loyalTo, hometown, detailInfo);
                MessageEvent messageEvent = new MessageEvent(heroInfo, isChangingHeroInfo);
                EventBus.getDefault().post(messageEvent);
                finish();
            }
        });

        changeAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                final int tempImageId = imageIdOfHero;      //保存原本的头像ID，如果点击取消，可以恢复到原本的头像。
                //设置提示框内的布局，为一个recycleList。
                LayoutInflater inflater = LayoutInflater.from(editActivity.this);
                View layout = inflater.inflate(R.layout.alert_image_list, null);
                RecyclerView image_list = (RecyclerView) layout.findViewById(R.id.imageList);
                final ImageView currentAvatar = (ImageView) layout.findViewById(R.id.currentImage);      //选择头像列表上，表示当前选择的图片
                currentAvatar.setImageResource(imageIdOfHero);
                LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                image_list.setLayoutManager(manager);

                imageAdapter = new CommonAdapter<ImageInfomation>(getApplicationContext(), R.layout.alert_image_item, imageInfomationList) {
                    @Override
                    public void convert(myViewHolder holder, ImageInfomation s) {
                        ImageView image = holder.getView(R.id.imageItemForRecycle);
                        image.setImageResource(s.imageId);
                    }
                };
                // 点击英雄头像
                imageAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        imageIdOfHero = imageInfomationList.get(position).imageId;
                        currentAvatar.setImageResource(imageIdOfHero);
                    }
                });
                image_list.setAdapter(imageAdapter);
                imageAdapter.notifyDataSetChanged();

                //弹出提示框给用户选择
                final AlertDialog.Builder alerDialog = new AlertDialog.Builder(editActivity.this);
                alerDialog.setTitle("选择头像").setView(layout)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                imageIdOfHero = tempImageId;
                                //Toast.makeText(getApplicationContext(), "您选择了[取消]", Toast.LENGTH_SHORT).show();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                avatar.setImageResource(imageIdOfHero);
                                //Toast.makeText(getApplicationContext(), "您选择了[确定]", Toast.LENGTH_SHORT).show();
                            }
                        }).create();
                alerDialog.show();
            }
        });
    }
    public void initImageIdList() {
        imageInfomationList.add(new ImageInfomation(R.drawable.alt1));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt2));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt3));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt4));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt5));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt6));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt7));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt8));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt9));
        imageInfomationList.add(new ImageInfomation(R.drawable.alt10));
    }
}
