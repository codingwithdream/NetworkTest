package com.dreamxu.networktest.service

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

// Extract network requests into a public class to improve code reusability

object HttpUtil {

    fun sendOkHttpRequest(address: String, callBack: okhttp3.Callback) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(address)
            .build()
        client.newCall(request).enqueue(callBack)
    }

    fun sendHttpURLRequest(address: String, listener: HttpCallBackListener) {
       // start child thread to complete network request, and use listener to return result
        thread {
            var connection: HttpURLConnection? = null
            try {
                val response = StringBuilder()
                val url = URL(address)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val input = connection.inputStream
                val reader = BufferedReader(InputStreamReader(input))
                reader.use {
                    reader.forEachLine {
                        response.append(it)
                    }
                }
                listener.onFinish(response.toString())
            } catch (e: Exception){
                e.printStackTrace()
                listener.onError(e)
            } finally {
                connection?.disconnect()
            }
        }
    }

    interface HttpCallBackListener{
        fun onFinish(responseData: String)
        fun onError(error: Exception)
    }
}
