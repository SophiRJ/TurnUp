package com.example.turnup.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.turnup.R
import com.example.turnup.databinding.ActivityBaseBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityBaseBinding
    protected lateinit var mAuth: FirebaseAuth
    protected lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // toolbar viene desde app_bar_main.xml que está incluido
        setSupportActionBar(binding.root.findViewById(R.id.toolbarMain))

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.root.findViewById(R.id.toolbarMain),
            R.string.bar_title, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_eventos -> startActivity(Intent(this, EventosActivity::class.java))
            R.id.nav_item_singout -> {
                mAuth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.nav_item_main -> checkUserRoleAndRedirect()
        }
        binding.drawerLayout.closeDrawers()
        return true
    }

    private fun checkUserRoleAndRedirect() {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            db.collection("Usuarios").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("rol")
                    when (role) {
                        "Administrador" -> startActivity(Intent(this, AdminActivity::class.java))
                        "Voluntario" -> startActivity(Intent(this, UserActivity::class.java))
                        else -> {
                            mAuth.signOut()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                .addOnFailureListener {
                    mAuth.signOut()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }
}
