package com.nemislimus.crossvszero.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentPlayersBinding
import com.nemislimus.crossvszero.domain.models.Player
import com.nemislimus.crossvszero.ui.models.PlayersState
import com.nemislimus.crossvszero.ui.rv.PlayersAdapter
import com.nemislimus.crossvszero.ui.viewmodels.PlayersFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayersFragment : BindingFragment<FragmentPlayersBinding>() {

    private var playerNameTextWatcher: TextWatcher? = null
    private var createInfoJob: Job? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val viewModel by viewModel<PlayersFragmentViewModel>()

    private var players: MutableList<Player> = mutableListOf()
    private var playerX: Player? = null
    private var playerO: Player? = null

    private val playersAdapter = PlayersAdapter {}

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayersBinding {
        return FragmentPlayersBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUiOnCreate()
        setClickListeners()
    }

    override fun onDestroyFragment() {
        binding.etCreatePlayer.removeTextChangedListener(playerNameTextWatcher)
        createInfoJob = null
        playerNameTextWatcher = null
        super.onDestroyFragment()
    }

    private fun setUiOnCreate() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.PlayerBottomSheetLayout.root).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.Veil.isVisible = false
                    }
                    else -> {
                        binding.Veil.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.Veil.alpha = veilFadeControl(slideOffset)
            }
        })



        playerNameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                activateCreatePlayerButton(s.isNullOrBlank())
            }
        }

        with(binding) {
            etCreatePlayer.addTextChangedListener(playerNameTextWatcher)
            ivCreatePlayerButton.isEnabled = false
            PlayerBottomSheetLayout.rvPlayersList.adapter = playersAdapter
        }

        viewModel.getPlayersState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setClickListeners() {
        binding.Veil.setOnClickListener { }

        // PLAYERS SELECTION SWITCH
        binding.swChoiceSwitch.setOnCheckedChangeListener { _, checked ->
            manageStartButtonEnabledState(!checked)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.openPlayerSelectionOption(checked)
            }
        }

        binding.btnStart.setOnClickListener {
            changeStartButtonColorOnClick(it)
            findNavController().navigate(
                R.id.action_playersFragment_to_gameFragment
            )
        }

        binding.tbPlayersToolBar.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.ivCreatePlayerButton.setOnClickListener {
            changeSavePlayerButtonColorOnClick(it)
            viewLifecycleOwner.lifecycleScope.launch {
                val playerExist = isPlayerExist()
                showCreateMessage(playerExist)
                if (!playerExist) {

                    val newPlayersList = async(Dispatchers.IO) {
                        viewModel.savePlayer(binding.etCreatePlayer.text.toString().trim())
                        viewModel.getPlayers().toMutableList()
                    }

                    players = newPlayersList.await()
                    updateAdapterPlayers()
                    binding.etCreatePlayer.text = null
                    hideKeyboard()
                }
            }
        }

        binding.ivAddCrossPlayer.setOnClickListener {
            hideKeyboard()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.ivAddZeroPlayer.setOnClickListener {
            hideKeyboard()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun render(state: PlayersState) {
        when(state) {
            PlayersState.SelectDisable -> showSelectionDisable()
            is PlayersState.SelectEnable -> showSelectionEnable(state.players)
        }
    }

    private fun showSelectionDisable() {
        hideKeyboard()
        with(binding) {
            grSelectPlayerInterface.isVisible = false
            tvXPlayerValue.text = null
            tvOPlayerValue.text = null
            tvNewPlayerInfo.text = null
            etCreatePlayer.text = null
        }
        playerO = null
        playerX = null
        players.clear()
    }

    private fun showSelectionEnable(allPlayersFromDb: List<Player>) {
        players.addAll(allPlayersFromDb)
        binding.grSelectPlayerInterface.isVisible = true
        configurePlayerListState()
    }


    private fun configurePlayerListState(playersList: MutableList<Player> = players) {
        updateAdapterPlayers()

        // SHOW RV OR INFO
        with(binding.PlayerBottomSheetLayout) {
            rvPlayersList.isVisible = playersList.isNotEmpty()
            tvNoPlayersText.isVisible = playersList.isEmpty()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterPlayers(playersList: MutableList<Player> = players) {
        playersAdapter.players.clear()
        playersAdapter.players.addAll(playersList)
        playersAdapter.notifyDataSetChanged()
    }

    private fun isPlayerExist(): Boolean {
        val playerNames = players.map { player -> player.name }.toSet()
        return playerNames.contains(binding.etCreatePlayer.text.toString().trim())
    }

    private suspend fun showCreateMessage(playerExists: Boolean) {
        createInfoJob?.cancel()
        createInfoJob = viewLifecycleOwner.lifecycleScope.launch {
            if (playerExists) {
                binding.tvNewPlayerInfo.setTextColor(requireContext().getColor(R.color.crimson))
                binding.tvNewPlayerInfo.text = requireContext().getString(R.string.player_not_created)
            } else {
                binding.tvNewPlayerInfo.setTextColor(requireContext().getColor(R.color.grey_pale))
                binding.tvNewPlayerInfo.text = requireContext().getString(R.string.player_created)
            }

            binding.tvNewPlayerInfo.isVisible = true
            delay(CREATE_INFO_TIMER)
            binding.tvNewPlayerInfo.isVisible = false
        }
    }

    private fun activateCreatePlayerButton(textIsEmpty: Boolean) {
        binding.ivCreatePlayerButton.isEnabled = !textIsEmpty
        binding.ivCreatePlayerButton.alpha = if (textIsEmpty) 0.2f else 1f
    }

    private fun changeStartButtonColorOnClick(button: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            button.setBackgroundColor(requireContext().getColor(R.color.crimson))
            delay(25)
            button.setBackgroundColor(requireContext().getColor(R.color.dark))
        }
    }

    private fun changeSavePlayerButtonColorOnClick(button: View) {
        val imageView = button as ImageView
        viewLifecycleOwner.lifecycleScope.launch {
            imageView.drawable.setTint(requireContext().getColor(R.color.crimson))
            delay(100)
            imageView.drawable.setTint(requireContext().getColor(R.color.grey_pale))
        }
    }

    private fun manageStartButtonEnabledState(enabled: Boolean) {
        with(binding.btnStart) {
            isEnabled = enabled
            alpha = if (enabled) 1f else 0.2f
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.etCreatePlayer.windowToken, 0)
    }

    private fun veilFadeControl(slideOffset: Float): Float {
        val alphaDarkBoost: Float
        val alphaDelta: Float

        if (slideOffset >= 0) {
            alphaDarkBoost = 0.6f
            alphaDelta = 0.4f * slideOffset
        } else {
            alphaDarkBoost = 0.6f
            alphaDelta = 0.6f * slideOffset
        }
        return alphaDarkBoost + alphaDelta
    }

    companion object {
        const val CREATE_INFO_TIMER = 2500L
    }

}