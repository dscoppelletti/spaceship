
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.sample.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import it.scoppelletti.spaceship.sample.R
import it.scoppelletti.spaceship.sample.model.Item
import kotlinx.android.synthetic.main.item_layout.view.*

class ItemListAdapter(
        private val onItemClick: (itemId: Int) -> Unit
) : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ItemViewHolder {
        val itemView: View
        val inflater: LayoutInflater

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item: Item

        item = getItem(position)
        with (holder.itemView) {
            txtCode.text = item.code
            txtDesc.text = item.desc
            setOnClickListener {
                onItemClick(item.id)
            }
        }
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        holder.itemView.setOnClickListener(null)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {

            override fun areItemsTheSame(
                    oldItem: Item,
                    newItem: Item
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                    oldItem: Item,
                    newItem: Item
            ): Boolean = oldItem == newItem
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}