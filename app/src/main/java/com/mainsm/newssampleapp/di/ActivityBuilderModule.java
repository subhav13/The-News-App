package com.mainsm.newssampleapp.di;

import com.mainsm.newssampleapp.di.news.NewsModule;
import com.mainsm.newssampleapp.di.news.NewsViewModelModule;
import com.mainsm.newssampleapp.ui.news.NewsListActivity;
import com.mainsm.newssampleapp.ui.splash.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {
    @ContributesAndroidInjector(
            modules = {NewsViewModelModule.class,
                    NewsModule.class}
    )
    abstract NewsListActivity newsListActivity();

}
