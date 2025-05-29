package com.maikeruwu.substats.ui.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maikeruwu.substats.R

abstract class AbstractListAdapter(
    protected val neverString: String,
    protected var onItemClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<AbstractListAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
        return ListViewHolder(view)
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: RelativeLayout = itemView.findViewById(R.id.cardItem)
        val name: TextView = itemView.findViewById(R.id.name)
        val playCount: TextView = itemView.findViewById(R.id.valuePlayCount)
        val lastPlayed: TextView = itemView.findViewById(R.id.valueLastPlayed)
        val coverArt: ImageView = itemView.findViewById(R.id.coverArt)

        init {
            card.setOnClickListener {
                // Deep view callback
                (itemView.context as? Activity)?.let {
                    (itemView.parent as? RecyclerView)?.adapter?.let { adapter ->
                        if (adapter is AbstractListAdapter) {
                            adapter.onItemClickListener.invoke(adapterPosition)
                        }
                    }
                }
            }
        }
    }
}