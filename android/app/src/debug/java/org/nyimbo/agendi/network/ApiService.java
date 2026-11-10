package org.nyimbo.agendi.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("events") Call<List<EventDto>> events();
    @GET("hymns") Call<List<HymnDto>> hymns(@Query("version") int version);
    @GET("news") Call<List<NewsDto>> news();
}
