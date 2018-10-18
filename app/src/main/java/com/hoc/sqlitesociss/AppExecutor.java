package com.hoc.sqlitesociss;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;

/**
 * Created by Peter Hoc on 10/12/2018.
 */

@Singleton
public class AppExecutor {
  private final Executor mDiskIO;
  private final Executor mMainThread;

  private AppExecutor(Executor mDiskIO, Executor mMainThread) {
    this.mDiskIO = mDiskIO;
    this.mMainThread = mMainThread;
  }

  @Inject
  public AppExecutor() {
    this(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
  }

  public Executor getDiskIO() {
    return mDiskIO;
  }

  public Executor getMainThread() {
    return mMainThread;
  }

  private static class MainThreadExecutor implements Executor {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable command) {
      mainHandler.post(command);
    }
  }
}
