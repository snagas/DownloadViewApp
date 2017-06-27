package com.example.lenovot550.downloadviewapp.rest;

import com.example.lenovot550.downloadviewapp.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Lenovo T550 on 23/06/2017.
 */

public interface ApiInterface {
    // @GET surasomi API parametrai kuriais buildins URL :?
    @GET("articles?source=techcrunch")
    Call<ArticleResponse> getStatusAndSource(@Query("apiKey") String apiKey);
}
