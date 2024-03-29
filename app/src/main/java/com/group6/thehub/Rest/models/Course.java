package com.group6.thehub.Rest.models;

/**
 * Created by Sathwik on 17-Oct-15.
 */
public class Course {

    String courseCode;
    String courseName;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public boolean equals(Object o) {
        boolean same = false;

        if (o != null && o instanceof Course)
        {
            same = this.courseCode.equals(((Course) o).courseCode);
        }

        return same;
    }
}
