package ru.netology.nework.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.api.ApiService
import ru.netology.nework.db.AppDb
import ru.netology.nework.db.EventDao
import ru.netology.nework.db.EventRemoteKeyDao
import ru.netology.nework.db.PostDao
import ru.netology.nework.db.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(
        apiService: ApiService,
        postDao: PostDao,
        appDb: AppDb,
        postRemoteKeyDao: PostRemoteKeyDao
    ): PostRepository =
        PostRepository(postDao, apiService, appDb, postRemoteKeyDao)

    @Provides
    @Singleton
    fun providesSignInUpRepository(apiService: ApiService): SignInUpRepository =
        SignInUpRepository(apiService)

    @Provides
    @Singleton
    fun provideEventRepository(
        apiService: ApiService,
        eventDao: EventDao,
        appDb: AppDb,
        eventRemoteKeyDao: EventRemoteKeyDao
    ): EventRepository = EventRepository(appDb, apiService, eventDao, eventRemoteKeyDao)
}