package being.test.app



import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import being.test.app.firestoreutils.FirebaseFunctions
import being.test.app.firestoreutils.FirebaseFunctionsResponse
import being.test.app.models.BlogItem
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.io.File
import java.io.Serializable


class WriteBlog :AppCompatActivity(), FirebaseFunctionsResponse {
    val PICK_IMAGE = 827
    private var TAG = WriteBlog::class.java.simpleName
    lateinit var reporters_article_upload_imageview: AppCompatImageView
    lateinit var camera_button: ColorCircle
    lateinit var reporters_article_posting_as: TextView
    private var pictureUri: Uri? = null
    lateinit var reporters_article_send_btn: Button
    lateinit var reporters_article_input_title: TextInputEditText
    lateinit var reporters_article_input_content: TextInputEditText
    lateinit var reporters_article_pb: ProgressBar
    lateinit var fileName: TextView
    private var media_type = "image"
    var alreadyBlogItem: BlogItem? = null
    lateinit var selectedMedia: String
    var stringcategory = ArrayList<String>()
    lateinit var user: FirebaseUser
    val db = FirebaseFirestore.getInstance()
    val fbAuth = FirebaseAuth.getInstance()


    override fun onNewIntent(intent: Intent?) {
        alreadyBlogItem = null
        super.onNewIntent(intent)
        setIntent(intent)
        freshData(intent!!)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        alreadyBlogItem = null
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

        freshData(intent)
    }

    fun freshData(i: Intent) {
        try {
            alreadyBlogItem = i.extras?.get("blogItem") as BlogItem

            if (alreadyBlogItem != null){
                thisisthearticle(alreadyBlogItem!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()


        }
    }

    private fun thisisthearticle(blogItem: BlogItem) {


        reporters_article_input_title.setText(blogItem.title)
        reporters_article_input_content.setText(blogItem.content)
        reporters_article_posting_as.setText(blogItem.author_name)
        reporters_article_upload_imageview.load(blogItem.image_url)
    }


    private fun validateAndUpload() {


        if (reporters_article_input_title.text!!.length > 5 && reporters_article_input_content.text!!.length > 10) {
            val storageRef = FirebaseStorage.getInstance().reference


            if (alreadyBlogItem != null) {

                if (pictureUri != null) {


                    val filePath =
                                FirebaseFunctions().getRealPathFromURI(this, pictureUri!!)
                            if (filePath != null) {
                                val file = File(filePath)


                                val extensionFunctionType = Utilities.getMimeType(this, pictureUri)
                                // Log.d("UPLOAD", "${extensionFunctionType}")
                                val newfilename = System.currentTimeMillis()
                                val riversRef =
                                    storageRef.child("blogs_media/${newfilename}.$extensionFunctionType")
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


                                        val j = mapOf(
                                            "media_url" to "${riversRef.path}",
                                            "title" to reporters_article_input_title.text.toString(),
                                            "content" to reporters_article_input_content.text.toString(),
                                            "updated_on" to (System.currentTimeMillis() / 1000),
                                            "document_key" to alreadyBlogItem!!.document_key
                                        )


                                        FirebaseFunctions().updateDataToFirestoreDatabase(
                                            this,
                                            "data/v1/blogs",
                                            j as Map<String, Serializable>
                                        )


                                    }


                            } else {
                                reporters_article_pb.visibility = View.GONE
                                reporters_article_send_btn.visibility = View.VISIBLE
                                Snackbar.make(
                                    this.findViewById(android.R.id.content),
                                    getString(R.string.please_upload_image), Snackbar.LENGTH_SHORT
                                ).show()
                                reporters_article_send_btn.isEnabled = true

                            }


                } else {


                    val j = mapOf(
                        "title" to reporters_article_input_title.text.toString(),
                        "content" to reporters_article_input_content.text.toString(),
                        "updated_on" to (System.currentTimeMillis() / 1000),
                        "document_key" to alreadyBlogItem!!.document_key
                    )


                    FirebaseFunctions().updateDataToFirestoreDatabase(
                        this,
                        "data/v1/blogs",
                        j as Map<String, Serializable>
                    )

                }


            } else {
                if (pictureUri != null) {


                    val filePath =
                                FirebaseFunctions().getRealPathFromURI(this, pictureUri!!)
                            if (filePath != null) {
                                val file = File(filePath)


                                val extensionFunctionType = Utilities.getMimeType(this, pictureUri)
                                // Log.d("UPLOAD", "${extensionFunctionType}")
                                val newfilename = System.currentTimeMillis()
                                val riversRef =
                                    storageRef.child("blogs_media/${newfilename}.$extensionFunctionType")
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
                                            "media_url" to "${riversRef.path}",
                                            "title" to reporters_article_input_title.text.toString(),
                                            "content" to reporters_article_input_content.text.toString(),
                                            "author_name" to cUser!!.displayName,
                                            "reported_on" to (System.currentTimeMillis() / 1000),
                                            "availability" to true
                                        )


                                        FirebaseFunctions().addDataToFirestoreDatabase(
                                            this,
                                            "data/v1/blogs",
                                            j as HashMap<String, Serializable>
                                        )


                                    }


                            } else {
                                reporters_article_pb.visibility = View.GONE
                                reporters_article_send_btn.visibility = View.VISIBLE
                                Snackbar.make(
                                    this.findViewById(android.R.id.content),
                                    getString(R.string.please_upload_image), Snackbar.LENGTH_SHORT
                                ).show()
                                reporters_article_send_btn.isEnabled = true

                            }


                } else {
                    reporters_article_pb.visibility = View.GONE
                    reporters_article_send_btn.visibility = View.VISIBLE
                    Snackbar.make(
                        this.findViewById(android.R.id.content),
                        getString(R.string.select_a_file), Snackbar.LENGTH_SHORT
                    ).show()
                    reporters_article_send_btn.isEnabled = true
                }

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
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_a_image)), PICK_IMAGE)
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 290)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
             290 ->{
                 if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                     openImagePicker()
                 }else{
                     finish()
                 }

             }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            pictureUri = data?.data
            Log.d("dataeeeeeeeeeeeeeeeee", "${resultCode} ${data} $pictureUri")
            reporters_article_upload_imageview.load(pictureUri)

        }




    }


    override fun dataAddSuccess(successful: Boolean) {


        Log.d(TAG, "$successful")
        if (successful) {
            Toast.makeText(this, "Added Successfully", Toast.LENGTH_LONG).show()
            reporters_article_input_title.text!!.clear()
            reporters_article_input_content.text!!.clear()

            finish()
        } else {
            Toast.makeText(this, "Failed to add", Toast.LENGTH_LONG).show()

        }

        reporters_article_pb.visibility = View.GONE
        reporters_article_send_btn.visibility = View.VISIBLE
    }

    override fun firebaseFunctionsResponse(jsonArray: JSONObject?, type: String?) {
    }

    override fun dataUpdateSuccess(successful: Boolean) {
        Log.d(TAG, "$successful")
        if (successful) {
            Toast.makeText(this, getString(R.string.successfully_updated), Toast.LENGTH_LONG).show()
            reporters_article_input_title.text!!.clear()
            reporters_article_input_content.text!!.clear()

            finish()
        } else {
            Toast.makeText(this, getString(R.string.failed_blog_update), Toast.LENGTH_LONG).show()

        }

        reporters_article_pb.visibility = View.GONE
        reporters_article_send_btn.visibility = View.VISIBLE

    }

    override fun checkFavorite(isFavorite: Boolean) {

    }

    override fun toggleFavorite(isFavorite: Boolean) {

    }
}
