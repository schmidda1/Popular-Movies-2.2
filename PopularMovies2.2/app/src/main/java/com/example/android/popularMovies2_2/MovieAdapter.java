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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularMovies2_2.database.MovieModel;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    ArrayList<MovieModel> mMovieData;
    private String[] mMovieFiles;//for UI testing


    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieModel movie, int position);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.movie_iv);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieModel movie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movie, adapterPosition);
        }
    }
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_image;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {

        MovieModel movie = mMovieData.get(position);
        String posterUrl = movie.getPosterUrl();
        Picasso.get()
                .load(posterUrl)
                .error(R.drawable.android2)
                .into(movieAdapterViewHolder.mMovieImageView);
    }
    public static int getResId(String resName, Class<?> c){
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }
    public void setMovieData(ArrayList<MovieModel> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }
    public void setMovieFiles(String[] movieData) {
        mMovieFiles = movieData;
        notifyDataSetChanged();
    }
}