package com.example.android.popularMovies2_2.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;
@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    LiveData<List<MovieModel>> loadAllMovies();

    @Insert
    void insertMovie(MovieModel movie);

    @Delete
    void deleteMovie(MovieModel movie);

    @Query("SELECT * FROM movies WHERE movie_id = :movieId")
    LiveData<MovieModel> loadMovieById(int movieId);

}
