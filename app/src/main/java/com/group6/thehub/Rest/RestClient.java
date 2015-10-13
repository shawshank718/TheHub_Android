package com.group6.thehub.Rest;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


/**
 * Created by sathwiksingari on 9/17/15.
 */
public class RestClient {

    private static RestClient restClient;

    private final Context mContext;

    public final TheHubApi theHubApi;

    private final String baseUrl = "http://10.19.90.100/";

    public RestClient(@NonNull Context context) {
        this.mContext = context.getApplicationContext();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        theHubApi = restAdapter.create(TheHubApi.class);
    }

    public synchronized static RestClient getInstance(@NonNull Context context) {
        if (restClient == null) {
            restClient = new RestClient(context);
        }
        return restClient;
    }


}
