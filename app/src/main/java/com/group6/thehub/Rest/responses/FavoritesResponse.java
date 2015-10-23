package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;

import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.UserDetails;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sathwik on 21-Oct-15.
 */
public class FavoritesResponse extends BaseResponse {

    public static FavoritesListener mListener;

    List<UserDetails> details;

    public List<UserDetails> getDetails() {
        return details;
    }

    public static void favorites(Context context, int tutorId, int studentId, String action) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (FavoritesListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.favorites(tutorId, studentId, action, new Callback<FavoritesResponse>() {
            @Override
            public void success(FavoritesResponse favoritesResponse, Response response) {
                if (favoritesResponse.getMeta().isSuccess()) {
                    mListener.onFavoriteActionSuccess(favoritesResponse.getMeta().getMessage());
                } else {
                    mListener.onFavoriteCallFail(favoritesResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void getFavorites(Context context, int studentId) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Updating. Please Wait...");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (FavoritesListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.getFavorites(studentId, new Callback<FavoritesResponse>() {
            @Override
            public void success(FavoritesResponse favoritesResponse, Response response) {
                if (favoritesResponse.getMeta().isSuccess()) {
                    mListener.onFavoritesRetrieved(favoritesResponse.getDetails());
                } else {
                    mListener.onFavoriteCallFail(favoritesResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public interface FavoritesListener {
        void onFavoritesRetrieved(List<UserDetails> users) ;
        void onFavoriteActionSuccess(String message);
        void onFavoriteCallFail(String message);
    }
}
