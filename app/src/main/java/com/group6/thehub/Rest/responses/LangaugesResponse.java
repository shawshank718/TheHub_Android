package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;

import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.LanguageDetails;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sathwik on 17-Oct-15.
 */
public class LangaugesResponse extends BaseResponse {

    public static LanguageDetailsListener languageDetailsListener;

    LanguageDetails details;

    public LanguageDetails getDetails() {
        return details;
    }

    public static void loadAllLanguages(Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        languageDetailsListener = (LanguageDetailsListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.loadLangauges(new Callback<LangaugesResponse>() {
            @Override
            public void success(LangaugesResponse langaugesResponse, Response response) {
                if (langaugesResponse.getMeta().isSuccess()) {
                    languageDetailsListener.languagesRetrieved(langaugesResponse.getDetails());
                } else {
                    languageDetailsListener.languageDetailsFail(langaugesResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public interface LanguageDetailsListener {
        public void languagesRetrieved(LanguageDetails languageDetails);
        public void languageDetailsFail(String message);
    }

}
