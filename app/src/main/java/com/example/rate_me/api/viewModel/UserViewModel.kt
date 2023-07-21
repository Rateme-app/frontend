package com.example.rate_me.api.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rate_me.api.models.User
import com.example.rate_me.api.service.ApiBuilder
import com.example.rate_me.api.service.UserService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val userService = ApiBuilder.buildService(UserService::class.java)

    fun getById(id: String, context: Context): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        val call = userService.getById(id)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return userLiveData
    }

    fun getAll(context: Context): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()

        val call = userService.getAll()
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    usersLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return usersLiveData
    }

    fun login(loginBody: UserService.LoginBody, context: Context): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        val call = userService.login(loginBody)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userLiveData.value = response.body()
                } else {
                    if (response.code() == 403) {
                        Toast.makeText(
                            context,
                            "Invalid credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Network error : ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return userLiveData
    }

    fun loginWithSocial(
        email: String,
        firstname: String,
        lastname: String,
        context: Context
    ): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        val call = userService.loginWithSocial(email, firstname, lastname)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return userLiveData
    }

    fun register(user: User, context: Context): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        val call = userService.register(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userLiveData.value = response.body()
                } else {
                    if (response.code() == 403) {
                        Toast.makeText(
                            context,
                            "User exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Network error : ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return userLiveData
    }

    fun forgotPassword(
        forgotPasswordBody: UserService.ForgotPasswordBody,
        context: Context
    ): LiveData<Unit> {
        val resultLiveData = MutableLiveData<Unit>()

        val call = userService.forgotPassword(forgotPasswordBody)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    resultLiveData.value = Unit
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return resultLiveData
    }

    fun updatePassword(
        updatePasswordBody: UserService.UpdatePasswordBody,
        context: Context
    ): LiveData<Unit> {
        val resultLiveData = MutableLiveData<Unit>()

        val call = userService.updatePassword(updatePasswordBody)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    resultLiveData.value = Unit
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return resultLiveData
    }

    fun updateProfile(user: User, context: Context): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        val call = userService.updateProfile(user)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return userLiveData
    }

    fun updateProfilePicture(
        userId: MultipartBody.Part,
        filename: RequestBody,
        fileToUpload: MultipartBody.Part,
        context: Context
    ): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        val call = userService.updateProfilePicture(userId, filename, fileToUpload)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    userLiveData.value = response.body()
                } else {
                    Toast.makeText(
                        context,
                        "Network error : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("API", "${t.message}")
                Toast.makeText(
                    context,
                    "Api failure : ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        return userLiveData
    }
}
