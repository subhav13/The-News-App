package com.mainsm.newssampleapp.ui.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mainsm.newssampleapp.R;
import com.mainsm.newssampleapp.adopters.NewsAdapter;
import com.mainsm.newssampleapp.di.viewmodel.ViewModelProviderFactory;
import com.mainsm.newssampleapp.models.NewsHeadlinesResponse;
import com.mainsm.newssampleapp.utils.Resource;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class NewsListActivity extends DaggerAppCompatActivity implements Observer<Resource<NewsHeadlinesResponse>>, NewsListener {

    private static final String TAG = NewsListActivity.class.getSimpleName() ;
    @Inject
    ViewModelProviderFactory providerFactory;
    private boolean isEnabled;
    private static String ADS_ENABLED = "native_ads_enabled";
    private NewsViewModel viewModel;
    private RecyclerView newsRv;
    private NewsAdapter adapter;
    private String searchTxt;


    private String codeString;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        initToolbar();
        setRemoteConfig();
        viewModel = new ViewModelProvider(this, providerFactory).get(NewsViewModel.class);
        viewModel.getCountryCode(this);
    }

    private void setRemoteConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(120)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        
        fetchAndActivate();
    }

    private void fetchAndActivate() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        Toast.makeText(getApplicationContext(), "Fetch Successful " + task, Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Fetch Failed " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
        isEnabled = mFirebaseRemoteConfig.getBoolean(ADS_ENABLED);
        Log.e(TAG, "fetchAndActivate: " + isEnabled );
    }


    private void setRv() {
        newsRv = findViewById(R.id.news_rv);
        newsRv.setLayoutManager(new LinearLayoutManager(NewsListActivity.this));
        newsRv.setAdapter(adapter);
    }

    @Override
    public void onChanged(Resource<NewsHeadlinesResponse> newsHeadlinesResponseResource) {}

    @Override
    public void setCode(String code) {
        codeString = code;
        searchTxt = "";
        viewModel.observeNews(code, searchTxt).removeObserver( NewsListActivity.this );
        viewModel.observeNews(code, searchTxt).observe(NewsListActivity.this, newsHeadlinesResponseResource -> {
            if(newsHeadlinesResponseResource != null){
                switch (newsHeadlinesResponseResource.status){

                    case LOADING:{
                        Log.d(TAG, "onChanged: LOADING...");
                        break;
                    }

                    case SUCCESS:{
                        Log.d(TAG, "onChanged: got posts...");
                        if (newsHeadlinesResponseResource.data != null) {
                            adapter = new NewsAdapter(NewsListActivity.this, newsHeadlinesResponseResource.data, isEnabled);
                            setRv();
                        }
                        break;
                    }

                    case ERROR:{
                        Log.e(TAG, "onChanged: ERROR..." + newsHeadlinesResponseResource.message );
                        break;
                    }
                }

            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        toolbar.inflateMenu(R.menu.menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search_post)
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem search = menu.findItem(R.id.search_post);
        SearchView searchView = (SearchView) search.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getNews(s.trim());
                Log.e(TAG, "onQueryTextSubmit: " + s );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e(TAG, "onQueryTextChange: "+ s );
                if(TextUtils.isEmpty(s))
                    getNews("");
                getNews(s.trim());
                return false;
            }
        });
        return true;
    }

    private void getNews(String query) {
        viewModel.observeNews(codeString, query)
                .observe(NewsListActivity.this, newsHeadlinesResponseResource -> {
                    if(newsHeadlinesResponseResource != null){
                        switch (newsHeadlinesResponseResource.status){

                            case LOADING:{
                                Log.d(TAG, "onChanged: LOADING...");
                                break;
                            }

                            case SUCCESS:{
                                Log.d(TAG, "onChanged: got posts...");
                                if (newsHeadlinesResponseResource.data != null) {
                                    adapter = new NewsAdapter(NewsListActivity.this, newsHeadlinesResponseResource.data, isEnabled);
                                    setRv();
                                }
                                break;
                            }

                            case ERROR:{
                                Log.e(TAG, "onChanged: ERROR..." + newsHeadlinesResponseResource.message );
                                break;
                            }
                        }

                    }
                });
    }


}