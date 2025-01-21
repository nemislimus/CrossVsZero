package com.nemislimus.crossvszero.ui.viewmodels

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.lifecycle.ViewModel

class MainFragmentViewModel : ViewModel() {
    private var backgroundAnimation: AnimatedVectorDrawable? = null

    fun startBackAnimation(backAnimation: AnimatedVectorDrawable) {
        if (backgroundAnimation == null) {
            backgroundAnimation = backAnimation.also { it.start() }
        }
    }

    fun restoreBackAnimation(): AnimatedVectorDrawable? = backgroundAnimation

    override fun onCleared() {
        backgroundAnimation?.stop()
        super.onCleared()
    }
}