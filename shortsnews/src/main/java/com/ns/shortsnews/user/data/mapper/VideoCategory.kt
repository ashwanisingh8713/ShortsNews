package com.ns.shortsnews.user.data.mapper

/**
 * Created by Ashwani Kumar Singh on 24,April,2023.
 */
data class VideoCategory(
    val status: Boolean = false,
    val id: String = "",
    val name: String = ""
) {
    fun mapper(status: Boolean, id: String, name: String):VideoCategory {
        return VideoCategory(status = status, id= id, name=name)
    }
}
