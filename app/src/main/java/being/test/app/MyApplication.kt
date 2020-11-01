package being.test.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApplication : Application() {

    val db = FirebaseFirestore.getInstance()
    override fun onCreate() {
        super.onCreate()


        FirebaseApp.initializeApp(this)
        val settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .setPersistenceEnabled(true)
            .build()

        db.firestoreSettings = settings
    }
}