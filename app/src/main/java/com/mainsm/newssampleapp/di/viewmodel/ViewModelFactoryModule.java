package com.mainsm.newssampleapp.di.viewmodel;

import androidx.lifecycle.ViewModelProvider;

import com.mainsm.newssampleapp.di.viewmodel.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory modelProviderFactory);

}
