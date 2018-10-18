package com.hoc.sqlitesociss.di;

import com.hoc.sqlitesociss.AppExecutor;
import com.hoc.sqlitesociss.ui.add.AddContactViewModel;
import com.hoc.sqlitesociss.ui.detail.DetailViewModel;
import com.hoc.sqlitesociss.ui.main.MainViewModel;
import com.squareup.sqlbrite3.BriteDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by Peter Hoc on 10/12/2018.
 */

@Singleton
public class ViewModelFactory implements ViewModelProvider.Factory {
  private final BriteDatabase db;
  private final AppExecutor executor;

  @Inject
  public ViewModelFactory(BriteDatabase db, AppExecutor executor) {
    this.db = db;
    this.executor = executor;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(MainViewModel.class)) {
      return (T) new MainViewModel(db, executor);
    }
    if (modelClass.isAssignableFrom(AddContactViewModel.class)) {
      return (T) new AddContactViewModel(db, executor);
    }
    if (modelClass.isAssignableFrom(DetailViewModel.class)) {
      return (T) new DetailViewModel(db, executor);
    }
    throw new IllegalStateException("Don't know ViewModel");
  }
}
