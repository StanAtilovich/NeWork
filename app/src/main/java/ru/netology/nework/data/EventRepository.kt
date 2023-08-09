package ru.netology.nework.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.db.AppDb
import ru.netology.nework.db.EventDao
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.db.EventRemoteKeyDao
import ru.netology.nework.dto.Event
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.DbError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UndefinedError
import java.io.IOException
import java.sql.SQLException


class EventRepository @Inject constructor(
    private val appDb: AppDb,
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao

) {

    @ExperimentalPagingApi
    fun getAllEvents(): Flow<Unit> = Pager(
        config = PagingConfig(pageSize = DEFAULT_EVENT_PAGE_SIZE, enablePlaceholders = true),
        remoteMediator = EventRemoteMediator(appDb, eventRemoteKeyDao, apiService, eventDao),
        pagingSourceFactory = { eventDao.getEventPagingSource() }
    ).flow
        .map { entityList ->
            entityList.map { it.toDto() }
        }


    suspend fun createEvent(event: Event) {
        try {
            val createEventResponse = apiService.createEvent(event)
            if (!createEventResponse.isSuccessful) {
                throw ApiError(createEventResponse.code())
            }
            val createEventBody =
                createEventResponse.body() ?: throw ApiError(createEventResponse.code())

            val getEventResponse = apiService.getEventById(createEventBody.id)
            if (!getEventResponse.isSuccessful) throw ApiError(getEventResponse.code())
            val getEventBody = getEventResponse.body() ?: throw ApiError(getEventResponse.code())

            eventDao.insertEvent(EventEntity.fromDto(getEventBody))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UndefinedError
        }
    }

    suspend fun likeEvent(event: Event) {
        try {
            val likedEvent = event.copy(
                likeCount = if (event.likedByMe) event.likeCount.dec()
                else event.likeCount.inc(),
                likedByMe = !event.likedByMe
            )
            eventDao.insertEvent(EventEntity.fromDto(likedEvent))
            val response = if (event.likedByMe) apiService.dislikeEventById(event.id)
            else apiService.likeEventById(event.id)

            if (!response.isSuccessful) throw ApiError(response.code())
        } catch (e: IOException) {
            eventDao.insertEvent(EventEntity.fromDto(event))
            throw NetworkError
        } catch (e: SQLException) {
            eventDao.insertEvent(EventEntity.fromDto(event))
            throw DbError
        } catch (e: Exception) {
            eventDao.insertEvent(EventEntity.fromDto(event))
            throw UndefinedError
        }
    }

    suspend fun participateInEvent(event: Event) {
        try {
            val attendedEvent = event.copy(
                participantsCount = if (event.participatedByMe) event.participantsCount.dec()
                else event.participantsCount.inc(),
                participatedByMe = !event.participatedByMe
            )
            eventDao.insertEvent(EventEntity.fromDto(attendedEvent))
            val response = if (event.participatedByMe) apiService.unparticipateEventById(event.id)
            else apiService.participateEventById(event.id)

            if (!response.isSuccessful) throw ApiError(response.code())
        } catch (e: IOException) {
            eventDao.insertEvent(EventEntity.fromDto(event))
            throw NetworkError
        } catch (e: SQLException) {
            eventDao.insertEvent(EventEntity.fromDto(event))
            throw DbError
        } catch (e: Exception) {
            eventDao.insertEvent(EventEntity.fromDto(event))
            throw UndefinedError
        }
    }

    suspend fun deleteEvent(eventId: Long) {
        val eventToDelete = eventDao.getEventById(eventId)

        try {
            eventDao.deleteEvent(eventId)
            val response = apiService.deleteEvent(eventId)
            if (!response.isSuccessful) {
                eventDao.insertEvent(eventToDelete)
                throw ApiError(response.code())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UndefinedError
        }
    }
}