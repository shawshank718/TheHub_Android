package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.UserDetails;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


/**
 * Created by sathwiksingari on 9/17/15.
 */
public class UserResponse extends BaseResponse {

    public static UserResponseListener responseListener;

    public static  ImageUploadResponseListener imageUploadResponseListener;

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
        responseListener = (UserResponseListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.registerUser(firstName, lastName, email, password, type, time, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    responseListener.onRegistrationComplete(userResponse.getDetails());
                    progress.dismiss();
                } else {
                    responseListener.onFail(userResponse.getMeta().getMessage());
                    progress.dismiss();
                }
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
        responseListener = (UserResponseListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.loginUser(email, password, new Callback<UserResponse>() {
            @Override
            public void success(UserResponse userResponse, Response response) {
                if (userResponse.getMeta().isSuccess()) {
                    responseListener.onSignInComplete(userResponse.getDetails());
                    progress.dismiss();
                } else {
                    responseListener.onFail(userResponse.getMeta().getMessage());
                    progress.dismiss();
                }
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
                    imageUploadResponseListener.onFail(userResponse.getMeta().getMessage());
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
        SharedPreferences prefs = context.getSharedPreferences("userDetails", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public interface UserResponseListener {
        public void onRegistrationComplete(UserDetails userDetails);
        public void onSignInComplete(UserDetails userDetails);
        public void onImageUpload(UserDetails userDetails);
        public void onFail(String message);
    }

    public interface  ImageUploadResponseListener {
        public void onImageUpload(UserDetails userDetails);
        public void onFail(String message);
    }


}
