package com.mainsm.newssampleapp.ui.news;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.mainsm.newssampleapp.models.CountryDataResponse;
import com.mainsm.newssampleapp.models.NewsHeadlinesResponse;
import com.mainsm.newssampleapp.utils.Constants;
import com.mainsm.newssampleapp.utils.Resource;
import com.mainsm.newssampleapp.utils.network.news.NewsApi;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsViewModel extends ViewModel {
    private String countryCode = null;
    private static final String TAG = NewsViewModel.class.getSimpleName() ;
    private final NewsApi api;
    private MediatorLiveData<Resource<NewsHeadlinesResponse>> mNewRes = new MediatorLiveData<>();
    @Inject
    public NewsViewModel(NewsApi newsApi) {
        this.api = newsApi;
        Log.e(TAG, "NewsViewModel: " + "VIEW MODEL RUNNING" );
        newsApi.getNewsHeadline("us", Constants.API_KEY, "", "")
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<NewsHeadlinesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsHeadlinesResponse newsHeadlinesResponse) {
                        Log.e(TAG, "onNext:CHECK " + newsHeadlinesResponse.getTotalResults() );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError:CHECK " + e.getMessage() );
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
    public LiveData<Resource<NewsHeadlinesResponse>> observeNews(String country, String search, String category){
        if(mNewRes == null){
            mNewRes = new MediatorLiveData<>();
            mNewRes.setValue(Resource.loading(null));
        }
        Log.e(TAG, "observeNews: " + country);
        final LiveData<Resource<?>> source = LiveDataReactiveStreams.fromPublisher(
                api.getNewsHeadline(country, Constants.API_KEY, search, category)
                .onErrorReturn(throwable -> {
                    NewsHeadlinesResponse response = new NewsHeadlinesResponse();
                    response.setTotalResults(-1);
                    Log.e(TAG, "observeNews: " + throwable.getMessage() );
                    return response;
                })
              .map(newsHeadlinesResponse -> {
                      if(newsHeadlinesResponse.getTotalResults() == -1)
                          return Resource.error("Something went wrong", null);
                  Log.e(TAG, "observeNews: " + newsHeadlinesResponse.getTotalResults() );

                  return Resource.success(newsHeadlinesResponse);
              })
                .subscribeOn(Schedulers.io())
        );


        mNewRes.addSource(source, resource -> {
            mNewRes.setValue((Resource<NewsHeadlinesResponse>) resource);
            mNewRes.removeSource(source);
        });

        return mNewRes;
    }


    public void getCountryCode( NewsListener listener){
        String url = "http://ip-api.com/json/";
        api.getCountry(url).enqueue(new Callback<CountryDataResponse>() {
            @Override
            public void onResponse(Call<CountryDataResponse> call, Response<CountryDataResponse> response) {

                if(response.isSuccessful() && null != response.body()){
                    countryCode = response.body().getCountryCode().toLowerCase();
                    Log.e(TAG, "onResponse: " + countryCode );
                    if(null != listener)
                        listener.setCode(countryCode);



                }else {
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "onResponse: ERROR"+ response.errorBody().string() );
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<CountryDataResponse> call, Throwable t) {

                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }



}
