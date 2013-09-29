package org.huangxiaozhe.Common;

import static org.huangxiaozhe.Common.Constants.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventsData extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "events.db";
   private static final int DATABASE_VERSION = 1;
   private static String[] FROM = {XSCALE,YSCALE,COUNT};
   private static String ORDER_BY = COUNT + " ASC";

   public EventsData(Context context) { 
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
	   //CREATE TABLE events.db (count INTEGER PRIMARY KEY,xscale INTEGER,yscale INTEGER,player INTEGER);
	   db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COUNT
			   + " INTEGER PRIMARY KEY, " + XSCALE
			   + " INTEGER," + YSCALE + " INTEGER," + PLAYER +" INTEGER);");
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	   db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	   onCreate(db);
   }
   
   public void addEvent(int count,int xscale,int yscale,int player) {
	  SQLiteDatabase database= this.getWritableDatabase();
	  ContentValues values = new ContentValues();
	  values.put(COUNT,count);
	  values.put(XSCALE, xscale);
	  values.put(YSCALE, yscale);
	  values.put(PLAYER, player);
	   database.insertOrThrow(TABLE_NAME, null, values);//*/
   }
   
   public void clearEvent(){
	   SQLiteDatabase database= this.getWritableDatabase();
	   database.delete(TABLE_NAME, null, null);
   }
   
   public boolean delete2Events(int t[]){
	   SQLiteDatabase database= this.getWritableDatabase();
	   Cursor cursor = database.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY,"2");
	   int i= -1;
	   int c[]=new int[2];
	   while (cursor.moveToNext()) {
		   t[++i]=cursor.getInt(0);
		   t[++i]=cursor.getInt(1);
		   c[i/2]=cursor.getInt(2);
	   }
	   if(i!=3)
		   return false;
	   database.delete(TABLE_NAME, COUNT + "=" + String.valueOf(c[0]),null );
	   database.delete(TABLE_NAME, COUNT + "=" + String.valueOf(c[1]),null );
	   return true;
   }

@Override
public synchronized void close() {
	// TODO Auto-generated method stub
	SQLiteDatabase database= this.getWritableDatabase();
	database.close();
	super.close();
}

}