# [TigerDB](http://ittiger.cn/2015/10/01/%E7%AE%80%E5%8D%95%E5%AE%9E%E7%94%A8%E7%9A%84Android-ORM%E6%A1%86%E6%9E%B6TigerDB/)
基于Android中原生SDK操作SQLite的封装，大大简化了实体对象与本地数据库间的交互操作，提升App的开发效率<br/>


# 个人微信公众号，欢迎扫码关注交流：   
![](https://img-blog.csdnimg.cn/2019052410035231.jpg)

# 使用方式
compile 'cn.ittiger:tigerdb:1.0'

##
TigerDB是一个简单的Android ORM框架，它能让你一句话实现数据库的增删改查，同时支持实体对象的持久化和自动映射，同时你也不必关心表结构的变化，因为它会自动检测新增字段来更新你的表结构。

该库主要包括如下几个要点：<br/>
* 根据实体对象自动建表、新增字段(SQLite不支持删除字段列)<br/>
* 支持注解配置表名、字段名、字段默认值、主键是否自增长以及哪些字段不作为数据表中的映射字段<br/>
* 直接映射实体对象到SQLite数据库，实现一行代码对SQLite数据库增删改查<br/>
* 支持在SDCard中新建数据库db<br/>
* 解决在onCreate，onUpgrade中执行数据库其他操作时出现的异常(java.lang.IllegalStateException: getDatabase called recursively)<br/>
* 支持原生SQL语句操作数据库<br/>

该ORM库使用过程中的主要类说明：<br/>
* SQLiteDBConfig：主要用于设置数据库的名字、创建路径、版本号、数据创建更新时的监听<br/>
* SQLiteDB：创建完数据库之后，主要通过此类来操作数据库的增删改查<br/>
* SQLiteDBFactory：该类主要用于创建SQLite数据库，同时缓存当前创建的SQLiteDB对象<br/>
* CursorUtil：查询数据库时，检测游标对象Cursor是否正常，解析Cursor数据为实体对象<br/>
* IDBListener：数据库创建、升级时的监听类，提供了空实现SimpleDBListener<br/>
* Column：该注解用来设置字段名、字段默认值<br/>
* PrimaryKey：该注解用来设置主键、主键字段名、以及主键是否为自增长<br/>
* Table：该注解用来设置表名，不设置的话默认类名为表名<br/>
* NotDBColumn：该注解用来设置哪些实体属性不映射到数据表中<br/>

主要用法如下：<br/>
```
//新建实体，可以通过注解@Table设置表名,如果不设置默认以类名User作为表名
public class User {
	//设置主键id为自增长，也可以通过注解@Column设置字段名或字段默认值
	@PrimaryKey(isAutoGenerate=true)
	private long id;
		
	private String name;
		
	//设置age字段默认值为1
	@Column(defaultValue="1")
	private int age;
		
	//该字段不作为数据表中的字段
	@NotDBColumn
	private String bz;
		
	//必须实现无参构造
	public User() {
	}
		
	public User(String name) {
		super();
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
		
	@Override
	public String toString() {
		return "{id=" + id + ",name=" + name + "}";
	}
}
	
//SQLite相关配置，包括数据库名字、创建路径、版本号、数据创建和升级时的监听
SQLiteDBConfig config = new SQLiteDBConfig(this);
//设置数据库创建更新时的监听，有提供空实现：SimpleDBListener
config.setDbListener(new IDBListener() {
 	@Override
 	public void onUpgradeHandler(SQLiteDatabase db, int oldVersion, int newVersion) {
  				
  	}
  			
  	@Override
  	public void onDbCreateHandler(SQLiteDatabase db) {
  		showLongToast("数据库创建成功");
  	}
 });
 //创建db，在创建数据库的时候，不需要在onDbCreateHandler手动去创建相关的数据表，在对实体对象进行数据操作的时候，会自动判断表是否存在，不存在的话会自动创建，同时如果有新增的字段也会自动更新表结构
 SQLiteDB db = SQLiteDBFactory.createSQLiteDB(config);
 
//保存单个实体对象
User user = new User("添加单个对象");
int rtn = db.save(user)
		
//保存集合对象
List list = new ArrayList();
int rtn = db.save(list)
		
//查询User表中的所有数据
List list = db.queryAll(User.class);
	
//根据实体id(主键)查询User
User user = db.query(User.class, "1");
	
//查询User表中的数据总数
long total = db.queryTotal(User.class);
 
//删除指定实体对象
db.delete(user);
	
//更新实体对象
db.update(user);
	
//分页查询
db.queryPage(claxx, curPage, pageSize);
	
//根据SQL查询
Cursor cursor = db.query(sql, bindArgs)；
```
