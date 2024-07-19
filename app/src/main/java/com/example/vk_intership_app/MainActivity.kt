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
import com.example.vk_intership_app.databinding.ActivityMainBinding

import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setupSpinners()
        setupConvertButton()
        observeConversionResult()
    }

    private fun setupSpinners() {
        val currencies =
            arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SEK", "NZD")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.fromCurrencySpinner.adapter = adapter
        binding.toCurrencySpinner.adapter = adapter
    }

    private fun setupConvertButton() {
        binding.convertButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull()
            val fromCurrency = binding.fromCurrencySpinner.selectedItem.toString()
            val toCurrency = binding.toCurrencySpinner.selectedItem.toString()

            if (amount != null) {
                lifecycleScope.launch {
                    viewModel.convertCurrency(amount, fromCurrency, toCurrency)
                }
            } else {
                binding.resultTextView.text = "Введите корректную сумму"
            }
        }
    }

    private fun observeConversionResult() {
        lifecycleScope.launch {
            viewModel.conversionResult.collect { result ->
                when (result) {
                    is ConversionResult.Initial -> {
                        binding.resultTextView.text = ""
                        binding.loadingProgressBar.visibility = View.GONE
                    }
                    is ConversionResult.Loading -> {
                        binding.loadingProgressBar.visibility = View.VISIBLE
                        binding.resultTextView.text = ""
                    }
                    is ConversionResult.Success -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.resultTextView.text = String.format("%.2f", result.result)
                    }
                    is ConversionResult.Error -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.resultTextView.text = result.message
                        // Можно добавить более подробное описание ошибки здесь
                        }
                    else -> {
                        Log.e("MainActivity", "Unexpected ConversionResult state")
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.resultTextView.text = "Неизвестная ошибка"
                    }
                }
            }
        }
    }
}