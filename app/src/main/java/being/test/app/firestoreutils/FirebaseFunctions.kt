package being.test.app.firestoreutils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.loader.content.CursorLoader
import being.test.app.R
import coil.api.load
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class FirebaseFunctions {

    private val TAG = "FirebaseFunctions"


    public companion object

    fun addDataToFirestoreDatabase(firebaseFunctionsResponse: FirebaseFunctionsResponse, collection_name: String, jsonObject: HashMap<String, Serializable>) {
        Log.d(TAG, "inside Post CollectionDataFron Firbase")
        val firebaseStoreRoot = FirebaseFirestore.getInstance()

        firebaseStoreRoot.collection(collection_name)
                .add(jsonObject)
                .addOnSuccessListener { documentReference ->
                    firebaseFunctionsResponse.dataAddSuccess(true)
                }
                .addOnFailureListener { e ->
                    firebaseFunctionsResponse.dataAddSuccess(false)
                }
    }

    fun getDataFromFirestoreDatabase(firebaseFunctionsResponse: FirebaseFunctionsResponse,
                                     collection_name: String,
                                     topic: String) {
        Log.d(TAG, "inside Get CollectionDataFron Firbase")
        val firebaseStoreRoot = FirebaseFirestore.getInstance()
        firebaseStoreRoot.collection(collection_name).get()
                .addOnCompleteListener { task ->
                    if (task.result != null && task.isSuccessful) {
                        val globalJSON = JSONObject()
                        val jsonArray = JSONArray()
                        globalJSON.put("error_code", 0)
                        globalJSON.put("message", "success")



                        for (document in task.result!!) {

                            val j = JSONObject()
                            document.data.forEach {
                                j.put(it.key, it.value)
                            }
                            jsonArray.put(j)
                            try {

                                globalJSON.put(topic, jsonArray)
                            } catch (ex: JSONException) {
                                ex.printStackTrace()
                            }

                        }
                        firebaseFunctionsResponse.firebaseFunctionsResponse(globalJSON, topic)
                    } else {
                        val globalJSON = JSONObject()
                        val jsonArray = JSONArray()
                        globalJSON.put("error_code", -1)
                        globalJSON.put("message", task.exception!!.message)
                        try {

                            globalJSON.put(topic, jsonArray)
                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }
                        firebaseFunctionsResponse.firebaseFunctionsResponse(globalJSON, topic)
                    }
                }
                .addOnFailureListener { e ->
                    val jsonArray = JSONArray()
                    val globalJSON = JSONObject()
                    globalJSON.put("error_code", -1)
                    globalJSON.put("message", e.message)
                    try {

                        globalJSON.put(topic, jsonArray)
                    } catch (ex: JSONException) {
                        ex.printStackTrace()
                    }
                    firebaseFunctionsResponse.firebaseFunctionsResponse(globalJSON, topic)
                }
    }


    fun loadImageFromFirebase(context: Context, path: String, imageView2: ImageView) {


        imageView2.load("${loadMediaUrl(path)}") {
            placeholder(R.drawable.blogs_default)
        }


    }

    fun loadMediaUrl(path: String): String? {
        var fullUrl: String? = null
        val storage = FirebaseStorage.getInstance()
        val listRef = storage.getReference(path)
        listRef.downloadUrl.addOnSuccessListener {
            fullUrl = "${it.scheme}://${it.host}${it.encodedPath}?alt=media"

        }
        return fullUrl
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var result: String? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            Log.d("fileSelection", "proj : " + proj.toString())
            val loader = CursorLoader(context, contentUri, proj, null, null, null)
            Log.d("fileSelection", "loader : " + loader.toString())
            val cursor = loader.loadInBackground()
            Log.d("fileSelection", "cursor : " + cursor.toString())
            val columnIndex = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
            Log.d("fileSelection", "columnIndex : " + columnIndex)
            cursor.moveToFirst()
            result = cursor.getString(columnIndex)
            Log.d("fileSelection", "result : " + result.toString())
            cursor.close()
        } catch (e: Exception) {
        }
        return result
    }
}