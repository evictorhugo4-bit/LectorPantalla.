package com.lectorpantalla.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            Toast.makeText(
                this,
                "Buscá 'Lector de Pantalla' en la lista y activalo",
                Toast.LENGTH_LONG
            ).show()
        }

        findViewById<Button>(R.id.btnOverlay).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "El permiso ya está concedido", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnStartFloating).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Toast.makeText(
                    this,
                    "Primero dale el permiso de superposición (paso 2)",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                startService(Intent(this, FloatingButtonService::class.java))
                Toast.makeText(this, "Botón flotante activado", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnStopFloating).setOnClickListener {
            stopService(Intent(this, FloatingButtonService::class.java))
            Toast.makeText(this, "Botón flotante detenido", Toast.LENGTH_SHORT).show()
        }
    }
}
