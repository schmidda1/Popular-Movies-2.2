/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.popularMovies2_2.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularMovies2_2.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie servers.
 */
public final class NetworkUtils {
    public static int sortPref;
    private static String finalUrl;

    private static final String api_key = BuildConfig.TmdbApiKey;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY_STRING = "api_key=" + api_key + "&language=en-US&page=1";
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String POPULAR = "movie/popular?";
    private static final String TOP_RATED = "movie/top_rated?";

    private static final String MOVIE_POPULAR_URL = MOVIE_BASE_URL + POPULAR + API_KEY_STRING;
    private static final String MOVIE_TOP_RATED_URL = MOVIE_BASE_URL + TOP_RATED + API_KEY_STRING;
    private static final String REVIEWS = "/reviews?";
    private static final String TRAILERS = "/videos?";

    public static URL buildUrl() {
        if (sortPref==0)
            finalUrl = MOVIE_POPULAR_URL;
        else if (sortPref==1)
            finalUrl = MOVIE_TOP_RATED_URL;
        Uri builtUri = Uri.parse(finalUrl).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }
    public static String getReviewsJson(int movidId)throws IOException {
        String reviewsStr = MOVIE_BASE_URL + "movie/" + movidId + REVIEWS + API_KEY_STRING;
        String reviewsJson;
        Uri builtUri = Uri.parse(reviewsStr).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        reviewsJson = getResponseFromHttpUrl(url);
        return reviewsJson;

    }
    public static String getTrailersJson(int movidId)throws IOException {
        String trailersStr = MOVIE_BASE_URL + "movie/" + movidId + TRAILERS + API_KEY_STRING;
        String trailersJson;
        Uri builtUri = Uri.parse(trailersStr).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        trailersJson = getResponseFromHttpUrl(url);
        return trailersJson;

    }



    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}