package com.dreamxu.networktest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dreamxu.networktest.model.ContactPerson
import com.dreamxu.networktest.service.ContactService
import com.dreamxu.networktest.service.HttpUtil
import com.dreamxu.networktest.service.ServiceCreator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // The address of the specified server is the computer, not 127.0.0.1
    private val address = "http://10.0.2.2/contacts.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendRequestBtn.setOnClickListener {

            // 1. HttpURLConnection
            HttpUtil.sendHttpURLRequest(address, object : HttpUtil.HttpCallBackListener{
                override fun onFinish(responseData: String) {
                    parseJSONWithJSONObject(responseData)
                }

                override fun onError(error: Exception) {
                    Log.e("MainActivity", error.toString())
                }
            })

            // 2. OkHttp
            /*HttpUtil.sendOkHttpRequest(address, object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val responseData: String? = response.body?.string()
                    if (responseData != null) {
                        parseJSONWithGSON(responseData)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.e("MainActivity", e.toString())
                }
            })*/
        }

        getContactDataBtn.setOnClickListener {
            val contactService = ServiceCreator.create<ContactService>()
            // when use okhttp.callback, it is wrong
            contactService.getContacts().enqueue(object : retrofit2.Callback<List<ContactPerson>> {
                override fun onResponse(call: Call<List<ContactPerson>>, response: Response<List<ContactPerson>>) {
                    val contactsList = response.body()
                    if (contactsList != null) {
                        for (contact in contactsList) {
                            Log.d("MainActivity", contact.title)
                        }
                    }
                }

                override fun onFailure(call: Call<List<ContactPerson>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    private fun parseJSONWithJSONObject(jsonData: String) {
        try {
            val jsonArray = JSONArray(jsonData)
            for (i in 0 until jsonArray.length()) {
                var jsonObject = jsonArray.getJSONObject(i)
                var firstName = jsonObject.getString("first_name")
                Log.d("MainActivity: ", "firstName is $firstName")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseJSONWithGSON(jsonData: String) {
        val gson = Gson()
        val typeOf = object : TypeToken<List<ContactPerson>>() {}.type
        val contactList = gson.fromJson<List<ContactPerson>>(jsonData, typeOf)
        for (contact in contactList) {
            Log.d("MainActivity", "avatar_filename is ${contact.avatar_filename}")
        }
    }

    private fun showResponse(responseData: String) {
        runOnUiThread{
            responseText.text = responseData
        }
    }
}