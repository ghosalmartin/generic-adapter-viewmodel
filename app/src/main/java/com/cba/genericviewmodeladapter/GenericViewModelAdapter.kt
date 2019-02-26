package com.cba.genericviewmodeladapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class GenericViewModelAdapter<ViewModelType : GenericAdapterViewModel>(var list: List<ViewModelType>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun updateList(newlist: List<ViewModelType>, comparator: ((ViewModelType, ViewModelType) -> Boolean)? = null) {
        val res = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // pointer
                return areContentsTheSame(oldItemPosition, newItemPosition)
            }

            override fun getOldListSize(): Int = list.size
            override fun getNewListSize(): Int = newlist.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // values
                if (comparator != null) {
                    return comparator(newlist[newItemPosition], list[oldItemPosition])
                }
                return false
            }
        })

        this.list = newlist
        res.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        list.find { it.viewType == viewType }!!
            .createViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list[position].bindViewHolder(holder)
    }

    override fun getItemViewType(position: Int) = list[position].viewType
    override fun getItemCount() = list.size
}

interface GenericAdapterViewModel {
    val buildView: (parent: ViewGroup) -> View
    val viewType: Int
        get() = javaClass.hashCode()

    fun provideViewHolder(view: View): RecyclerView.ViewHolder = object : RecyclerView.ViewHolder(view) {}
    fun bindViewHolder(viewHolder: RecyclerView.ViewHolder)

    fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = provideViewHolder(buildView(parent))
}