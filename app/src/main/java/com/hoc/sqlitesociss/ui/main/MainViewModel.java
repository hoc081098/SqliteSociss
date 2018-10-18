package com.hoc.sqlitesociss.ui.main;

import android.util.Log;

import com.hoc.sqlitesociss.AppExecutor;
import com.hoc.sqlitesociss.SingleLiveEvent;
import com.hoc.sqlitesociss.data.ContactEntity;
import com.hoc.sqlitesociss.data.DatabaseContract;
import com.squareup.sqlbrite3.BriteDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Peter Hoc on 10/12/2018.
 */

public class MainViewModel extends ViewModel {
  private static final String QUERY_ALL_CONTACTS =
      "SELECT * FROM " + DatabaseContract.ContactEntry.TABLE_NAME +
          " WHERE " + DatabaseContract.ContactEntry.COLUMN_NAME_NAME + " LIKE ? OR " +
          DatabaseContract.ContactEntry.COLUMN_NAME_ADDRESS + " LIKE ?" +
          " ORDER BY " + DatabaseContract.ContactEntry.COLUMN_NAME_NAME + " ASC";


  @NonNull
  private final BriteDatabase db;
  @NonNull
  private final AppExecutor executor;

  private final LiveData<List<ContactEntity>> contactsLiveData;
  private final SingleLiveEvent<CharSequence> message = new SingleLiveEvent<>();

  private final BehaviorProcessor<String> searchStringProcessor = BehaviorProcessor.create();

  public MainViewModel(@NonNull BriteDatabase db, @NonNull AppExecutor executor) {
    this.db = db;
    this.executor = executor;
    contactsLiveData = LiveDataReactiveStreams.fromPublisher(
        searchStringProcessor
            .doOnNext(s -> Log.d("@@@", "onNext searchQuery='" + s + "'"))
            .switchMap(s ->
                db.createQuery(DatabaseContract.ContactEntry.TABLE_NAME, QUERY_ALL_CONTACTS,
                    "%" + s + "%", "%" + s + "%")
                    .mapToList(ContactEntity.MAPPER)
                    .subscribeOn(Schedulers.io())
                    .toFlowable(BackpressureStrategy.LATEST)
            )
    );
  }

  public LiveData<List<ContactEntity>> getContacts() {
    return contactsLiveData;
  }

  public void deleteAllContacts() {
    executor.getDiskIO().execute(() -> {
      final int rows = db.delete(DatabaseContract.ContactEntry.TABLE_NAME, null);
      if (rows > 0) {
        message.postValue("Delete all successfully");
      } else {
        message.postValue("Delete all failed");
      }
    });
  }

  public LiveData<CharSequence> getMessage() {
    return message;
  }

  public void searchContact(Flowable<String> searchString) {
    searchString.map(String::toLowerCase).subscribe(searchStringProcessor);
  }
}
