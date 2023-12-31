package ru.netology.nework.dto

data class Post(
    val id: Long = 0L,
    val authorId: Long = 0L,
    val author: String = "Me",
    val authorAvatar: String = "",
    val content: String = "",
    val published: Long = 0L,
    val likedByMe: Boolean = false,
    val likeCount: Int = 0,
    val coords: Coords? = Coords(
        lat = 0.0,
        lng = 0.0
    ),
    val attachment: MediaAttachment? = MediaAttachment(
        url = "netology.jpg",
        type = "IMAGE"
    ),
    val ownedByMe: Boolean = false,
    val likeOwnerIds: Set<Int> = emptySet<Int>()
)


data class MediaAttachment(
    val url: String = "",
    val type: String = "",
)



data class Coords(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
)

