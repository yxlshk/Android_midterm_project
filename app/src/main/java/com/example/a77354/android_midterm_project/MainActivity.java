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
                list_data.add(new HeroInfomation(R.drawable.liubei,   "刘备", "玄德", "男", "161-223", "蜀", "幽州涿郡", "刘备，蜀汉的开国皇帝，汉景帝之子中山靖王刘胜的后代。刘备少年孤贫，以贩鞋织草席为生。黄巾起义时，刘备与关羽、张飞桃园结义，成为异姓兄弟，一同剿除黄巾，有功，任安喜县尉，不久辞官；董卓乱政之际，刘备随公孙瓒讨伐董卓，三人在虎牢关战败吕布。后诸侯割据，刘备势力弱小，经常寄人篱下，先后投靠过公孙瓒、曹操、袁绍、刘表等人，几经波折，却仍无自己的地盘。赤壁之战前夕，刘备在荆州三顾茅庐，请诸葛亮出山辅助，在赤壁之战中，联合孙权打败曹操，奠定了三分天下的基础。刘备在诸葛亮的帮助下占领荆州，不久又进兵益州，夺取汉中，建立了横跨荆益两州的政权。后关羽战死，荆州被孙权夺取，刘备大怒，于称帝后伐吴，在夷陵之战中为陆逊用火攻打得大败，不久病逝于白帝城，临终托孤于诸葛亮。"));
                list_data.add(new HeroInfomation(R.drawable.guanyu,   "关羽", "云长", "男", "？-219", "蜀", "司隶河东郡解", "因本处势豪倚势凌人，关羽杀之而逃难江湖。闻涿县招军破贼，特来应募。与刘备、张飞桃园结义，羽居其次。使八十二斤青龙偃月刀随刘备东征西讨。虎牢关温酒斩华雄，屯土山降汉不降曹。为报恩斩颜良、诛文丑，解曹操白马之围。后得知刘备音信，过五关斩六将，千里寻兄。刘备平定益州后，封关羽为五虎大将之首，督荆州事。羽起军攻曹，放水淹七军，威震华夏。围樊城右臂中箭，幸得华佗医治，刮骨疗伤。但未曾提防东吴袭荆州，关羽父子败走麦城，突围中被捕，不屈遭害。"));
                list_data.add(new HeroInfomation(R.drawable.zhangfei, "张飞", "益德", "男", "？-221", "蜀", "幽州涿郡", "与刘备和关羽桃园结义，张飞居第三。随刘备征讨黄巾，刘备终因功被朝廷受予平原相，后张飞鞭挞欲受赂的督邮。十八路诸侯讨董时，三英战吕布，其勇为世人所知。曹操以二虎竞食之计迫刘备讨袁术，刘备以张飞守徐州，诫禁酒，但还是因此而鞭打曹豹招致吕布东袭。刘备反曹后，反用劫寨计擒曹将刘岱，为刘备所赞。徐州终为曹操所破，张飞与刘备失散，占据古城。误以为降汉的关羽投敌，差点一矛将其杀掉。曹操降荊州后引骑追击，刘备败逃，张飞引二十余骑，立马于长阪桥，吓退曹军数十里。庞统死后刘备召其入蜀，张飞率军沿江而上，智擒巴郡太守严颜并生获之，张飞壮而释放。于葭萌关和马超战至夜间，双方点灯，终大战数百回合。瓦口关之战时扮作醉酒，智破张郃。后封为蜀汉五虎大将。及关羽卒，张飞悲痛万分，每日饮酒鞭打部下，导致为帐下将张达、范强所杀，他们持其首顺流而奔孙权。"));
                list_data.add(new HeroInfomation(R.drawable.caocao,   "曹操", "孟德", "男", "155-220", "魏", "豫州沛国谯", "曹操是西园八校尉之一，曾只身行刺董卓，失败后和袁绍共同联合天下诸侯讨伐董卓，后独自发展自身势力，一生中先后战胜了袁术、吕布、张绣、袁绍、刘表、张鲁、马超等割据势力，统一了北方。但是在南下讨伐江东的战役中，曹操在赤壁惨败。后来在和蜀汉的汉中争夺战中，曹操再次无功而返。曹操一生未称帝，他病死后，曹丕继位后不久称帝，追封曹操为魏武皇帝。"));
                list_data.add(new HeroInfomation(R.drawable.sunquan,  "孙权", "仲谋", "男", "182-252", "吴", "扬州吴郡富春", "孙权19岁就继承了其兄孙策之位，力据江东，击败了黄祖。后东吴联合刘备，在赤壁大战击溃了曹操军。东吴后来又和曹操军在合肥附近鏖战，并从刘备手中夺回荆州、杀死关羽、大破刘备的讨伐军。曹丕称帝后孙权先向北方称臣，后自己建吴称帝，迁都建业。"));
                list_data.add(new HeroInfomation(R.drawable.sunce,    "孙策", "伯符", "男", "175-200", "吴", "扬州吴郡富春", "父坚与荆州牧刘表攻，策时十七，与之相从。策箭射表将陈生。后坚为表将乱箭射死，策回到汉水，闻父尸在彼，乃用俘虏黄祖换之。后往淮南投袁术，深为术所爱。后策图自强，以父所留传国玉玺换术兵，术表策为折冲校尉、殄寇将军。策自此荡平东南，威震三江。破刘繇，挟死一将、喝死一将，人皆呼之为「小霸王」。又匹马于神亭与繇将太史慈酣战数百合，为众人所惊。后策平江东，迁讨逆将军，领会稽太守，封吴侯。曹公惮其强，与之结亲以安其心。后曹公与袁绍相拒于官渡，策阴欲袭许。先是，吴郡太守许贡暗发书与曹公，教徐图策，策知之，杀贡。一日，策行猎，为贡门客所刺，创甚。后又斩杀道士于吉而受其诅咒，伤重而亡，寿仅二十六岁。权称尊号，追谥策曰长沙桓王。"));
                list_data.add(new HeroInfomation(R.drawable.sunshangxiang, "孙尚香", "", "女", "？-？", "蜀", "扬州吴郡富春", "刘备向东吴借荆州不还，鲁肃身负关系；周瑜一为救友，二为国计，于是上书孙权，教使「美人计」，进妹予刘备为夫人，诱其丧志而疏远属下。孙夫人才捷刚猛，有诸兄之风，身边侍婢百余人，皆亲自执刀侍立。不料在诸葛亮的锦囊妙计安排下，假婚成真姻；后来夫人更助刘备返蜀，于路上怒斥追袭的吴将。后刘备入益州，使赵云领留营司马，留守荆州。此时孙权闻知刘备西征，于是遣周善引领舟船以迎孙夫人，而夫人带着后主刘禅回吴，幸得赵云与张飞勒兵截江，方重夺刘禅。彝陵之战，刘备战败，有讹言传入吴中，道刘备已死，孙夫人伤心不已，望西痛哭，投江而死。后人为其立庙，号曰「枭姬庙」。"));
                list_data.add(new HeroInfomation(R.drawable.caozhi,   "曹植", "子建", "男", "192-232", "魏", "豫州沛国谯", "曹操之子，极有才华，能举笔成章。曹操一度想立他为世子，但疑其乖巧，而不如曹丕心诚，最终听信贾诩的建议，立曹丕为嗣。曹操去世后，曹丕继位，以曹植不来奔丧为由问罪。曹丕发兵将曹植捉来，让其“七步成诗”，继而又让其立即成诗，曹植均能做到。曹丕迫于太后的压力，放过曹植，对其只做贬爵处罚。"));
                list_data.add(new HeroInfomation(R.drawable.caopi,    "曹丕", "子桓", "男", "187-226", "魏", "豫州沛国谯", "在争夺继承权问题上处心积虑，战胜了文才更胜一筹的弟弟曹植，被立为王世子。曹操逝世后，曹丕继位成为魏王，以不参加葬礼之罪逼弟弟曹植写下七步诗，险些将其杀害，又顺利夺下弟弟曹彰的兵权，坐稳了魏王之位。不久，曹丕逼汉献帝让位，代汉称帝，为魏国开国皇帝。刘备伐吴时，曹丕看出刘备要失败，但不听谋士之言，偏要坐山观虎斗，事后又起兵伐吴，结果被徐盛火攻击败。回洛阳后，曹丕大病，临终前托付曹睿给曹真、司马懿等人，终年四十岁。"));
                list_data.add(new HeroInfomation(R.drawable.zhaoyun,  "赵云", "子龙", "男", "？-229", "蜀", "冀州常山国真定", "初为袁绍将，后见绍不仁，于磐河战退绍将文丑，救瓒并投之。后又刺杀麹义。先主依讬瓒，云与之为田楷拒袁绍。后与先主执手泣别。后瓒败，云流浪卧牛山，与先主见，投之。当阳长阪恶战，云怀抱幼主，七进七出，杀曹军五十余将。先主娶孙夫人，云相随。及征蜀，云随诸葛亮、张飞等人沿江而上。及蜀平，又往征汉中，退曹大军。关羽亡，先主怒欲伐吴，云劝止，不从。后先主崩，云随亮南征、北伐，单骑退追兵。七年卒，后主哭倒于龙床上，谥云顺平侯、追大将军。"));
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
