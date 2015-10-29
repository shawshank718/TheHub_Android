package com.group6.thehub.recievers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.group6.thehub.activities.RequestSessionActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sathwiksingari on 29/10/2015.
 */
public class ParseReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, RequestSessionActivity.class);
        String json = intent.getExtras().getString("com.parse.Data");
        try {
            JSONObject jsonObject = new JSONObject(json);
            int sessionId = jsonObject.getInt("sessionId");
            i.putExtra("sessionId", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
