package com.nemislimus.crossvszero.ui.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.crossvszero.databinding.BsPlayerListItemBinding
import com.nemislimus.crossvszero.domain.models.Player

class PlayersAdapter(
    private val itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<PlayerViewHolder>() {

    val players = ArrayList<Player>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PlayerViewHolder(BsPlayerListItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val item = players[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { itemClickListener.onItemClick(item) }
    }

    override fun getItemCount(): Int = players.size

    fun interface ItemClickListener {
        fun onItemClick(item: Player)
    }

}