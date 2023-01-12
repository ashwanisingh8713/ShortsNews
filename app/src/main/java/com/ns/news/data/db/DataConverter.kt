package com.ns.news.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.ns.news.data.api.model.*
import com.squareup.moshi.*
import java.lang.reflect.Type


@ProvidedTypeConverter
class DataConverter(var moshi: Moshi) {

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
        return moshi.adapter(AWDataItem::class.java).fromJson(json)
    }

    @TypeConverter
    fun fromListAuthorItem(data: List<AuthorItem>): String? {
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            AuthorItem::class.java
        )
        return moshi.adapter<Any>(type).toJson(data)
    }


    @TypeConverter
    fun toListAuthorItem(json: String): List<AuthorItem>? {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            AuthorItem::class.java
        )
        val adapter: JsonAdapter<List<AuthorItem>> = moshi.adapter<List<AuthorItem>>(type)
        return adapter.fromJson(json)!!.map { it }
    }

    @TypeConverter
    fun fromAuthorItem(data: AuthorItem): String {
        return moshi.adapter(AuthorItem::class.java).toJson(data)
    }

    @TypeConverter
    fun toAuthorItem(json: String): AuthorItem? {
        return moshi.adapter(AuthorItem::class.java).fromJson(json)
    }


    @TypeConverter
    fun fromMediaItem(data: MediaItem): String {
        return moshi.adapter(MediaItem::class.java).toJson(data)
    }

    @TypeConverter
    fun toMediaItem(json: String): MediaItem? {
        return moshi.adapter(MediaItem::class.java).fromJson(json)
    }

    @TypeConverter
    fun fromImages(data: Images): String {
        return moshi.adapter(Images::class.java).toJson(data)
    }

    @TypeConverter
    fun toImages(json: String): Images? {
        return moshi.adapter(Images::class.java).fromJson(json)
    }


    @TypeConverter
    fun fromListSectionItem(data: List<SectionItem>): String? {
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            SectionItem::class.java
        )
        return moshi.adapter<Any>(type).toJson(data)
    }


    @TypeConverter
    fun toListSectionItem(json: String): List<SectionItem>? {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            SectionItem::class.java
        )
        val adapter: JsonAdapter<List<SectionItem>> = moshi.adapter<List<SectionItem>>(type)
        return adapter.fromJson(json)!!.map { it }
    }

    @TypeConverter
    fun fromSectionItem(data: SectionItem): String {
        return moshi.adapter(SectionItem::class.java).toJson(data)
    }

    @TypeConverter
    fun toSectionItem(json: String): SectionItem? {
        return moshi.adapter(SectionItem::class.java).fromJson(json)
    }

    @TypeConverter
    fun fromLogo(data: Logo): String {
        return moshi.adapter(Logo::class.java).toJson(data)
    }

    @TypeConverter
    fun toLogo(json: String): Logo? {
        return moshi.adapter(Logo::class.java).fromJson(json)
    }

    @TypeConverter
    fun fromCellBackground(data: CellBackground): String {
        return moshi.adapter(CellBackground::class.java).toJson(data)
    }

    @TypeConverter
    fun toCellBackground(json: String): CellBackground? {
        return moshi.adapter(CellBackground::class.java).fromJson(json)
    }


    @TypeConverter
    fun fromListString(data: List<String>): String? {
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            String::class.java
        )
        return moshi.adapter<Any>(type).toJson(data)
    }


    @TypeConverter
    fun toListString(json: String): List<String>? {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            String::class.java
        )
        val adapter: JsonAdapter<List<String>> = moshi.adapter<List<String>>(type)
        return adapter.fromJson(json)!!.map { it }
    }

    @TypeConverter
    fun fromListTagItem(data: List<TagItem>): String? {
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            TagItem::class.java
        )
        return moshi.adapter<Any>(type).toJson(data)
    }


    @TypeConverter
    fun toListTagItem(json: String): List<TagItem>? {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            TagItem::class.java
        )
        val adapter: JsonAdapter<List<TagItem>> = moshi.adapter<List<TagItem>>(type)
        return adapter.fromJson(json)!!.map { it }
    }

    @TypeConverter
    fun fromListMediaItem(data: List<MediaItem>): String? {
        val type: Type = Types.newParameterizedType(
            MutableList::class.java,
            MediaItem::class.java
        )
        return moshi.adapter<Any>(type).toJson(data)
    }


    @TypeConverter
    fun toListMediaItem(json: String): List<MediaItem>? {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            MediaItem::class.java
        )
        val adapter: JsonAdapter<List<MediaItem>> = moshi.adapter<List<MediaItem>>(type)
        return adapter.fromJson(json)!!.map { it }
    }


}
