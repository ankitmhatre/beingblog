package being.test.app.databases;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.local.city.dao.BlogsDao;
import com.local.city.models.BlogItem;


@Database(entities = {
        BlogItem.class}, version = 1, exportSchema = false)
public abstract class GlobalDatabase extends RoomDatabase {


    public static GlobalDatabase instance;
    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
    };

    public static synchronized GlobalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    GlobalDatabase.class, "allbigdb")
                    .addCallback(roomCallback)

                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


    public abstract BlogsDao newsDao();

}
