package com.gilklempert.waquickchat

import android.content.Context

class HistoryStore(context: Context) {
    private val prefs = context.getSharedPreferences("wa_history", Context.MODE_PRIVATE)
    private val key = "numbers"
    private val max = 20

    fun getAll(): List<String> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        return raw.split(",").filter { it.isNotEmpty() }
    }

    fun add(number: String) {
        val list = getAll().toMutableList()
        list.remove(number)
        list.add(0, number)
        if (list.size > max) list.subList(max, list.size).clear()
        save(list)
    }

    fun remove(number: String) {
        save(getAll().filter { it != number })
    }

    fun clear() {
        prefs.edit().remove(key).apply()
    }

    private fun save(list: List<String>) {
        prefs.edit().putString(key, list.joinToString(",")).apply()
    }
}
