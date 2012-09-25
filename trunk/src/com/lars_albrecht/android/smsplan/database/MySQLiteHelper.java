package com.lars_albrecht.android.smsplan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_EVENTS = "events";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_PHONENUMBER = "phonenumber";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_SENT = "sent";

	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 1;
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + MySQLiteHelper.TABLE_EVENTS + "(" + MySQLiteHelper.COLUMN_ID
			+ " integer primary key autoincrement, " + MySQLiteHelper.COLUMN_DATE + " long, " + MySQLiteHelper.COLUMN_PHONENUMBER
			+ " varchar(50), " + MySQLiteHelper.COLUMN_MESSAGE + " varchar(140), " + MySQLiteHelper.COLUMN_SENT + " int(1));";

	public MySQLiteHelper(final Context context) {
		super(context, MySQLiteHelper.DATABASE_NAME, null, MySQLiteHelper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase database){
		database.execSQL(MySQLiteHelper.DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion){
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + MySQLiteHelper.TABLE_EVENTS);
		this.onCreate(db);
	}
}
