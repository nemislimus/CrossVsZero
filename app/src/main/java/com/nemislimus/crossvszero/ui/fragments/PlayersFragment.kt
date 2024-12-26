package com.nemislimus.crossvszero.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nemislimus.crossvszero.databinding.FragmentPlayersBinding
import com.nemislimus.crossvszero.ui.viewmodels.PlayersFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
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

    }

}