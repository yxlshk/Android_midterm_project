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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    CommonAdapter<Map<String, Object>> hero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        // 初始化十个英雄
        final List<Map<String, Object>> main_list_data = new ArrayList<>();   // main_list_data表示在首页显示的英雄数据
        int[] people_head_img = new int[] {R.drawable.liubei, R.drawable.zhangfei, R.drawable.guanyu, R.drawable.caocao, R.drawable.sunquan, R.drawable.sunce, R.drawable.sunshangxiang, R.drawable.caozhi, R.drawable.caopi, R.drawable.zhaoyun};
        String[] people_name = new String[] {"刘备  字：玄德", "张飞  字：益德", "关羽  字：云长", "曹操  字：孟德", "孙权  字：仲谋", "孙策  字：伯符", "孙尚香", "曹植  字：子建", "曹丕  字：子桓", "赵云  字：子龙"};
        String[] people_detail_one = new String[] {"男 生卒(161-223)  籍贯：幽州涿郡涿", "男 生卒(？-221) 籍贯：幽州涿郡", "男 生卒(？-219)  籍贯：司隶河东郡解", "男 生卒(155-220)  籍贯：豫州沛国谯", "男 生卒(182-252)  籍贯：扬州吴郡富春", "男 生卒(175-200)  籍贯：扬州吴郡富春", "女 生卒(？-？)  籍贯：扬州吴郡富春", "男 生卒(192-232)  籍贯：豫州沛国谯", "男 生卒(187-226)  籍贯：豫州沛国谯", "男 生卒(？-229) 籍贯：冀州常山国真定"};
        String[] people_detail_two = new String[] {"刘备，蜀汉的开国皇帝，汉景帝之子中山靖王刘胜的后代。", "张飞穿针大眼瞪小眼", "尔等敢应战否", "宁教我负天下人休教天下人负我", "孙坚之子，孙策之弟。东汉建安五年，兄孙策病死，孙权继位吴侯、讨逆将军，领会稽太守，开始统领江东。", "江东小霸王", "刘备的老婆", "七步诗", "短命", "常山赵子龙"};
        for (int i = 0; i < 10; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("picture", people_head_img[i]);
            temp.put("name", people_name[i]);
            temp.put("detail_one", people_detail_one[i]);
            temp.put("detail_two", people_detail_two[i]);
            main_list_data.add(temp);
        }

        // 给首页的RecycleView赋值
        RecyclerView main_list = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        main_list.setLayoutManager(manager);
        hero = new CommonAdapter<Map<String, Object>>(this, R.layout.main_list_item, main_list_data) {
            @Override
            public void convert(myViewHolder holder, Map<String, Object> s) {
                ImageView people_head = holder.getView(R.id.people_head);
                people_head.setImageResource(Integer.parseInt(s.get("picture").toString()));
                TextView name = holder.getView(R.id.name);
                name.setText(s.get("name").toString());
                TextView detail_one = holder.getView(R.id.detail_one);
                detail_one.setText(s.get("detail_one").toString());
                TextView detail_two = holder.getView(R.id.detail_two);
                detail_two.setText(s.get("detail_two").toString());
            }
        };

        // 首页点击英雄事件
        hero.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(MainActivity.this, detail_activity.class);
//                good_focused = main_list_data.get(position);
//                startActivity(intent);
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
        main_list.setAdapter(hero);
    }
}
