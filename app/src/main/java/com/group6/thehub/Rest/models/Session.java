package com.group6.thehub.Rest.models;

/**
 * Created by Sathwik on 23-Oct-15.
 */
public class Session {

    int sessionId;
    int studentId;
    int tutorId;
    UserDetails student;
    UserDetails tutor;
    int locationId;
    String courseCode;
    String status;
    long startTime;
    long endTime;
    int rating;

    public int getSessionId() {
        return sessionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getTutorId() {
        return tutorId;
    }

    public UserDetails getStudent() {
        return student;
    }

    public UserDetails getTutor() {
        return tutor;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getRating() {
        return rating;
    }
}
