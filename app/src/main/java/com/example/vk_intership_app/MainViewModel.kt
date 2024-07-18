import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vk_intership_app.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {
    private val _conversionResult = MutableStateFlow<ConversionResult>(ConversionResult.Initial)
    val conversionResult: StateFlow<ConversionResult> = _conversionResult

    private val cache = mutableMapOf<String, Pair<Map<String, Double>, Long>>()
    private val cacheExpiration = 60 * 60 * 1000 // 1 час в миллисекундах

    fun convertCurrency(amount: Double, from: String, to: String) {
        viewModelScope.launch {
            _conversionResult.value = ConversionResult.Loading
            try {
                val rates = getRates(from)
                val baseRate = rates[from] ?: 1.0
                val targetRate = rates[to] ?: throw Exception("Rate not found for $to")
                val result = amount * (targetRate / baseRate)
                _conversionResult.value = ConversionResult.Success(result)
            } catch (e: Exception) {
                _conversionResult.value = ConversionResult.Error("Ошибка конвертации: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun getRates(base: String): Map<String, Double> {
        val currentTime = System.currentTimeMillis()
        cache[base]?.let { (rates, timestamp) ->
            if (currentTime - timestamp < cacheExpiration) {
                return rates
            }
        }

        val response = RetrofitClient.api.getLatestRates(RetrofitClient.getApiKey(), base)
        cache[base] = Pair(response.rates, currentTime)
        return response.rates
    }
}


sealed class ConversionResult {
    object Initial : ConversionResult()
    object Loading : ConversionResult()
    data class Success(val result: Double) : ConversionResult()
    data class Error(val message: String) : ConversionResult()
}
