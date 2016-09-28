package cn.ittiger.db.demo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.ittiger.database.SQLiteDB;
import cn.ittiger.database.SQLiteDBConfig;
import cn.ittiger.database.SQLiteDBFactory;
import cn.ittiger.database.listener.IDBListener;
import cn.ittiger.db.demo.model.User;

/**
 * @author laohu
 * @site http://ittiger.cn
 */
public class TigerDatabaseActivity extends Activity {
    private ListView listView;
    private List<String> list = new ArrayList<String>();
    private SQLiteDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UnCaughtCrashExceptionHandler handler = UnCaughtCrashExceptionHandler.getInstance();
        handler.init(this);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无标题
        setContentView(R.layout.activity_listview_layout);

        SQLiteDBConfig config = new SQLiteDBConfig(this);
        config.setDbListener(new IDBListener() {
            @Override
            public void onUpgradeHandler(SQLiteDatabase db, int oldVersion,
                                         int newVersion) {

            }

            @Override
            public void onDbCreateHandler(SQLiteDatabase db) {
                showLongToast("数据库创建成功");
            }
        });
        db = SQLiteDBFactory.createSQLiteDB(config);

        listView = (ListView) findViewById(R.id.lv_main);

        initList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    private void initList() {
        list.add("保存对象");
        list.add("保存集合");
        list.add("查询所有");
        list.add("根据主键查询");
        list.add("删除");
        list.add("更新");
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position + 1) {
                case 1://保存对象
                    addEntity();
                    break;
                case 2://保存集合
                    addCollection();
                    break;
                case 3://查询所的
                    queryAll();
                    break;
                case 4://根据主键查询
                    queryById();
                    break;
                case 5://删除
                    delete();
                    break;
                case 6://更新
                    update();
                    break;
            }
        }
    };

    public void addEntity() {
        User user = new User("添加单个对象");
        if(db.save(user) > 0) {
            showShortToast("添加成功：" + user.toString());
        }
    }

    public void addCollection() {
        List<User> list = new ArrayList<User>();
        for(int i = 0; i < 3; i++) {
            list.add(new User("添加集合" + (i+1)));
        }
        if(db.save(list) == list.size()) {
            showShortToast("添加集合成功");
        }
    }

    public void queryAll() {
        List<User> list = db.queryAll(User.class);
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).toString());
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        showLongToast(sb.toString());
    }

    public User queryById() {
        User user = db.query(User.class, "1");
        showLongToast(user.toString());
        return user;
    }

    public void delete() {
        long total1 = db.queryTotal(User.class);

        List<User> list = db.queryAll(User.class);

        if(list.size() > 0) {
            db.delete(list.get(list.size() - 1));
        }
        long total2 = db.queryTotal(User.class);
        showLongToast("删除之前总数为：" + total1 + ",删除之后总数为：" + total2);
    }

    public void update() {
        StringBuilder sb = new StringBuilder();
        List<User> list = db.queryAll(User.class);

        if(list.size() > 0) {
            sb.append(list.get(list.size() - 1));
            list.get(list.size() - 1).setName("更新结果");
            db.update(list.get(list.size() - 1));
        }
        List<User> list2 = db.queryAll(User.class);
        User user2 = list2.get(list2.size() - 1);

        sb.append(user2.toString());
        showLongToast(sb.toString());
    }

    private void showLongToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void showShortToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
