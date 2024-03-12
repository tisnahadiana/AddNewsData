package id.deeromptech.addnewsdata.view

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import id.deeromptech.addnewsdata.R
import id.deeromptech.addnewsdata.databinding.ActivityAddArticleBinding
import id.deeromptech.addnewsdata.utils.ViewBindingExt.viewBinding

class AddArticleActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAddArticleBinding::inflate)
    private var appBarConfiguration: AppBarConfiguration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val navController = getNavController()
        appBarConfiguration = navController?.graph?.let { AppBarConfiguration(it) }
        navController?.let {
            appBarConfiguration?.let { it1 ->
                setupActionBarWithNavController(
                    it,
                    it1
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getNavController(): NavController? {
        val fragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        if (fragment !is NavHostFragment) {
            throw IllegalStateException(
                "Activity " + this
                        + " does not have a NavHostFragment"
            )
        }
        return (fragment as NavHostFragment?)?.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return appBarConfiguration?.let { navController.navigateUp(it) } == true
                || super.onSupportNavigateUp()
    }
}