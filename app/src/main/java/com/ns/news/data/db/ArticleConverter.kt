package com.ns.news.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.ns.news.data.api.model.AWDataItem
import com.ns.news.data.api.model.AuthorItem
import com.ns.news.data.api.model.Images
import com.ns.news.data.api.model.MediaItem
import com.squareup.moshi.*
import java.lang.reflect.Type


@ProvidedTypeConverter
class ArticleConverter(var moshi: Moshi) {
    @TypeConverter
    fun fromListAWDataItem(data: List<AWDataItem>): String? {
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            AWDataItem::class.java
        )
        return moshi.adapter<Any>(type).toJson(data)
    }


    @TypeConverter
    fun toListAWDataItem(json: String): List<AWDataItem>? {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            AWDataItem::class.java
        )
        val adapter: JsonAdapter<List<AWDataItem>> = moshi.adapter<List<AWDataItem>>(type)
        return adapter.fromJson(json)!!.map { it }
    }

    @TypeConverter
    fun fromAWDataItem(data: AWDataItem): String {
        return moshi.adapter(AWDataItem::class.java).toJson(data)
    }

    @TypeConverter
    fun toAWDataItem(json: String): AWDataItem? {
        return moshi.adapter(AWDataItem::class.java).fromJson(json)}

    @TypeConverter
    fun fromAuthorItem(data: AuthorItem): String {
        return moshi.adapter(AuthorItem::class.java).toJson(data)
    }

    @TypeConverter
    fun toAuthorItem(json: String): AuthorItem? {
        return moshi.adapter(AuthorItem::class.java).fromJson(json)}


    @TypeConverter
    fun fromMediaItem(data: MediaItem): String {
        return moshi.adapter(MediaItem::class.java).toJson(data)
    }

    @TypeConverter
    fun toMediaItem(json: String): MediaItem? {
        return moshi.adapter(MediaItem::class.java).fromJson(json)}

    @TypeConverter
    fun fromImages(data: Images): String {
        return moshi.adapter(Images::class.java).toJson(data)
    }

    @TypeConverter
    fun toImages(json: String): Images? {
        return moshi.adapter(Images::class.java).fromJson(json)}

}
