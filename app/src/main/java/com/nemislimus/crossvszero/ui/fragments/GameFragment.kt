package com.nemislimus.crossvszero.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentGameBinding
import com.nemislimus.crossvszero.ui.models.GameState
import com.nemislimus.crossvszero.ui.viewmodels.GameFragmentViewModel
import com.nemislimus.crossvszero.utils.BindingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameFragment : BindingFragment<FragmentGameBinding>() {

    private lateinit var animationFadeIn: Animation
    private lateinit var fieldCellsViews: Array<ImageView>

    private val viewModel by viewModel<GameFragmentViewModel>()

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGameBinding {
        return FragmentGameBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStartUiState()

        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                fieldCellsViews = setFieldCellsViews()

                viewModel.getGameState().observe(viewLifecycleOwner) { state ->
                    renderField(state)
                }

                setCellsOnClickListeners()
            }
        }

//        binding.cell0.setOnClickListener {
//            binding.cell0.setImageResource(R.drawable.cross)
//            it.startAnimation(animationFadeIn)
//            binding.fabRestartGameButton.show()
//            binding.fabRestartGameButton.startAnimation(animationFadeIn)
//        }
    }

    private fun setStartUiState() {
        animationFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.fabRestartGameButton.hide()

        viewModel.isZeroTurn().observe(viewLifecycleOwner) { value ->
            markOfTurn(value)
        }

        binding.tbGameToolBar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun markOfTurn(value: Boolean) {
        with(binding.tvInfoText) {
            if (value) {
                text = "zero turn"
            } else {
                text = "cross turn"
            }
        }
    }

    private fun setFieldCellsViews(): Array<ImageView> {
        val cells: Array<ImageView>
        with(binding) {
            cells = arrayOf(cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8)
        }
        return cells
    }

    private fun setCellsOnClickListeners() {
        fieldCellsViews.forEachIndexed { index, cell ->
            cell.setOnClickListener {
                if (cell.drawable == null) {
                    viewModel.clickOnCell(index)
                }
            }
        }
    }

    private fun renderField(state: GameState) {
        when(state) {
            is GameState.FinishGame -> TODO()
            is GameState.GameInProcess -> {
                Toast.makeText(requireContext(), "cell click", Toast.LENGTH_SHORT).show()
            }
            GameState.NewGame -> showNewField()
        }
    }

    private fun showNewField() {
        with(binding) {
            fabRestartGameButton.hide()
            fieldCellsViews.forEach { cell -> cell.setImageDrawable(null) }
            ivGameFieldLines.startAnimation(animationFadeIn)
        }
    }

}