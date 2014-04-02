package com.mromanro.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mromanro.evader.User;

public class DatabaseHandler extends SQLiteOpenHelper{
	//Database version
	private static final int DATABASE_VERSION = 1;
	
	//Database name
	private static final String DATABASE_NAME = "colorManager";
	
	//Table name
	private static final String TABLE_USERS = "users";
	
	//Table's column's names
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_SCORE = "score";
	
	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" + 
				KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + 
				KEY_SCORE +
				")";
		db.execSQL(CREATE_USERS_TABLE);
		Log.d("Database", " onCreate");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		//drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		
		//Create tables again
		onCreate(db);
	}
	
	public void addUser(User user){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, user.getID());
		values.put(KEY_TITLE, user.getName());
		values.put(KEY_SCORE, user.getScore());
			
		db.insert(TABLE_USERS, null, values);
		db.close();
		Log.d("database", "finished addUser");
	}
	
	public User getUser(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_USERS,
				new String[] {KEY_ID, KEY_TITLE, KEY_SCORE},
				KEY_ID + "=?",
				new String[]{String.valueOf(id)}, 
				null,
				null, 
				null,
				null);
		
		if(cursor != null)
			cursor.moveToFirst();
		
		User user = new User(Integer.parseInt(cursor.getString(0)),
						cursor.getString(1),
						Integer.parseInt(cursor.getString(2)));
		
		return user;
	}
	
	public List<User> getAllUsers(){
		List<User> userList = new ArrayList<User>();
		
		String selectQuery = "SELECT * FROM " + TABLE_USERS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("database", "made past writable");
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.d("database", "made past cursor create");
		if(cursor.moveToFirst()){
			Log.d("database", "made past cursor move to first");
			do{
				User user = new User();
				user.setID(Integer.parseInt(cursor.getString(0)));
				user.setName(cursor.getString(1));
				user.setScore(Integer.parseInt(cursor.getString(2)));
				userList.add(user);
			}while(cursor.moveToNext());
		}
		return userList;
	}
	
	public int getUsersCount(){
		//Log.d("database", "made this far");
		String countQuery = "SELECT * FROM " + TABLE_USERS;
		//Log.d("database", "past this");
		SQLiteDatabase db = this.getWritableDatabase();
		//Log.d("database", "still no");
		Cursor cursor = db.rawQuery(countQuery, null);
		//Log.d("database", "nope");
		int i = cursor.getCount();
		cursor.close();
		return i;
	}
	
	public boolean existsUser(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_USERS,
				new String[] {KEY_ID, KEY_TITLE, KEY_SCORE},
				KEY_ID + " =?",
				new String[]{String.valueOf(id)}, 
				null,
				null, 
				null,
				null);
		if(cursor.moveToFirst()){
			do{
				if(id == Integer.parseInt(cursor.getString(0)))
					return true;
			}while(cursor.moveToNext());
		}
		
		return false;
	}
	
	public int updateUser(User user){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, user.getName());
		values.put(KEY_SCORE, user.getScore());
		
		return db.update(TABLE_USERS, 
							values,
							KEY_ID + " =?",
							new String[]{ String.valueOf(user.getID())});
	}
	
	public void deleteUser(User user){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_USERS, KEY_ID + " =?", new String[]{
												String.valueOf(user.getID())});
		db.close();
	}
}
