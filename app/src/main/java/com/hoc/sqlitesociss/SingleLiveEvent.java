package com.hoc.sqlitesociss;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Created by Peter Hoc on 10/11/2018.
 */

public class SingleLiveEvent<T> extends MutableLiveData<T> {
  private static final String TAG = "SingleLiveEvent";
  private final AtomicBoolean mPending = new AtomicBoolean(false);

  @MainThread
  @Override
  public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
    if (hasActiveObservers()) {
      Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
    }

    // Observe the internal MutableLiveData
    super.observe(owner, t -> {
      if (mPending.compareAndSet(true, false)) {
        observer.onChanged(t);
      }
    });
  }

  @MainThread
  public void setValue(@Nullable T t) {
    mPending.set(true);
    super.setValue(t);
  }

  @Override
  public void postValue(T value) {
    mPending.set(true);
    super.postValue(value);
  }

  /**
   * Used for cases where T is Void, to make calls cleaner.
   */
  @MainThread
  public void call() {
    setValue(null);
  }
}