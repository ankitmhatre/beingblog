package being.test.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity

class DashboardHome : AppCompatActivity(), View.OnClickListener {

    lateinit var userAccType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_home_layout)
        userAccType = PrefUtils.getString(this, PrefKeys.USER_ACC_TYPE, null)

    }

    override fun onStart() {
        super.onStart()
        val grid = findViewById<GridLayout>(R.id.quickAccess) as GridLayout

        grid.removeAllViews()

        if (userAccType != null) {
            if (userAccType.equals("admin")) {

                val writeCircle = ColorCircle(this)
                writeCircle.setColor(
                    android.R.attr.colorBackground,
                    resources.getColor(R.color.colorAccent)
                )
                writeCircle.id = R.id.writeBlogCircleView
                writeCircle.borderWidth = resources.getDimension(R.dimen.borderWidth)
                writeCircle.setImageResource(R.drawable.ic_create)
                writeCircle.circleRadius =
                    resources.getDimension(R.dimen.side_menu_quick_access_radius)
                writeCircle.setOnClickListener(this@DashboardHome)
                writeCircle.desc = getString(R.string.write)

                grid.addView(writeCircle)
            } else {
                val viewBlogs = ColorCircle(this)
                viewBlogs.setColor(
                    android.R.attr.colorBackground,
                    resources.getColor(R.color.colorAccent)
                )
                viewBlogs.id = R.id.viewBlogCircleView
                viewBlogs.borderWidth = resources.getDimension(R.dimen.borderWidth)
                viewBlogs.setImageResource(R.drawable.ic_list)
                viewBlogs.circleRadius =
                    resources.getDimension(R.dimen.side_menu_quick_access_radius)
                viewBlogs.setOnClickListener(this@DashboardHome)
                viewBlogs.desc = getString(R.string.read)

                grid.addView(viewBlogs)

                val favouriteBLogs = ColorCircle(this)
                favouriteBLogs.setColor(
                    android.R.attr.colorBackground,
                    resources.getColor(R.color.colorAccent)
                )
                favouriteBLogs.id = R.id.checkFavoriteCircleView
                favouriteBLogs.borderWidth = resources.getDimension(R.dimen.borderWidth)
                favouriteBLogs.setImageResource(R.drawable.ic_star)
                favouriteBLogs.circleRadius =
                    resources.getDimension(R.dimen.side_menu_quick_access_radius)
                favouriteBLogs.setOnClickListener(this@DashboardHome)
                favouriteBLogs.desc = getString(R.string.favourite)

                grid.addView(favouriteBLogs)

            }
        } else {
            startActivity(Intent(this@DashboardHome, LoginActivity::class.java))
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.writeBlogCircleView -> {
                startActivity(Intent(this@DashboardHome, WriteBlog::class.java))
            }
            R.id.viewBlogCircleView -> {
                startActivity(Intent(this@DashboardHome, ListAllBlogs::class.java))
            }
            R.id.checkFavoriteCircleView -> {

            }
        }
    }
}