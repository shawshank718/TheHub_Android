package com.group6.thehub.views;

import android.content.Context;
import android.util.AttributeSet;

import com.quinny898.library.persistentsearch.SearchBox;

/**
 * Created by Sathwik on 18-Oct-15.
 */
public class CustomSearchBox extends SearchBox {
    public CustomSearchBox(Context context) {
        super(context);
    }

    public CustomSearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void hideResults() {
        super.hideResults();
    }
}
