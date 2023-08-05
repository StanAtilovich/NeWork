package ru.netology.nework.data


import ru.netology.nework.api.PostApi
import ru.netology.nework.db.PostDao
import ru.netology.nework.db.PostEntity
import ru.netology.nework.db.toDto
import ru.netology.nework.db.toEntity
import ru.netology.nework.dto.Post
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.DbError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UndefinedError
import java.sql.SQLException

class PostRepository(private val postDao: PostDao) {

    fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map {
            it.toDto()
        }

    }

    suspend fun createPost(post: Post) {
        try {
            val createPostResponse = PostApi.retrofitService.createPost(post)
            if (!createPostResponse.isSuccessful) {
                Error().printStackTrace()
                throw ApiError(createPostResponse.code())
            }
            val createPostBody =
                createPostResponse.body() ?: throw ApiError(createPostResponse.code())

            val getPostResponse = PostApi.retrofitService.getPostById(createPostBody.id)
            if (!getPostResponse.isSuccessful) {
                throw ApiError(getPostResponse.code())
            }
            val getPostBody = getPostResponse.body() ?: throw ApiError(getPostResponse.code())

            postDao.createPost(PostEntity.fromDto(getPostBody))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw DbError
        } catch (e: Exception) {
            throw UndefinedError
        }
    }

    suspend fun deletePost(postId: Long) {
        val postToDelete = postDao.getPostById(postId)
        try {
            postDao.deletePost(postId)

            val response = PostApi.retrofitService.deletePost(postId)
            if (!response.isSuccessful) {
                postDao.createPost(postToDelete)
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

    suspend fun loadPostsFromWeb() {
        try {
            val response = PostApi.retrofitService.getAllPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.code())

            }

            val body = response.body() ?: throw ApiError(response.code())
            postDao.createPosts(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw UndefinedError

        }
    }

}


