package com.group6.thehub.Rest.models;

import java.util.ArrayList;

/**
 * Created by sathwiksingari on 9/17/15.
 */
public class UserDetails {

    int userId;
    String firstName;
    String lastName;
    String email;
    String type;
    ImageDetails image;
    String qualification;
    int star5;
    int star4;
    int star3;
    int star2;
    int star1;
    float rating;
    ArrayList<Language> languages;
    ArrayList<Course> courses;
    String phone;
    boolean favorite;

    public String getPhone() {
        if (phone != null) {
            return phone.replace("a","");
        } else {
            return "";
        }
    }

    public int getUserId() {
        return userId;
    }


    public UserDetails(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return this.firstName+ " "+this.lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public ImageDetails getImage() {
        return image;
    }

    public void setImage(ImageDetails image) {
        this.image = image;
    }

    public String getQualification() {
        return qualification;
    }

    public int getStar5() {
        return star5;
    }

    public int getStar4() {
        return star4;
    }

    public int getStar3() {
        return star3;
    }

    public int getStar2() {
        return star2;
    }

    public int getStar1() {
        return star1;
    }

    public float getRating() {
        return rating;
    }

    public ArrayList<Language> getLanguages() {
        return languages;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getChannel() {
        return this.firstName+this.lastName+this.userId;
    }
}
