package com.cse4471.osu.sos_osu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by xuweng on 3/29/16.
 */
// this is the adapter that serves as a bridge between view and model
public class UserDbAdapter {
    private static final String DATABASE_NAME = "SOS_DATABASE.db";
    private static final String USER_TABLE = "USER_TABLE";
    private static final int DATABASE_VERSION = 200;
    private final Context mCtx;
    public static String TAG = UserDbAdapter.class.getSimpleName();

    private DatabaseHelper mDbHelper;
    SQLiteDatabase mDb;

    public  static final String KEY_ROWID ="_id";
    public  static final String MESSAGE = "message";
    public  static final String[] USER_FIELDS = new String[] {
            KEY_ROWID,
            MESSAGE,
    };

    public  static final String CREATE_TABLE_USER =
            "create table " + USER_TABLE + " ("
                    + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MESSAGE + " text "
                    + ");";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public  void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_USER);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            onCreate(db);
        }
    }

    // constructor
    public UserDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public UserDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);

        mDb = mDbHelper.getWritableDatabase();
        //mDbHelper.onUpgrade(mDb, DATABASE_VERSION, mDb.getVersion());

        return this;
    }

    public UserDbAdapter upgrade() throws SQLException {
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
    public  long insertUser(ContentValues initialValues) {
        return mDb.insertWithOnConflict(USER_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean updateUser(int id, ContentValues newValues) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.update(USER_TABLE, newValues, KEY_ROWID + "=?", selectionArgs)>0;
    }

    public boolean deleteUser(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.delete(USER_TABLE, KEY_ROWID + "=?", selectionArgs) > 0;
    }

    /*
    public Cursor getContactById(int id) {
        return mDb.query(CONTACT_TABLE, CONTACT_FIELDS, KEY_ROWID + "=?",new String[] { String.valueOf(id) }, null, null, null);
    } */
    
    public Cursor getUsers() {
        return mDb.query(USER_TABLE, USER_FIELDS, null, null, null, null, null);
    }


    public  int getUserSize() {
        Cursor cursor =  getUsers();
        return  cursor.getCount();
    }

    public  static User getUserFromCursor(Cursor cursor) {
        User user = new User();
        user.mMessage = cursor.getString(cursor.getColumnIndex(MESSAGE));
        return user;
    }


}
