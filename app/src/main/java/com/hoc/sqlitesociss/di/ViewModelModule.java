package com.hoc.sqlitesociss.di;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;

/**
 * Created by Peter Hoc on 10/12/2018.
 */

@Module
public abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory provideViewModelFactory(ViewModelFactory factory);
}
