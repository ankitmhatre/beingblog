package being.test.app.firestoreutils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.loader.content.CursorLoader
import being.test.app.R
import being.test.app.models.BlogItem
import coil.api.load
import com.google.firebase.auth.FirebaseAuth
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

        val documentReference = firebaseStoreRoot.collection(collection_name).document()
        jsonObject.put("document_key", documentReference.id)
        firebaseStoreRoot.collection(collection_name).document(documentReference.id)
            .set(jsonObject)
            .addOnSuccessListener { documentReference ->

                firebaseFunctionsResponse.dataAddSuccess(true)
            }
            .addOnFailureListener { e ->
                firebaseFunctionsResponse.dataAddSuccess(false)
            }
    }


    fun checkFavorite(firebaseFunctionsResponse: FirebaseFunctionsResponse, blogItem: BlogItem) {
        Log.d(TAG, "inside update CollectionDataFron Firbase")
        val firebaseStoreRoot = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val documentReference = firebaseStoreRoot
            .collection("users/${user!!.uid}/favorites")


        documentReference.get()
            .addOnSuccessListener { documents ->

                var isFavor = false

                for (document in documents) {
                    try {
                        Log.d(
                            "ahadadad",
                            "${document.id} => ${document.data.get(blogItem.document_key)}}"
                        )
                    } catch (e: java.lang.Exception) {

                    }

                    if (document.data.get(blogItem.document_key) != null && (document.data.get(
                            blogItem.document_key
                        ) as Boolean)
                    ) {

                        isFavor = true
                    }

                }
                firebaseFunctionsResponse.checkFavorite(isFavor)

            }
            .addOnFailureListener {
                firebaseFunctionsResponse.checkFavorite(false)
            }


    }

    fun toggleFavorites(
        firebaseFunctionsResponse: FirebaseFunctionsResponse,
        key: String,
        isFavor: Boolean
    ) {

        val firebaseStoreRoot = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val documentReference = firebaseStoreRoot
            .collection("users/${user!!.uid}/favorites")


        val data5 = hashMapOf(
            key to isFavor
        )

        documentReference
            .add(data5)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                firebaseFunctionsResponse.toggleFavorite(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                firebaseFunctionsResponse.toggleFavorite(false)
            }

    }


    fun updateDataToFirestoreDatabase(
        firebaseFunctionsResponse: FirebaseFunctionsResponse,
        collection_name: String,
        jsonObject: Map<String, Serializable>
    ) {
        Log.d(TAG, "inside update CollectionDataFron Firbase")
        val firebaseStoreRoot = FirebaseFirestore.getInstance()

        val documentReference = firebaseStoreRoot.collection(collection_name)
            .document(jsonObject.get("document_key") as String)


        documentReference
            .update(jsonObject)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                firebaseFunctionsResponse.dataUpdateSuccess(true)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                firebaseFunctionsResponse.dataUpdateSuccess(false)
            }

    }


    fun getDataFromFirestoreDatabase(
        firebaseFunctionsResponse: FirebaseFunctionsResponse,
        collection_name: String
    ) {

        val firebaseStoreRoot = FirebaseFirestore.getInstance()
        firebaseStoreRoot.collection(collection_name)
        .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val globalJSON = JSONObject()
            val jsonArray = JSONArray()


            snapshot!!.documents.forEach{
                Log.d("adasdad", "Current data:  ${it.data}")


                val j = JSONObject()
                it.data!!.forEach {
                j.put(it.key, it.value)
                }
                jsonArray.put(j)


            }
            try {

                globalJSON.put("result", jsonArray)
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }

            firebaseFunctionsResponse.firebaseFunctionsResponse(globalJSON, type = "")
        }

    }


    fun getFavoritesFromMyAccount(
        firebaseFunctionsResponse: FirebaseFunctionsResponse
    ) {
        val firebaseStoreRoot = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        var favoriteBLog = mutableListOf<String>()
        val documentReference = firebaseStoreRoot
            .collection("users/${user!!.uid}/favorites")
            .get()
            .addOnSuccessListener {
                for (document in it.documents) {
                    favoriteBLog.addAll(document.data!!.keys.toMutableList())

                }
                Log.d(
                    "ahadadad",
                    "$favoriteBLog")
                firebaseStoreRoot.collection("data/v1/blogs")
                    .whereIn("document_key", favoriteBLog)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }

                        val globalJSON = JSONObject()
                        val jsonArray = JSONArray()


                        snapshot!!.documents.forEach {
                            Log.d("adasdad", "Current data:  ${it.data}")


                            val j = JSONObject()
                            it.data!!.forEach {
                                j.put(it.key, it.value)
                            }
                            jsonArray.put(j)


                        }
                        try {

                            globalJSON.put("result", jsonArray)
                        } catch (ex: JSONException) {
                            ex.printStackTrace()
                        }

                        firebaseFunctionsResponse.firebaseFunctionsResponse(globalJSON, type = "")
                    }

            }





    }

    fun searchDataFromFirestoreDatabase(
        firebaseFunctionsResponse: FirebaseFunctionsResponse,
        collection_name: String,
        topic: String
    ) {

        val firebaseStoreRoot = FirebaseFirestore.getInstance()
        firebaseStoreRoot.collection(collection_name)
            .whereGreaterThanOrEqualTo("content", topic)

            .get()
                .addOnCompleteListener { task ->
                    if (task.result != null && task.isSuccessful) {
                        val globalJSON = JSONObject()
                        val jsonArray = JSONArray()


                        for (document in task.result!!) {

                            val j = JSONObject()
                            document.data.forEach {
                                j.put(it.key, it.value)
                            }
                            jsonArray.put(j)
                            try {

                                globalJSON.put("result", jsonArray)
                            } catch (ex: JSONException) {
                                ex.printStackTrace()
                            }

                        }
                        firebaseFunctionsResponse.firebaseFunctionsResponse(globalJSON, topic)
                    } else{
                        firebaseFunctionsResponse.firebaseFunctionsResponse(null, topic)
                    }
                }
                .addOnFailureListener { e ->
                    firebaseFunctionsResponse.firebaseFunctionsResponse(null, topic)
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