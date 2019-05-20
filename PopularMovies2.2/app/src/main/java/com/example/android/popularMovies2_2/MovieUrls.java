package com.example.android.popularMovies2_2;

public class MovieUrls {
    public int movieId;
    public String[] reviewUrls;
    public String[] trailerUrls;
    public MovieUrls(){

    }
    public MovieUrls(int movieId, String[] reviewUrls, String[] trailerUrls){
        this.movieId = movieId;
        this.reviewUrls = reviewUrls;
        this.trailerUrls = trailerUrls;
    }


}

