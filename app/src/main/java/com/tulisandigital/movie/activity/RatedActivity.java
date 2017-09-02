package com.tulisandigital.movie.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.tulisandigital.movie.AppVar;
import com.tulisandigital.movie.BaseActivity;
import com.tulisandigital.movie.R;
import com.tulisandigital.movie.adapter.ListAdapter;
import com.tulisandigital.movie.adapter.viewholders.MovieViewHolder;
import com.tulisandigital.movie.app.AppMovie;
import com.tulisandigital.movie.model.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RatedActivity extends BaseActivity {
    AlertDialogManager alert = new AlertDialogManager();
    private static final String TAG = "RatedActivity";
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private ArrayList<Movies> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Top Rated Movies</font>"));
        initRecycler();
        initAdapterByRated();
      /*  getDB().clearMovie();
      */  requestToServer();
        showDialog("Loading...");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular) {
            Intent intent = new Intent(this, MovieActivity.class);
            startActivity(intent);
        }
        if (id == R.id.rated) {
            refresh();
        }
        if (id == R.id.exitapp) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }
    private void refresh(){
        Intent intent = new Intent(this, MovieActivity.class);
        startActivity(intent);
    }
    private void exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Exit Movie App?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alert.showAlertDialog(RatedActivity.this, "Closing program....", "Please Wait...", false);
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                        finish();
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void requestToServer() {
        if (isInternetConnectionAvailable()) {
            getDataByRate(AppVar.URL_MOVIE_TOP_RATED);
        } else {
            Toast.makeText(this, "no connection", Toast.LENGTH_SHORT).show();
            hideDialog();
        }
    }
    private void initRecycler() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 1));
    }
    private void initAdapterByRated() {
        listAdapter = new ListAdapter<Movies
                , MovieViewHolder>
                (R.layout.item_list_movie
                        , MovieViewHolder.class
                        , Movies.class
                        , list) {
            @Override
            protected void bindView(MovieViewHolder holder, final Movies model, int position) {
                Picasso.with(getApplicationContext())
                        .load(AppVar.BASE_IMAGE+model.getPoster_path())
                        .into(holder.gambar_movie);
                Log.d("Reading: ", "Reading all movies..");
                List<Movies> movies = getDB().getAllMovies();
                for (Movies mv : movies) {
                    String log = "Id: " + mv.getId() + " ,Title: " + mv.getTitle() +  " ,Image: " + mv.getPoster_path() + " ,favorite: " + mv.getFavorite();
                    Log.d("movie : ", log);
                }
                holder.getItem().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(RatedActivity.this,DetailMovieActivity.class);
                        in.putExtra("movie",model);
                        startActivity(in);
                    }
                });
            }
        };
        recyclerView.setAdapter(listAdapter);
    }
    public void GetMovieBypopular(String url) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Movies</font>"));
        StringRequest request = new StringRequest(Request.Method.GET
                , url
                , new Response.Listener<String>() { //response api
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject parent = new JSONObject(response);
                    JSONArray results = parent.getJSONArray("results");
                    list = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject sourceParam = results.getJSONObject(i);
                        Movies datajson = new Movies();
                        datajson.setId(sourceParam.getInt("id"));
                        datajson.setOriginal_language(sourceParam.getString("original_language"));
                        datajson.setTitle(sourceParam.getString("title"));
                        datajson.setOverview(sourceParam.getString("overview"));
                        datajson.setPopularity(sourceParam.getDouble("popularity"));
                        datajson.setPoster_path(sourceParam.getString("poster_path"));
                        datajson.setRelease_date(sourceParam.getString("release_date"));
                        datajson.setVote_average(sourceParam.getDouble("vote_average"));
                        datajson.setVote_count(sourceParam.getInt("vote_count"));
                        if (getDB().getCountByRated() != results.length()) {
                            getDB().addMovie(datajson);
                        }
                        list.add(datajson);
                    }

                    listAdapter.swapData(getDB().getAllListRated());
                    hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideDialog();

            }
        });
        AppMovie.getInstance().addToRequestQueue(request, TAG);
    }
    public void getDataByRate(String url) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Top Rated Movies</font>"));
        StringRequest request = new StringRequest(Request.Method.GET
                , url
                , new Response.Listener<String>() { //response api
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject parent = new JSONObject(response);
                    JSONArray results = parent.getJSONArray("results");
                    list = new ArrayList<>();

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject sourceParam = results.getJSONObject(i);
                        Movies datajson = new Movies();
                        datajson.setId(sourceParam.getInt("id"));
                        datajson.setOriginal_language(sourceParam.getString("original_language"));
                        datajson.setTitle(sourceParam.getString("title"));
                        datajson.setOverview(sourceParam.getString("overview"));
                        datajson.setPopularity(sourceParam.getDouble("popularity"));
                        datajson.setPoster_path(sourceParam.getString("poster_path"));
                        datajson.setRelease_date(sourceParam.getString("release_date"));
                        datajson.setVote_average(sourceParam.getDouble("vote_average"));
                        datajson.setVote_count(sourceParam.getInt("vote_count"));
                        if (getDB().getCountByRated() != results.length()) {
                            getDB().addMovie(datajson);
                        }
                        list.add(datajson);
                    }
                    listAdapter.swapData(getDB().getAllListRated());
                    hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { // error response
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideDialog();

            }
        });
        AppMovie.getInstance().addToRequestQueue(request, TAG);
    }
}
