package com.nemislimus.crossvszero.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentPlayersBinding
import com.nemislimus.crossvszero.ui.viewmodels.PlayersFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayersFragment : BindingFragment<FragmentPlayersBinding>() {

    private val viewModel by viewModel<PlayersFragmentViewModel>()

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayersBinding {
        return FragmentPlayersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStart.setOnClickListener {
            changeButtonColorOnClick(it)
            findNavController().navigate(
                R.id.action_playersFragment_to_gameFragment
            )
        }

        binding.tbPlayersToolBar.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun changeButtonColorOnClick(button: View) {
        lifecycleScope.launch {
            button.setBackgroundColor(requireContext().getColor(R.color.crimson))
            delay(25)
            button.setBackgroundColor(requireContext().getColor(R.color.dark))
        }
    }

}