import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.vk_intership_app.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _conversionResult = MutableStateFlow<ConversionResult>(ConversionResult.Initial)
    val conversionResult: StateFlow<ConversionResult> = _conversionResult

    fun convertCurrency(amount: Double, from: String, to: String) {
        viewModelScope.launch {
            _conversionResult.value = ConversionResult.Loading
            try {
                val response = RetrofitClient.api.getLatestRates(from)
                val rate = response.rates[to] ?: throw Exception("Rate not found")
                _conversionResult.value = ConversionResult.Success(result = amount * rate)
            } catch (e: Exception) {
                _conversionResult.value = ConversionResult.Error("Ошибка конвертации: ${e.localizedMessage}")
            }
        }
    }
}

sealed class ConversionResult {
    object Initial : ConversionResult()
    object Loading : ConversionResult()
    data class Success(val result: Double) : ConversionResult()
    data class Error(val message: String) : ConversionResult()
}