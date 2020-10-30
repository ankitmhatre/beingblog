package com.local.city.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class BlogItem(
        @SerializedName("nid")
        @PrimaryKey
        var nid: Long,
        @SerializedName("image")
        var image_url: String,
        @SerializedName("content")
        var content: String,
        @SerializedName("title")
        var title: String,
        @SerializedName("reported_by")
        var publisher: String,
       // @SerializedName("full_url")
       // var full_url: String,
        @SerializedName("reported_on")
        var timestamp: Long,

        @SerializedName("availability")
        var availability: String,

        @SerializedName("category_id")
        var category_id: String,

        @SerializedName("media_type")
        var media_type: String,

        val isPinned: Int = 0

) : Serializable
/*
package com.local.city.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class NewsItem(

        @SerializedName("nid")
        @PrimaryKey
        var nid: Long,
        @SerializedName("image")
        var image_url: String,
        @SerializedName("content")
        var content: String,
        @SerializedName("title")
        var title: String,
        @SerializedName("publisher")
        var publisher: String,
        @SerializedName("full_url")
        var full_url: String,
        @SerializedName("timestamp")
        var timestamp: Long,

        val isPinned: Int = 0
)

 */