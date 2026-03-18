package dev.nsomnia.chadsuno.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nsomnia.chadsuno.data.local.AppPreferences
import dev.nsomnia.chadsuno.data.local.ChadSunoDatabase
import dev.nsomnia.chadsuno.data.local.SongDao
import dev.nsomnia.chadsuno.data.remote.SunoApiClient
import dev.nsomnia.chadsuno.data.remote.SunoApiService
import dev.nsomnia.chadsuno.data.repository.GenerationRepository
import dev.nsomnia.chadsuno.data.repository.SongRepository
import dev.nsomnia.chadsuno.data.repository.SunoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChadSunoDatabase {
        return Room.databaseBuilder(
            context,
            ChadSunoDatabase::class.java,
            "chadsuno_db"
        ).build()
    }

    @Provides
    fun provideSongDao(database: ChadSunoDatabase): SongDao {
        return database.songDao()
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSunoApiClient(
        @ApplicationContext context: Context,
        preferences: AppPreferences
    ): SunoApiClient {
        return SunoApiClient(
            getCookie = {
                runBlocking {
                    preferences.cookie.first()
                }
            },
            baseUrl = runBlocking { preferences.apiBaseUrl.first() } ?: "https://suno.gcui.ai/"
        )
    }

    @Provides
    @Singleton
    fun provideSunoApiService(client: SunoApiClient): SunoApiService {
        return client.apiService
    }

    @Provides
    @Singleton
    fun provideSongRepository(songDao: SongDao, apiService: SunoApiService): SongRepository {
        return SongRepository(songDao, apiService)
    }

    @Provides
    @Singleton
    fun provideGenerationRepository(apiService: SunoApiService): GenerationRepository {
        return GenerationRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSunoRepository(apiService: SunoApiService): SunoRepository {
        return SunoRepository(apiService)
    }
}
