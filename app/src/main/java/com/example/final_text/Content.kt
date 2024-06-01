package com.example.final_text

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.final_text.fragments.HomeFragment
import com.example.final_text.fragments.ListFragment
import com.example.final_text.fragments.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Content : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        title = resources.getString(R.string.home)
        loadFragment(HomeFragment())
        bottomNavigation?.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.nav_home -> {
                    title = resources.getString(R.string.home)
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_list -> {
                    title = resources.getString(R.string.list)
                    loadFragment(ListFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_user -> {
                    title = resources.getString(R.string.user)
                    loadFragment(UserFragment())
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        // 特别处理UserFragment，确保传递用户名参数

        if (fragment is UserFragment) {
            val incomingIntent = intent
            val incomingUsername = incomingIntent.getStringExtra("username")
            if (incomingUsername != null) {
                val args = Bundle()
                args.putString("username", incomingUsername)
                fragment.arguments = args
                Log.d("ContentActivity", "ContentActivity用户名: $incomingUsername")
            }

        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

