package com.example.wildrunning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_record)
        setSupportActionBar(toolbar)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Esto hace que el usuario cuando vaya para atras en el menu, haga esto (testear)
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.order_records_by, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.orderby_date -> {
                if (item.title == getString(R.string.orderby_dateZA)){
                    item.title = getString(R.string.orderby_dateAZ)
                }
                else{
                    item.title = getString(R.string.orderby_dateZA)
                }
                return true
            }
            R.id.orderby_duration ->{
                var option = getString(R.string.orderby_durationZA)
                if (item.title == getString(R.string.orderby_durationZA)){
                    item.title = getString(R.string.orderby_durationAZ)
                }
                else{
                    item.title = getString(R.string.orderby_durationZA)
                }
                return true
            }

            R.id.orderby_distance ->{
                var option = getString(R.string.orderby_distanceZA)
                if (item.title == option){
                    item.title = getString(R.string.orderby_distanceAZ)
                }
                else{
                    item.title = getString(R.string.orderby_distanceZA)
                }
                return true
            }

            R.id.orderby_avgspeed ->{
                var option = getString(R.string.orderby_avgspeedZA)
                if (item.title == getString(R.string.orderby_avgspeedZA)){
                    item.title = getString(R.string.orderby_avgspeedAZ)
                }
                else{
                    item.title = getString(R.string.orderby_avgspeedZA)
                }
                return true
            }

            R.id.orderby_maxspeed ->{
                var option = getString(R.string.orderby_maxspeedZA)
                if (item.title == getString(R.string.orderby_maxspeedZA)){
                    item.title = getString(R.string.orderby_maxspeedAZ)
                }
                else{
                    item.title = getString(R.string.orderby_maxspeedZA)
                }
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun callHome(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }
}