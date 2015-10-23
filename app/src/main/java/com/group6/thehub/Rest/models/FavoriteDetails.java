package com.group6.thehub.Rest.models;

import java.util.List;

/**
 * Created by Sathwik on 21-Oct-15.
 */
public class FavoriteDetails extends SearchDetails {

    List<UserDetails> users;

    public List<UserDetails> getUsers() {
        return users;
    }

}
