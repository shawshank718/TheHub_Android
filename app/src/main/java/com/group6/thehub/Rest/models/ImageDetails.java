package com.group6.thehub.Rest.models;

import org.parceler.Parcel;

/**
 * Created by sathwiksingari on 9/19/15.
 */
@Parcel
public class ImageDetails {
    String imageName;
    String imageUrl;

    public ImageDetails() {
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
