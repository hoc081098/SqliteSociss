package com.hoc.sqlitesociss.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.functions.Function;

import static android.provider.BaseColumns._ID;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.COLUMN_NAME_ADDRESS;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.COLUMN_NAME_CREATED_AT;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.COLUMN_NAME_MALE;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.COLUMN_NAME_NAME;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.COLUMN_NAME_PHONE;
import static com.hoc.sqlitesociss.data.DatabaseContract.ContactEntry.COLUMN_NAME_UPDATED_AT;

/**
 * Created by Peter Hoc on 10/11/2018.
 */

public class ContactEntity implements Parcelable {
    private final long id;

    @NonNull
    private final String name;

    @NonNull
    private final String phone;

    @NonNull
    private final String address;

    private final boolean male;

    @NonNull
    private final Date createdAt;

    @Nullable
    private final Date updatedAt;

    public ContactEntity(long id, @NonNull String name, @NonNull String phone, @NonNull String address, boolean male, @NonNull Date createdAt, @Nullable Date updatedAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.male = male;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    protected ContactEntity(Parcel in) {
        id = in.readLong();
        name = in.readString();
        phone = in.readString();
        address = in.readString();
        male = in.readByte() != 0;
        createdAt = new Date(in.readLong());
        final long l = in.readLong();
        updatedAt = l == -1 ? null : new Date(l);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeByte((byte) (male ? 1 : 0));
        dest.writeLong(createdAt.getTime());
        dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContactEntity> CREATOR = new Creator<ContactEntity>() {
        @Override
        public ContactEntity createFromParcel(Parcel in) {
            return new ContactEntity(in);
        }

        @Override
        public ContactEntity[] newArray(int size) {
            return new ContactEntity[size];
        }
    };

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getPhone() {
        return phone;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public boolean isMale() {
        return male;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactEntity that = (ContactEntity) o;
        return id == that.id &&
                male == that.male &&
                Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(address, that.address) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone, address, male, createdAt, updatedAt);
    }

    public static final Function<Cursor, ContactEntity> MAPPER = cursor -> {
        final long id = cursor.getLong(cursor.getColumnIndex(_ID));
        final String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NAME));
        final String phone = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE));
        final String address = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ADDRESS));
        final int male = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_MALE));
        final Date createdAt = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_CREATED_AT)));
        final int columnUpdatedAtIndex = cursor.getColumnIndex(COLUMN_NAME_UPDATED_AT);
        final Date updatedAt = cursor.isNull(columnUpdatedAtIndex)
                ? null
                : new Date(cursor.getLong(columnUpdatedAtIndex));
        ;
        return new ContactEntity(id, name, phone, address, male == 1, createdAt, updatedAt);
    };
}
