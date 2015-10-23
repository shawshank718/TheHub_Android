package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;

import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.Location;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sathwik on 22-Oct-15.
 */
public class LocationResponse extends BaseResponse {

    static LocationResponseListener mListener;

    List<Location> locations;

    public List<Location> getLocations() {
        return locations;
    }

    public static void getLocations(Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (LocationResponseListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.getLocations(new Callback<LocationResponse>() {
            @Override
            public void success(LocationResponse locationResponse, Response response) {
                if (locationResponse.getMeta().isSuccess()) {
                    mListener.onLocationsRetrieved(locationResponse.getLocations());
                } else {
                    mListener.onLocationFail(locationResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public interface LocationResponseListener {
        void onLocationsRetrieved(List<Location> locations);
        void onLocationFail(String message);
    }
}
