package com.example.rate_me.api.service

import com.example.rate_me.api.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    data class LoginBody(
        val email: String,
        val password: String
    )

    data class ForgotPasswordBody(
        val resetCode: String,
        val email: String
    )

    data class UpdatePasswordBody(
        val email: String,
        val newPassword: String
    )

    @GET("/user/{id}")
    fun getById(@Path("id") id: String): Call<User>

    @GET("/user")
    fun getAll(): Call<List<User>>

    @POST("/user/login")
    fun login(@Body loginBody: LoginBody): Call<User>

    @POST("/user/login-with-social")
    fun loginWithSocial(
        @Query("email") email: String,
        @Query("firstname") firstname: String,
        @Query("lastname") lastname: String
    ): Call<User>

    @POST("/user/register")
    fun register(@Body user: User): Call<User>

    @POST("/user/forgot-password")
    fun forgotPassword(@Body forgotPasswordBody: ForgotPasswordBody): Call<Unit>

    @PUT("/user/update-password")
    fun updatePassword(@Body updatePasswordBody: UpdatePasswordBody): Call<Unit>

    @PUT("/user/update-profile")
    fun updateProfile(@Body user: User): Call<User>

    @Multipart
    @POST("/user/update-profile-picture")
    fun updateProfilePicture(
        @Part userId: MultipartBody.Part,
        @Part("picture") name: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<User>
}