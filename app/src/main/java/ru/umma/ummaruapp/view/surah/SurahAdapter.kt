package ru.umma.ummaruapp.view.surah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.l4digital.fastscroll.FastScroller
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.view.base.BaseViewHolder
import ru.umma.ummaruapp.view.base.RVAdapter

class SurahAdapter : RVAdapter<AyahBlock, SurahAdapter.Holder>(), FastScroller.SectionIndexer {
    inner class Holder(itemView: View) : BaseViewHolder<AyahBlock>(itemView) {
        override fun bind(data: AyahBlock) {
            itemView.findViewById<AppCompatTextView>(R.id.number).text = data.number
            itemView.findViewById<AppCompatTextView>(R.id.transcription).text = data.transcription
            itemView.findViewById<AppCompatTextView>(R.id.translate).text = data.translate
            itemView.findViewById<AppCompatTextView>(R.id.arabic).text = data.arabic
            itemView.findViewById<AppCompatTextView>(R.id.explanation).text = data.explanation

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ayah, parent, false)
        )
    }

    override fun getSectionText(position: Int): CharSequence {
        return currentList[position].number
    }
}