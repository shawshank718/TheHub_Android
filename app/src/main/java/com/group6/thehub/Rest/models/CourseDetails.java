package com.group6.thehub.Rest.models;

import java.util.List;

/**
 * Created by Sathwik on 18-Oct-15.
 */
public class CourseDetails {

    List<Course> courses;
    List<String> courseCodes;

    public List<Course> getCourses() {
        return courses;
    }

    public List<String> getCourseCodes() {
        return courseCodes;
    }
}
