package com.nemislimus.crossvszero.ui.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentGameBinding
import com.nemislimus.crossvszero.utils.BindingFragment

class GameFragment : BindingFragment<FragmentGameBinding>() {

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGameBinding {
        return FragmentGameBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToMainBar.setOnClickListener {
            findNavController().navigateUp()
        }

        val animationFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

        binding.restartGameButton.hide()

        binding.cell11.setOnClickListener {
            binding.cell11.setImageResource(R.drawable.cross)
            it.startAnimation(animationFadeIn)
            binding.restartGameButton.show()
            binding.restartGameButton.startAnimation(animationFadeIn)
        }

        binding.cell13.setOnClickListener {
            binding.cell13.drawable.setTint(requireContext().getColor(R.color.crimson))
            val animDrawable = binding.cell13.drawable as? AnimatedVectorDrawable
            animDrawable?.start()
        }
    }
}