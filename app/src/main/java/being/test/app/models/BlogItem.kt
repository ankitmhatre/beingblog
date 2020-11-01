package being.test.app.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.Serializable

@Entity
data class BlogItem(

        @SerializedName("media_url")
        var image_url: String,

        @SerializedName("content")
        var content: String,
        @SerializedName("title")
        var title: String,
        @SerializedName("author_name")
        var author_name: String,
       // @SerializedName("full_url")
       // var full_url: String,
        @SerializedName("reported_on")
        var timestamp: Long,

        @SerializedName("availability")
        var availability: Boolean,

        val isPinned: Int = 0,

        @SerializedName("document_key")
        @PrimaryKey
        var document_key: String


) : Serializable
