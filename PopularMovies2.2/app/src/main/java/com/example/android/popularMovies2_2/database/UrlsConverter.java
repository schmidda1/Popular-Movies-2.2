package com.example.android.popularMovies2_2.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;

public class UrlsConverter {
    private static Gson gson = new Gson();
    @TypeConverter
    public static String toJson(String[] movieUrls){
        return movieUrls == null ? null : gson.toJson(movieUrls);
    }
    @TypeConverter
    public static String[] toUrls(String UrlsJson){
        return UrlsJson == null ? null : gson.fromJson(UrlsJson, String[].class);
    }
}
