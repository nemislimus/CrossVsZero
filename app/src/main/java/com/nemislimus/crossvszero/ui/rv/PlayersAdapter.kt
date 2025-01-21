package com.nemislimus.crossvszero.ui.rv

import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.crossvszero.R
import com.nemislimus.crossvszero.databinding.BsPlayerListItemBinding
import com.nemislimus.crossvszero.databinding.StatListItemBinding
import com.nemislimus.crossvszero.domain.models.Player

class PlayersAdapter(
    private val isStatisticList: Boolean,
    private val menuListener: ItemMenuClickListener?,
    private val itemClickListener: ItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val players = ArrayList<Player>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PLAYER -> PlayerViewHolder(
                BsPlayerListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )

            TYPE_STAT_PLAYER -> StatisticViewHolder(
                StatListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            )

            else -> throw ClassNotFoundException(R.string.unknown_viewholder_create.toString())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlayerViewHolder -> {
                val item = players[position]
                holder.bind(item)
                holder.itemView.setOnClickListener { itemClickListener.onItemClick(item) }
            }

            is StatisticViewHolder -> {
                val item = players[position]
                holder.bind(item)
                holder.getBinding().ivDeletePlayer.setOnClickListener { showPopupMenu(it, item) }
            }

            else -> throw ClassNotFoundException(R.string.unknown_viewholder_bind.toString())
        }
    }

    override fun getItemCount(): Int = players.size

    override fun getItemViewType(position: Int): Int =
        if (isStatisticList) TYPE_STAT_PLAYER else TYPE_PLAYER

    fun interface ItemClickListener {
        fun onItemClick(item: Player)
    }

    interface ItemMenuClickListener {
        fun handleDelete(model: Player)
        fun handleCleanUp(model: Player)
    }

    private fun showPopupMenu(view: View, model: Player) {
        val wrapper = ContextThemeWrapper(view.context, R.style.StatisticPopupMenuStyle)
        val popupMenu = PopupMenu(wrapper, view,Gravity.BOTTOM)
        popupMenu.menuInflater.inflate(R.menu.stat_item_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_cleanup -> menuListener?.handleCleanUp(model)
                R.id.action_delete -> menuListener?.handleDelete(model)
            }
            true
        }
        popupMenu.show()
    }

    companion object {
        private const val TYPE_STAT_PLAYER = 0
        private const val TYPE_PLAYER = 1
    }
}