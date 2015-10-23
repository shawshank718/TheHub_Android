package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.UserDetails;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * Created by sathwiksingari on 9/17/15.
 */
public class UserResponse extends BaseResponse {

    public static UserVerificationListener verificationListener;

    public static  ImageUploadResponseListener imageUploadResponseListener;

    public static UserDetailsQueryListener userDetailsQueryListener;

    public UserDetails details;

    public UserDetails getDetails() {
        return details;
    }

    public void setDetails(UserDetails details) {
        this.details = details;
    }

    public static void callRegisterUser(Context context, String firstName, String lastName, String email, String password, String type, long time) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Creating an account. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        verificationListener = (UserVerificationListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.registerUser(firstName, lastName, email, password, type, time, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    verificationListener.onRegistrationComplete(userResponse.getDetails());
                } else {
                    verificationListener.onFail(userResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });

    }

    public static void callLoginUser(Context context, String email, String password) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Logging in. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        verificationListener = (UserVerificationListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.loginUser(email, password, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    verificationListener.onSignInComplete(userResponse.getDetails());
                } else {
                    verificationListener.onFail(userResponse.getMeta().getMessage());
                }

                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void retrieveUserDetails(Context context, int id, int userId) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        userDetailsQueryListener = (UserDetailsQueryListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.getUserDetails(id, userId, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    userDetailsQueryListener.onDetailsRetrieved(userResponse.getDetails());
                } else {
                    userDetailsQueryListener.onDetailsQueryFail(userResponse.getMeta().getMessage());
                }
                progress.dismiss();

            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void updateUserDetails(Context context, int userId, String phone, String qualification,
                                         List<String> addedLanguages, List<String> deletedLanguages, List<String> addedCourses, List<String> deletedCourses) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        userDetailsQueryListener = (UserDetailsQueryListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.updateUserDetails(userId, phone, qualification, addedLanguages, deletedLanguages, addedCourses, deletedCourses, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    userDetailsQueryListener.onDetailsUpdated(userResponse.getDetails());
                } else {
                    userDetailsQueryListener.onDetailsQueryFail(userResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void uploadImageToServer(Context context, TypedFile image, long userId) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Uploading image. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        imageUploadResponseListener = (ImageUploadResponseListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.uploadImage(image, userId, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    imageUploadResponseListener.onImageUpload(userResponse.getDetails());
                    progress.dismiss();
                } else {
                    imageUploadResponseListener.onUploadFail(userResponse.getMeta().getMessage());
                    progress.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static boolean checkUserLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE );
        String userJson = prefs.getString("user", null);
        if (userJson != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveUserDetails(Context context, UserDetails userDetails) {
        SharedPreferences prefs = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(userDetails);
        editor.putString("user", userJson);
        Log.d("UserDetails", userJson);
        editor.commit();

    }

    public static UserDetails getUserDetails(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE );
        Gson gson = new Gson();
        String userJson = prefs.getString("user", null);
        UserDetails userDetails = gson.fromJson(userJson, UserDetails.class);
        Log.d("UserDetails", userJson);
        return  userDetails;
    }

    public static void callLogout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public interface UserVerificationListener {
        public void onRegistrationComplete(UserDetails userDetails);
        public void onSignInComplete(UserDetails userDetails);
        public void onFail(String message);
    }

    public interface  ImageUploadResponseListener {
        void onImageUpload(UserDetails userDetails);
        void onUploadFail(String message);
    }

    public interface UserDetailsQueryListener {
        void onDetailsRetrieved(UserDetails userDetails);
        void onDetailsUpdated(UserDetails userDetails);
        void onDetailsQueryFail(String message);
    }

}
