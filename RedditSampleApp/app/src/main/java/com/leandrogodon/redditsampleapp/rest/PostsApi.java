package com.leandrogodon.redditsampleapp.rest;

import com.leandrogodon.redditsampleapp.model.Listing;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface PostsApi {

    @GET("/top")
    Observable<Listing> getTopPosts(@Query("t") String period, @Query("limit") int limit);

    @GET("/top")
    Observable<Listing> getTopPostsAfter(@Query("t") String period, @Query("limit") int limit, @Query("after") String after);
}
