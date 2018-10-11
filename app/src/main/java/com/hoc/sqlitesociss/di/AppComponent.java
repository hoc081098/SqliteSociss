package com.hoc.sqlitesociss.di;

import com.hoc.sqlitesociss.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Peter Hoc on 10/11/2018.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(MainActivity mainActivity);

}
