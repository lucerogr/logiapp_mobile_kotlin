package com.example.logiapplication.logisticOperator

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.LoginActivity.Companion.DateBirth
import com.example.logiapplication.LoginActivity.Companion.Email
import com.example.logiapplication.LoginActivity.Companion.LastName
import com.example.logiapplication.LoginActivity.Companion.Name
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticActivityMainBinding
import com.example.logiapplication.logisticOperator.ui.profile.LogisticProfileFragment
import com.example.logiapplication.logisticOperator.ui.register.RegisterFragment
import com.google.android.material.navigation.NavigationView
import java.util.*

class LogisticMainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: LogisticActivityMainBinding

    lateinit var nameUser : TextView
    var userCodigo : Int = 0
    lateinit var name : String
    lateinit var lastName : String
    lateinit var birthDate : String
    lateinit var email : String

    lateinit var sharedPreferences: SharedPreferences

    companion object {
        var UserCodigo ="userCodigo"
        var EmailU = "email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        binding = LogisticActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.logisticToolbar)

        val drawerLayout: DrawerLayout = binding.logisticDrawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.logistic_nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_logistic_profile, R.id.nav_register, R.id.nav_followup, R.id.nav_reporting
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //EN EL MENU DE NAVEGACION S EMUESTRO EL NOMBRE DEL USUARIO QUE INICIO SESION
        val headerView: View = navView.getHeaderView(0)
        nameUser = headerView.findViewById(R.id.nameAndLastNameTextO)
        nameUser.text = sharedPreferences.getString(LoginActivity.FirstName, null)

        //RECUPERAR INFO DEL USUARIO DESDE EL LOGIN
        userCodigo = intent.getIntExtra("UserId", 0)
        name = intent.getStringExtra(Name).toString()
        lastName = intent.getStringExtra(LastName).toString()
        birthDate = intent.getStringExtra(DateBirth).toString()
        email = intent.getStringExtra(Email).toString()

        //println(userCodigo)
        val bundle = Bundle()
        val logisticProfileFragment = LogisticProfileFragment()
        bundle.putString(Name, name)
        bundle.putString(LastName, lastName)
        bundle.putString(DateBirth, birthDate)
        bundle.putString(EmailU, email)
        logisticProfileFragment.arguments = bundle

        //println(registerFragment.arguments)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.logistic_profile_frag, logisticProfileFragment)
        transaction.commit()

        //MANDAR ID DEL USUARIO A NEXTREGISTER ACTIVITY
        val editor: SharedPreferences.Editor=sharedPreferences.edit()
        editor.putInt(UserCodigo, userCodigo)
        //editor.putString(UserId, getCodigo.toString())
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logistic_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.itemId
        when (item.itemId) {
            R.id.back -> goBack()
            R.id.log_out_action -> goToLoginActivity()
        }
        return super.onOptionsItemSelected(item)
    }
    fun goBack() {
        val intent = Intent(this, LogisticMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
    fun goToLoginActivity() {
        val editor: SharedPreferences.Editor=sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.logistic_nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}