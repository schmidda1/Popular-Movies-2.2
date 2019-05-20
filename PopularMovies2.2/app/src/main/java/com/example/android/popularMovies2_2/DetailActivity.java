package com.example.android.popularMovies2_2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularMovies2_2.database.AppDatabase;
import com.example.android.popularMovies2_2.utilities.NetworkUtils;
import com.example.android.popularMovies2_2.utilities.OpenMovieJsonUtils;
import com.example.android.popularMovies2_2.database.MovieModel;
import com.squareup.picasso.Picasso;
import java.util.concurrent.TimeUnit;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    private MovieModel mMovie;
    ImageView mThumbnail;
    TextView mOriginalTitle;
    TextView mPlotSynopsis;
    TextView mVoteAverage;
    TextView mReleaseDate;
    Button mFavoriteBtn;
    Intent intent;
    MovieUrls mMovieUrls;
    Integer asyncFinished = 0;
    private AppDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mThumbnail = (ImageView) findViewById(R.id.thumbnail);
        mOriginalTitle = (TextView) findViewById(R.id.original_title);
        mPlotSynopsis = (TextView) findViewById(R.id.plot_synopsis);
        mVoteAverage = (TextView) findViewById(R.id.vote_average);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mFavoriteBtn = (Button) findViewById(R.id.favorite_btn);
        mDb = AppDatabase.getInstance(getApplicationContext());
        intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        mMovie = intent.getParcelableExtra(MainActivity.MOVIE_EXTRA);
        if (mMovie.getFavorite() == 0) {//unassigned
            isFavorite(mMovie.getMovieId());
        }
        if (mMovie.getFavorite() == 1) {//need to get mMovieUrls
            asyncFinished = 0;
            new getMovieUrls2().execute(mMovie.getMovieId());
            while (asyncFinished != 1) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            mMovie.setReviewUrls(mMovieUrls.reviewUrls);
            mMovie.setTrailerUrls(mMovieUrls.trailerUrls);
            mFavoriteBtn.setText(getString(R.string.favorite_btn_label_save));
        } else {
            mFavoriteBtn.setText(getString(R.string.favorite_btn_label_remove));
        }


        populateUI(mMovie);
    }

    private void isFavorite(int movieId){
        if (MainActivity.mMovieDataFromDb == null) {
            mMovie.setFavorite(1);
            return;
        }
        int length = MainActivity.mMovieDataFromDb.size();
        for (int i = 0; i < length; ++i){
            if (MainActivity.mMovieDataFromDb.get(i).getMovieId() == movieId ){
                mMovie.setFavorite(2);
                mMovie.setReviewUrls(MainActivity.mMovieDataFromDb.get(i).getReviewUrls());
                mMovie.setTrailerUrls(MainActivity.mMovieDataFromDb.get(i).getTrailerUrls());
                return;
            }
        }
        mMovie.setFavorite(1);
    }
    public void onClickSaveFavorite(View v){
        int sortType = MainActivity.getSortType();
        Context context = getParent();
        if (sortType != 2) {//detail view holds internet object
            if (getString(R.string.favorite_btn_label_save).equals(mFavoriteBtn.getText().toString())){//not in database or in MainActivity.mMoviesDataFromDb
                mMovie.setFavorite(2);//mark as favorite;
                saveMovie(this, mMovie);
                MainActivity.mMovieDataFromDb.add(mMovie);
                mFavoriteBtn.setText(getString(R.string.favorite_btn_label_remove));
            } else {//remove as favorite
                mMovie.setFavorite(1);
                deleteMovie(this, mMovie);
                deleteMovieFromList();
                mFavoriteBtn.setText(getString(R.string.favorite_btn_label_save));
            }
        } else {//detail view holds database object
            int length = MainActivity.mMovieDataFromDb.size();
            MovieAdapter movieAdapter;
            deleteMovie(this, mMovie);
            deleteMovieFromList();
            movieAdapter = MainActivity.getmMovieAdapter();
            movieAdapter.setMovieData(MainActivity.mMovieDataFromDb);
            if (length == 1){//mMovieDataFromDb is empty
                MainActivity.showEmptyView();
            }
            this.finish();//return to MainActivity
        }
    }
    private int deleteMovieFromList(){
        int length = MainActivity.mMovieDataFromDb.size();
        for (int i = 0; i < length; ++i){
            if (mMovie.getMovieId() == MainActivity.mMovieDataFromDb.get(i).getMovieId()){
                MainActivity.mMovieDataFromDb.remove(i);
                return 1;
            }
        }
        return 0;
    }
    public void onClickOpenRtActivity(View v) {
        launchRtActivity();
    }
    private void launchRtActivity(){
        Context context = this;
        Class destClass = ReviewsTrailersActivity.class;
        Intent intent = new Intent(context, destClass);
        intent.putExtra(MainActivity.MOVIE_EXTRA, mMovie);
        startActivity(intent);
    }

    private void closeOnError() {
        finish();
            Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }
    public class getMovieUrls2 extends AsyncTask<Integer, Void, Integer>{
        @Override
        protected Integer doInBackground(Integer...params){
            int movidId = params[0];
            try{
                String reviewsJson = NetworkUtils.getReviewsJson(movidId);
                String trailersJson = NetworkUtils.getTrailersJson(movidId);
                String[] reviewUrls = OpenMovieJsonUtils.getReviewUrls(reviewsJson);
                String[] trailerUrls = OpenMovieJsonUtils.getTrailerUrls(trailersJson);
                MovieUrls movieUrls2 = new MovieUrls(movidId, reviewUrls, trailerUrls);
                mMovieUrls = movieUrls2;
                asyncFinished = 1;
                return 1;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Integer result){
        }

    }//end class getMovieUrls

    private void populateUI(MovieModel movie) {
        Picasso.get()
                .load(movie.getPosterUrl())
                .error(R.drawable.android2)
                .into(mThumbnail);

        setTitle(getString(R.string.movie_details));
        mOriginalTitle.setText(movie.getOriginalTitle());
        mPlotSynopsis.setText(movie.getPlotSynopsis());
        mVoteAverage.setText(String.valueOf(movie.getVote_average()));
        mReleaseDate.setText(movie.getReleaseDate());

    }
    public void saveMovie(Context context, final MovieModel movie){
        AppExecutors.getInstance().diskIO().execute(new Runnable(){
            @Override
            public void run(){
                mDb.movieDao().insertMovie(movie);
            }
        });
    }

    public void deleteMovie(Context context, final MovieModel movie){
        AppExecutors.getInstance().diskIO().execute(new Runnable(){
            @Override
            public void run(){
                mDb.movieDao().deleteMovie(movie);
            }
        });
    }

}
