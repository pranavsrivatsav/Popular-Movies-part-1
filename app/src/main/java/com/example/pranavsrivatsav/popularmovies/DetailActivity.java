package com.example.pranavsrivatsav.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private String LOG_TAG = DetailActivity.class.getSimpleName();
    private TextView mTitleText;
    private ImageView mPoster;
    private TextView mRatingText;
    private TextView mReleaseText;
    private TextView mSynopsis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleText = (TextView) findViewById(R.id.Movie_title);
        mPoster = (ImageView) findViewById(R.id.Movie_poster);
        mRatingText = (TextView) findViewById(R.id.Movie_Rating);
        mReleaseText = (TextView) findViewById(R.id.Movie_date);
        mSynopsis = (TextView) findViewById(R.id.Movie_synopsis);

        Intent intent = getIntent();
        if (intent.hasExtra("TITLE"))
            mTitleText.setText(intent.getStringExtra("TITLE"));
        if (intent.hasExtra("RATING"))
            mRatingText.setText(intent.getStringExtra("RATING"));
        if (intent.hasExtra("RELEASE"))
            mReleaseText.setText("Released on "+intent.getStringExtra("RELEASE"));
        Log.i(LOG_TAG, mReleaseText.getText().toString());
        if (intent.hasExtra("SYNOPSIS"))
            mSynopsis.setText(intent.getStringExtra("SYNOPSIS"));
        if (intent.hasExtra("POSTER"))
            Picasso.with(this).load(intent.getStringExtra("POSTER")).into(mPoster);

    }
}
