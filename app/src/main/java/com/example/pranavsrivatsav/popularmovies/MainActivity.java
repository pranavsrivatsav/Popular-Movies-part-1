package com.example.pranavsrivatsav.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;
    private String LOG_TAG=MainActivity.class.getSimpleName();
    private String sortChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerview_movies);
        mLoadingIndicator=(ProgressBar)findViewById(R.id.progressbar_main);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new MovieAdapter(this);
        mErrorMessage=(TextView)findViewById(R.id.error_message);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        loadMoviegrid();
        Log.i(LOG_TAG,"Finished on create");
    }

    private void loadMoviegrid(){
        Log.i(LOG_TAG,"Inside loadMoviegrid");
        showMovieGrid();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        sortChoice=prefs.getString("sort_by","popular");
        new FetchMoviesList().execute(sortChoice);
    }

    private void showMovieGrid(){
        Log.i(LOG_TAG,"Inside ShowMoviegrid");
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesList extends AsyncTask<String,Void,Movie[]> {


        @Override
        protected void onPreExecute() {
            Log.i(LOG_TAG,"Entered onPreExecute");
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            Log.i(LOG_TAG,"Entered doInBackground");
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            String jsonString=null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https");
                builder.authority("api.themoviedb.org");
                builder.appendPath("3");
                builder.appendPath("movie");
                builder.appendPath(strings[0]);
                builder.appendQueryParameter("api_key", "<api key>");
                String urlpath = builder.build().toString();
                Log.i(LOG_TAG,"URL PATH:"+urlpath);
                URL url = new URL(urlpath);


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");
                if (buffer.length() == 0)
                    return null;
                jsonString = buffer.toString();
                Log.i(LOG_TAG,"Json String:"+jsonString);
                inputStream.close();




            }catch (Exception e){
                e.printStackTrace();
                Log.e(LOG_TAG,"Error Fetching data");
                return null;
            }

            try{

                JSONObject jsonParent=new JSONObject(jsonString);
                JSONArray jsonResults=jsonParent.getJSONArray("results");
                Movie[] container=new Movie[jsonResults.length()];
                String mTitle,mYear,mReleaseDate,mSynopsis,mPoster,poster_path;
                Double mRating;
                for(int i=0;i<jsonResults.length();i++){
                    mTitle=jsonResults.getJSONObject(i).getString("title");
                    mReleaseDate=jsonResults.getJSONObject(i).getString("release_date");
                    mYear=mReleaseDate.substring(0,4);
                    mSynopsis=jsonResults.getJSONObject(i).getString("overview");
                    mRating=jsonResults.getJSONObject(i).getDouble("vote_average");
                    poster_path=jsonResults.getJSONObject(i).getString("poster_path").substring(1);
                    mPoster="http://image.tmdb.org/t/p/w342/"+poster_path;
                    container[i]=new Movie(mTitle,mRating,mSynopsis,mReleaseDate,mYear,mPoster);
                    Log.i(LOG_TAG,container[i].toString());
                }

                return container;

            }catch(Exception e){
                e.printStackTrace();
                Log.v(LOG_TAG,"Error parsing json");
                return null;
            }finally {
                urlConnection.disconnect();
            }

        }

        protected void onPostExecute(Movie[] movies) {
            Log.i(LOG_TAG,"Entered PostExecute");
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies==null)
                showErrorMessage();
            else{
                showMovieGrid();
                Log.i(LOG_TAG,"COUNT:"+ Integer.toString(movies.length));
                mAdapter.setMovie_poster_list(movies);
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_grid,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh_grid){
            mAdapter.setMovie_poster_list(null);
            loadMoviegrid();
            return true;
        }

        if(item.getItemId()==R.id.sort){
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnClick(Movie movie) {
        Intent intent=new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("TITLE",movie.getTitle()+" ("+movie.getYear()+")");
        intent.putExtra("POSTER",movie.getPoster());
        intent.putExtra("RATING",Double.toString(movie.getRating()));
        intent.putExtra("RELEASE",movie.getReleaseDate());
        intent.putExtra("SYNOPSIS",movie.getSynopsis());
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"ONSTART");
        loadMoviegrid();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(LOG_TAG,"ONRESUME");
        loadMoviegrid();

    }
}
