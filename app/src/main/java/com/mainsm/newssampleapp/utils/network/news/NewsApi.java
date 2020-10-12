package com.mainsm.newssampleapp.utils.network.news;

import com.mainsm.newssampleapp.models.CountryDataResponse;
import com.mainsm.newssampleapp.models.NewsHeadlinesResponse;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface NewsApi {

    @GET("top-headlines")
    Flowable<NewsHeadlinesResponse> getNewsHeadline(
            @Query("country") String country,
            @Query("apiKey") String api,
            @Query("q") String searchQuery,
            @Query("category") String category
    );

    @GET
    Call<CountryDataResponse> getCountry (@Url String url);

}
