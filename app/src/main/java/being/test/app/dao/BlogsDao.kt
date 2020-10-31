package com.local.city.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.local.city.models.BlogItem

@Dao
interface BlogsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(Blog: BlogItem)

    //TODO("Also search by content")
    @Query("SELECT * FROM BlogItem where title LIKE  '%' || :filter  || '%' ORDER BY timestamp DESC")
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

    @Query("DELETE FROM BlogItem WHERE blog_id = :BlogId ")
    fun deleteThisBlogArticle(BlogId: Long)

    @Query("SELECT isPinned FROM BlogItem WHERE blog_id = :BlogId")
    fun getPinnedStatus(BlogId: Long): LiveData<Int>



}