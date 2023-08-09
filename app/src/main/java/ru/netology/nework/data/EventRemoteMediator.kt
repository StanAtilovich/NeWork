package ru.netology.nework.data

import EventRemoteKeyEntity
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nework.api.ApiService
import ru.netology.nework.db.AppDb
import ru.netology.nework.db.EventDao
import ru.netology.nework.db.EventRemoteKeyDao
import ru.netology.nework.dto.Event
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.toEntity
import ru.netology.nework.error.ApiError

const val DEFAULT_EVENT_PAGE_SIZE: Int = 10

@ExperimentalPagingApi
class EventRemoteMediator(
    private val appDb: AppDb,
    private val remoteKeyDao: EventRemoteKeyDao,
    private val apiService: ApiService,
    private val eventDao: EventDao
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    apiService.getLatestEvents(state.config.initialLoadSize)
                }
                LoadType.PREPEND -> {
                    val maxKey = remoteKeyDao.getMaxKey() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getEventsAfter(maxKey, state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val minKey = remoteKeyDao.getMinKey() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getEventsBefore(minKey, state.config.pageSize)
                }
            }
            if (!response.isSuccessful) throw ApiError(response.code())
            val receivedBody = response.body() ?: throw ApiError(response.code())
            if (receivedBody.isEmpty()) return MediatorResult.Success(
                endOfPaginationReached = true)

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        remoteKeyDao.removeAll()
                        insertMinKey(receivedBody)
                        insertMaxKey(receivedBody)
                        eventDao.clearEventTable()
                    }
                    LoadType.PREPEND -> insertMaxKey(receivedBody)
                    LoadType.APPEND -> insertMinKey(receivedBody)
                }
                eventDao.insertEvents(receivedBody.toEntity())
            }
            return MediatorResult.Success(
                endOfPaginationReached =
                receivedBody.isEmpty()
            )
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun insertMaxKey(receivedBody: List<Event>) {
        remoteKeyDao.insertKey(
            EventRemoteKeyEntity(
                type = EventRemoteKeyEntity.KeyType.AFTER,
                id = receivedBody.first().id
            )
        )
    }

    private suspend fun insertMinKey(receivedBody: List<Event>) {
        remoteKeyDao.insertKey(
            EventRemoteKeyEntity(
                type = EventRemoteKeyEntity.KeyType.BEFORE,
                id = receivedBody.last().id,
            )
        )
    }
}