package com.dreamxu.networktest.service

import com.dreamxu.networktest.model.ContactPerson
import retrofit2.Call
import retrofit2.http.GET

interface ContactService {
    @GET("contacts.json")
    fun getContacts(): Call<List<ContactPerson>>
}