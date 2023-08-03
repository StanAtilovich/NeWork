package ru.netology.nework.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nework.api.PostApi
import ru.netology.nework.db.PostDao
import ru.netology.nework.db.PostEntity
import ru.netology.nework.db.toDto
import ru.netology.nework.db.toEntity
import ru.netology.nework.dto.Post
import java.io.IOException

class PostRepository(private val postDao: PostDao) {

    fun getAllPosts(): LiveData<List<Post>> {
        return postDao.getAllPosts().map {
            it.toDto()
        }

    }

    suspend fun createPost(post: Post) {
        try {
            val response = PostApi.retrofitService.createPost(post)
            if (!response.isSuccessful) {
                Error().printStackTrace()
                throw Error()
                // TODO create a comprehensive wrapper for all app errors
            }
            // TODO create a comprehensive wrapper for all app errors
            val body = response.body() ?: throw Error()
            postDao.createPost(PostEntity.fromDto(body))
        } catch (e: IOException) {
            e.printStackTrace()
            // TODO create a comprehensive wrapper for all app errors
        } catch (e: Exception) {
            e.printStackTrace()
            // TODO create a comprehensive wrapper for all app errors
        }
    }

    suspend fun deletePost(postId: Long) {
        val postToDelete = postDao.getPostById(postId)
        try {
            postDao.deletePost(postId)

            val response = PostApi.retrofitService.deletePost(postId)
            if (!response.isSuccessful) {
                postDao.createPost(postToDelete)
                throw Error()

            }
        } catch (e: IOException) {
            e.printStackTrace()

        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

   suspend fun loadPostsFromWeb() {
       try {
           val response = PostApi.retrofitService.getAllPosts()
           if (!response.isSuccessful){
               throw Error()

           }

           val body = response.body() ?: throw Error()
           postDao.createPosts(body.toEntity())
       } catch (e: IOException) {
           e.printStackTrace()

       } catch (e: Exception) {
           e.printStackTrace()

       }
    }

}


