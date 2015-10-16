package com.group6.thehub.Rest;

import com.group6.thehub.Rest.responses.BaseResponse;
import com.group6.thehub.Rest.responses.UserResponse;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Created by sathwiksingari on 9/17/15.
 */
public interface TheHubApi {

    @FormUrlEncoded
    @POST("/theHub/register.php")
    void registerUser(@Field("firstName") String firstName, @Field("lastName") String lastName, @Field("email") String email, @Field("password") String password, @Field("type") String type,
                      @Field("registeredOn") long time, Callback<UserResponse> response);

    @FormUrlEncoded
    @POST("/theHub/login.php")
    void loginUser(@Field("email") String email, @Field("password") String password, Callback<UserResponse> response);

    @Multipart
    @POST("/theHub/upload.php")
    void uploadImage(@Part("image") TypedFile image, @Part("userId") long userId,  Callback<UserResponse> response );

    @GET("/theHub/getDetails.php")
    void getUserDetails(@Query("id") int userId, Callback<UserResponse> resoponse);

    @GET("/theHub/getUserDetails.php")
    void getUserDetails(@QueryMap Map<String, String> params, Callback<UserResponse> resoponse);

}
