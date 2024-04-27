package com.example.game.retrofit;

import android.content.Context;

import com.example.game.helpers.SharedPreferenceUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class RetrofitClient {
    private static RetrofitClient instance;
    private SharedPreferenceUtils sharedPreferenceUtils ;
    private String baseUrl  = "https://religator.loca.lt";
    //private String baseUrl  = "http://192.168.2.32:3001";
    private String loginUrl  = baseUrl + "/users/authenticate";
    private String token;
    private ApiRequest apiRequest;
    public static RetrofitClient init(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

//    .connectTimeout(5, TimeUnit.MINUTES)
//                .writeTimeout(5, TimeUnit.MINUTES)
//                .readTimeout(5, TimeUnit.MINUTES)
    public RetrofitClient(Context context) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        sharedPreferenceUtils = new SharedPreferenceUtils(context);
        token = sharedPreferenceUtils.getString("token","");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request()
                        .newBuilder()
                        .header("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                return chain.proceed(newRequest);
            }
        }).build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build();
        apiRequest = retrofit.create(ApiRequest.class);
    }
    public ApiRequest getApiRequest() {
        return apiRequest;
    }

}