package being.test.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.local.city.models.BlogItem;


import java.util.List;

import being.test.app.repository.GlobalRepository;

public class GlobalViewModel extends AndroidViewModel {
    private GlobalRepository globalRepository;

    public GlobalViewModel(@NonNull Application application) {
        super(application);
        globalRepository = new GlobalRepository(application);

    }

    public LiveData<BlogItem> getBlogItem(long id) {
        return globalRepository.getBlogItem(id);
    }


    public LiveData<List<BlogItem>> getBlogLiveList(String filter) {
        return globalRepository.getBlogList(filter);
    }
    
    public LiveData<List<BlogItem>> getPinnedList(String filter) {
        return globalRepository.getPinnedList(filter);
    }

    public LiveData<List<BlogItem>> getOldBlog(long oldDate) {
        return globalRepository.getOldBlogItem(oldDate);
    }

    public LiveData<Integer> getPinStatus(long BlogId) {
        return globalRepository.getPinStatus(BlogId);
    }


}
