package com.example.logiapplication.logisticOperator.ui.followup

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.logiapplication.LoginActivity
import com.example.logiapplication.R
import com.example.logiapplication.databinding.LogisticViewRegisterCargoActivityBinding
import com.example.logiapplication.logisticOperator.LogisticMainActivity

class ViewRegisterCargoActivity:AppCompatActivity() {
    private lateinit var binding: LogisticViewRegisterCargoActivityBinding
    lateinit var backButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LogisticViewRegisterCargoActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        backButton = binding.root.findViewById(R.id.btn_back)


        backButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, LogisticMainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        })

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logistic_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.itemId
        when (item.itemId) {
            R.id.back-> goBack()
            R.id.log_out_action ->goToLoginActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    fun goBack() {
        val intent = Intent(this, LogisticMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
    fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
}