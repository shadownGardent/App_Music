package com.example.app_music.apiservice;

import com.example.app_music.service.model.SongList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    // https://thantrieu.com/resources/braniumapis/song.json

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://thantrieu.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("resources/braniumapis/song.json")
    Call<SongList> getListSong();
}
