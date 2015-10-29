package com.group6.thehub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.parse.ParseCloud;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sathwiksingari on 9/18/15.
 */
public class AppHelper {

    private static Context mContext;

    public static final String PARSE_APP_ID = "iIWPI0G0ujKtJ8sjZQLeFp3e5NWOqi4ccDZCRNUO";
    public static final String PARSE_CLIENT_KEY = "AAUDYuWcZ9wkqpyLi0tp6BzjGdTynaszxnWDOtBd";

    private static final String USER_PREFS = "com.group6.thehub.user_prefs";

    public static final String END_POINT = "http://192.168.137.249/"; //"http://10.19.90.100/";

    public AppHelper(Context mContext) {
        this.mContext = mContext;
    }

    public static void slideInStayStill(AppCompatActivity from, Class to, Bundle bundle) {
        Intent intent = new Intent(from, to);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        from.startActivity(intent);
        from.overridePendingTransition(R.anim.push_left_in, 0);
    }

    public static void slideUpPushUp(Intent intent) {
        mContext.startActivity(intent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    public static void slideDownPushDown(Intent intent) {
        mContext.startActivity(intent);
        ((AppCompatActivity) mContext).overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
    }

    public static void saveToUserPrefs(String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getFromUserPrefs(String key, String defaultValue) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, defaultValue);
        return value;
    }

    public static float getDensity(Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    public static int dipToPixels(Context context, int dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int convertPixtoDip(Context context, int pixel){
        float scale = getDensity(context);
        return (int)((pixel - 0.5f)/scale);
    }

    public static String getDate(long epochTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date(epochTime * 1000));
        return date;
    }

    public static String getTime(long epochTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String time = sdf.format(new Date(epochTime*1000));
        return time;
    }

    public static void sendPushNotification(String channel, String message, String title, int sessionId) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("channel", channel);
        params.put("message", message);
        params.put("title", title);
        params.put("sessionId", sessionId);
        ParseCloud.callFunctionInBackground("sendPush", params);
    }
}
