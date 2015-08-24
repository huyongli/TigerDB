package cn.ittiger.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite帮助类
 * Author: hyl
 * Time: 2015-8-15下午1:52:02
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	private SQLiteDBConfig mConfig;
	
	public SQLiteHelper(SQLiteDBConfig config) {
		super(new SQLiteContext(config.getContext(), config), config.getDbName(), null, config.getVersion());
		this.mConfig = config;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if(mConfig.getDbListener() != null) {
			mConfig.getDbListener().onDbCreateHandler(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(mConfig.getDbListener() != null) {
			mConfig.getDbListener().onUpgradeHandler(db, oldVersion, newVersion);
		}
	}

}
