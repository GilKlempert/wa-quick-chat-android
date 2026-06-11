package com.gilklempert.waquickchat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var store: HistoryStore
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store = HistoryStore(this)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val openBtn = findViewById<Button>(R.id.openBtn)
        val clearBtn = findViewById<Button>(R.id.clearBtn)
        val historyList = findViewById<RecyclerView>(R.id.historyList)

        adapter = HistoryAdapter(
            store.getAll().toMutableList(),
            onItemClick = { openWhatsApp(it) },
            onDeleteClick = { num ->
                store.remove(num)
                refreshHistory()
            }
        )
        historyList.layoutManager = LinearLayoutManager(this)
        historyList.adapter = adapter

        phoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val clean = s.toString().replace(Regex("[\\s\\-()]"), "")
                openBtn.isEnabled = clean.length >= 9
            }
        })

        openBtn.setOnClickListener {
            val number = PhoneUtils.formatNumber(phoneInput.text.toString())
            if (number.length >= 9) {
                store.add(number)
                refreshHistory()
                openWhatsApp(number)
                phoneInput.text.clear()
                openBtn.isEnabled = false
            }
        }

        clearBtn.setOnClickListener {
            store.clear()
            refreshHistory()
        }
    }

    private fun openWhatsApp(number: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$number")))
    }

    private fun refreshHistory() {
        adapter.update(store.getAll())
    }
}

class HistoryAdapter(
    private val items: MutableList<String>,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val numberText: TextView = view.findViewById(R.id.numberText)
        val deleteBtn: ImageButton = view.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val num = items[position]
        holder.numberText.text = PhoneUtils.displayNumber(num)
        holder.itemView.setOnClickListener { onItemClick(num) }
        holder.deleteBtn.setOnClickListener { onDeleteClick(num) }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
