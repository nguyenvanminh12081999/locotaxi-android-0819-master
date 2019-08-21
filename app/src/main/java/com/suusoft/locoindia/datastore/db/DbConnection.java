package com.suusoft.locoindia.datastore.db;

import android.content.Context;
import android.database.Cursor;

import java.io.IOException;
import java.util.HashMap;


/**
 * Class connects to Database, get all data here
 *
 * 
 */
public class DbConnection implements IDatabaseConfig {

	private Context mContext;
	private DataBaseHelper mDBHelper;

	public DbConnection(Context mContext) {
		this.mContext = mContext;
		this.mDBHelper = new DataBaseHelper(this.mContext, DATABASE_NAME);
	}

	/**
	 * close database
	 */
	private void close() {
		mDBHelper.close();
	}

	/**
	 * open and connect to database
	 */
	private void openAndConnectDB() {
		try {
			mDBHelper.createDataBase();
			mDBHelper.openDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeDB() {
		this.mDBHelper.close();
	}

	private void execSQL(String sqlQuery) {
		mDBHelper.getmDatabase().execSQL(sqlQuery);
	}

	public HashMap<String,String> getAllCachingTime(){
		this.openAndConnectDB();
		HashMap<String, String> cachingTimes = new HashMap<>();
		String response = "";
		String query = "SELECT * FROM " + TABLE_CACHING ;
		Cursor mCursor = this.mDBHelper.rawQuery(query);
		if (mCursor.moveToFirst()){
			do {
				cachingTimes.put(mCursor.getString(1),mCursor.getString(3));
			}while (mCursor.moveToNext());
		}

		return cachingTimes;
	}


}
