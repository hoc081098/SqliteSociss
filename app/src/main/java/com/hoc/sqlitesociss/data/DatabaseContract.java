package com.hoc.sqlitesociss.data;

import android.provider.BaseColumns;

/**
 * Created by Peter Hoc on 10/11/2018.
 */

public class DatabaseContract {

  private DatabaseContract() {
  }

  public static final class ContactEntry implements BaseColumns {
    public static final String TABLE_NAME = "contacts";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_PHONE = "phone";
    public static final String COLUMN_NAME_ADDRESS = "address";
    public static final String COLUMN_NAME_MALE = "male";
    public static final String COLUMN_NAME_CREATED_AT = "created_at";
    public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
  }
}
