package com.example.a77354.android_midterm_project;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    CommonAdapter<HeroInfomation> hero;
    private List<HeroInfomation> main_list_data = new ArrayList<HeroInfomation>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    public void init() {
        // 初始化十个英雄
        main_list_data.add(new HeroInfomation(R.drawable.liubei,   "刘备", "玄德", "男", "161-223", "蜀", "幽州涿郡", "刘备，蜀汉的开国皇帝，汉景帝之子中山靖王刘胜的后代。"));
        main_list_data.add(new HeroInfomation(R.drawable.zhangfei, "张飞", "益德", "男", "？-221", "蜀", "幽州涿郡", "张飞穿针大眼瞪小眼"));
        main_list_data.add(new HeroInfomation(R.drawable.guanyu,   "关羽", "云长", "男", "？-219", "蜀", "司隶河东郡解", "尔等敢应战否"));
        main_list_data.add(new HeroInfomation(R.drawable.caocao,   "曹操", "孟德", "男", "155-220", "魏", "豫州沛国谯", "宁教我负天下人休教天下人负我"));
        main_list_data.add(new HeroInfomation(R.drawable.sunquan,  "孙权", "仲谋", "男", "182-252", "吴", "扬州吴郡富春", "孙坚之子，孙策之弟。东汉建安五年，兄孙策病死，孙权继位吴侯、讨逆将军，领会稽太守，开始统领江东。"));
        main_list_data.add(new HeroInfomation(R.drawable.sunce,    "孙策", "伯符", "男", "175-200", "吴", "扬州吴郡富春", "江东小霸王"));
        main_list_data.add(new HeroInfomation(R.drawable.sunshangxiang, "孙尚香", "", "女", "？-？", "蜀", "扬州吴郡富春", "刘备的老婆"));
        main_list_data.add(new HeroInfomation(R.drawable.caozhi,   "曹植", "子建", "男", "192-232", "魏", "豫州沛国谯", "七步诗"));
        main_list_data.add(new HeroInfomation(R.drawable.caopi,    "曹丕", "子桓", "男", "187-226", "魏", "豫州沛国谯", "短命"));
        main_list_data.add(new HeroInfomation(R.drawable.zhaoyun,  "赵云", "子龙", "男", "？-229", "蜀", "冀州常山国真定", "常山赵子龙"));
        // main_list_data表示在首页显示的英雄数据

        // 给首页的RecycleView赋值
        RecyclerView main_list = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        main_list.setLayoutManager(manager);
        hero = new CommonAdapter<HeroInfomation>(this, R.layout.main_list_item, main_list_data) {
            @Override
            public void convert(myViewHolder holder, HeroInfomation s) {
                ImageView people_head = holder.getView(R.id.people_head);
                people_head.setImageResource(s.imageId);
                TextView name = holder.getView(R.id.name);
                name.setText(s.name + "  字：" + s.nickName);
                TextView detail_one = holder.getView(R.id.detail_one);
                detail_one.setText(s.sex + " 生卒(" + s.bornDiedDate + ")  籍贯：" + s.homeTown);
                TextView detail_two = holder.getView(R.id.detail_two);
                detail_two.setText(s.detailInfo);
            }
        };
        main_list.setAdapter(hero);
        setClickEvent();
    }

    private void setClickEvent() {
        // 首页点击英雄事件
        hero.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, detailActivity.class);
                HeroInfomation heroInfo = main_list_data.get(position);
                intent.putExtra("heroInfo", heroInfo);
                startActivity(intent);
                // 点击跳转代码在这里写
            }
        });

        // 首页长按英雄删除的事件
        hero.setOnItemLongClickListener(new CommonAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                main_list_data.remove(position);
                hero.notifyDataSetChanged();
                int num = position + 1;
                Toast.makeText(getApplicationContext(), "删除第"+num+"个英雄", Toast.LENGTH_SHORT).show();
            }
        });

        //点击+号进入编辑页面。
        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.flo);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, editActivity.class);
                startActivity(intent);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.isChangingHeroInfo) {  //是修改模式的话，要修改原英雄信息
            int index = getIndexOfHero(event.heroInfo);
            main_list_data.set(index, event.heroInfo);
        } else {
            main_list_data.add(event.heroInfo);
        }
        hero.notifyDataSetChanged();
    }

    private int getIndexOfHero(HeroInfomation heroInfo) {
        for(int i = 0; i < main_list_data.size(); i++) {
            if(heroInfo.name.equals(main_list_data.get(i).name))
                return i;
        }
        return -1;
    }
}
