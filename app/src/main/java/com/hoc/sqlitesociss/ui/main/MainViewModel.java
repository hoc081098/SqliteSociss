package com.hoc.sqlitesociss.ui.main;

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

/**
 * Created by Peter Hoc on 10/12/2018.
 */

public class MainViewModel extends ViewModel {
    private static final String QUERY_ALL_CONTACTS = "SELECT * FROM " + DatabaseContract.ContactEntry.TABLE_NAME;

    @NonNull
    private final BriteDatabase db;
    @NonNull
    private final AppExecutor executor;

    private final LiveData<List<ContactEntity>> contactsLiveData;
    private final SingleLiveEvent<CharSequence> message = new SingleLiveEvent<>();

    public MainViewModel(@NonNull BriteDatabase db, @NonNull AppExecutor executor) {
        this.db = db;
        this.executor = executor;
        contactsLiveData = LiveDataReactiveStreams.fromPublisher(
                this.db.createQuery(DatabaseContract.ContactEntry.TABLE_NAME, QUERY_ALL_CONTACTS)
                        .mapToList(ContactEntity.MAPPER)
                        .toFlowable(BackpressureStrategy.LATEST)
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
}
