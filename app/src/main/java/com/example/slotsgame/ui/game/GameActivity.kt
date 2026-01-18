package com.example.slotsgame.ui.game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.slotsgame.R
import com.example.slotsgame.databinding.ActivityGameBinding
import com.example.slotsgame.ui.history.HistoryActivity

class GameActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()
    private var username: String = ""

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0

    private val symbolHeight = 512f
    private val fullSpriteHeight = 2560f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra("USERNAME") ?: "Player"
        viewModel.loginOrRegister(username)

        setupReels()
        setupSensors()
        setupUI()
        setupObservers()
    }

    private fun setupReels() {
        val sprite = ContextCompat.getDrawable(this, R.drawable.slot_sprite)!!
        val layerDrawable = LayerDrawable(arrayOf(sprite, sprite))
        val reels = listOf(binding.ivReel1, binding.ivReel2, binding.ivReel3)
        reels.forEach { reel ->
            reel.setImageDrawable(layerDrawable)
        }
    }

    private fun setupUI() {
        binding.btnSpin.setOnClickListener { 
            binding.btnSpin.isEnabled = false
            val betText = binding.etBet.text.toString()
            val bet = betText.toDoubleOrNull() ?: 10.0
            viewModel.spin(bet)
        }

        binding.btnHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(this) { user ->
            binding.tvBalance.text = "Portfel: ${String.format("%.2f", user.balance)} $"
        }

        viewModel.spinResult.observe(this) { symbols ->
            animateReels(symbols)
        }

        viewModel.winMessage.observe(this) { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun animateReels(symbols: List<Int>) {
        val reels = listOf(binding.ivReel1, binding.ivReel2, binding.ivReel3)
        reels.forEachIndexed { index, reel ->
            val targetPosition = (symbols[index] * symbolHeight)
            animateReel(reel, targetPosition, 2000L + (index * 500L))
        }
    }

    private fun animateReel(reel: ImageView, targetPosition: Float, duration: Long) {
        val animator = ObjectAnimator.ofFloat(reel, "translationY", 0f, -fullSpriteHeight).apply {
            this.interpolator = LinearInterpolator()
            this.duration = 500
            this.repeatCount = ObjectAnimator.INFINITE
            start()
        }

        reel.postDelayed({
            animator.cancel()
            val finalAnimator = ObjectAnimator.ofFloat(reel, "translationY", reel.translationY, -targetPosition).apply {
                this.duration = 1000
                this.interpolator = LinearInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.btnSpin.isEnabled = true
                    }
                })
                start()
            }
        }, duration)
    }

    private fun setupSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH
            if (acceleration > 12) {
                val now = System.currentTimeMillis()
                if (now - lastShakeTime > 2000) {
                    lastShakeTime = now
                    binding.btnSpin.performClick()
                    Toast.makeText(this, "Shake Spin!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}