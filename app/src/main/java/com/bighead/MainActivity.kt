package com.bighead

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
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

        val layoutKey = findViewById<View>(R.id.layoutKey)
        val layoutMain = findViewById<View>(R.id.layoutMain)
        val etKey = findViewById<EditText>(R.id.etKey)
        val btnActivate = findViewById<Button>(R.id.btnActivate)
        val btnGetKey = findViewById<Button>(R.id.btnGetKey)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val tvKey = findViewById<TextView>(R.id.tvKey)
        val arc1 = findViewById<View>(R.id.arc1)
        val arc2 = findViewById<View>(R.id.arc2)
        val arc3 = findViewById<View>(R.id.arc3)
        val core = findViewById<View>(R.id.coreCircle)

        fun makeRing(color: Int, strokeDp: Int): GradientDrawable {
            val d = GradientDrawable()
            d.shape = GradientDrawable.OVAL
            d.setColor(android.graphics.Color.TRANSPARENT)
            val px = (strokeDp * resources.displayMetrics.density).toInt()
            d.setStroke(px, color)
            return d
        }

        arc1.background = makeRing(0xFFA78BFA.toInt(), 2)
        arc2.background = makeRing(0xFF7C3AED.toInt(), 2)
        arc3.background = makeRing(0xFF6D28D9.toInt(), 2)

        val coreDrawable = GradientDrawable()
        coreDrawable.shape = GradientDrawable.OVAL
        coreDrawable.setColor(0xFFA78BFA.toInt())
        core.background = coreDrawable

        fun showMain() {
            layoutKey.visibility = View.GONE
            layoutMain.visibility = View.VISIBLE

            btnStart.alpha = 0f
            btnStop.alpha = 0f
            tvKey.alpha = 0f

            arc1.post {
                startCircleAnimations()
                spawnParticles()

                btnStart.animate().alpha(1f).setDuration(500)
                    .setInterpolator(OvershootInterpolator()).setStartDelay(200).start()
                btnStop.animate().alpha(1f).setDuration(500)
                    .setInterpolator(OvershootInterpolator()).setStartDelay(350).start()
                tvKey.animate().alpha(1f).setDuration(400).setStartDelay(500).start()
            }
        }

        if (savedKey != null && VALID_KEYS.contains(savedKey)) {
            tvKey.text = savedKey
            showMain()
        } else {
            layoutKey.visibility = View.VISIBLE
            layoutMain.visibility = View.GONE
        }

        btnGetKey.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Head_Aim_bot")))
        }

        btnActivate.setOnClickListener {
            val entered = etKey.text.toString().trim().uppercase()
            if (VALID_KEYS.contains(entered)) {
                prefs.edit().putString("key", entered).apply()
                tvKey.text = entered
                showMain()
                Toast.makeText(this, "Активировано!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Неверный ключ!", Toast.LENGTH_SHORT).show()
                btnActivate.animate().translationX(10f).setDuration(50).withEndAction {
                    btnActivate.animate().translationX(-10f).setDuration(50).withEndAction {
                        btnActivate.animate().translationX(0f).setDuration(50).start()
                    }.start()
                }.start()
            }
        }

        btnStart.setOnClickListener {
            btnStart.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                btnStart.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                startService(Intent(this, FloatingService::class.java))
                Toast.makeText(this, "Big Head запущен!", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            btnStop.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                btnStop.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()
            stopService(Intent(this, FloatingService::class.java))
            Toast.makeText(this, "Выключен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun spawnParticles() {
        val root = findViewById<android.view.ViewGroup>(android.R.id.content)
        val display = windowManager.defaultDisplay
        val size = android.graphics.Point()
        display.getSize(size)
        val screenW = size.x
        val screenH = size.y

        for (i in 0..15) {
            val dot = View(this)
            val dp = (2 + Math.random() * 4).toInt()
            val px = (dp * resources.displayMetrics.density).toInt()
            val dotDrawable = GradientDrawable()
            dotDrawable.shape = GradientDrawable.OVAL
            dotDrawable.setColor(0xFFA78BFA.toInt())
            dot.background = dotDrawable
            val lp = FrameLayout.LayoutParams(px, px)
            lp.gravity = Gravity.BOTTOM
            lp.leftMargin = (Math.random() * screenW).toInt()
            dot.layoutParams = lp
            dot.alpha = 0f
            root.addView(dot)

            val delay = (Math.random() * 3000).toLong()
            val dur = (3000 + Math.random() * 4000).toLong()

            dot.animate()
                .translationY((-screenH * (0.5 + Math.random() * 0.5)).toFloat())
                .alpha(0.5f)
                .setDuration(dur)
                .setStartDelay(delay)
                .withEndAction { root.removeView(dot) }
                .start()
        }
    }

    private fun startCircleAnimations() {
        val arc1 = findViewById<View>(R.id.arc1)
        val arc2 = findViewById<View>(R.id.arc2)
        val arc3 = findViewById<View>(R.id.arc3)
        val core = findViewById<View>(R.id.coreCircle)

        fun spinForever(view: View, duration: Long, reverse: Boolean) {
            val deg = if (reverse) -360f else 360f
            view.animate().rotationBy(deg).setDuration(duration)
                .setInterpolator(LinearInterpolator())
                .withEndAction { spinForever(view, duration, reverse) }.start()
        }

        fun pulseForever(view: View) {
            view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(800).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(800)
                    .withEndAction { pulseForever(view) }.start()
            }.start()
        }

        spinForever(arc1, 1500, false)
        spinForever(arc2, 1000, true)
        spinForever(arc3, 2000, false)
        pulseForever(core)
    }
}
