package com.example.android.popularMovies2_2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularMovies2_2.database.MovieModel;

public class ReviewsTrailersActivity extends AppCompatActivity{
    String[] reviewUrls;
    String[] trailerUrls;
    String[] reviewLabels;
    String[] trailerLabels;
    MovieModel mMovie;
    final int DEFAULT_ID = -1;
    ListView mReviewsLv;
    ListView mTrailersLv;
    TextView mErrorMsg;
    Intent intent;
    private static final int MOVIE_URLS_LOADER = 100;
    private static final String MOVIE_ID_EXTRA = "movie_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_trailers);
        mReviewsLv = (ListView) findViewById(R.id.reviews_lv);
        mTrailersLv = (ListView) findViewById(R.id.trailers_lv);
        mErrorMsg = (TextView) findViewById(R.id.error_message);
        intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        mMovie = intent.getParcelableExtra(MainActivity.MOVIE_EXTRA);
        if (mMovie == null) {
            // MOVIE_EXTRA not found
            closeOnError();
            return;
        }
        setTitle(mMovie.getOriginalTitle());
        getMovieUrls();
        createLabels();
        ArrayAdapter<String> reviewsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reviewLabels);
        mReviewsLv.setAdapter(reviewsAdapter);
        ArrayAdapter<String> trailersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trailerLabels);
        mTrailersLv.setAdapter(trailersAdapter);
        mTrailersLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openWebPage(trailerUrls[position]);
            }
        });
        mReviewsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openWebPage(reviewUrls[position]);
            }
        });

    }//end onCreate()

    private void getMovieUrls(){
        reviewUrls = mMovie.getReviewUrls();
        trailerUrls = mMovie.getTrailerUrls();
    }//end getMovieUrls()
    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private void createLabels(){
        int reviewUrlsSize = reviewUrls.length;
        int trailerUrlsSize = trailerUrls.length;
        reviewLabels = new String[reviewUrlsSize];
        trailerLabels = new String[trailerUrlsSize];
        for (int i = 0; i < reviewUrlsSize; ++i){
            reviewLabels[i] = "Review " + (i + 1);
        }
        for (int i = 0; i < trailerUrlsSize; ++i){
            trailerLabels[i] = "Trailer " + (i + 1);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

}
