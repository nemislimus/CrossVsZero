package com.nemislimus.crossvszero.ui.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentMainBinding
import com.nemislimus.crossvszero.ui.viewmodels.MainFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : BindingFragment<FragmentMainBinding>() {

    private var fragmentBackgroundAnimation: AnimatedVectorDrawable? = null
    private val viewModel by viewModel<MainFragmentViewModel>()


    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startBackAnimation(
            binding.mainBackgroundAnimated.drawable as AnimatedVectorDrawable
        )

        // Play
        binding.playButton.setOnClickListener {
            changeButtonColorOnClick(it)
            findNavController().navigate(
                R.id.action_mainFragment_to_gameFragment
            )
        }

        // Exit
        binding.exitButton.setOnClickListener {
            changeButtonColorOnClick(it)
            requireActivity().finishAndRemoveTask()
        }

        // Click on version
        binding.mainVersion.setOnClickListener {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.version_click_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        restoreBackAnimation()
    }

    private fun changeButtonColorOnClick(button: View) {
        lifecycleScope.launch {
            button.setBackgroundColor(requireContext().getColor(R.color.crimson))
            delay(25)
            button.setBackgroundColor(requireContext().getColor(R.color.dark))
        }
    }

    private fun restoreBackAnimation() {
        binding.mainBackgroundAnimated.setImageDrawable(
            viewModel.restoreBackAnimation()
        )
    }
}