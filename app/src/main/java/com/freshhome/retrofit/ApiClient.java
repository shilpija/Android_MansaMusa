package com.freshhome.retrofit;

import android.content.Context;

import com.freshhome.CommonUtil.AllAPIs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//public class ApiClient {
//
//    private static Retrofit retrofit = null;
//
//    public static Retrofit getClient() {
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//                Request request = original.newBuilder()
//                        .header("Authorization", "Bearer " + AllAPIs.token)
//                        .header("Accept", "application/json")
//                        .method(original.method(), original.body())
//                        .build();
//                return chain.proceed(request);
//            }
//        });
//
//        OkHttpClient client = httpClient.build();
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(AllAPIs.BaseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(client)
//                    .build();
//        }
//        return retrofit;
//    }

public class ApiClient {
    private static final String Base_URL = "http://18.191.90.186:3000/mobile/";
    private static ApiClient apiClientConnection = null;
    private static ApiInterface apiInterface = null;
    private ApiInterface clientService;

    public static ApiClient getInstance() {
        if (apiClientConnection == null) {
            apiClientConnection = new ApiClient();
        }
        return apiClientConnection;
    }

    public ApiInterface getClient() {
        if (apiInterface == null) {

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(80, TimeUnit.SECONDS)
                    .readTimeout(80, TimeUnit.SECONDS);


            httpBuilder.addNetworkInterceptor(loggingInterceptor);
            httpBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + AllAPIs.token)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });


            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(AllAPIs.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build());

            Retrofit retrofit = builder.build();
            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }


    public ApiInterface getClientPay() {
        if (apiInterface != null) {

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(80, TimeUnit.SECONDS)
                    .readTimeout(80, TimeUnit.SECONDS);


            httpBuilder.addNetworkInterceptor(loggingInterceptor);
            httpBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + AllAPIs.token)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            });


            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(AllAPIs.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build());

            Retrofit retrofit = builder.build();
            apiInterface = retrofit.create(ApiInterface.class);
        }
        return apiInterface;
    }


}

