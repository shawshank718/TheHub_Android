package com.group6.thehub;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Sathwik on 23-Oct-15.
 */
public class TheHubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, AppHelper.PARSE_APP_ID, AppHelper.PARSE_CLIENT_KEY );
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

}
