package being.test.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ListAllBlogs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_holder)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentFrame, BlogsFragment())
            .commit()
    }
}
