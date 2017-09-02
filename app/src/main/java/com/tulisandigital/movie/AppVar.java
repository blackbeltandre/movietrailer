package com.tulisandigital.movie;

public class AppVar {

    public static String BASE_URL = "https://api.themoviedb.org/3/movie/";

    public static String URL_YOUTUBE = "http://www.youtube.com/";

    public static final String BASE_IMAGE = "http://image.tmdb.org/t/p/w185";

    public static final String id = "id";
    public static final String title = "title";
    public static String api_key = "c1f211c61a515335a6836a91aa0e41ee";
    public static String key = "cxLG2wtE7TM";
    /*masukkan keynya di String api_key c1f211c61a515335a6836a91aa0e41ee*/

    public static final String URL_MOVIE_550 = BASE_URL + "popular?api_key=" + api_key;

    public static final String URL_MOVIE_TOP_RATED = BASE_URL + "top_rated?api_key=" + api_key;
    public static final String URL_PARAM = "https://api.themoviedb.org/3/movie/211672/videos?api_key="+ api_key + "&language=en-US";
    public static final String URL_TO_YOUTUBE = URL_YOUTUBE + "watch?v=" + key;
    public static final String URL_API_YOUTUBE = "https://api.themoviedb.org/3/movie/211672/videos?api_key=" +api_key;
   /* https://www.youtube.com/watch?v=571b76d8c3a36864e00025a0*/
    /*http://www.youtube.com/watch?v=jc86EFjLFV4*/
   public static final String TAG_JSON_ARRAY="results";

    public static final String tag_iso_639_1="iso_639_1";
    public static final String tag_iso_3166_1="iso_3166_1";
    public static final String tag_key="key";
    public static final String tag_name="name";
    public static final String tag_site="site";
    public static final String tag_size="size";
    public static final String tag_type="type";


}
