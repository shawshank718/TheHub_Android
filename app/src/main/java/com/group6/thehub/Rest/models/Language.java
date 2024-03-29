package com.group6.thehub.Rest.models;

/**
 * Created by Sathwik on 17-Oct-15.
 */
public class Language {

    String shortName;
    String englishName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    @Override
    public boolean equals(Object o) {
        boolean same = false;

        if (o != null && o instanceof Language)
        {
            same = this.shortName.equals(((Language) o).shortName);
        }

        return same;
    }
}
