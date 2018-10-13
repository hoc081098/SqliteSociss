package com.hoc.sqlitesociss.ui.detail;

import com.hoc.sqlitesociss.AppExecutor;
import com.hoc.sqlitesociss.data.ContactEntity;
import com.hoc.sqlitesociss.data.DatabaseContract;
import com.squareup.sqlbrite3.BriteDatabase;

import org.reactivestreams.Publisher;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import io.reactivex.BackpressureStrategy;

/**
 * Created by Peter Hoc on 10/13/2018.
 */
public class DetailViewModel extends ViewModel {
    private static final String QUERY_CONTACT_BY_ID = String.format("SELECT * FROM %s WHERE %s = ? LIMIT 1", DatabaseContract.ContactEntry.TABLE_NAME, DatabaseContract.ContactEntry._ID);

    @NonNull
    private final BriteDatabase db;
    @NonNull
    private final AppExecutor executor;

    private final MutableLiveData<Long> contactId = new MutableLiveData<>();
    private final LiveData<ContactEntity> contactEntityLiveData;
    private final MutableLiveData<Pair<CharSequence, Boolean>> messageAndFinish = new MutableLiveData<>();

    public DetailViewModel(@NonNull BriteDatabase db, @NonNull AppExecutor executor) {
        this.db = db;

        contactEntityLiveData = Transformations.switchMap(contactId, id -> {
            final Publisher<ContactEntity> flowable = db.createQuery(DatabaseContract.ContactEntry.TABLE_NAME, QUERY_CONTACT_BY_ID, id)
                    .mapToOne(ContactEntity.MAPPER)
                    .toFlowable(BackpressureStrategy.LATEST);
            return LiveDataReactiveStreams.fromPublisher(flowable);
        });
        this.executor = executor;
    }

    public LiveData<ContactEntity> getContact() {
        return contactEntityLiveData;
    }

    public void setContactId(long id) {
        contactId.setValue(id);
    }

    public void deleteContact() {
        final Long id = contactId.getValue();
        if (id != null) {
            executor.getDiskIO().execute(() -> {
                final int rows = db.delete(DatabaseContract.ContactEntry.TABLE_NAME, DatabaseContract.ContactEntry._ID + " = ?", id.toString());

                if (rows > 0) {
                    messageAndFinish.postValue(new Pair<>("Delete successfully", true));
                } else {
                    messageAndFinish.postValue(new Pair<>("Delete failed", false));
                }
            });
        }
    }

    public LiveData<Pair<CharSequence, Boolean>> getMessageAndFinish() {
        return messageAndFinish;
    }
}