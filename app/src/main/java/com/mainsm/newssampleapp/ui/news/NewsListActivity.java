package com.mainsm.newssampleapp.ui.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.mainsm.newssampleapp.R;
import com.mainsm.newssampleapp.adopters.NewsAdapter;
import com.mainsm.newssampleapp.di.viewmodel.ViewModelProviderFactory;
import com.mainsm.newssampleapp.models.NewsHeadlinesResponse;
import com.mainsm.newssampleapp.utils.Resource;

import java.text.MessageFormat;
import java.util.Objects;

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
    private String searchTxt = "";
    private FloatingActionButton fab;
    private TextView removeFilter;
    private TextView business, entertainment, general, health, science, sports, tech;
    private ImageView cancel;
    private ProgressBar progressBar;



    private String codeString;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        initToolbar();
        setRemoteConfig();
        initUi();
        viewModel = new ViewModelProvider(this, providerFactory).get(NewsViewModel.class);
        viewModel.getCountryCode(this);
    }

    private void initUi() {
        progressBar = findViewById(R.id.progressBar);
        removeFilter = findViewById(R.id.remove_filters);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openFilterDialog());

        removeFilter.setOnClickListener(view -> {
            getNews("", "", null);
            removeFilter.setVisibility(View.GONE);

        });
    }

    private void openFilterDialog() {
        Dialog dialog = new Dialog(NewsListActivity.this);
        dialog.setContentView(R.layout.filter_news_dialog);
        initDialogUI(dialog);
        business.setOnClickListener(view -> getNews("", "business", dialog));
        entertainment.setOnClickListener(view -> getNews("", "entertainment", dialog));
        general.setOnClickListener(view -> getNews("", "general", dialog));
        health.setOnClickListener(view -> getNews("", "health", dialog));
        science.setOnClickListener(view -> getNews("", "science", dialog));
        sports.setOnClickListener(view -> getNews("", "sports", dialog));
        tech.setOnClickListener(view -> getNews("", "technology", dialog));
        cancel.setOnClickListener(view -> dialog.dismiss());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setCancelable(false);
    }

    private void initDialogUI(Dialog dialog) {
        business = dialog.findViewById(R.id.business);
        entertainment = dialog.findViewById(R.id.entertainment);
        general = dialog.findViewById(R.id.general);
        health = dialog.findViewById(R.id.health);
        science = dialog.findViewById(R.id.science);
        sports = dialog.findViewById(R.id.sports);
        tech = dialog.findViewById(R.id.technology);
        cancel = dialog.findViewById(R.id.cancel);
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
        viewModel.observeNews(code, searchTxt, "").removeObserver( NewsListActivity.this );
        viewModel.observeNews(code, searchTxt, "").observe(NewsListActivity.this, newsHeadlinesResponseResource -> {
            if(newsHeadlinesResponseResource != null){
                switch (newsHeadlinesResponseResource.status){

                    case LOADING:{
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onChanged: LOADING...");
                        break;
                    }

                    case SUCCESS:{
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onChanged: got posts...");
                        if (newsHeadlinesResponseResource.data != null) {
                            adapter = new NewsAdapter(NewsListActivity.this, newsHeadlinesResponseResource.data, isEnabled);
                            setRv();
                        }else {
                            Toast.makeText(getApplicationContext(), "No NEWS available, try again later", Toast.LENGTH_LONG).show();

                        }
                        break;
                    }

                    case ERROR:{
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_LONG).show();
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
                getNews(s.trim(), "", null);
                Log.e(TAG, "onQueryTextSubmit: " + s );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e(TAG, "onQueryTextChange: "+ s );
                if(TextUtils.isEmpty(s))
                    getNews("", "", null);
                getNews(s.trim(), "", null);
                return false;
            }
        });
        return true;
    }

    private void getNews(String query, String category, Dialog dialog) {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.observeNews(codeString, query, category)
                .observe(NewsListActivity.this, newsHeadlinesResponseResource -> {
                    if(newsHeadlinesResponseResource != null){
                        switch (newsHeadlinesResponseResource.status){

                            case LOADING:{
                                progressBar.setVisibility(View.VISIBLE);
                                Log.d(TAG, "onChanged: LOADING...");
                                break;
                            }

                            case SUCCESS:{
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "onChanged: got posts...");
                                if (newsHeadlinesResponseResource.data != null) {
                                    adapter = new NewsAdapter(NewsListActivity.this, newsHeadlinesResponseResource.data, isEnabled);
                                    setRv();
                                    if(null != dialog) {
                                        dialog.dismiss();
                                        removeFilter.setText(MessageFormat.format("Remove Filter:\n{0}", category));
                                        removeFilter.setVisibility(View.VISIBLE);
                                    }else
                                        removeFilter.setVisibility(View.GONE);
                                }else {
                                    Toast.makeText(getApplicationContext(), "No Related NEWS available", Toast.LENGTH_LONG).show();
                                }
                                break;
                            }

                            case ERROR:{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Error Occurred", Toast.LENGTH_LONG).show();
                                Log.e(TAG, "onChanged: ERROR..." + newsHeadlinesResponseResource.message );
                                break;
                            }
                        }

                    }
                });
    }


}