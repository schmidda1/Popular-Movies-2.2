package com.example.android.popularMovies2_2.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
@Entity(tableName = "movies")
public class MovieModel implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;
    private String originalTitle;
    private String moviePoster;
    private String plotSynopsis;
    private float vote_average;
    private String releaseDate;
    private String posterUrl;
    private String[] reviewUrls;
    private String[] trailerUrls;
    private int favorite = 0;//0 = not assigned, 1 = not favorite, 2 = favorite
    @Ignore
    public MovieModel(){
    }
    public MovieModel(int movieId, String originalTitle, String moviePoster, String plotSynopsis, float vote_average,
                      String releaseDate, String posterUrl, String[] reviewUrls, String[] trailerUrls, int favorite){
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.moviePoster = moviePoster;
        this.plotSynopsis = plotSynopsis;
        this.vote_average = vote_average;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.reviewUrls = reviewUrls;
        this.trailerUrls = trailerUrls;
        this.favorite = favorite;
    }
    //get methods
    public int getMovieId(){return movieId;}
    public String getOriginalTitle(){
        return originalTitle;
    }
    public String getMoviePoster(){
        return moviePoster;
    }
    public String getPlotSynopsis(){
        return plotSynopsis;
    }
    public float getVote_average(){
        return vote_average;
    }
    public String getReleaseDate(){
        return releaseDate;
    }
    public String getPosterUrl(){
        return posterUrl;
    }
    public String[] getReviewUrls(){return reviewUrls;}
    public String[] getTrailerUrls(){return trailerUrls;}
    public int getFavorite(){return favorite;}
    //set methods
    public void setMovieId(int movieId){this.movieId = movieId;}
    public void setOriginalTitle(String originalTitle){
        this.originalTitle = originalTitle;
    }
    public void setMoviePoster(String moviePoster){
        this.moviePoster = moviePoster;
    }
    public void setPlotSynopsis(String plotSynopsis){
        this.plotSynopsis = plotSynopsis;
    }
    public void setVote_average(float vote_average){
        this.vote_average = vote_average;
    }
    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }
    public void setPosterUrl(String posterUrl){
        this.posterUrl = posterUrl;
    }
    public void setReviewUrls(String[] reviewUrls){this.reviewUrls = reviewUrls;}
    public void setTrailerUrls(String[] trailerUrls){this.trailerUrls = trailerUrls;}
    public void setFavorite(int favorite){this.favorite = favorite;}
    //Parcel Constructor
    @Ignore
    public MovieModel(Parcel in){
        this.movieId = in.readInt();
        this.originalTitle = in.readString();
        this.moviePoster = in.readString();
        this.plotSynopsis = in.readString();
        this.vote_average = in.readFloat();
        this.releaseDate = in.readString();
        this.posterUrl = in.readString();
        this.reviewUrls = in.createStringArray();
        this.trailerUrls = in.createStringArray();
        this.favorite = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(originalTitle);
        dest.writeString(moviePoster);
        dest.writeString(plotSynopsis);
        dest.writeFloat(vote_average);
        dest.writeString(releaseDate);
        dest.writeString(posterUrl);
        dest.writeStringArray(reviewUrls);
        dest.writeStringArray(trailerUrls);
        dest.writeInt(favorite);

    }
    public static final Parcelable.Creator<MovieModel> CREATOR
            = new Parcelable.Creator<MovieModel>(){
        public MovieModel createFromParcel(Parcel in){
            return new MovieModel(in);
        }
        public MovieModel[] newArray(int size){
            return new MovieModel[size];
        }
    };
}
