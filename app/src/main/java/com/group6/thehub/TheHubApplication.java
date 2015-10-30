package com.group6.thehub;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

/**
 * Created by Sathwik on 23-Oct-15.
 */
public class TheHubApplication extends Application {

    @Override
    public void onCreate() {
        Log.d("Application", "onCreate");
        Parse.initialize(this, AppHelper.PARSE_APP_ID, AppHelper.PARSE_CLIENT_KEY );
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Application", "Done");
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
        super.onCreate();
    }

}
