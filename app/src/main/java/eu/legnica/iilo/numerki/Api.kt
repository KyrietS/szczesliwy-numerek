package eu.legnica.iilo.numerki

import retrofit2.Call
import retrofit2.http.GET

interface Api {
    @GET("/api/numerki")
    fun getNumbers(): Call<ApiResponse>
}