package com.example.a77354.android_midterm_project;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
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
    private IBinder mBinder;
    private List<HeroInfomation> query_list = new ArrayList<HeroInfomation>();
    SQLiteDatabase db;
    String SQL_CREATE_TABLE = "create table if not exists hero_table(name text, nick_name text, sex text, born_die_date text, home_town text, loyal_to text, detail_info text, image_id int)" ;
    String SQL_INSERT_TABLE = "insert into hero_table values(?,?,?,?,?,?,?,?)";
    String SQL_SELECT_ALL_TABLE = "select * from hero_table";
    String SQL_SELECT_CHECK_TABLE = "select count(*) as c from Sqlite_master  where type ='table' and name ='hero_table'";
    MusicService musicService = new MusicService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "create", Toast.LENGTH_LONG).show();
        db = openOrCreateDatabase("hero_db.db", Context.MODE_PRIVATE, null);
        initDB();
        init();
       Intent intent = new Intent(this, MusicService.class);
       startService(intent);
        //初始化sc
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinder = service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                sc = null;
            }
        };
        bindService(intent, sc, Context.BIND_AUTO_CREATE);

 //       mp.start();
        EventBus.getDefault().register(this);

    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        db.close();
    //    mp.release();
        unbindService(sc);
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        super.onDestroy();
        System.exit(0);
    }
    public void init() {

    //    mp = MediaPlayer.create(MainActivity.this, R.raw.sanguo_01);

        Cursor cursor = db.rawQuery(SQL_SELECT_ALL_TABLE, null);
        while ( cursor.moveToNext()){
            HeroInfomation heroInfomation = new HeroInfomation(
                    cursor.getInt(cursor.getColumnIndex("image_id")),
                   cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("nick_name")),
                   cursor.getString(cursor.getColumnIndex("sex")),cursor.getString(cursor.getColumnIndex("born_die_date")),
                    cursor.getString(cursor.getColumnIndex("loyal_to")), cursor.getString(cursor.getColumnIndex("home_town")),
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
                HeroInfomation heroInfo = query_list.get(position);
                intent.putExtra("heroInfo", heroInfo);
                startActivity(intent);
                // 点击跳转代码在这里写
            }
        });

        // 搜索内容长按英雄删除的事件
        search.setOnItemLongClickListener(new CommonAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                String name = query_list.get(position).name;
                query_list.remove(position);
                search.notifyDataSetChanged();
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

        final FloatingActionButton musicButton = (FloatingActionButton)findViewById(R.id.music);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService.mp.isPlaying()){
                    musicService.mp.pause();
                    musicButton.setImageResource(R.drawable.no_music);
                }else{
                    musicService.mp.start();
                    musicButton.setImageResource(R.drawable.music);
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

    }

    public void initDB(){
        Cursor cursor = db.rawQuery(SQL_SELECT_CHECK_TABLE, null);
        if (cursor.moveToNext()) {
            int count = cursor.getInt(0);
            if (count <= 0) {
                db.execSQL(SQL_CREATE_TABLE);
                List<HeroInfomation> list_data = new ArrayList<HeroInfomation>();
                list_data.add(new HeroInfomation(R.drawable.liubei,   "刘备", "玄德", "男", "161-223", "蜀", "幽州涿郡", "蜀汉的开国皇帝，汉景帝之子中山靖王刘胜的后代。"));
                list_data.add(new HeroInfomation(R.drawable.guanyu,   "关羽", "云长", "男", "？-219", "蜀", "司隶河东郡解", "虎牢关温酒斩华雄，屯土山降汉不降曹。为报恩斩颜良、诛文丑，解曹操白马之围。刘备平定益州后，封关羽为五虎大将之首，督荆州事。"));
                list_data.add(new HeroInfomation(R.drawable.zhangfei, "张飞", "益德", "男", "？-221", "蜀", "幽州涿郡", "十八路诸侯讨董时，三英战吕布，其勇为世人所知。刘备败逃，张飞引二十余骑，立马于长阪桥，吓退曹军数十里。瓦口关之战时扮作醉酒，智破张郃。后封为蜀汉五虎大将。"));
                list_data.add(new HeroInfomation(R.drawable.caocao,   "曹操", "孟德", "男", "155-220", "魏", "豫州沛国谯", "曹操是西园八校尉之一，曾只身行刺董卓，失败后和袁绍共同联合天下诸侯讨伐董卓，后独自发展自身势力，一生中先后战胜了袁术、吕布、张绣、袁绍、刘表、张鲁、马超等割据势力，统一了北方。"));
                list_data.add(new HeroInfomation(R.drawable.sunquan,  "孙权", "仲谋", "男", "182-252", "吴", "扬州吴郡富春", "孙权19岁就继承了其兄孙策之位，力据江东，击败了黄祖。后东吴联合刘备，在赤壁大战击溃了曹操军。"));
                list_data.add(new HeroInfomation(R.drawable.sunce,    "孙策", "伯符", "男", "175-200", "吴", "扬州吴郡富春", "破刘繇，挟死一将、喝死一将，人皆呼之为「小霸王」。又匹马于神亭与繇将太史慈酣战数百合，为众人所惊。后策平江东，迁讨逆将军，领会稽太守，封吴侯。"));
                list_data.add(new HeroInfomation(R.drawable.sunshangxiang, "孙尚香", "", "女", "？-？", "蜀", "扬州吴郡富春", "孙夫人才捷刚猛，有诸兄之风，身边侍婢百余人，皆亲自执刀侍立。不料在诸葛亮的锦囊妙计安排下，假婚成真姻；后来夫人更助刘备返蜀，于路上怒斥追袭的吴将。"));
                list_data.add(new HeroInfomation(R.drawable.caozhi,   "曹植", "子建", "男", "192-232", "魏", "豫州沛国谯", "曹操之子，极有才华，能举笔成章。曹操去世后，曹丕继位，以曹植不来奔丧为由问罪。曹丕发兵将曹植捉来，让其“七步成诗”，继而又让其立即成诗，曹植均能做到。"));
                list_data.add(new HeroInfomation(R.drawable.caopi,    "曹丕", "子桓", "男", "187-226", "魏", "豫州沛国谯", "曹操逝世后，曹丕继位成为魏王，以不参加葬礼之罪逼弟弟曹植写下七步诗，险些将其杀害，又顺利夺下弟弟曹彰的兵权，坐稳了魏王之位。不久，曹丕逼汉献帝让位，代汉称帝，为魏国开国皇帝。"));
                list_data.add(new HeroInfomation(R.drawable.zhaoyun,  "赵云", "子龙", "男", "？-229", "蜀", "冀州常山国真定", "初为袁绍将，后见绍不仁，于磐河战退绍将文丑，救瓒并投之。当阳长阪恶战，云怀抱幼主，七进七出，杀曹军五十余将。"));
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

    //按返回键直接finish当前页。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
