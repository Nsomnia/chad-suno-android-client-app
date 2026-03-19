package dev.nsomnia.chadsuno.data.remote

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import dev.nsomnia.chadsuno.domain.model.SongStatus
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

data class HandshakePayload(
    @SerializedName("suno.com/claims/token_type")
    val tokenType: String?,
    val iss: String?,
    val handshake: List<String>?
)

class SunoApiClient(
    private val getCookie: () -> String?,
    baseUrl: String = "https://suno.gcui.ai/"
) {
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(SongStatus::class.java, SongStatusDeserializer())
        .registerTypeAdapter(SongStatus::class.java, SongStatusSerializer())
        .create()

    private fun extractCookiesFromHandshake(input: String): String {
        val trimmed = input.trim()
        
        if (trimmed.contains("__session=") || trimmed.contains("__client=")) {
            return trimmed
        }
        
        val parts = trimmed.split(".")
        if (parts.size == 3) {
            try {
                val payload = parts[1]
                val paddedPayload = payload.padEnd((payload.length + 3) / 4 * 4, '=')
                val decoded = Base64.decode(paddedPayload, Base64.URL_SAFE).toString(Charsets.UTF_8)
                
                val handshakePayload = gson.fromJson(decoded, HandshakePayload::class.java)
                
                if (handshakePayload.handshake != null && handshakePayload.handshake.isNotEmpty()) {
                    val cookies = mutableListOf<String>()
                    
                    for (cookieEntry in handshakePayload.handshake) {
                        val cookieValue = cookieEntry.substringBefore(";").trim()
                        if (cookieValue.contains("=") && 
                            (cookieValue.startsWith("__session=") || 
                             cookieValue.startsWith("__client") ||
                             cookieValue.startsWith("__clerk"))) {
                            cookies.add(cookieValue)
                        }
                    }
                    
                    if (cookies.isNotEmpty()) {
                        return cookies.joinToString("; ")
                    }
                }
            } catch (e: Exception) {
            }
        }
        
        return trimmed
    }

    private val authInterceptor = Interceptor { chain ->
        val rawInput = getCookie()
        val cookies = if (!rawInput.isNullOrEmpty()) extractCookiesFromHandshake(rawInput) else ""
        val deviceId = UUID.randomUUID().toString()
        
        val request = chain.request().newBuilder()
            .apply {
                if (cookies.isNotEmpty()) {
                    addHeader("Cookie", cookies)
                }
                addHeader("Accept", "application/json")
                addHeader("Content-Type", "application/json")
                addHeader("Device-Id", "\"$deviceId\"")
                addHeader("x-suno-client", "Android prerelease-4nt180t 1.0.42")
                addHeader("X-Requested-With", "com.suno.android")
                addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
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
