package com.group6.thehub.Rest.responses;

import android.app.ProgressDialog;
import android.content.Context;

import com.group6.thehub.Rest.RestClient;
import com.group6.thehub.Rest.models.CourseDetails;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sathwik on 18-Oct-15.
 */
public class CourseResponse extends BaseResponse {

    public static CourseDetailsListener courseDetailsListener;

    CourseDetails details;

    public CourseDetails getDetails() {
        return details;
    }

    public static void loadAllCourses(Context context) {
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Loading information. Please Wait");
        progress.setIndeterminate(true);
        progress.show();
        courseDetailsListener = (CourseDetailsListener) context;
        RestClient restClient = new RestClient(context);
        restClient.theHubApi.loadCourses(new Callback<CourseResponse>() {
            @Override
            public void success(CourseResponse courseResponse, Response response) {
                if (courseResponse.getMeta().isSuccess()) {
                    courseDetailsListener.coursesRetrieved(courseResponse.getDetails());
                } else {
                    courseDetailsListener.courseDetailsFail(courseResponse.getMeta().getMessage());
                }
                progress.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progress.dismiss();
            }
        });
    }

    public interface CourseDetailsListener {
        public void coursesRetrieved(CourseDetails courseDetails);
        public void courseDetailsFail(String message);
    }
}
