package com.ns.news.presentation.adapter

import com.ns.news.data.db.Cell
import com.ns.news.presentation.adapter.ParentDiff

/**
 * Diff for [DiffAdapter].
 */
class TestDiff : ParentDiff<Cell>() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition) ?: return false
        val newItem = getNewItem(newItemPosition) ?: return false

        return oldItem.data[0].id == newItem.data[0].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = getOldItem(oldItemPosition) ?: return false
        val newItem = getNewItem(newItemPosition) ?: return false

//        return oldItem == newItem
        return oldItem.data == newItem.data
    }
}