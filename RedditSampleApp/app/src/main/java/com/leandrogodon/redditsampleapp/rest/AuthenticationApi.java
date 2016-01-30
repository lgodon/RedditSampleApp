package com.leandrogodon.redditsampleapp.rest;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface AuthenticationApi {

    @FormUrlEncoded
    @POST("/access_token")
    AuthenticationResponse getAccessToken(@Field("grant_type") String grantType, @Field("device_id") String deviceId);
}
