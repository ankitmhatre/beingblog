package being.test.app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {



    public static final String BASE_URL = "https://uran-redef.firebaseio.com/";
    public static final String HOSTING_URL = "https://uran-redef.firebaseapp.com/";

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }



}