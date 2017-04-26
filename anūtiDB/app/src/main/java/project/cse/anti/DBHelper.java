package project.cse.anti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by akshay on 30/12/16.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME ="ContactsDB.db";
    public static final String CONTACTS_TABLE_NAME="contacts";
    public static final String CONTACTS_COLUMN_ID="id";
    public static final String CONTACTS_COLUMN_NAME="name";
    public static final String CONTACTS_COLUMN_PHONE="phone";
    public static final String CONTACTS_COLUMN_MESSAGE="message";

    private static DBHelper mInstance=null;

    private DBHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    public static DBHelper getInstance(Context ctx){
        if(mInstance==null){
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table contacts " +
                "(id integer primary key, name text, phone text, message text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);


    }

    public boolean insertContact(String name, String phone, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("phone",phone);
        contentValues.put("message",message);
        db.insert("contacts",null,contentValues);

        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts",null);
        return res;
    }
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where id="+id+"",null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db,CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String name, String phone, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("phone",phone);
        contentValues.put("message",message);
        db.update("contacts",contentValues,"id = ? ",new String[]{ Integer.toString(id)});

        return true;
    }



    public Integer deleteContact(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME, "id = ? ",new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllContacts(String column_name){
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase db=null;
        Cursor res=null;
    try {

        db = this.getReadableDatabase();
        try {
            res = db.rawQuery("select * from contacts", null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                arrayList.add(res.getString(res.getColumnIndex(column_name)));
                res.moveToNext();
            }
        }
        finally {
           if(res!=null && !res.isClosed())
            res.close();
        }
    }finally{


    }

        return arrayList;
    }


}
