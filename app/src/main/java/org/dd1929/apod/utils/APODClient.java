package org.dd1929.apod.utils;

import org.dd1929.apod.models.APOD;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APODClient {

    @GET("planetary/apod")
    Call<APOD> getAPOD(@Query("api_key") String apiKey, @Query("thumbs") Boolean thumbs, @Query("date") String date);

    @GET("planetary/apod")
    Call<List<APOD>> getRandom(@Query("api_key") String apiKey, @Query("count") int count);

    @GET("planetary/apod")
    Call<List<APOD>> getMonthly(@Query("api_key") String apiKey, @Query("start_date") String startDate, @Query("end_date") String endDate);

}
