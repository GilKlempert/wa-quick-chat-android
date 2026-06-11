package com.gilklempert.waquickchat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class QuickInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_input)

        val store = HistoryStore(this)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val openBtn = findViewById<Button>(R.id.openBtn)
        val recentList = findViewById<ListView>(R.id.recentList)
        val overlay = findViewById<View>(R.id.overlay)

        overlay.setOnClickListener { finish() }

        val history = store.getAll()
        if (history.isNotEmpty()) {
            val displayItems = history.map { PhoneUtils.displayNumber(it) }
            recentList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayItems)
            recentList.setOnItemClickListener { _, _, position, _ ->
                val number = history[position]
                store.add(number)
                openWhatsApp(number)
            }
        } else {
            recentList.visibility = View.GONE
        }

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
                openWhatsApp(number)
            }
        }

        phoneInput.requestFocus()
    }

    private fun openWhatsApp(number: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$number")))
        finish()
    }
}
