package com.example.a77354.android_midterm_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CommonAdapter<HeroInfomation> hero;
    CommonAdapter<HeroInfomation> search;
    private List<HeroInfomation> main_list_data = new ArrayList<HeroInfomation>();
    private ServiceConnection sc;
    private MusicService.MyBinder myBinder;

    private List<HeroInfomation> query_list = new ArrayList<HeroInfomation>();
    SQLiteDatabase db;
    String SQL_CREATE_TABLE = "create table if not exists hero_table(name text, nick_name text, sex text, born_die_date text, home_town text, loyal_to text, detail_info text, image_id int)" ;
    String SQL_INSERT_TABLE = "insert into hero_table values(?,?,?,?,?,?,?,?)";
    String SQL_SELECT_ALL_TABLE = "select * from hero_table";
    String SQL_SELECT_CHECK_TABLE = "select count(*) as c from Sqlite_master  where type ='table' and name ='hero_table'";
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("hero_db.db", Context.MODE_PRIVATE, null);
        initDB();
        init();
//        Intent intent = new Intent(this, MusicService.class);
//        startService(intent);
//        bindService(intent, sc, Context.BIND_AUTO_CREATE);

        mp.start();
        EventBus.getDefault().register(this);

    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        db.close();
        mp.release();
        super.onDestroy();
    }
    public void init() {

        mp = MediaPlayer.create(MainActivity.this, R.raw.sanguo_01);

        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_TABLE, null);
        while ( cursor.moveToNext()){
            HeroInfomation heroInfomation = new HeroInfomation(
                    cursor.getInt(cursor.getColumnIndex("image_id")),
                   cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("nick_name")),
                   cursor.getString(cursor.getColumnIndex("sex")),cursor.getString(cursor.getColumnIndex("born_die_date")),
                   cursor.getString(cursor.getColumnIndex("home_town")),cursor.getString(cursor.getColumnIndex("loyal_to")),
                   cursor.getString(cursor.getColumnIndex("detail_info"))
            );
            main_list_data.add(heroInfomation);
        }

        // 给首页的RecycleView赋值
        RecyclerView main_list = (RecyclerView) findViewById(R.id.list);
        RecyclerView search_list = (RecyclerView) findViewById(R.id.search_list);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        LinearLayoutManager manager_search = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        main_list.setLayoutManager(manager);
        search_list.setLayoutManager(manager_search);
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
        search = new CommonAdapter<HeroInfomation>(MainActivity.this, R.layout.main_list_item, query_list){
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
        search_list.setAdapter(search);
        setClickEvent();
        searchHero();
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

        // 搜索内容点击英雄事件
        hero.setOnItemLongClickListener(new CommonAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                String name = main_list_data.get(position).name;
                main_list_data.remove(position);
                hero.notifyDataSetChanged();
                int num = position + 1;
                Toast.makeText(getApplicationContext(), "删除第"+num+"个英雄", Toast.LENGTH_SHORT).show();
                db.delete("hero_table", "name = ?", new String[]{name});
            }
        });
        // 首页长按英雄删除的事件
        search.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, detailActivity.class);
                HeroInfomation heroInfo = main_list_data.get(position);
                intent.putExtra("heroInfo", heroInfo);
                startActivity(intent);
                // 点击跳转代码在这里写
            }
        });

        // 搜索内容长按英雄删除的事件
        search.setOnItemLongClickListener(new CommonAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                String name = main_list_data.get(position).name;
                main_list_data.remove(position);
                hero.notifyDataSetChanged();
                int num = position + 1;
                Toast.makeText(getApplicationContext(), "删除第"+num+"个英雄", Toast.LENGTH_SHORT).show();
                db.delete("hero_table", "name = ?", new String[]{name});
            }
        });

        //点击+号进入编辑页面。
        FloatingActionButton addButton = (FloatingActionButton)findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, editActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton musicButton = (FloatingActionButton)findViewById(R.id.music);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()){
                    mp.stop();
                }else{
                    mp=MediaPlayer.create(MainActivity.this, R.raw.sanguo_01);
                    mp.start();
                }
            }
        });

        final ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView main_list = (RecyclerView)findViewById(R.id.list);
                RecyclerView search_list = (RecyclerView)findViewById(R.id.search_list);
                main_list.setVisibility(View.VISIBLE);
                search_list.setVisibility(View.INVISIBLE);
                back.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.isChangingHeroInfo) {  //是修改模式的话，要修改原英雄信息
            int index = getIndexOfHero(event.heroInfo);
            main_list_data.set(index, event.heroInfo);
            ContentValues cv = new ContentValues();
            cv.put("name", main_list_data.get(index).name);
            cv.put("nick_name", main_list_data.get(index).nickName);
            cv.put("sex", main_list_data.get(index).sex);
            cv.put("home_town", main_list_data.get(index).homeTown);
            cv.put("born_die_date", main_list_data.get(index).bornDiedDate);
            cv.put("loyal_to", main_list_data.get(index).loyalTo);
            cv.put("detail_info", main_list_data.get(index).detailInfo);
            cv.put("image_id", main_list_data.get(index).imageId);
            db.update("hero_table", cv, "name=?", new String[]{main_list_data.get(index).name});
        } else {
            main_list_data.add(event.heroInfo);
            db.execSQL(SQL_INSERT_TABLE, new Object[]{
                    event.heroInfo.name, event.heroInfo.nickName,
                    event.heroInfo.sex, event.heroInfo.bornDiedDate,
                    event.heroInfo.homeTown, event.heroInfo.loyalTo,
                    event.heroInfo.detailInfo, event.heroInfo.imageId
            });
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

    public void searchHero(){
        //搜素框设置
        final RecyclerView main_list = (RecyclerView) findViewById(R.id.list);
        final SearchView searchView = (SearchView)findViewById(R.id.search_view);
        final RecyclerView searchListView = (RecyclerView)findViewById(R.id.search_list);
        final ImageView back = (ImageView)findViewById(R.id.back);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!query.equals("")){
                    query_list.clear();
                    for(int i = 0; i < main_list_data.size(); i++){
                        HeroInfomation heroitem = main_list_data.get(i);
                        if(HeroMessageContain(heroitem, query)){
                            query_list.add(heroitem);
                        }
                    }
                    if(!query_list.isEmpty()) {
                        main_list.setVisibility(View.INVISIBLE);
                        searchListView.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        search.notifyDataSetChanged();
                    }else{
                        main_list.setVisibility(View.INVISIBLE);
                        searchListView.setVisibility(View.INVISIBLE);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public boolean HeroMessageContain(HeroInfomation heroitem, String query){
        return heroitem.name.contains(query) || heroitem.nickName.contains(query)
                || heroitem.sex.contains(query) || heroitem.bornDiedDate.contains(query)
                || heroitem.homeTown.contains(query) || heroitem.loyalTo.contains(query)
                || heroitem.detailInfo.contains(query);
    }

    protected void onStop(){
        super.onStop();
        mp.stop();
    }

    public void initDB(){
        Cursor cursor = db.rawQuery(SQL_SELECT_CHECK_TABLE, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count <= 0) {
                db.execSQL(SQL_CREATE_TABLE);
                List<HeroInfomation> list_data = new ArrayList<HeroInfomation>();
                list_data.add(new HeroInfomation(R.drawable.liubei,   "刘备", "玄德", "男", "161-223", "蜀", "幽州涿郡", "刘备，蜀汉的开国皇帝，汉景帝之子中山靖王刘胜的后代。"));
                list_data.add(new HeroInfomation(R.drawable.zhangfei, "张飞", "益德", "男", "？-221", "蜀", "幽州涿郡", "张飞穿针大眼瞪小眼"));
                list_data.add(new HeroInfomation(R.drawable.guanyu,   "关羽", "云长", "男", "？-219", "蜀", "司隶河东郡解", "尔等敢应战否"));
                list_data.add(new HeroInfomation(R.drawable.caocao,   "曹操", "孟德", "男", "155-220", "魏", "豫州沛国谯", "宁教我负天下人休教天下人负我"));
                list_data.add(new HeroInfomation(R.drawable.sunquan,  "孙权", "仲谋", "男", "182-252", "吴", "扬州吴郡富春", "孙坚之子，孙策之弟。东汉建安五年，兄孙策病死，孙权继位吴侯、讨逆将军，领会稽太守，开始统领江东。"));
                list_data.add(new HeroInfomation(R.drawable.sunce,    "孙策", "伯符", "男", "175-200", "吴", "扬州吴郡富春", "江东小霸王"));
                list_data.add(new HeroInfomation(R.drawable.sunshangxiang, "孙尚香", "", "女", "？-？", "蜀", "扬州吴郡富春", "刘备的老婆"));
                list_data.add(new HeroInfomation(R.drawable.caozhi,   "曹植", "子建", "男", "192-232", "魏", "豫州沛国谯", "七步诗"));
                list_data.add(new HeroInfomation(R.drawable.caopi,    "曹丕", "子桓", "男", "187-226", "魏", "豫州沛国谯", "短命"));
                list_data.add(new HeroInfomation(R.drawable.zhaoyun,  "赵云", "子龙", "男", "？-229", "蜀", "冀州常山国真定", "常山赵子龙"));
                for (int i = 0; i < list_data.size(); i++){
                    HeroInfomation heroInfo = list_data.get(i);
                    db.execSQL(SQL_INSERT_TABLE, new Object[]{
                            heroInfo.name, heroInfo.nickName,
                            heroInfo.sex, heroInfo.bornDiedDate,
                            heroInfo.homeTown, heroInfo.loyalTo,
                            heroInfo.detailInfo, heroInfo.imageId
                    });
                }
            }
        }
    }
}
