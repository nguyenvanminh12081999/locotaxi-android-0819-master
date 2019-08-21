package com.suusoft.locoindia.retrofit;

import com.suusoft.locoindia.retrofit.response.RespondGetOnline;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.suusoft.aradriver.retrofit.param.Param.PARAM_TOKEN;


public interface APIService {

    @POST("/posts")
    @FormUrlEncoded
    Call<Object> savePost(@Field("title") String title,
                          @Field("body") String body,
                          @Field("userId") long userId);


    @GET("get-online-duration")
    @Headers("Cache-Control: no-cache") // no cache
    Call<RespondGetOnline> getOnline(@Query(PARAM_TOKEN) String token);

//    @GET("transport/trip-list-new")
//    @Headers("Cache-Control: no-cache") // no cache
//    Call<ResponseListTripObj> getTripListNew(@Query(PARAM_USER_ID) String userId,
//                                             @Query(PARAM_TOKEN) String token);
}
