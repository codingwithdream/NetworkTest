package com.dreamxu.networktest.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton, Retrofit is developed based on OkHttp. But OkHttp now requires that you enable Java 8
// in your builds to function. So you need to add compileOptions in build.gradle
object ServiceCreator {

    private const val BASE_URL = "http://10.0.2.2/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // External Call: ServiceCreator.create(ContactService::class.java)
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    // External Call: ServiceCreator.create<ContactService>()
    inline fun <reified T> create(): T = create(T::class.java)

}