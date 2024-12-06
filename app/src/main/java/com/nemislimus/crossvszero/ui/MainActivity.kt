package com.nemislimus.crossvszero.ui

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var zeroAnimation: AnimatedVectorDrawable
    private lateinit var backgroundAnimation: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Background Animation
        backgroundAnimation = binding.mainBackgroundAnimated.drawable as AnimatedVectorDrawable
        backgroundAnimation.start()

        // Play
        binding.playButton.setOnClickListener {
            changeButtonColorOnClick(it)
            TransitionManager.beginDelayedTransition(binding.root);
            startActivity(
                Intent(this, GameActivity::class.java)
            )
        }

        // Exit
        binding.exitButton.setOnClickListener {
            changeButtonColorOnClick(it)
            finishAndRemoveTask()
        }

        binding.mainVersion.setOnClickListener {
            Toast.makeText(this, "PLAY AND ENJOY!", Toast.LENGTH_LONG).show()
        }
    }

    private fun changeButtonColorOnClick(button: View) {
        lifecycleScope.launch {
            button.setBackgroundColor(getColor(R.color.crimson))
            delay(50)
            button.setBackgroundColor(getColor(R.color.dark))
        }

    }
}