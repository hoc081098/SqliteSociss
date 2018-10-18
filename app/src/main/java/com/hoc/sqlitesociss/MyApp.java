package com.hoc.sqlitesociss;

import android.app.Application;
import android.content.Context;

import com.hoc.sqlitesociss.di.AppComponent;
import com.hoc.sqlitesociss.di.AppModule;
import com.hoc.sqlitesociss.di.DaggerAppComponent;
import com.hoc.sqlitesociss.di.DbModule;

/**
 * Created by Peter Hoc on 10/11/2018.
 */

public final class MyApp extends Application {
  private AppComponent appComponent;

  public static AppComponent getAppComponent(Context context) {
    return ((MyApp) context.getApplicationContext()).appComponent;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    appComponent = DaggerAppComponent.builder()
        .appModule(new AppModule(this))
        .dbModule(new DbModule())
        .build();
  }
}
