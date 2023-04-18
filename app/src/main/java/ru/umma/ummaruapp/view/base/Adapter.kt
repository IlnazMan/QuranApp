package ru.umma.ummaruapp.view.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Базовый адаптер для recycler view
 */
abstract class RVAdapter<T : Any?, VH : BaseViewHolder<T>>(
    private val areItemsTheSame: ((T, T) -> Boolean) = { old, new -> old == new },
    private val areContentTheSame: ((T, T) -> Boolean) = { old, new -> old == new },
) : ListAdapter<T, VH>(
    object : DiffUtil.ItemCallback<T>()
    {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areItemsTheSame(oldItem, newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = areContentTheSame(oldItem, newItem)
    })
{
    override fun onBindViewHolder(holder: VH, position: Int)
    {
        holder.bind(getItem(position))
    }
}

/**
 * Базовый холдер для элемента recycler view
 */
abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    protected val context: Context
        get() = itemView.context

    abstract fun bind(data: T)
}