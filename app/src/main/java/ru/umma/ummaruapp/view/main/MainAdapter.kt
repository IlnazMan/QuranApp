package ru.umma.ummaruapp.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.l4digital.fastscroll.FastScroller
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.view.base.BaseViewHolder
import ru.umma.ummaruapp.view.base.RVAdapter

class MainAdapter(private val onClick: (Surah) -> Unit) :
    RVAdapter<Surah, MainAdapter.Holder>(), FastScroller.SectionIndexer {

    inner class Holder(itemView: View) : BaseViewHolder<Surah>(itemView) {
        override fun bind(data: Surah) {
            itemView.findViewById<AppCompatTextView>(R.id.sura_name).text = data.name
            itemView.findViewById<AppCompatTextView>(R.id.sura_number).text = data.number

            itemView.setOnClickListener {
                onClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sura, parent, false)
        )
    }

    override fun getSectionText(position: Int): CharSequence {
        return currentList[position].number
    }
}