package com.group6.thehub.Rest;

import com.group6.thehub.Rest.models.UserSearchDetails;
import com.group6.thehub.Rest.responses.BaseResponse;
import com.group6.thehub.Rest.responses.CourseResponse;
import com.group6.thehub.Rest.responses.FavoritesResponse;
import com.group6.thehub.Rest.responses.LangaugesResponse;
import com.group6.thehub.Rest.responses.LocationResponse;
import com.group6.thehub.Rest.responses.UserResponse;
import com.group6.thehub.Rest.responses.UserSearchResponse;
import com.squareup.okhttp.Call;

import java.util.List;
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
    void getUserDetails(@Query("id") int id, @Query("userId") int userId, Callback<UserResponse> resoponse);

    @GET("/theHub/getUserDetails.php")
    void getUserDetailsFromCourse(@Query("coursecode") String courseCode, @Query("type") String type, Callback<UserSearchResponse> resoponse);

    @FormUrlEncoded
    @POST("/theHub/updateUserDetails.php")
    void updateUserDetails(@Field("userId") int userId, @Field("phone") String phone, @Field("qualification") String qualification,
                           @Field("langs[]")List<String> addedLanguages, @Field("delLangs[]")List<String> deletedLanguages,
                           @Field("courses[]")List<String> addedCourses, @Field("delCourses[]")List<String> deletedCourses,
                           Callback<UserResponse> response);

    @GET("/theHub/loadLanguages.php")
    void loadLangauges(Callback<LangaugesResponse> response);

    @GET("/theHub/loadCourses.php")
    void loadCourses(Callback<CourseResponse> response);

    @FormUrlEncoded
    @POST("/theHub/favorites.php")
    void favorites(@Field("tutorId") int tutorId, @Field("studentId") int studentId, @Field("ACTION") String action, Callback<FavoritesResponse> response);

    @GET("/theHub/favorites.php")
    void getFavorites(@Query("studentId") int studentId, Callback<FavoritesResponse> response);

    @GET("/theHub/getLocations.php")
    void getLocations(Callback<LocationResponse> response);

    @FormUrlEncoded
    @POST("/theHub/sessions.php")
    void createSession(@Field("studentId") int studentId, @Field("tutorId") int tutorId, @Field("locationId") int locationId, @Field("courseCode") String courseCode,
                       @Field("startTime") long startTime, @Field("endTime") long endTime, @Field("ACTION") String action);
}
