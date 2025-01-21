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
    private var isClickAllowed = true
    private val viewModel by viewModel<PlayersFragmentViewModel>()

    private var players: MutableList<Player> = mutableListOf()
    private var playerX: Player? = null
    private var playerO: Player? = null
    private var zeroPlayerWorkInProgress = false

    private val playersAdapter = PlayersAdapter(false, null) { if (clickDebounce()) selectPlayer(it) }

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

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
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
            manageStartButtonEnableState(!checked)
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.managePlayerSelectionOption(checked)
            }
        }

        // CLICK ON START
        binding.btnStart.setOnClickListener {
            changeStartButtonColorOnClick(it)
            findNavController().navigate(
                R.id.action_playersFragment_to_gameFragment,
                GameFragment.createArguments(playerX?.name, playerO?.name)
            )
        }

        binding.tbPlayersToolBar.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.ivCreatePlayerButton.setOnClickListener {
            if (clickDebounce()) {
                changeSavePlayerButtonColorOnClick(it)
                clickOnSavePlayerButton()
            }
        }

        binding.ivAddCrossPlayer.setOnClickListener {
            zeroPlayerWorkInProgress = false
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            hideKeyboard()
        }

        binding.ivClearCrossPlayer.setOnClickListener {
            zeroPlayerWorkInProgress = false
            clearSelectedPlayer()
        }

        binding.ivAddZeroPlayer.setOnClickListener {
            zeroPlayerWorkInProgress = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            hideKeyboard()
        }

        binding.ivClearZeroPlayer.setOnClickListener {
            zeroPlayerWorkInProgress = true
            clearSelectedPlayer()
        }

    }

    private fun render(state: PlayersState) {
        when (state) {
            PlayersState.SelectDisable -> showSelectionDisable()
            is PlayersState.SelectEnable -> showSelectionEnable(state.players)
        }
    }

    private fun showSelectionDisable() {
        hideKeyboard()
        clearSelectionInterface()
    }

    private fun clearSelectionInterface() {
        with(binding) {
            grSelectPlayerInterface.isVisible = false
            ivClearZeroPlayer.isVisible = false
            ivClearCrossPlayer.isVisible = false
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
        configureBottomSheetViews()
    }


    private fun configureBottomSheetViews() {
        updateAdapterPlayers()
        // SHOW RV OR INFO MESSAGE
        with(binding.PlayerBottomSheetLayout) {
            rvPlayersList.isVisible = playersAdapter.players.isNotEmpty()
            tvNoPlayersText.isVisible = playersAdapter.players.isEmpty()
        }
    }

    private fun clearSelectedPlayer() {
        selectPlayer(null)
    }

    private fun selectPlayer(player: Player?) {
        val viewToInvisible: ImageView?
        val viewToVisible: ImageView?

        if (zeroPlayerWorkInProgress) {

            if (player != null) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.tvOPlayerValue.text = player.name
                playerO = player
                viewToInvisible = binding.ivAddZeroPlayer
                viewToVisible = binding.ivClearZeroPlayer
            } else {
                binding.tvOPlayerValue.text = null
                playerO = null
                viewToInvisible = binding.ivClearZeroPlayer
                viewToVisible = binding.ivAddZeroPlayer
            }

        } else {

            if (player != null) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.tvXPlayerValue.text = player.name
                playerX = player
                viewToInvisible = binding.ivAddCrossPlayer
                viewToVisible = binding.ivClearCrossPlayer
            } else {
                binding.tvXPlayerValue.text = null
                playerX = null
                viewToInvisible = binding.ivClearCrossPlayer
                viewToVisible = binding.ivAddCrossPlayer
            }

        }

        replaceButtonsVisibility(viewToInvisible, viewToVisible)
        updateAdapterPlayers()
        checkStartPossibility()
    }

    private fun checkStartPossibility() {
        manageStartButtonEnableState(playerX != null && playerO != null)
    }

    private fun replaceButtonsVisibility(toInvisible: ImageView?, toVisible: ImageView?) {
        toInvisible?.isVisible = false
        toVisible?.isVisible = true
    }

    private fun clickOnSavePlayerButton() {
        viewLifecycleOwner.lifecycleScope.launch {
            val playerExist = isPlayerExist()
            showCreateMessage(playerExist)

            if (!playerExist) {
                val newPlayersList = async(Dispatchers.IO) {
                    viewModel.savePlayer(binding.etCreatePlayer.text.toString().trim())
                    viewModel.getPlayers().toMutableList()
                }
                players = newPlayersList.await()
                configureBottomSheetViews()
                binding.etCreatePlayer.text = null
                hideKeyboard()
            }
        }
    }

    private fun isPlayerExist(): Boolean {
        val playerNames = players.map { player -> player.name }.toSet()
        return playerNames.contains(binding.etCreatePlayer.text.toString().trim())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateAdapterPlayers(playersList: MutableList<Player> = players) {
        val filteredPlayers = filterSelectedPlayers(playersList)
        playersAdapter.players.clear()
        playersAdapter.players.addAll(filteredPlayers)
        playersAdapter.notifyDataSetChanged()
    }

    private fun filterSelectedPlayers(playersList: MutableList<Player>): MutableList<Player> {
        val selectedNames = setOf(
            binding.tvXPlayerValue.text.toString(),
            binding.tvOPlayerValue.text.toString(),
        )

        return playersList.filterNot { player: Player ->
            player.name in selectedNames
        }.toMutableList()
    }

    private suspend fun showCreateMessage(playerExists: Boolean) {
        createInfoJob?.cancel()
        createInfoJob = viewLifecycleOwner.lifecycleScope.launch {
            if (playerExists) {
                binding.tvNewPlayerInfo.setTextColor(requireContext().getColor(R.color.crimson))
                binding.tvNewPlayerInfo.text =
                    requireContext().getString(R.string.player_not_created)
            } else {
                binding.tvNewPlayerInfo.setTextColor(requireContext().getColor(R.color.grey_pale))
                binding.tvNewPlayerInfo.text = requireContext().getString(R.string.player_created)
            }

            binding.tvNewPlayerInfo.isVisible = true
            delay(CREATE_INFO_VISIBILITY_TIMER)
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

    private fun manageStartButtonEnableState(enabled: Boolean) {
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
        val alphaVeilBoost: Float
        val alphaDelta: Float

        if (slideOffset >= 0) {
            alphaVeilBoost = 0.6f
            alphaDelta = 0.4f * slideOffset
        } else {
            alphaVeilBoost = 0.6f
            alphaDelta = 0.6f * slideOffset
        }
        return alphaVeilBoost + alphaDelta
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        const val CREATE_INFO_VISIBILITY_TIMER = 2500L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}