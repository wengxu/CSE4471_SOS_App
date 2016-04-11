package com.cse4471.osu.sos_osu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.Key;
import java.sql.RowId;
import java.sql.SQLException;

/**
 * Created by xuweng on 3/29/16.
 */
// this is the adapter that serves as a bridge between view and model
public class ContactDbAdapter {
    private static final String DATABASE_NAME = "SOS_DATABASE.db";
    private static final String CONTACT_TABLE = "CONTACT_TABLE";
    private static final int DATABASE_VERSION = 200;
    private final Context mCtx;
    public static String TAG = ContactDbAdapter.class.getSimpleName();

    private DatabaseHelper mDbHelper;
    SQLiteDatabase mDb;

    public  static final String KEY_ROWID ="_id";
    public  static final String FIRST_NAME = "first_name";
    public  static final String LAST_NAME ="last_name";
    public  static final String PHONE_NUM = "phone_num";
    public  static final String[] CONTACT_FIELDS = new String[] {
            KEY_ROWID,
            FIRST_NAME,
            LAST_NAME,
            PHONE_NUM
    };

    public  static final String CREATE_TABLE_CONTACT =
            "create table " + CONTACT_TABLE + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIRST_NAME + " text, "
                    + LAST_NAME + " text, "
                    + PHONE_NUM + " text "
                    + ");";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public  void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_CONTACT);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
            onCreate(db);
        }
    }

    // constructor
    public ContactDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public ContactDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public ContactDbAdapter upgrade() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        mDbHelper.onUpgrade(mDb, DATABASE_VERSION, DATABASE_VERSION + 1);
        return this;
    }

    public void  close() {
        if(mDbHelper != null) {
            mDbHelper.close();
        }
    }

    // adding deleting and updating data
    public  long insertContact(ContentValues initialValues) {
        return mDb.insertWithOnConflict(CONTACT_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean updateContact(int id, ContentValues newValues) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.update(CONTACT_TABLE, newValues, KEY_ROWID + "=?", selectionArgs)>0;
    }

    public boolean deleteContact(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.delete(CONTACT_TABLE, KEY_ROWID + "=?", selectionArgs) > 0;
    }

    public Cursor getContactById(int id) {
        return mDb.query(CONTACT_TABLE, CONTACT_FIELDS, KEY_ROWID + "=?",new String[] { String.valueOf(id) }, null, null, null);
    }
    
    public Cursor getContacts() {
        return mDb.query(CONTACT_TABLE, CONTACT_FIELDS, null, null, null, null, null);
    }

    public  int getContactSize() {
        Cursor cursor =  getContacts();
        return  cursor.getCount();
    }

    // insert with encodeing
    public  long insertContactWithEncoding(ContentValues initialValues) {
        return mDb.insertWithOnConflict(CONTACT_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }


    public  static Contact getContactFromCursor(Cursor cursor) {
        Contact contact = new Contact();
        contact.mFirstName = cursor.getString(cursor.getColumnIndex(FIRST_NAME));
        contact.mLastName = cursor.getString(cursor.getColumnIndex(LAST_NAME));
        contact.mPhoneNum = cursor.getString(cursor.getColumnIndex(PHONE_NUM));
        return contact;
    }


}
