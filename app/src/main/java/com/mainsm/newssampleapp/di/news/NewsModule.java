package com.mainsm.newssampleapp.di.news;

import android.app.Application;

import com.mainsm.newssampleapp.adopters.NewsAdapter;
import com.mainsm.newssampleapp.utils.network.news.NewsApi;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class NewsModule {
    @Provides
    static NewsApi provideNewsApi(Retrofit retrofit){
        return retrofit.create(NewsApi.class);
    }

    @Provides
    static NewsAdapter provideAdapter(){
        return new NewsAdapter();
    }
}
