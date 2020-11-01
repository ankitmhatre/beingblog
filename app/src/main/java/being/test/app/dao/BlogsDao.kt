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

    @Query("SELECT * FROM BlogItem WHERE blog_id = :BlogId")
    fun getBlogItem(BlogId: Long) : LiveData<BlogItem>


    @Query("UPDATE BlogItem SET isPinned = :isPin WHERE blog_id = :BlogId")
    fun updatePinned(BlogId: Long, isPin: Int)


    @Query("SELECT * FROM BlogItem WHERE isPinned = 1 AND content LIKE '%' || :filter || '%'")
    fun getAllPinnedBlog(filter: String): LiveData<List<BlogItem>>

    @Query("SELECT * FROM BlogItem WHERE timestamp < :suppliedStamp")
    fun getOldBlogThan(suppliedStamp: Long): LiveData<List<BlogItem>>

    @Query("DELETE FROM BlogItem WHERE document_key = :docKey ")
    fun deleteThisBlogArticle(docKey: String)

    @Query("SELECT isPinned FROM BlogItem WHERE blog_id = :BlogId")
    fun getPinnedStatus(BlogId: Long): LiveData<Int>


    @Query("UPDATE BlogItem SET title = :title, content = :content,  image_url= :mediaUrl WHERE document_key = :docKey")
    fun update(title: String, content: String, mediaUrl :String, docKey: String)

}