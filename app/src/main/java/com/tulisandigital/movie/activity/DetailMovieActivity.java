package com.tulisandigital.movie.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tulisandigital.movie.AppVar;
import com.tulisandigital.movie.BaseActivity;
import com.tulisandigital.movie.R;
import com.tulisandigital.movie.model.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tulisandigital.movie.AppVar.api_key;

public class DetailMovieActivity extends BaseActivity {
    AlertDialogManager alert = new AlertDialogManager();
    private static final String TAG = "DetailMovieActivity";
    private Movies movies;
    public TextView id;
    private TextView title;
    private TextView overview;
    private TextView vote_average;
    private TextView release_date;
    private TextView popularity;
    private TextView vote_count;
    private ImageView gambar_movies;
    private TextView name;
    private ImageView mark_as_favourite;
    private ImageView delete;
    private String JSON_STRING;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        listView = (ListView) findViewById(R.id.listView);
        getTrailer();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#333333")));
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Detail Movie</font>"));
        movies = (Movies) getIntent().getSerializableExtra("movie");
        popularity = (TextView) findViewById(R.id.popularity);
        id = (TextView) findViewById(R.id.id);
        title = (TextView) findViewById(R.id.title);
        vote_count = (TextView) findViewById(R.id.vote_count);
        release_date = (TextView) findViewById(R.id.release_date);
        vote_average = (TextView) findViewById(R.id.vote_average);
        overview = (TextView) findViewById(R.id.overview);
    /*    title.setText(movies.getTitle());*/
        overview.setPaintFlags(overview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        overview.setPaintFlags(overview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
       String [] dateParts = movies.getRelease_date().split("-");
         String day = dateParts[0];
         release_date.setText(day);
        int param_id = (int) movies.getId();
        String idnya = String.valueOf(param_id);
        id.setText(idnya);
        title.setText(movies.getTitle());
       /* release_date.setText(movies.getRelease_date());
       */
        int param_count = (int) movies.getVote_count();
        String convertcount = String.valueOf(param_count);
        vote_count.setText(convertcount);
        double param_popularity = (double) movies.getPopularity();
        String convertpopularity = String.valueOf(param_popularity);
        popularity.setText(convertpopularity);
        float param = (float) movies.getVote_average();
        String convertparam = String.valueOf(param);
        vote_average.setText(convertparam);
        overview.setText(movies.getOverview());
        gambar_movies = (ImageView) findViewById(R.id.gambar_movie);
        mark_as_favourite = (ImageView) findViewById(R.id.mark_as_favourite);
        mark_as_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movies.setFavorite("true");
                getDB().updateMovie(movies);
                Toast.makeText(DetailMovieActivity.this, "Done ," + movies.getTitle().toString() + " now your favorite.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        delete = (ImageView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailMovieActivity.this);
                alertDialogBuilder.setMessage("Are You Sure To Delete This Record?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                alert.showAlertDialog(DetailMovieActivity.this, "Deleting movie....", "Please Wait...", false);
                                getDB().deleteMovie(movies);
                                Toast.makeText(DetailMovieActivity.this, "Done ," + movies.getTitle().toString() + " Movie Deleted", Toast.LENGTH_SHORT).show();
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
        });
        Picasso.with(this).load(AppVar.BASE_IMAGE + movies.getPoster_path()).into(gambar_movies);
    }

    private void getTrailer() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailMovieActivity.this, "Sedang Memuat", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Show_trailer_detail();
            }

            private void Show_trailer_detail() {
                JSONObject jsonObject = null;
                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                try {
                    jsonObject = new JSONObject(JSON_STRING);
                    JSONArray result = jsonObject.getJSONArray(AppVar.TAG_JSON_ARRAY);
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jo = result.getJSONObject(i);
                        String iso_en = jo.getString(AppVar.tag_iso_639_1);
                        String iso_us = jo.getString(AppVar.tag_iso_3166_1);
                        String key = jo.getString(AppVar.tag_key);
                        String name = jo.getString(AppVar.tag_name);
                        String site = jo.getString(AppVar.tag_site);
                        String size = jo.getString(AppVar.tag_size);
                        String type = jo.getString(AppVar.tag_type);
                        HashMap<String, String> dem = new HashMap<>();
                        dem.put(AppVar.tag_iso_639_1, iso_en);
                        dem.put(AppVar.tag_iso_3166_1, iso_us);
                        dem.put(AppVar.tag_key, key);
                        dem.put(AppVar.tag_name, name);
                        dem.put(AppVar.tag_site, site);
                        dem.put(AppVar.tag_size, size);
                        dem.put(AppVar.tag_type, type);
                        list.add(dem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
    /*            ListAdapter adapter = new SimpleAdapter(
                        DetailMovieActivity.this, list, R.layout.list_detail_movie,
                        new String[]{AppVar.tag_name, AppVar.tag_key},
                        new int[]{R.id.name, R.id.key});
                        listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long id) {
                        TextView api = (TextView) findViewById(R.id.key);
                        String shareapi = api.getText().toString();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+ shareapi)));
                        Log.d("id Api Trailer : ", String.valueOf(shareapi));

                    }
                });

            }*/
            ListAdapter adapter = new SimpleAdapter(
                    DetailMovieActivity.this, list, R.layout.list_detail_movie,
                    new String[]{AppVar.tag_name, AppVar.tag_key},
                    new int[]{R.id.name, R.id.key});
                        listView.setAdapter(adapter);
                        listView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    name = (TextView) findViewById(R.id.name);
                    name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView api = (TextView) findViewById(R.id.key);
                            String shareapi = api.getText().toString();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+ shareapi)));
                            Log.d("id Api Trailer : ", String.valueOf(shareapi));

                        }
                    });
                    return false;
                }
            });
        }

            @Override
            protected String doInBackground(Void... params) {
                com.tulisandigital.movie.activity.RequestHandler rh = new RequestHandler();
                /*String id = AppVar.id.toString();*/
               /* String id = AppVar.id.toString();
                TextView my_tv = (TextView) findViewById(R.id.id);
                String my_tv_text = my_tv.getText().toString();*/

                List<Movies> movies = getDB().getAllMovies();
                for (Movies mv : movies) {
                    int param_id = (int) mv.getId();
                    String idnya = String.valueOf(param_id).toString();


                    String log = "Id: " + mv.getId();
                    Log.d("movie : ", log);

                String requestApi = rh.sendGetRequestParam("https://api.themoviedb.org/3/movie/" + mv.getId() + "/videos?api_key=" + api_key + "&language=en-US");
                return requestApi;

            }
                return null;
            }

        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MovieActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular) {
            backtofrontend();
        }
        if (id == R.id.rated) {
            Intent Rated = new Intent(this, RatedActivity.class);
            startActivity(Rated);
        }
        if (id == R.id.exitapp) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void backtofrontend() {
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
                        alert.showAlertDialog(DetailMovieActivity.this, "Closing program....", "Please Wait...", false);
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

}
   /* @Override
    public void onClick(View v) {
        *//*Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(AppVar.URL_TO_YOUTUBE));
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);*//*
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM")));
        *//*
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.themoviedb.org/3/movie/211672/videos?api_key=" + AppVar.api_key)));*//*
    }
}
*/