package com.hoc.sqlitesociss.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

/**
 * Created by Peter Hoc on 10/11/2018.
 */
public class DbCallback extends SupportSQLiteOpenHelper.Callback {
    public static final String DATABASE_NAME = "app_db.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            String.format(
                    "CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL," +
                            " %s TEXT NOT NULL, %s INTEGER NOT NULL DEFAULT 0, %s INTEGER NOT NULL, %s INTEGER)",
                    DatabaseContract.ContactEntry.TABLE_NAME,
                    DatabaseContract.ContactEntry._ID,
                    DatabaseContract.ContactEntry.COLUMN_NAME_NAME,
                    DatabaseContract.ContactEntry.COLUMN_NAME_PHONE,
                    DatabaseContract.ContactEntry.COLUMN_NAME_ADDRESS,
                    DatabaseContract.ContactEntry.COLUMN_NAME_MALE,
                    DatabaseContract.ContactEntry.COLUMN_NAME_CREATED_AT,
                    DatabaseContract.ContactEntry.COLUMN_NAME_UPDATED_AT
            );
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DatabaseContract.ContactEntry.TABLE_NAME;

    public DbCallback() {
        super(DATABASE_VERSION);
    }

    @Override
    public void onCreate(SupportSQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        addData(db);
    }

    private void addData(SupportSQLiteDatabase db) {
        for (int i = 0; i < 100; i++) {
            final char ch = (char) ('a' + i);
            final String name = "Name " + String.valueOf(ch);
            final String address = "Address " + String.valueOf(ch);
            final StringBuilder phone = new StringBuilder();
            for (int j = 0; j < 10; j++) phone.append(i % 10);
            final int isMale = i % 2;

            final ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, name);
            contentValues.put(DatabaseContract.ContactEntry.COLUMN_NAME_ADDRESS, address);
            contentValues.put(DatabaseContract.ContactEntry.COLUMN_NAME_PHONE, phone.toString());
            contentValues.put(DatabaseContract.ContactEntry.COLUMN_NAME_MALE, isMale);
            contentValues.put(DatabaseContract.ContactEntry.COLUMN_NAME_CREATED_AT, Calendar.getInstance().getTimeInMillis());
            db.insert(DatabaseContract.ContactEntry.TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, contentValues);
        }
    }

    @Override
    public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
