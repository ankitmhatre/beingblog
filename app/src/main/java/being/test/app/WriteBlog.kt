package being.test.app



import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import being.test.app.firestoreutils.FirebaseFunctions
import being.test.app.firestoreutils.FirebaseFunctionsResponse

import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.Serializable


class WriteBlog :AppCompatActivity(), FirebaseFunctionsResponse {
    val PICK_IMAGE = 827
    val PICK_VIDEO = 828
    val PICK_AUDIO = 829
    private var TAG = WriteBlog::class.java.simpleName
    lateinit var reporters_article_upload_imageview: AppCompatImageView
    lateinit var camera_button :ColorCircle
    lateinit var reporters_article_posting_as: TextView
    private var pictureUri: Uri? = null
    lateinit var reporters_article_send_btn: Button
    lateinit var reporters_article_input_title: TextInputEditText
    lateinit var reporters_article_input_content: TextInputEditText
    lateinit var reporters_article_pb: ProgressBar
    lateinit var fileName: TextView
    lateinit var postArticleSpinner: Spinner
    private var media_type = "image"
    lateinit var selectedMedia: String
    var stringcategory = ArrayList<String>()
    lateinit var user: FirebaseUser
    val db = FirebaseFirestore.getInstance()
    val fbAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_blog_layout)
        user = fbAuth.currentUser as FirebaseUser
        reporters_article_upload_imageview = findViewById(R.id.reporters_article_upload_imageview)
        camera_button = findViewById(R.id.camera_button)
        reporters_article_posting_as = findViewById(R.id.reporters_article_input_author)
        reporters_article_pb = findViewById(R.id.reporters_article_pb)
        reporters_article_send_btn = findViewById(R.id.reporters_article_send_btn)
        reporters_article_input_title = findViewById(R.id.reporters_article_input_title)
        reporters_article_input_content = findViewById(R.id.reporters_article_input_content)



        //run and api call to populate categories
        reporters_article_pb.visibility = View.GONE
        reporters_article_send_btn.visibility = View.VISIBLE


        if (user != null) {
            reporters_article_posting_as.text = user.displayName
            reporters_article_posting_as.isEnabled = false
        }


        reporters_article_send_btn.setOnClickListener {
            reporters_article_pb.visibility = View.VISIBLE
            reporters_article_send_btn.visibility = View.GONE
            reporters_article_send_btn.isEnabled = false
            validateAndUpload()
        }

        // reporters_article_posting_as.text = "-by " + intent.extras!!.getString("full_name")
        camera_button.setOnClickListener {
            pictureUri = null
            openImagePicker()
            media_type = "image"
        }


    }


    private fun validateAndUpload() {
        var newNid = -1

        if (reporters_article_input_title.text!!.length > 5 && reporters_article_input_content.text!!.length > 10) {
            val storageRef = FirebaseStorage.getInstance().reference


            if (pictureUri != null) {
                db.collection("data/v1/news")
                    .get()
                    .addOnSuccessListener { result ->

                        result.forEach {
                            if (it.get("nid") as Long > newNid) {
                                newNid = "${it.get("nid")}".toInt()
                            }
                        }
                        newNid++


                        val filePath = FirebaseFunctions().getRealPathFromURI(this, pictureUri!!)
                        if (filePath != null) {
                            val file = File(filePath)


                            val extensionFunctionType = Utilities.getMimeType(this, pictureUri)
                            // Log.d("UPLOAD", "${extensionFunctionType}")
                            val newfilename = System.currentTimeMillis()
                            val riversRef = storageRef.child("news_media/${newfilename}.$extensionFunctionType")
                            val uploadTask = riversRef.putFile(pictureUri!!)


// Register observers to listen for when the download is done or if it fails
                            uploadTask
                                .addOnFailureListener {
                                    // Handle unsuccessful uploads
                                    reporters_article_pb.visibility = View.GONE
                                    reporters_article_send_btn.isEnabled = true
                                    reporters_article_send_btn.visibility = View.VISIBLE
                                    //failed
                                }
                                .addOnSuccessListener {
                                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                                    // ...
                                    val a = File(it.uploadSessionUri?.encodedPath)
                                    Log.d("Uploaded", "$a")
                                    Log.d("Uploaded", "${riversRef.path}")
                                    Log.d("Uploaded", "${it.uploadSessionUri}")
                                    val cUser = FirebaseAuth.getInstance().currentUser


                                    val j = hashMapOf(
                                        "nid" to newNid,
                                        "media_type" to media_type,
                                        "category_name" to postArticleSpinner.selectedItem,
                                        "category_id" to postArticleSpinner.selectedItemPosition,
                                        "availability" to "available",
                                        "media_url" to "${riversRef.path}",
                                        "title" to reporters_article_input_title.text.toString(),
                                        "content" to reporters_article_input_content.text.toString(),
                                        "isVerified" to false,
                                        "reported_by" to cUser!!.displayName,
                                        "reported_on" to (System.currentTimeMillis() / 1000)
                                    )


                                    FirebaseFunctions().addDataToFirestoreDatabase(this, "data/v1/news", j as HashMap<String, Serializable>)


                                }


                        } else {
                            reporters_article_pb.visibility = View.GONE
                            reporters_article_send_btn.visibility = View.VISIBLE
                            Snackbar.make(this.findViewById(android.R.id.content),
                                getString(R.string.please_upload_image), Snackbar.LENGTH_SHORT).show()
                            reporters_article_send_btn.isEnabled = true

                        }


                    }
                    .addOnFailureListener { e ->
                        reporters_article_send_btn.isEnabled = true
                        reporters_article_pb.visibility = View.GONE
                        reporters_article_send_btn.visibility = View.VISIBLE
                        Log.w(TAG, "Error adding document", e)
                        Snackbar.make(this.findViewById(android.R.id.content),
                            "Failed ${e.message}", Snackbar.LENGTH_SHORT).show()
                    }
            } else {
                reporters_article_pb.visibility = View.GONE
                reporters_article_send_btn.visibility = View.VISIBLE
                Snackbar.make(this.findViewById(android.R.id.content),
                    getString(R.string.select_a_file), Snackbar.LENGTH_SHORT).show()
                reporters_article_send_btn.isEnabled = true
            }
        } else {
            reporters_article_send_btn.isEnabled = true
            reporters_article_pb.visibility = View.GONE
            reporters_article_send_btn.visibility = View.VISIBLE
            val snackbar = Snackbar.make(this.findViewById(android.R.id.content),
                getString(R.string.title_content_length_error), Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
            snackbar.show()

        }


    }

    private fun openImagePicker() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, PICK_IMAGE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

Log.d("dataeeeeeeeeeeeeeeeee", "${resultCode} ${data}")
        super.onActivityResult(requestCode, resultCode, data)

//        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
//            val imageBitmap = data!!.extras!!.get("data") as Bitmap
//            reporters_article_upload_imageview.setImageBitmap(imageBitmap)
//            //pictureUri = data?.data
//        }
//
//


    }


    override fun dataAddSuccess(successful: Boolean) {


        Log.d(TAG, "$successful")
        if (successful) {
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_LONG).show()
            reporters_article_input_title.text!!.clear()
            reporters_article_input_content.text!!.clear()

            finish()
        } else {
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()

        }

        reporters_article_pb.visibility = View.GONE
        reporters_article_send_btn.visibility = View.VISIBLE
    }

    override fun firebaseFunctionsResponse(jsonArray: JSONObject?, type: String?) {
    }


}
