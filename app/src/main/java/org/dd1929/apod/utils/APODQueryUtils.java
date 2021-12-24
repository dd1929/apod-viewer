package org.dd1929.apod.utils;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.dd1929.apod.BuildConfig;
import org.dd1929.apod.models.APOD;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APODQueryUtils {

    private static final String TAG = "APODQueryUtils";

    private static String API_KEY;

    private final GsonBuilder mMultiParsingGson;
    private final Retrofit.Builder mRetrofitBuilder;

    private final DataFetchListener mListener;

    public interface DataFetchListener{
        void onDataFetched(APOD apod);
        void onDataFetched(List<APOD> apods);
    }

    public APODQueryUtils(String apiKey, String baseUrl, DataFetchListener listener){
        API_KEY = apiKey;
        mListener = listener;

        Type APODTypeList = new TypeToken<ArrayList<APOD>>(){}.getType();
        mMultiParsingGson = new GsonBuilder().registerTypeAdapter(APODTypeList, new APODDeserializer());

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		
		if (BuildConfig.DEBUG){
			HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
			interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
			clientBuilder.addInterceptor(interceptor);
		}

        mRetrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl).client(clientBuilder.build());
    }

    public void fetchSingle(Date date){
        String dateString = null;
        if (date != null){
            dateString = DateParseUtils.getStringFromDate(date);
        }

        Retrofit retrofit = mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create()).build();

        APODClient apodClient = retrofit.create(APODClient.class);
        Call<APOD> call = apodClient.getAPOD(API_KEY, false, dateString);

        call.enqueue(new Callback<APOD>() {
            @Override
            public void onResponse(Call<APOD> call, Response<APOD> response) {
                if (response.body() != null){
                    mListener.onDataFetched(response.body());
                }
            }

            @Override
            public void onFailure(Call<APOD> call, Throwable t) {

            }
        });
    }

    public void fetchRandom(int count){
        Gson gson = mMultiParsingGson.create();
        Retrofit retrofit = mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson)).build();

        APODClient apodClient = retrofit.create(APODClient.class);
        Call<List<APOD>> call = apodClient.getRandom(API_KEY, count);

        call.enqueue(new Callback<List<APOD>>() {
            @Override
            public void onResponse(Call<List<APOD>> call, Response<List<APOD>> response) {
                List<APOD> apods = response.body();
                if(apods != null){
                    apods.get(0).setIsRandom(true);
                    mListener.onDataFetched(apods);
                }
            }

            @Override
            public void onFailure(Call<List<APOD>> call, Throwable t) {

            }
        });
    }

    public void fetchRange(String startDate, String endDate){
        Gson gson = mMultiParsingGson.create();
        Retrofit retrofit = mRetrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson)).build();

        APODClient apodClient = retrofit.create(APODClient.class);
        Call<List<APOD>> call = apodClient.getMonthly(API_KEY, startDate, endDate);

        call.enqueue(new Callback<List<APOD>>() {
            @Override
            public void onResponse(Call<List<APOD>> call, Response<List<APOD>> response) {
                List<APOD> apods = response.body();
                if(apods != null){
                    mListener.onDataFetched(apods);
                }
            }

            @Override
            public void onFailure(Call<List<APOD>> call, Throwable t) {

            }
        });
    }
}
