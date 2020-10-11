package com.mainsm.newssampleapp.di.news;

import androidx.lifecycle.ViewModel;

import com.mainsm.newssampleapp.di.viewmodel.ViewModelKey;
import com.mainsm.newssampleapp.ui.news.NewsViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class NewsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel.class)
    public abstract ViewModel bindNewsViewModel(NewsViewModel newsViewModel);
}
