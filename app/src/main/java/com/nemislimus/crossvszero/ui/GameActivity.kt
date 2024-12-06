package com.nemislimus.crossvszero.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var zeroAnimation: AnimatedVectorDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backToMainBar.setOnClickListener {
            finish()
        }

        val animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val crosses = listOf(
            binding.cell13.drawable,
            binding.cell22.drawable,
            binding.cell31.drawable,
        )

        crosses.forEach { cell ->
            (cell as AnimatedVectorDrawable).stop()
        }

        binding.restartGameButton.hide()

        binding.cell11.setOnClickListener {
            binding.cell11.setImageResource(R.drawable.cross)
            it.startAnimation(animationFadeIn)

            binding.restartGameButton.show()
            binding.restartGameButton.startAnimation(animationFadeIn)
        }
    }
}