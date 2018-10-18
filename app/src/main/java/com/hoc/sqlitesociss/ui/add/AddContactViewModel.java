package com.hoc.sqlitesociss.ui.add;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.hoc.sqlitesociss.AppExecutor;
import com.hoc.sqlitesociss.R;
import com.hoc.sqlitesociss.SingleLiveEvent;
import com.hoc.sqlitesociss.data.DatabaseContract;
import com.squareup.sqlbrite3.BriteDatabase;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Peter Hoc on 10/12/2018.
 */

public class AddContactViewModel extends ViewModel {
  @NonNull
  private final BriteDatabase db;
  @NonNull
  private final AppExecutor executor;

  private final SingleLiveEvent<Pair<CharSequence, Boolean>> mMessage = new SingleLiveEvent<>();

  public AddContactViewModel(@NonNull BriteDatabase db, @NonNull AppExecutor executor) {
    this.db = db;
    this.executor = executor;
  }

  void addContact(String name, String phone, String address, int checkedRadioButtonId) {
    if (address.isEmpty() || name.isEmpty() || phone.isEmpty() || checkedRadioButtonId == -1) {
      mMessage.setValue(new Pair<>("Please fill in full information", false));
      return;
    }

    executor.getDiskIO().execute(() -> {
      final ContentValues values = new ContentValues();
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_ADDRESS, address);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_MALE, checkedRadioButtonId == R.id.radio_male ? 1 : 0);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, name);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_PHONE, phone);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_CREATED_AT, System.currentTimeMillis());
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_UPDATED_AT, (Long) null);

      final long rowId = db.insert(DatabaseContract.ContactEntry.TABLE_NAME, SQLiteDatabase.CONFLICT_NONE, values);
      if (rowId != -1) {
        mMessage.postValue(new Pair<>("Add successfully", true));
      } else {
        mMessage.postValue(new Pair<>("Add failed", false));
      }
    });
  }

  public LiveData<Pair<CharSequence, Boolean>> getMessage() {
    return mMessage;
  }

  void updateContact(long id, String name, String phone, String address, int checkedRadioButtonId) {
    if (address.isEmpty() || name.isEmpty() || phone.isEmpty() || checkedRadioButtonId == -1) {
      mMessage.setValue(new Pair<>("Please fill in full information", false));
      return;
    }

    executor.getDiskIO().execute(() -> {
      final ContentValues values = new ContentValues();
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_ADDRESS, address);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_MALE, checkedRadioButtonId == R.id.radio_male ? 1 : 0);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, name);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_PHONE, phone);
      values.put(DatabaseContract.ContactEntry.COLUMN_NAME_UPDATED_AT, System.currentTimeMillis());

      final long rows = db.update(DatabaseContract.ContactEntry.TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, values,
          DatabaseContract.ContactEntry._ID + " = ?", String.valueOf(id));

      if (rows > 0) {
        mMessage.postValue(new Pair<>("Update successfully", true));
      } else {
        mMessage.postValue(new Pair<>("Update failed", false));
      }
    });
  }
}
