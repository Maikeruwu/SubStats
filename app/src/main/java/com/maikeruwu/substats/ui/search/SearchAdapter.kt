package com.maikeruwu.substats.ui.search

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maikeruwu.substats.R
import com.maikeruwu.substats.model.data.SearchItem
import com.maikeruwu.substats.service.loadCoverArt

class SearchAdapter(
    private val searchItems: List<SearchItem<Any>>,
    private var onItemClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_search_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = searchItems[position]

        holder.name.text = item.name
        holder.type.text = item.type
        item.coverArt?.let {
            holder.coverArt.loadCoverArt(it)
        }
    }

    override fun getItemCount(): Int {
        return searchItems.size
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: RelativeLayout = itemView.findViewById(R.id.cardItem)
        val name: TextView = itemView.findViewById(R.id.name)
        val type: TextView = itemView.findViewById(R.id.valueType)
        val coverArt: ImageView = itemView.findViewById(R.id.coverArt)

        init {
            card.setOnClickListener {
                // Deep view callback
                (itemView.context as? Activity)?.let {
                    (itemView.parent as? RecyclerView)?.adapter?.let { adapter ->
                        if (adapter is SearchAdapter) {
                            adapter.onItemClickListener.invoke(adapterPosition)
                        }
                    }
                }
            }
        }
    }
}