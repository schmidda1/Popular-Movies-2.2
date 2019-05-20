package com.example.android.popularMovies2_2;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.popularMovies2_2.database.AppDatabase;
import com.example.android.popularMovies2_2.database.MovieModel;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<MovieModel>> movies;
    public MainViewModel(Application application){
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the movies from the Database");
        movies = database.movieDao().loadAllMovies();
    }
    public LiveData<List<MovieModel>> getMovies(){return movies;}

}
