package com.mainsm.newssampleapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.mainsm.newssampleapp.R;
import com.mainsm.newssampleapp.di.viewmodel.ViewModelProviderFactory;
import com.mainsm.newssampleapp.models.NewsHeadlinesResponse;
import com.mainsm.newssampleapp.ui.news.NewsListActivity;
import com.mainsm.newssampleapp.ui.news.NewsViewModel;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        new Handler().postDelayed(() -> {
            startActivity(new Intent(MainActivity.this, NewsListActivity.class));
            finish();
        }, 1500);

    }
}