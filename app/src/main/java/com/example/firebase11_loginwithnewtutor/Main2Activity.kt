package com.example.firebase11_loginwithnewtutor

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

            var latiApp: String? = null
            var langiApp: String? = null
            if (intent.extras != null){
                val terima = intent.extras
                latiApp = terima?.getString("latiApp")
                langiApp = terima?.getString("langiApp")
            }else{
                latiApp = "0.0"
                langiApp = "0.0"
            }
            // Add a marker in Sydney and move the camera
            var add1 = latiApp?.toDouble()
            var add2 = langiApp?.toDouble()
            say_halo.text = add1.toString()

    }

}
