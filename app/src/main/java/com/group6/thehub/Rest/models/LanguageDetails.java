package com.group6.thehub.Rest.models;

import java.util.List;

/**
 * Created by Sathwik on 17-Oct-15.
 */
public class LanguageDetails {
    List<Language> languages;
    List<String> englishNames;

    public List<Language> getLanguages() {
        return languages;
    }

    public List<String> getEnglishNames() {
        return englishNames;
    }
}
