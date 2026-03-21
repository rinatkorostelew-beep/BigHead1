package com.bighead

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val VALID_KEYS = listOf("BIGHEAD-1234", "BIGHEAD-5678", "BIGHEAD-ABCD")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("bighead", MODE_PRIVATE)
        val savedKey = prefs.getString("key", null)

        val layoutKey = findViewById<android.view.View>(R.id.layoutKey)
        val layoutMain = findViewById<android.view.View>(R.id.layoutMain)
        val etKey = findViewById<EditText>(R.id.etKey)
        val btnActivate = findViewById<Button>(R.id.btnActivate)
        val btnGetKey = findViewById<Button>(R.id.btnGetKey)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val tvKey = findViewById<TextView>(R.id.tvKey)

        if (savedKey != null && VALID_KEYS.contains(savedKey)) {
            layoutKey.visibility = android.view.View.GONE
            layoutMain.visibility = android.view.View.VISIBLE
            tvKey.text = "Ключ: $savedKey"
        } else {
            layoutKey.visibility = android.view.View.VISIBLE
            layoutMain.visibility = android.view.View.GONE
        }

        btnGetKey.setOnClickListener {
            // замени ссылку на своего Telegram бота
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Head_Aim_bot")))
        }

        btnActivate.setOnClickListener {
            val entered = etKey.text.toString().trim().uppercase()
            if (VALID_KEYS.contains(entered)) {
                prefs.edit().putString("key", entered).apply()
                layoutKey.visibility = android.view.View.GONE
                layoutMain.visibility = android.view.View.VISIBLE
                tvKey.text = "Ключ: $entered"
                Toast.makeText(this, "Активировано!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Неверный ключ!", Toast.LENGTH_SHORT).show()
                etKey.setBackgroundColor(0x33FF0000)
            }
        }

        btnStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                startService(Intent(this, FloatingService::class.java))
                Toast.makeText(this, "Big Head запущен!", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            stopService(Intent(this, FloatingService::class.java))
            Toast.makeText(this, "Выключен", Toast.LENGTH_SHORT).show()
        }
    }
}
