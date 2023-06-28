package com.example.todoapp.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofitClient: Retrofit? = null

    fun getClient(baseUrl:String):Retrofit{
        if(retrofitClient == null){
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer unaffordable")
                    .build()
                chain.proceed(newRequest)
            }.connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS).addInterceptor(interceptor).build()
            retrofitClient = Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)

                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitClient!!
    }
}