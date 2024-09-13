package com.example.recursosmultimedia

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cambiar el color de la barra de notificaciones
        window.statusBarColor = getColor(R.color.black)

        // Configurar el botón para ir a MusicActivity
        findViewById<View>(R.id.layoutMusic).setOnClickListener {
            val intent = Intent(this, MusicActivity::class.java)
            startActivity(intent)
        }

        // Configurar el botón para ir a VideoActivity
        findViewById<View>(R.id.layoutVideo).setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }
    }
}
