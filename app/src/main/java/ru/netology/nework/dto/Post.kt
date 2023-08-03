package ru.netology.nework.dto

data class Post(
    val id: Long = 0L,
    val authorId: Long = 0L,
    val author: String = "Me",
    val authorAvatar: String = "",
    val content: String = "",
    val published: Long = 0L,
    val isLiked: Boolean = false,
    val likeCount: Int = 0,
    val coords: PostCoords? = PostCoords(
        lat = 0.0,
        lng = 0.0
    ),
    val attachment: MediaAttachment? = MediaAttachment(
        url = "netology.jpg",
        type = "IMAGE"
    )
) {
}

data class MediaAttachment(
    val url: String = "",
    val type: String = "",
) {

}

data class PostCoords(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
) {

}