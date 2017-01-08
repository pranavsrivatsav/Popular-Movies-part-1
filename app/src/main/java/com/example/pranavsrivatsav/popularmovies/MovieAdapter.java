package com.example.pranavsrivatsav.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Pranav Srivatsav on 1/7/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter <MovieAdapter.MovieAdapterViewHolder> {

    private String LOG_TAG=MovieAdapter.class.getSimpleName();
    private Movie[] movieList;
    private final MovieAdapterOnClickHandler mClickHandler;

    public void setMovie_poster_list(Movie[] movie_poster_list) {
        Log.i(LOG_TAG,"Entered setter poster_list");
        this.movieList = movie_poster_list;
        notifyDataSetChanged();
        //Log.i(LOG_TAG,"COUNT:"+Integer.toString(this.movie_poster_list.length));
    }

    public interface MovieAdapterOnClickHandler{
        void OnClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        Log.i(LOG_TAG,"Constructing MovieAdapter");
        this.mClickHandler = mClickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            Log.i(LOG_TAG,"Constructing ViewHolder");
            mImageView=(ImageView) itemView.findViewById(R.id.grid_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            mClickHandler.OnClick(movieList[position]);
        }

    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(LOG_TAG,"OnCreate ViewHolder");
        int ViewHolderLayout=R.layout.grid_item;
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(ViewHolderLayout,parent,false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Log.i(LOG_TAG,"Binding ViewHolder");
        String imagelist=movieList[position].getPoster();
        Picasso.with(holder.itemView.getContext()).load(imagelist).into(holder.mImageView);

        Log.i(LOG_TAG,"loaded Image");
    }

    @Override
    public int getItemCount() {
        Log.i(LOG_TAG,"Getting Timecount");

        if(movieList==null)
            return 0;

        else{
            Log.i(LOG_TAG,"count:"+Integer.toString(movieList.length));
            return movieList.length;
        }

    }
}
