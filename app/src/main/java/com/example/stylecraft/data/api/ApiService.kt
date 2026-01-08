package com.example.stylecraft.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    companion object {
        const val BASE_URL = "https://stylecraft-backend.onrender.com/"
        // if Render shows a slightly different host, copy that exactly, with trailing slash
    }

    @Multipart
    @POST("analyze-face")
    suspend fun analyzeFaceShape(
        @Part image: MultipartBody.Part
    ): Response<FaceShapeResponse>

    @Multipart
    @POST("apply-hair-color")
    suspend fun applyHairColor(
        @Part image: MultipartBody.Part,
        @Part("color") color: RequestBody,
        @Part("intensity") intensity: RequestBody
    ): Response<HairColorResponse>

    @Multipart
    @POST("apply-hair-adjustments")
    suspend fun applyHairAdjustments(
        @Part image: MultipartBody.Part,
        @Part("brightness") brightness: RequestBody,
        @Part("contrast") contrast: RequestBody,
        @Part("saturation") saturation: RequestBody,
        @Part("highlight") highlight: RequestBody,
        @Part("shadow") shadow: RequestBody,
        @Part("color") color: RequestBody?,
        @Part("colorIntensity") colorIntensity: RequestBody?
    ): Response<HairColorResponse>
}

data class HairColorResponse(
    val image: String,  // Base64 encoded image
    val width: Int,
    val height: Int
)
