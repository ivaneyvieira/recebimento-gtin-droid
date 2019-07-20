package br.com.pintos.recebimentogtin

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInitializer {
  val URL_REST = "http://10.1.10.244:8989/"
//  val URL_REST = "http://10.1.10.100:8080/"

  val okHttpClient = OkHttpClient.Builder()
      .readTimeout(60, TimeUnit.SECONDS)
      .connectTimeout(60, TimeUnit.SECONDS)
      .build()

  val retrofit = Retrofit.Builder()
      .baseUrl(URL_REST)
      .addConverterFactory(GsonConverterFactory.create())
      .validateEagerly(true)
      .client(okHttpClient)
      .build()

  fun gtinService() = retrofit.create(GtinService::class.java)
}