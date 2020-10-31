package being.test.app.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import being.test.app.dao.BlogsDao;


import java.util.List;

import being.test.app.databases.GlobalDatabase;
import being.test.app.models.BlogItem;

public class GlobalRepository {


    private BlogsDao BlogDao;


    public GlobalRepository(Application application) {
        GlobalDatabase globalDatabase = GlobalDatabase.getInstance(application);

        BlogDao = globalDatabase.newsDao();
    }


    public void insertBlog(BlogItem Blog) {
        new InsertBlogTask(BlogDao).execute(Blog);
    }

 


    public void deleteAllBlog() {
        new DeleteAllBlog(BlogDao).execute();
    }

    public LiveData<List<BlogItem>> getBlogList(String filter) {
        return BlogDao.getAllBlog(filter);
    }

    public LiveData<BlogItem> getBlogItem(long id) {
        return BlogDao.getBlogItem(id);
    }

    public LiveData<List<BlogItem>> getPinnedList(String filter) {
        return BlogDao.getAllPinnedBlog(filter);
    }

    public void updatePinned(Long id, int isPin) {
        new UpdateBlogTask(BlogDao, id, isPin).execute();
    }

    public LiveData<List<BlogItem>> getOldBlogItem(Long oldstamp) {
        return BlogDao.getOldBlogThan(oldstamp);
    }

    public LiveData<Integer> getPinStatus(Long BlogId) {
        return BlogDao.getPinnedStatus(BlogId);
    }

    public void deleteSpecificBlog(Long id) {
        new DeleteSpecificBlog(BlogDao).execute(id);
    }

    private static class UpdateBlogTask extends AsyncTask<Void, Void, Void> {
        private BlogsDao BlogDao;
        private Long id;
        private int value;

        public UpdateBlogTask(BlogsDao BlogDao, Long id, int val) {
            this.BlogDao = BlogDao;
            this.id = id;
            this.value = val;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            BlogDao.updatePinned(id, value);
            return null;
        }
    }




    private static class InsertBlogTask extends AsyncTask<BlogItem, Void, Void> {
        private BlogsDao BlogDao;

        public InsertBlogTask(BlogsDao BlogDao) {
            this.BlogDao = BlogDao;
        }


        @Override
        protected Void doInBackground(BlogItem... Blog) {
            BlogDao.insert(Blog[0]);
            return null;
        }
    }

 

    private static class DeleteAllBlog extends AsyncTask<Void, Void, Void> {
        BlogsDao BlogDao;

        public DeleteAllBlog(BlogsDao BlogDao) {
            this.BlogDao = BlogDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            BlogDao.deleteAllBlog();
            return null;
        }
    }

    private static class DeleteSpecificBlog extends AsyncTask<Long, Void, Void> {
        BlogsDao BlogDao;

        public DeleteSpecificBlog(BlogsDao BlogDao) {
            this.BlogDao = BlogDao;
        }

        @Override
        protected Void doInBackground(Long... voids) {
            BlogDao.deleteThisBlogArticle(voids[0]);
            return null;
        }
    }


   

}
