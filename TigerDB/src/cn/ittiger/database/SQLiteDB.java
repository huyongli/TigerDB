package cn.ittiger.database;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.ittiger.database.bean.PrimaryKey;
import cn.ittiger.database.manager.EntityTableManager;
import cn.ittiger.database.manager.SQLExecuteManager;
import cn.ittiger.database.util.CursorUtil;
import cn.ittiger.util.ValueUtil;

/**
 * 数据库操作类
 * 使用此功能需要如下两个权限：
 * android.permission.MOUNT_UNMOUNT_FILESYSTEMS
 * android.permission.WRITE_EXTERNAL_STORAGE
 * 
 * 注：所有操作的实体必须实现无参构造函数
 * 
 * @author: huylee
 * @time:	2015-8-12下午10:06:20
 */
public class SQLiteDB {
	/**
	 * 数据库配置
	 */
	private SQLiteDBConfig mConfig;
	/**
	 * 数据库操作类
	 */
	private SQLiteDatabase mDB;
	/**
	 * SQL语句执行管理器
	 */
	private SQLExecuteManager mSQLExecuteManager;
	
	public SQLiteDBConfig getConfig() {
		return mConfig;
	}
	
	public SQLiteDB(SQLiteDBConfig mConfig) {
		super();
		this.mConfig = mConfig;
		mDB = new SQLiteHelper(mConfig).getWritableDatabase();
		if(mDB == null) {
			throw new NullPointerException("创建数据库对象失败");
		}
		mSQLExecuteManager = new SQLExecuteManager(mDB);
	}

	/**
	 * 保存一个实体
	 * 当主键设置为自增长时，手动设置的主键值不会起作用，同时会自动加保存之后最新的主键值填充到对象中，并返回
	 * @author: huylee
	 * @time:	2015-8-12下午10:08:30
	 * @param entity
	 * @return
	 */
	public <T> long save(T entity) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, entity);
		long rowid = mSQLExecuteManager.insert(SQLBuilder.getInsertSQL(entity));
		PrimaryKey key = EntityTableManager.getEntityTable(entity).getPrimaryKey();
		if(key.isAutoGenerate()) {
			key.getField().setAccessible(true);
			try {
				key.setValue(entity, rowid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rowid;
	}
	
	/**
	 * 保存集合实体
	 * Author: hyl
	 * Time: 2015-8-17下午9:35:18
	 * @param collection
	 * @return	如果集合为空或者批量保存失败则返回-1，保存成功返回集合大小
	 */
	public <T> long save(Collection<T> collection) {
		long rowId = -1;
		if(ValueUtil.isEmpty(collection)) {
			return rowId;
		}
		try {
			mSQLExecuteManager.beginTransaction();
			Iterator<T> iterator = collection.iterator();
			while(iterator.hasNext()) {
				rowId = save(iterator.next());
				if(rowId == -1) {
					throw new SQLException("删除实体失败");
				}
			}
			mSQLExecuteManager.successTransaction();
			rowId = collection.size();
		} catch (SQLException e) {
			e.printStackTrace();
			rowId = -1;
		} finally {
			mSQLExecuteManager.endTransaction();
		}
		return rowId;
	}
	
	/**
	 * 删除指定实体(根据主键删除)
	 * Author: hyl
	 * Time: 2015-8-17下午9:43:05
	 * @param entity	要删除的实体
	 */
	public <T> void delete(T entity) {
		if(ValueUtil.isEmpty(entity)) {
			return ;
		}
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, entity);
		mSQLExecuteManager.delete(SQLBuilder.getDeleteSQL(entity));
	}
	
	/**
	 * 删除集合中的实体(有事务控制)
	 * Author: hyl
	 * Time: 2015-8-17下午10:25:59
	 * @param collection	要删除的实体集合
	 */
	public <T> void delete(Collection<T> collection) {
		if(ValueUtil.isEmpty(collection)) {
			return;
		}
		
		try {
			Iterator<T> iterator = collection.iterator();
			this.mSQLExecuteManager.beginTransaction();
			while(iterator.hasNext()) {
				delete(iterator.next());
			}
			this.mSQLExecuteManager.successTransaction();
		} finally {
			this.mSQLExecuteManager.endTransaction();
		}
	}
	
	/**
	 * 删除实体类中指定主键的实体
	 * @author: huylee
	 * @time:	2015-8-20下午9:07:26
	 * @param mClass				要删除的实体类
	 * @param primaryKeyValue		要删除的实体的主键值
	 */
	public void delete(Class<?> mClass, String primaryKeyValue) {
		if(ValueUtil.isEmpty(primaryKeyValue)) {
			throw new IllegalArgumentException("要删除的实体的主键不能为空");
		}
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		mSQLExecuteManager.delete(SQLBuilder.getDeleteSQL(mClass, primaryKeyValue));
	}
	
	/**
	 * 根据where条件句删除相关实体
	 * @author: huylee
	 * @time:	2015-8-20下午9:09:11
	 * @param tableName			要删除的数据表
	 * @param whereClause		where后面的条件句(delete from XXX where XXX)，参数使用占位符
	 * @param whereArgs			占位符参数
	 * @return
	 */
	public void delete(String tableName, String whereClause, String[] whereArgs) {
		mSQLExecuteManager.delete(tableName, whereClause, whereArgs);
	}
	
	/**
	 * 删除,表名不能使用占位符
	 * @author: huylee
	 * @time:	2015-8-20下午10:18:01
	 * @param sql		删除语句(参数使用占位符)
	 * @param args		占位符参数
	 */
	public void delete(String sql, String[] bindArgs) {
		mSQLExecuteManager.updateOrDelete(sql, bindArgs);
	}
	
	/**
	 * 删除实体类所有数据
	 * @author: huylee
	 * @time:	2015-8-20下午11:00:46
	 * @param mClass
	 */
	public void deleteAll(Class<?> mClass) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		mSQLExecuteManager.delete(SQLBuilder.getDeleteSQL(mClass, null));
	}
	
	/**
	 * 更新指定实体(必须设置主键)
	 * @author: huylee
	 * @time:	2015-8-20下午10:02:41
	 * @param entity
	 * @return
	 */
	public <T> void update(T entity) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, entity);
		mSQLExecuteManager.update(SQLBuilder.getUpdateSQL(entity));
	}
	
	/**
	 * 更新指定集合数据
	 * @author: huylee
	 * @time:	2015-8-20下午10:49:45
	 * @param collection
	 */
	public <T> void update(Collection<T> collection) {
		if(ValueUtil.isEmpty(collection)) {
			return;
		}
		
		try {
			
			Iterator<T> iterator = collection.iterator();
			this.mSQLExecuteManager.beginTransaction();
			while(iterator.hasNext()) {
				update(iterator.next());
			}
			this.mSQLExecuteManager.successTransaction();
		} finally {
			this.mSQLExecuteManager.endTransaction();
		}
	}
	
	/**
	 * 更新
	 * @author: huylee
	 * @time:	2015-8-20下午10:51:01
	 * @param sql
	 * @param bindArgs
	 */
	public void update(String sql, String[] bindArgs) {
		mSQLExecuteManager.updateOrDelete(sql, bindArgs);
	}
	
	/**
	 * 查询实体类全部数据
	 * @author: huylee
	 * @time:	2015-8-20下午10:58:53
	 * @param mClass	要查询的实体类
	 * @return			实体列表
	 */
	public <T> List<T> queryAll(Class<T> mClass) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getQuerySQL(mClass));
		return CursorUtil.parseCursor(cursor, mClass);
	}
	
	/**
	 * 根据主键查询指定实体
	 * Author: hyl
	 * Time: 2015-8-21上午10:45:50
	 * @param primaryKeyValue
	 * @return
	 */
	public <T> T query(Class<T> mClass, String primaryKeyValue) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getQuerySQLById(mClass, primaryKeyValue));
		return CursorUtil.parseCursorOneResult(cursor, mClass);
	}
	
	/**
	 * 根据条件查询实体类
	 * Author: hyl
	 * Time: 2015-8-21上午11:29:05
	 * @param mClass		查询的实体类
	 * @param whereClause	查询条件where子句
	 * @param whereArgs		where子句参数
	 * @return
	 */
	public <T> List<T> query(Class<T> mClass, String whereClause, String[] whereArgs) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getQuerySQL(mClass, whereClause, whereArgs));
		return CursorUtil.parseCursor(cursor, mClass);
	}
	
	/**
	 * 分页查询
	 * Author: hyl
	 * Time: 2015-8-21上午11:42:32
	 * @param mClass	查询实体类
	 * @param curPage	当前页码
	 * @param pageSize	每页数据条数
	 * @return
	 */
	public <T> List<T> queryPage(Class<T> mClass, int curPage, int pageSize) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getQueryPageSQL(mClass, curPage, pageSize));
		return CursorUtil.parseCursor(cursor, mClass);
	}
	
	/**
	 * 分页查询
	 * Author: hyl
	 * Time: 2015-8-21上午11:42:32
	 * @param mClass	查询实体类，返回实体类型
	 * @param curPage	当前页码
	 * @param pageSize	每页数据条数
	 * @return
	 */
	/**
	 * 分页查询
	 * Author: hyl
	 * Time: 2015-8-21上午11:43:18
	 * @param mClass		查询实体类，返回实体类型
	 * @param sql			查询语句
	 * @param bindArgs		查询语句中的参数
	 * @param curPage		当前页码
	 * @param pageSize		每页数据条数
	 * @return
	 */
	public <T> List<T> queryPage(Class<T> mClass, String whereClause, String[] whereArgs, int curPage, int pageSize) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getQueryPageSQL(mClass, whereClause, whereArgs, curPage, pageSize));
		return CursorUtil.parseCursor(cursor, mClass);
	}
	
	/**
	 * 查询实体类的总数据条数
	 * Author: hyl
	 * Time: 2015-8-21上午11:05:27
	 * @param mClass	查询实体类
	 * @return
	 */
	public long queryTotal(Class<?> mClass) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getTotalSQL(mClass, null, null));
		return CursorUtil.parseCursorTotal(cursor);
	}
	
	/**
	 * 查询实体类指定条件下的数据总数
	 * Author: hyl
	 * Time: 2015-8-21上午11:09:24
	 * @param mClass			查询实体类
	 * @param whereClause		查询条件
	 * @param whereArgs			查询条件中的占位符参数
	 * @return
	 */
	public long queryTotal(Class<?> mClass, String whereClause, String[] whereArgs) {
		EntityTableManager.checkOrCreateTable(mSQLExecuteManager, mClass);
		Cursor cursor = mSQLExecuteManager.query(SQLBuilder.getTotalSQL(mClass, whereClause, whereArgs));
		return CursorUtil.parseCursorTotal(cursor);
	}
	
	/**
	 * 根据SQL语句查询数据条数(解析结果为第一列值)
	 * Author: hyl
	 * Time: 2015-8-21上午11:09:24
	 * @param sql			查询SQL
	 * @param bindArgs		占位符参数值
	 * @return
	 */
	public long queryTotal(String sql, String[] bindArgs) {
		Cursor cursor = mSQLExecuteManager.query(sql, bindArgs);
		return CursorUtil.parseCursorTotal(cursor);
	}
	
	/**
	 * 根据SQL语句查询
	 * Author: hyl
	 * Time: 2015-8-21上午11:52:11
	 * @param sql
	 * @param bindArgs
	 * @return
	 */
	public Cursor query(String sql, String[] bindArgs) {
		return mSQLExecuteManager.query(sql, bindArgs);
	}
}
