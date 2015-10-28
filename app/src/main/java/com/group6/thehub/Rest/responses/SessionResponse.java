package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.Session;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;

/**
 * Created by Sathwik on 23-Oct-15.
 */
public class SessionResponse extends BaseResponse {

    static SessionResponseListner mListener;

    Session session;

    @SerializedName("details")
    List<Session> sessions;

    public Session getSession() {
        return session;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public static void getUserSessions(Context context, int userId) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (SessionResponseListner) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.getUserSessions(userId, new Callback<SessionResponse>() {
            @Override
            public void success(SessionResponse sessionResponse, Response response) {
                if (sessionResponse.getMeta().isSuccess()) {
                    mListener.onSessionsRetrieved(sessionResponse.getSessions());
                } else {
                    mListener.onSessionFails(sessionResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void getSession(Context context, int sessionId) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (SessionResponseListner) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.getSession(sessionId, new Callback<SessionResponse>() {
            @Override
            public void success(SessionResponse sessionResponse, Response response) {
                if (sessionResponse.getMeta().isSuccess()) {
                    mListener.onSessionRetrieved(sessionResponse.getSession());
                } else {
                    mListener.onSessionFails(sessionResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void createSession(Context context, int studentId, int tutorId, int locationId, String courseCode, long startTime, long endTime, String action) {

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Please Wait...");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (SessionResponseListner) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.createSession(studentId, tutorId, locationId, courseCode, startTime, endTime, action, new Callback<SessionResponse>() {
            @Override
            public void success(SessionResponse sessionResponse, Response response) {
                if (sessionResponse.getMeta().isSuccess()) {
                    mListener.onSessionActionSuccess(sessionResponse.getSession());
                } else {
                    mListener.onSessionFails(sessionResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void acceptFinishSession(Context context, int sessionId, String action) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Please Wait...");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (SessionResponseListner) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.acceptFinishSession(sessionId, action, new Callback<SessionResponse>() {
            @Override
            public void success(SessionResponse sessionResponse, Response response) {

                if (sessionResponse.getMeta().isSuccess()) {
                    mListener.onSessionActionSuccess(sessionResponse.getSession());
                } else {
                    mListener.onSessionFails(sessionResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public static void rateSession(Context context, int sessionId, int rating, int tutorId, String action) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Please Wait...");
        progress.setIndeterminate(true);
        progress.show();
        mListener = (SessionResponseListner) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.rateSession(sessionId, rating, tutorId, action, new Callback<SessionResponse>() {
            @Override
            public void success(SessionResponse sessionResponse, Response response) {

                if (sessionResponse.getMeta().isSuccess()) {
                    mListener.onSessionActionSuccess(sessionResponse.getSession());
                } else {
                    mListener.onSessionFails(sessionResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public interface SessionResponseListner {
        void onSessionsRetrieved(List<Session> sessions);
        void onSessionRetrieved(Session session);
        void onSessionActionSuccess(Session session);
        void onSessionFails(String message);
    }
}
