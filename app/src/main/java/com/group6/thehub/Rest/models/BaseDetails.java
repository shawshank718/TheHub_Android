package com.group6.thehub.Rest.models;

import org.parceler.Parcel;

/**
 * Created by sathwiksingari on 9/17/15.
 */

@Parcel
public class BaseDetails {

    boolean success;
    String message;

    public BaseDetails() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
