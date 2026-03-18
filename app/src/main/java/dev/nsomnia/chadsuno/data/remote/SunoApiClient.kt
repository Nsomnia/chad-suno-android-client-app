package dev.nsomnia.chadsuno.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.nsomnia.chadsuno.domain.model.SongStatus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SunoApiClient(
    private val getCookie: () -> String?,
    baseUrl: String = "https://suno.gcui.ai/"
) {
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(SongStatus::class.java, SongStatusDeserializer())
        .registerTypeAdapter(SongStatus::class.java, SongStatusSerializer())
        .create()

    private val authInterceptor = Interceptor { chain ->
        val cookie = getCookie()
        val request = chain.request().newBuilder()
            .apply {
                if (!cookie.isNullOrEmpty()) {
                    addHeader("Cookie", cookie)
                }
                addHeader("Accept", "application/json")
                addHeader("Content-Type", "application/json")
            }
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val apiService: SunoApiService = retrofit.create(SunoApiService::class.java)

    fun updateBaseUrl(newBaseUrl: String): SunoApiClient {
        return SunoApiClient(getCookie, newBaseUrl)
    }
}
