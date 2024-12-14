package com.nemislimus.crossvszero.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BindingFragment<T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding: T get() = _binding!!

    abstract fun createFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = createFragmentBinding(inflater, container)
        return binding.root
    }

    open fun onDestroyFragment() {}

    final override fun onDestroyView() {
        super.onDestroyView()
        onDestroyFragment()
        _binding = null
    }

    final override fun onDestroy() {
        super.onDestroy()
    }

    final override fun onDetach() {
        super.onDetach()
    }
}
