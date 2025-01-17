package com.nemislimus.crossvszero.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.FragmentInfoBinding
import com.nemislimus.crossvszero.utils.BindingFragment

class InfoFragment : BindingFragment<FragmentInfoBinding>() {

    private lateinit var animationFadeIn: Animation
    private var allowRuClick = true
    private var allowEnClick = false

    override fun createFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding {
        return FragmentInfoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animationFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

        binding.tbInfoToolBar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnEnSelect.setOnClickListener {
            if (allowEnClick) {
                allowEnClick = false
                changeLanguageButtonState(binding.btnEnSelect, binding.btnRuSelect)
                setTextLanguage(true)
                binding.slAllText.startAnimation(animationFadeIn)
                allowRuClick = true
            }
        }

        binding.btnRuSelect.setOnClickListener {
            if (allowRuClick) {
                allowRuClick = false
                changeLanguageButtonState(binding.btnRuSelect, binding.btnEnSelect)
                setTextLanguage(false)
                binding.slAllText.startAnimation(animationFadeIn)
                allowEnClick = true
            }
        }

    }

    private fun changeLanguageButtonState(clickedButton: MaterialButton, anotherButton: MaterialButton) {
        clickedButton.setTextColor(requireContext().getColor(R.color.crimson))
        clickedButton.setStrokeColorResource(R.color.crimson)
        anotherButton.setTextColor(requireContext().getColor(R.color.grey_pale))
        anotherButton.setStrokeColorResource(R.color.grey_pale)
    }

    private fun setTextLanguage(isEnglish: Boolean) {
        if (isEnglish) {
            with(binding) {
                tvRuleTitle.text = requireContext().getString(R.string.title_rules_en)
                tvRulesText.text = requireContext().getString(R.string.rules_en)
                tvFeaturesTitle.text = requireContext().getString(R.string.title_features_en)
                tvFeaturesText.text = requireContext().getString(R.string.features_en)
            }
        } else {
            with(binding) {
                tvRuleTitle.text = requireContext().getString(R.string.title_rules_ru)
                tvRulesText.text = requireContext().getString(R.string.rules_ru)
                tvFeaturesTitle.text = requireContext().getString(R.string.title_features_ru)
                tvFeaturesText.text = requireContext().getString(R.string.features_ru)
            }
        }
    }

}