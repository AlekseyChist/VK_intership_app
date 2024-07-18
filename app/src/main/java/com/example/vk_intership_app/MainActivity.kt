package com.example.vk_intership_app;

import MainViewModel
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.vk_intership_app.R
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var amountEditText: EditText
    private lateinit var fromCurrencySpinner: Spinner
    private lateinit var toCurrencySpinner: Spinner
    private lateinit var convertButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        amountEditText = findViewById(R.id.amountEditText)
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)
        convertButton = findViewById(R.id.convertButton)
        resultTextView = findViewById(R.id.resultTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        setupSpinners()
        setupConvertButton()
        observeConversionResult()
    }

    private fun setupSpinners() {
        val currencies =
            arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SEK", "NZD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        fromCurrencySpinner.adapter = adapter
        toCurrencySpinner.adapter = adapter
    }

    private fun setupConvertButton() {
        convertButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val fromCurrency = fromCurrencySpinner.selectedItem.toString()
            val toCurrency = toCurrencySpinner.selectedItem.toString()

            if (amount != null) {
                lifecycleScope.launch {
                    viewModel.convertCurrency(amount, fromCurrency, toCurrency)
                }
            } else {
                resultTextView.text = "Введите корректную сумму"
            }
        }
    }

    private fun observeConversionResult() {
        lifecycleScope.launch {
            viewModel.conversionResult.collect { result ->
                when (result) {
                    is ConversionResult.Initial -> {
                        resultTextView.text = ""
                        loadingProgressBar.visibility = View.GONE
                    }
                    is ConversionResult.Loading -> {
                        loadingProgressBar.visibility = View.VISIBLE
                        resultTextView.text = ""
                    }
                    is ConversionResult.Success -> {
                        loadingProgressBar.visibility = View.GONE
                        resultTextView.text = String.format("%.2f", result.result)
                    }
                    is ConversionResult.Error -> {
                        loadingProgressBar.visibility = View.GONE
                        resultTextView.text = result.message
                    }
                    else -> {
                        Log.e("MainActivity", "Unexpected ConversionResult state")
                        loadingProgressBar.visibility = View.GONE
                        resultTextView.text = "Неизвестная ошибка"
                    }
                }
            }
        }
    }
}