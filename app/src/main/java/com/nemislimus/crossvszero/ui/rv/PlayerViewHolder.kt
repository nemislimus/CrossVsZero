package com.nemislimus.crossvszero.ui.rv

import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.crossvszero.databinding.BsPlayerListItemBinding
import com.nemislimus.crossvszero.domain.models.Player

class PlayerViewHolder(
    private val binding: BsPlayerListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Player) {
        with(binding) {
            tvNameValue.text = model.name
            tvVictoriesValue.text = model.victories.toString()
            tvDefeatsValue.text = model.defeats.toString()
        }
    }
}