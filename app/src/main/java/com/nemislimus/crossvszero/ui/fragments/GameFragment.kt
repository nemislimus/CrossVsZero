package com.nemislimus.crossvszero.ui.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.bundle.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentGameBinding
import com.nemislimus.crossvszero.domain.models.GameCell
import com.nemislimus.crossvszero.ui.models.GameState
import com.nemislimus.crossvszero.ui.viewmodels.GameFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GameFragment : BindingFragment<FragmentGameBinding>() {

    private lateinit var animationFadeIn: Animation
    private lateinit var fieldCellsViews: List<ImageView>

    private var crossPlayerName: String? = null
    private var zeroPlayerName: String? = null
    private var playersNamesExist = false

    private val viewModel by viewModel<GameFragmentViewModel> {
        parametersOf(
            crossPlayerName,
            zeroPlayerName,
        )
    }

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGameBinding {
        return FragmentGameBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStartProperties()

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                fieldCellsViews = setFieldCellsViews()

                viewModel.getGameState().observe(viewLifecycleOwner) { state ->
                    renderField(state)
                }

                setCellsOnClickListeners()
            }
        }
    }

    private fun setStartProperties() {
        crossPlayerName = requireArguments().getString(CROSS_PLAYER)
        zeroPlayerName = requireArguments().getString(ZERO_PLAYER)
        playersNamesExist = areNamesExist(crossPlayerName, zeroPlayerName)
        animationFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.fabRestartGameButton.hide()
        binding.fabRestartGameButton.setOnClickListener { viewModel.resetFieldOnButtonClick() }

        viewModel.isZeroTurn().observe(viewLifecycleOwner) { value ->
            markOfTurn(value)
        }

        binding.tbGameToolBar.setOnClickListener {
            viewModel.resetFieldOnExit()
            findNavController().popBackStack(
                destinationId = R.id.mainFragment,
                inclusive = false
            )
        }
    }

    private fun areNamesExist(crossName: String?, zeroName: String?): Boolean {
        return !crossName.isNullOrBlank() && !zeroName.isNullOrBlank()
    }

    private fun markOfTurn(value: Boolean) {
        binding.tvInfoText.text = createMessageWithName(value)
    }

    private fun createMessageWithName(isZeroTurn: Boolean, hasWin: Boolean = false): String {
        val message: String
        if (hasWin) {
            message = if (playersNamesExist) {
                (if (isZeroTurn) {
                    "$zeroPlayerName ${getString(R.string.win)}"
                } else {
                    "$crossPlayerName ${getString(R.string.win)}"
                }).toString()
            } else {
                if (isZeroTurn) getString(R.string.zero_win) else getString(R.string.cross_win)
            }
        } else {
            message = if (playersNamesExist) {
                (if (isZeroTurn) {
                    "$zeroPlayerName ${getString(R.string.turn)}"
                } else {
                    "$crossPlayerName ${getString(R.string.turn)}"
                }).toString()
            } else {
                if (isZeroTurn) getString(R.string.zero_turn) else getString(R.string.cross_turn)
            }
        }
        return message
    }

    private fun setFieldCellsViews(): MutableList<ImageView> {
        val cells: MutableList<ImageView>
        with(binding) {
            cells = mutableListOf(cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8)
        }
        return cells
    }

    private fun setCellsOnClickListeners() {
        fieldCellsViews.forEachIndexed { index, cell ->
            cell.setOnClickListener {
                if (cell.drawable == null) {
                    clickOnCell(index)
                }
            }
        }
    }

    private fun clickOnCell(index: Int) {
        val weakElementIndex = viewModel.weakElementIndex
        weakElementIndex?.let {
            fieldCellsViews[it].setImageDrawable(null)
        }
        viewModel.clickOnCell(index)
    }

    private fun clearCellsClickListeners() {
        fieldCellsViews.forEach { cell ->
            cell.setOnClickListener(null)
        }
    }

    private fun renderField(state: GameState) {
        when (state) {
            GameState.NewGame -> prepareNewField()

            is GameState.GotWinner -> showEndGame(
                state.cells,
                state.winCellsIndexes,
                state.zeroTurn
            )

            is GameState.GameInProcess -> updateGameField(
                state.cells,
                state.winCellsIndexes,
                state.zeroTurn,
                state.weakElementIndex
            )

            is GameState.NoWinner -> showEndGame(
                state.cells,
                state.winCellsIndexes,
                state.zeroTurn
            )
        }
    }

    private fun showEndGame(cells: List<GameCell>, winCellsIndexes: List<Int>, zeroTurn: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            clearCellsClickListeners()
            updateGameField(cells, winCellsIndexes, zeroTurn)

            val updatePlayerStatisticJob = launch(Dispatchers.IO) {
                viewModel.savePlayersGameResults(zeroTurn)
            }
            updatePlayerStatisticJob.join()

            with(binding) {
                fabRestartGameButton.show()
                fabRestartGameButton.startAnimation(animationFadeIn)
                tvInfoText.setTextColor(requireContext().getColor(R.color.crimson))

                val infoText = if (winCellsIndexes.isNotEmpty()) {
                    createMessageWithName(zeroTurn, true)
                } else {
                    getString(R.string.no_winner)
                }

                tvInfoText.text = infoText
            }
        }
    }

    private fun updateGameField(
        cells: List<GameCell>,
        winCellsIndexes: List<Int>,
        zeroTurn: Boolean,
        weakCellIndex: Int = -1
    ) {
        if (weakCellIndex != -1) {
            val animatedDrawable = choseDrawable(
                !zeroTurn,
                R.drawable.anim_zero_scale_down,
                R.drawable.anim_cross_scale_down
            )

            (animatedDrawable as? AnimatedVectorDrawable)?.start()
            fieldCellsViews[weakCellIndex].setImageDrawable(animatedDrawable)
        }

        drawCellsElements(
            cells.map { it.index },
            winCellsIndexes,
            zeroTurn
        )
    }

    private fun drawCellsElements(cells: List<Int>, winCellsIndexes: List<Int>, zeroTurn: Boolean) {
        val drawable = choseDrawable(
            zeroTurn,
            R.drawable.zero,
            R.drawable.cross
        )
        val animatedDrawable = choseDrawable(
            zeroTurn,
            R.drawable.anim_zero_scale,
            R.drawable.anim_cross_rotate
        )

        fieldCellsViews.forEachIndexed { index, cell ->
            if (cells.contains(index)) {
                if (winCellsIndexes.contains(index)) {
                    animatedDrawable?.setTint(requireContext().getColor(R.color.crimson))
                    (animatedDrawable as? AnimatedVectorDrawable)?.start()
                    cell.setImageDrawable(animatedDrawable)
                } else {
                    cell.setImageDrawable(drawable)
                }
            }
        }
    }

    private fun choseDrawable(
        zeroTurn: Boolean,
        @DrawableRes zeroId: Int,
        @DrawableRes crossId: Int
    ): Drawable? {
        return if (zeroTurn) {
            AppCompatResources.getDrawable(requireContext(), zeroId)
        } else {
            AppCompatResources.getDrawable(requireContext(), crossId)
        }
    }

    private fun prepareNewField() {
        with(binding) {
            setCellsOnClickListeners()
            binding.tvInfoText.setTextColor(requireContext().getColor(R.color.grey_pale))
            viewModel.whoIsFirst()
            fabRestartGameButton.hide()
            fieldCellsViews.forEach { cell -> cell.setImageDrawable(null) }
            ivGameFieldLines.isVisible = true
            ivGameFieldLines.startAnimation(animationFadeIn)
        }
    }

    companion object {
        private const val CROSS_PLAYER = "cross_player"
        private const val ZERO_PLAYER = "zero_player"

        fun createArguments(xPlayerName: String?, oPlayerName: String?): Bundle = bundleOf(
            CROSS_PLAYER to xPlayerName,
            ZERO_PLAYER to oPlayerName,
        )
    }
}