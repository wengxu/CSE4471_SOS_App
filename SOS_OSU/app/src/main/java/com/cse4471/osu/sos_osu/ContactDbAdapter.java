package com.cse4471.osu.sos_osu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.Key;
import java.sql.RowId;
import java.sql.SQLException;

/**
 * Created by xuweng on 3/29/16.
 */
// this is the adapter that serves as a bridge between view and model
// it is used as middleman to access database.
public class ContactDbAdapter {
    private static final String DATABASE_NAME = "SOS_DATABASE.db";
    private static final String CONTACT_TABLE = "CONTACT_TABLE";
    private static final int DATABASE_VERSION = 200;
    private final Context mCtx;
    public static String TAG = ContactDbAdapter.class.getSimpleName();
    public static int password = 1;

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
            "create table if not exists " + CONTACT_TABLE + " ("
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

        if(!mCtx.getDatabasePath(DATABASE_NAME + ".db").exists()) {
            mDbHelper.onCreate(mDb);
        }
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
        //return mDb.insertWithOnConflict(CONTACT_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        return mDb.insertWithOnConflict(CONTACT_TABLE, null, encodeContentValues(initialValues), SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean updateContact(int id, ContentValues newValues) {
        String[] selectionArgs = {String.valueOf(id)};
        //return mDb.update(CONTACT_TABLE, newValues, KEY_ROWID + "=?", selectionArgs)>0;
        return mDb.update(CONTACT_TABLE, encodeContentValues(newValues), KEY_ROWID + "=?", selectionArgs)>0;
    }

    public boolean deleteContact(int id) {
        String[] selectionArgs = {String.valueOf(id)};
        return mDb.delete(CONTACT_TABLE, KEY_ROWID + "=?", selectionArgs) > 0;
    }

    public Cursor getContactById(int id) {
        //return mDb.query(CONTACT_TABLE, CONTACT_FIELDS, KEY_ROWID + "=?",new String[] { String.valueOf(id) }, null, null, null);
        MatrixCursor matrixCursor= new MatrixCursor(CONTACT_FIELDS);
        Cursor dbCursor =mDb.query(CONTACT_TABLE, CONTACT_FIELDS, KEY_ROWID + "=?",new String[] { String.valueOf(id) }, null, null, null);
        if (dbCursor.moveToFirst()) {
            Contact contact = getContactFromCursor(dbCursor);
            int key_rowid =  Integer.parseInt(dbCursor.getString(dbCursor.getColumnIndex(KEY_ROWID))) ;
            matrixCursor.addRow(new Object[] {key_rowid, contact.mFirstName,  contact.mLastName, contact.mPhoneNum});
        }
        return matrixCursor;
    }
    
    public Cursor getContacts() {
        //return mDb.query(CONTACT_TABLE, CONTACT_FIELDS, null, null, null, null, null);

        MatrixCursor matrixCursor= new MatrixCursor(CONTACT_FIELDS);
        // startManagingCursor(matrixCursor);
        Cursor dbCursor = mDb.query(CONTACT_TABLE, CONTACT_FIELDS, null, null, null, null, null);
        if (dbCursor.moveToFirst()) {
            do {
                Contact contact = getContactFromCursor(dbCursor);
                int key_rowid =  Integer.parseInt(dbCursor.getString(dbCursor.getColumnIndex(KEY_ROWID))) ;
                matrixCursor.addRow(new Object[] {key_rowid, contact.mFirstName,  contact.mLastName, contact.mPhoneNum});
            } while (dbCursor.moveToNext());
        }

        return matrixCursor;
    }

    public  int getContactSize() {
        Cursor cursor =  getContacts();
        return  cursor.getCount();
    }

    // insert with encodeing
    public  long insertContactWithEncoding(ContentValues initialValues) {
        return mDb.insertWithOnConflict(CONTACT_TABLE, null, encodeContentValues(initialValues), SQLiteDatabase.CONFLICT_IGNORE);
    }

    // get content values from cursor
    public  ContentValues getContentValuesfromCursor(Cursor cursor) {
        ContentValues contentValues = new ContentValues();
        String name = cursor.getString(cursor.getColumnIndex(FIRST_NAME));
        String phoneNum = cursor.getString(cursor.getColumnIndex(PHONE_NUM));
        contentValues.put(FIRST_NAME, shiftString(name, 0-password) );
        contentValues.put(LAST_NAME, "" );
        contentValues.put(PHONE_NUM, shiftString(phoneNum , 0-password));
        return contentValues;
    }


    public   Contact getContactFromCursor(Cursor cursor) {
        Contact contact = new Contact();
        contact.mFirstName = cursor.getString(cursor.getColumnIndex(FIRST_NAME));
        contact.mLastName = cursor.getString(cursor.getColumnIndex(LAST_NAME));
        contact.mPhoneNum = cursor.getString(cursor.getColumnIndex(PHONE_NUM));
        // decode
        contact.mFirstName = shiftString(contact.mFirstName, 0-password);
        contact.mPhoneNum = shiftString(contact.mPhoneNum, 0-password);
        return contact;
    }

    public ContentValues encodeContentValues(ContentValues contentValues) {
        ContentValues encodedContentValues = new ContentValues();
        for (String keypart : contentValues.keySet()) {
            Log.d("keypart is " , keypart);
            encodedContentValues.put(keypart, shiftString(contentValues.getAsString(keypart), password));
        }
        return encodedContentValues;
    }

    // shift the string by shift amount
    public  String shiftString(String s, int shift) {
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            result += shiftChar(s.charAt(i), shift);
        }
        return  result;
    }

    // this function shifts the char by shift amount
    public char shiftChar(char c, int shift) {
        // check char Ansi
        int ansiInt = (int) c;
        char result = c;
        // if numbers
        if (ansiInt >= 48 && ansiInt <= 57) {
            result = (char) modOperation(ansiInt, 48, 57, shift);
        }
        // if A-Z
        else if (ansiInt >= 65 && ansiInt <= 90) {
            result = (char) modOperation(ansiInt, 65, 90, shift);
        }
        // if a-z
        else if (ansiInt >= 97 && ansiInt <= 122) {
            result = (char) modOperation(ansiInt, 97, 122, shift);
        }

        return result;
    }
    // this function use modulo to move numbers within range
    public int modOperation(int current, int start, int end, int shift) {
        int baseCurrent = current - start;
        int mod = end  - start + 1;
        int baseResult = (((baseCurrent + shift) % mod ) + mod ) % mod;
        return baseResult + start;
    }


}
