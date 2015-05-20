package com.example.sysulibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper{
	//private final String DB_NAME = "Contacts.db";
	private final String TABLE_NAME1 = "Records";
	private final String TABLE_NAME2 = "Books";
	private final String CREATE_TABLE_SQL1 = "create table " + TABLE_NAME1+ " (_id integer primary key autoincrement," +
			"content, date);";
	private final String CREATE_TABLE_SQL2 = "create table " + TABLE_NAME2+ " (_id integer primary key autoincrement," +
			"bookimg, bookname, author, number, isbn, publisher, year);";
	
	public MyDatabaseHelper(Context context, String name, int version){
		super(context, name, null, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_SQL1);
		db.execSQL(CREATE_TABLE_SQL2);
		System.out.println("Executed");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		
	}
}

