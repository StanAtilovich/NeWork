package ru.netology.nework.data


import androidx.room.withTransaction
import ru.netology.nework.db.PostDao
import ru.netology.nework.db.PostEntity
import ru.netology.nework.db.toDto
import ru.netology.nework.db.toEntity
import ru.netology.nework.dto.Post
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.db.AppDb
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.DbError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UndefinedError
import java.sql.SQLException
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postDao: PostDao,
    private val postApi: ApiService,
    private val db: AppDb
) {
    fun getAllPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map {
            it.toDto()
        }
    }

    suspend fun createPost(post: Post) {
        try {
            val createPostResponse = postApi.createPost(post)
            if (!createPostResponse.isSuccessful) {
                throw ApiError(createPostResponse.code())
            }
            val createPostBody =
                createPostResponse.body() ?: throw ApiError(createPostResponse.code())
            val getPostResponse = postApi.getPostById(createPostBody.id)
            if (!getPostResponse.isSuccessful) {
                throw ApiError(getPostResponse.code())
            }
            val getPostBody = getPostResponse.body() ?: throw ApiError(getPostResponse.code())
            postDao.insertPost(PostEntity.fromDto(getPostBody))
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
            val response = postApi.deletePost(postId)
            if (!response.isSuccessful) {
                postDao.insertPost(postToDelete)
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
            val response = postApi.getAllPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.code())
            }
            val body = response.body()?.map {
                it.copy(likeCount = it.likeOwnerIds.size)
            } ?: throw ApiError(response.code())
            db.withTransaction {
                postDao.clearPostTable()
                postDao.insertPosts(body.toEntity())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: SQLException) {
            throw UndefinedError
        }
    }

    suspend fun likePost(post: Post) {
        try {
            val likedPost = post.copy(
                likeCount = if (post.likedByMe) post.likeCount.dec()
                else post.likeCount.inc(),
                likedByMe = !post.likedByMe
            )
            postDao.insertPost(PostEntity.fromDto(likedPost))

            val response = if (post.likedByMe) postApi.dislikePostById(post.id)
            else postApi.likePostById(post.id)

            if (!response.isSuccessful)
                throw ApiError(response.code())
        } catch (e: IOException) {
            postDao.insertPost(PostEntity.fromDto(post))
            throw NetworkError
        } catch (e: SQLException) {
            postDao.insertPost(PostEntity.fromDto(post))
            throw DbError
        } catch (e: Exception) {
            postDao.insertPost(PostEntity.fromDto(post))
            throw UndefinedError
        }
    }
}


