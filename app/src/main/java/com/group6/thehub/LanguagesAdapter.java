package com.group6.thehub;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.group6.thehub.Rest.models.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sathwik on 17-Oct-15.
 */
public class LanguagesAdapter extends ArrayAdapter<Language> {

    Context context;
    int layoutResourceId;
    List<Language> languages;

    public LanguagesAdapter(Context context, int resource, List<Language> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.languages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        TextView text;
        if(convertView == null) {
            LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        else {
            row = convertView;
        }
        text = (TextView) row;
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setText(languages.get(position).getEnglishName());
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row;
        TextView text;
        if(convertView == null) {
            LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        else {
            row = convertView;
        }
        text = (TextView) row;
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setText(languages.get(position).getEnglishName());
        return row;
    }


}
