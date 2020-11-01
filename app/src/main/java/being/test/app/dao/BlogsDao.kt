package being.test.app.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import being.test.app.models.BlogItem

@Dao
interface BlogsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(Blog: BlogItem)

    @Query("SELECT * FROM BlogItem where title LIKE  '%' || :filter  || '%' OR content LIKE  '%' || :filter  || '%' ORDER BY timestamp DESC")
    fun getAllBlog(filter: String): LiveData<List<BlogItem>>

    @Query("DELETE FROM BlogItem")
    fun deleteAllBlog()

    @Query("SELECT * FROM BlogItem WHERE document_key = :documenKey")
    fun getBlogItem(documenKey: String): LiveData<BlogItem>


    @Query("UPDATE BlogItem SET isPinned = :isPin WHERE document_key = :documenKey")
    fun updatePinned(documenKey: String, isPin: Int)


    @Query("SELECT isPinned FROM BlogItem WHERE document_key = :document_key")
    fun getPinnedStatus(document_key: String): LiveData<Int>

    @Query("SELECT * FROM BlogItem WHERE isPinned = 1 AND content LIKE '%' || :filter || '%'")
    fun getAllPinnedBlog(filter: String): LiveData<List<BlogItem>>

    @Query("SELECT * FROM BlogItem WHERE timestamp < :suppliedStamp")
    fun getOldBlogThan(suppliedStamp: Long): LiveData<List<BlogItem>>

    @Query("DELETE FROM BlogItem WHERE document_key = :docKey ")
    fun deleteThisBlogArticle(docKey: String)


    @Query("UPDATE BlogItem SET title = :title, content = :content,  image_url= :mediaUrl WHERE document_key = :docKey")
    fun update(title: String, content: String, mediaUrl :String, docKey: String)

}