package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;

import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.SearchDetails;
import com.group6.thehub.Rest.models.UserSearchDetails;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sathwik on 21-Oct-15.
 */
public class UserSearchResponse extends BaseResponse {

    UserSearchDetails details;

    static UserSearchResponseListener mListener;

    public UserSearchDetails getDetails() {
        return details;
    }

    public static void searchByCourseCode(Context context, String courseCode) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (UserSearchResponseListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.getUserDetailsFromCourse(courseCode, "T", new Callback<UserSearchResponse>() {
            @Override
            public void success(UserSearchResponse userSearchResponse, Response response) {
                if (userSearchResponse.getMeta().isSuccess()) {
                    mListener.onUserSearchSuccess(userSearchResponse.getDetails());
                } else {
                    mListener.onUserSearchFailed(userSearchResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public interface UserSearchResponseListener {
        public void onUserSearchSuccess(UserSearchDetails userSearchDetails);
        public void onUserSearchFailed(String message);
    }
}
