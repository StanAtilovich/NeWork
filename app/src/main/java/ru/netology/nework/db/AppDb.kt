package ru.netology.nework.db

import EventRemoteKeyEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.PostRemoteKeyEntity


@Database(
    entities = [PostEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        PostRemoteKeyEntity::class], version = 3
)
abstract class AppDb: RoomDatabase() {
    abstract fun postDao() : PostDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}