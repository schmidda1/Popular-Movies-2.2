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
package com.example.android.popularMovies2_2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularMovies2_2.MovieAdapter.MovieAdapterOnClickHandler;
import com.example.android.popularMovies2_2.database.AppDatabase;

import com.example.android.popularMovies2_2.utilities.NetworkUtils;
import com.example.android.popularMovies2_2.utilities.OpenMovieJsonUtils;
import com.example.android.popularMovies2_2.database.MovieModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler{
    public static final String MOVIE_EXTRA = "movie";
    private static final String MOVIE_URL_EXTRA = "movie_url";
    private static final String SORT_PREF = "sort_pref";
    private static final int DEFAULT_SORT_TYPE = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_INSTANCE_STATE_RV_POSITION = "rv_position";
    //private static final String BUNDLE_MOVIES = "key_movies";
    private static final String BUNDLE_RECYCLER_LAYOUT = "MainActivity.recycler.layout";
    private static final int MOVIE_LOADER = 22;
    private static RecyclerView mRecyclerView;
    private static MovieAdapter mMovieAdapter;

    private static TextView mErrorMessageDisplay;
    private static TextView mEmptyView;

    private ProgressBar mLoadingIndicator;
    private MenuItem mPopular;
    private MenuItem mTopRated;
    private MenuItem mFavorites;
    private static int mSortType;
    int asyncFinished;
    static ArrayList<MovieModel> mMovieData;
    static ArrayList<MovieModel> mMovieDataFromDb;
    private AppDatabase mDb;
    RecyclerView.LayoutManager mLayoutManager;
    int mScrollPosition;
    Bundle savedState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //onRestoreInstanceState(savedState);

        savedState = savedInstanceState;
        setContentView(R.layout.activity_main);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        /*
        if (savedInstanceState != null) {
            mSortType= savedInstanceState.getInt(SORT_PREF);
        } else {
            mSortType = 0;
        }*/
        NetworkUtils.sortPref = mSortType;
        int orientation = getResources().getConfiguration().orientation;
        int columnns;
        if (orientation == 1){//portrait
            columnns = 2;
        } else {//landscape
            columnns = 2;
        }
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, columnns);

        mRecyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        } else {
            mSortType = 0;
        }

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // COMPLETED (11) Pass in 'this' as the MovieAdapterOnClickHandler
        /*
         * The MovieAdapter is responsible for linking our movie data with the Views that
         * will end up displaying our movie data.
         */
        if (mMovieAdapter == null) {
            mMovieAdapter = new MovieAdapter(this);
        } else {
            mMovieData = mMovieAdapter.mMovieData;
            if (savedState != null) {
                Parcelable savedRecyclerLayoutState = savedState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
                if (savedRecyclerLayoutState != null){
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                }
            }

        }
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
        if (mMovieDataFromDb == null){//database is empty
            mMovieDataFromDb = new ArrayList<>(5);//instantiate member
        }

        /* Once all of our views are setup, we can load the movie data. */
        //getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);
        if (mSortType == 2){
            setTitle(getString(R.string.favorites_title));
            processDbData();
        } else if (mSortType == 1){
            setTitle(getString(R.string.top_rated_title));
            if (mMovieData == null) getMovieDbData();
        } else if (mSortType == 0){
            setTitle(getString(R.string.popular_title));
            if (mMovieData == null) getMovieDbData();
        }
    }
    private void setupViewModel(){
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieModel>>() {
            @Override
            public void onChanged(@Nullable List<MovieModel> movies) {
                Log.d(TAG, "Updating list of movies from LiveData in ViewModel");
                mMovieDataFromDb = (ArrayList<MovieModel>)movies;
                if (mSortType == 2) {
                    mMovieAdapter.setMovieData(mMovieDataFromDb);
                } else {
                    mMovieAdapter.setMovieData(mMovieData);
                }
            }
        });
    }
    private void setCheckboxes(int sortType){
        if (mPopular == null || mTopRated == null || mFavorites == null){
            return;
        }
        switch (sortType){
            case 0:
                mPopular.setChecked(true);
                mTopRated.setChecked(false);
                mFavorites.setChecked(false);
                break;
            case 1:
                mPopular.setChecked(false);
                mTopRated.setChecked(true);
                mFavorites.setChecked(false);
                break;
            case 2:
                mPopular.setChecked(false);
                mTopRated.setChecked(false);
                mFavorites.setChecked(true);
                break;
        }
    }
    public static MovieAdapter getmMovieAdapter(){
        return mMovieAdapter;
    }
    public static int getSortType(){return mSortType;}
    //for UI debug
    private void loadMovieData() {
        showMovieDataView();
        String[] movieFiles = getResources().getStringArray(R.array.movie_files);
        mMovieAdapter.setMovieFiles(movieFiles);
    }

    private void getMovieDbData(){
        showMovieDataView();
        NetworkUtils.sortPref = mSortType;
        asyncFinished = 0;
        final URL url = NetworkUtils.buildUrl();
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String movieJson = NetworkUtils.getResponseFromHttpUrl(url);
                    mMovieData = OpenMovieJsonUtils.getMovieListFromJson(movieJson);
                    asyncFinished = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        while (asyncFinished != 1){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mMovieAdapter.setMovieData(mMovieData);
    }

    private void processDbData(){
        if (mMovieDataFromDb.isEmpty()){
            showEmptyView();
        } else {
            showMovieDataView();
        }
        mMovieAdapter.setMovieData(mMovieDataFromDb);
    }

    @Override
    public void onClick(MovieModel movie, int position) {
        Context context = this;
        launchDetailActivity(movie, position);
    }
    private void launchDetailActivity(MovieModel movie, int position){

        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);
        intent.putExtra(MOVIE_EXTRA, movie);
        startActivity(intent);
    }
    /**
     * This method will make the View for the movie data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the movie
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    void showErrorMessage() {
        /* First, hide the currently visible data */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
    }
    static void showEmptyView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        mPopular = menu.findItem(R.id.popular_mi);
        mTopRated = menu.findItem(R.id.top_rated_mi);
        mFavorites = menu.findItem(R.id.favorites_mi);
        setCheckboxes(mSortType);
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular_mi && mSortType != 0) {
            mSortType = 0;
            getMovieDbData();
            setTitle(getString(R.string.popular_title));
            mPopular.setChecked(true);
            mTopRated.setChecked(false);
            mFavorites.setChecked(false);
            return true;
        }
        else if (id == R.id.top_rated_mi && mSortType != 1){
            mSortType = 1;
            getMovieDbData();
            setTitle(getString(R.string.top_rated_title));
            mPopular.setChecked(false);
            mTopRated.setChecked(true);
            mFavorites.setChecked(false);
            return true;
        }
        else if (id == R.id.favorites_mi && mSortType != 2){
            mSortType = 2;
            processDbData();
            setTitle(getString(R.string.favorites_title));
            mPopular.setChecked(false);
            mTopRated.setChecked(false);
            mFavorites.setChecked(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt(SORT_PREF, mSortType);
    }
    @Override
    protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            mSortType = savedInstanceState.getInt(SORT_PREF, DEFAULT_SORT_TYPE);
        } else {
            mSortType = 0;
        }

    }

}