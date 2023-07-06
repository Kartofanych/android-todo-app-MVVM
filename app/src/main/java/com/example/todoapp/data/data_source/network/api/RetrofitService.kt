package com.example.todoapp.data.data_source.network.api

import com.example.todoapp.data.data_source.network.dto.responses.GetListApiResponse
import com.example.todoapp.data.data_source.network.dto.requests.PatchListApiRequest
import com.example.todoapp.data.data_source.network.dto.requests.PostItemApiRequest
import com.example.todoapp.data.data_source.network.dto.responses.PostItemApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {


    @GET("list")
    suspend fun getList(): GetListApiResponse

    @PATCH("list")
    suspend fun updateList(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body list: PatchListApiRequest
    ): GetListApiResponse

    @POST("list")
    suspend fun postElement(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body element: PostItemApiRequest
    ): PostItemApiResponse

    @DELETE("list/{id}")
    suspend fun deleteElement(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
    ): PostItemApiResponse

    @PUT("list/{id}")
    suspend fun updateElement(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body item: PostItemApiRequest
    ): PostItemApiResponse


}