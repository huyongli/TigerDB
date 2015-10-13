package cn.ittiger.database;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import cn.ittiger.util.SdCardUtil;
import cn.ittiger.util.ValueUtil;

/**
 * 自定义数据库创建容器(设置数据库创建的自定义目录)
 * Author: hyl
 * Time: 2015-8-21下午11:20:03
 */
public class SQLiteContext extends ContextWrapper {
	private SQLiteDBConfig config;
	
	public SQLiteContext(Context base, SQLiteDBConfig config) {
		super(base);
		this.config = config;
	}
	
	@Override
	public File getDatabasePath(String name) {
		if (ValueUtil.isEmpty(config.getDbDirectoryPath())) {
			return super.getDatabasePath(name);
		}

		String dbPath = SdCardUtil.getRootPath() + config.getDbDirectoryPath();
		if(!config.getDbDirectoryPath().endsWith("/")) {
			dbPath = dbPath + "/";
		}
		dbPath = dbPath + config.getDbName();
		
		makeParentDir(dbPath);
		
		return new File(dbPath);
	}
	
	/**
	 * 判断其父目录是否存在，不存在则创建
	 * Author: hyl
	 * Time: 2015-8-21下午11:13:35
	 * @param path
	 */
	private void makeParentDir(String path) {
		String parentPath = getParentPath(path);
		File file = new File(parentPath);
		if(!file.exists()) {
			makeParentDir(parentPath);
			file.mkdir();
		}
	}
	
	/**
	 * 获取父级目录
	 * Author: hyl
	 * Time: 2015-8-21下午11:12:44
	 * @param path
	 * @return
	 */
	public static String getParentPath(String path) {
		if (path.equals("/")) {
			return path;
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		path = path.substring(0, path.lastIndexOf("/"));
		return path.equals("") ? "/" : path;
	}
	
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
		if (ValueUtil.isEmpty(config.getDbDirectoryPath())) {
			return super.openOrCreateDatabase(name, mode, factory);
		}
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}
	
	@SuppressLint("NewApi")
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		if (ValueUtil.isEmpty(config.getDbDirectoryPath())) {
			return super.openOrCreateDatabase(name, mode, factory, errorHandler);
		}
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}
}
