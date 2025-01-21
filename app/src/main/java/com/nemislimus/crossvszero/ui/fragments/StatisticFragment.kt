package com.nemislimus.crossvszero.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nemislimus.crossvszero.databinding.FragmentStatisticBinding
import com.nemislimus.crossvszero.domain.models.Player
import com.nemislimus.crossvszero.ui.models.StatisticState
import com.nemislimus.crossvszero.ui.rv.PlayersAdapter
import com.nemislimus.crossvszero.ui.viewmodels.StatisticFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticFragment : BindingFragment<FragmentStatisticBinding>() {

    private lateinit var statisticAdapter: PlayersAdapter

    private val viewModel by viewModel<StatisticFragmentViewModel>()

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStatisticBinding {
        return FragmentStatisticBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStartProperties()

        viewModel.getStatisticState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: StatisticState) {
        when(state) {
            StatisticState.NoStatistic -> showMessage()
            is StatisticState.ShowStatistic -> showStatistic(state.players)
        }
    }

    private fun setStartProperties() {
        val menuListener = object : PlayersAdapter.ItemMenuClickListener {
            override fun handleDelete(model: Player) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deletePlayer(model.name)
                    val getPlayersJob = async {
                        viewModel.getPlayers()
                    }
                    val players = getPlayersJob.await()
                    viewModel.setState(players)
                }
            }

            override fun handleCleanUp(model: Player) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.cleanupPlayerStat(model)
                    viewModel.getStatisticState()
                    val getPlayersJob = async {
                        viewModel.getPlayers()
                    }
                    val players = getPlayersJob.await()
                    viewModel.setState(players)
                }
            }

        }

        statisticAdapter = PlayersAdapter(true, menuListener) { }

        with(binding) {
            rvPlayersStatList.adapter = statisticAdapter

            tbStatisticToolBar.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun showMessage() {
        binding.grStatisticEnable.isVisible = false
        binding.tvNoStatText.isVisible = true
    }

    private fun showStatistic(players: List<Player>) {
        binding.tvNoStatText.isVisible = false
        updateAdapterPlayers(players)
        binding.grStatisticEnable.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterPlayers(playersList: List<Player>) {
        statisticAdapter.players.clear()
        statisticAdapter.players.addAll(playersList)
        statisticAdapter.notifyDataSetChanged()
    }

}