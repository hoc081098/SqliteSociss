package com.hoc.sqlitesociss.di;

import android.app.Application;
import android.util.Log;

import com.hoc.sqlitesociss.data.DbCallback;
import com.squareup.sqlbrite3.BriteDatabase;
import com.squareup.sqlbrite3.SqlBrite;

import javax.inject.Singleton;

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Peter Hoc on 10/11/2018.
 */

@Module
public class DbModule {
  @Provides
  @Singleton
  SqlBrite provideSqlBrite() {
    return new SqlBrite.Builder().logger(message -> Log.d("@@@", message)).build();
  }

  @Provides
  @Singleton
  BriteDatabase provideDatabase(SqlBrite sqlBrite, Application application) {
    final SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration
        .builder(application)
        .name(DbCallback.DATABASE_NAME)
        .callback(new DbCallback())
        .build();
    SupportSQLiteOpenHelper helper = new FrameworkSQLiteOpenHelperFactory()
        .create(configuration);
    final BriteDatabase briteDatabase = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
    briteDatabase.setLoggingEnabled(true);
    return briteDatabase;
  }
}
