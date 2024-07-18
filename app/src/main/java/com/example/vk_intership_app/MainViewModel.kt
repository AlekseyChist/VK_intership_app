import androidx.lifecycle.ViewModel
import com.example.vk_intership_app.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow



class MainViewModel : ViewModel() {
    private val _conversionResult = MutableStateFlow<ConversionResult>(ConversionResult.Initial)
    val conversionResult: StateFlow<ConversionResult> = _conversionResult

    private val cache = mutableMapOf<String, Pair<Map<String, Double>, Long>>()
    private val cacheExpiration = 60 * 60 * 1000 // 1 час в миллисекундах

    suspend fun convertCurrency(amount: Double, from: String, to: String) {
        _conversionResult.value = ConversionResult.Loading
        try {
            val rates = getRates("EUR") // Базовая валюта всегда EUR для бесплатного плана
            val euroRate = if (from == "EUR") 1.0 else 1 / (rates[from] ?: throw Exception("Rate not found for $from"))
            val targetRate = rates[to] ?: throw Exception("Rate not found for $to")
            val result = amount * euroRate * targetRate
            _conversionResult.value = ConversionResult.Success(result)
        } catch (e: Exception) {
            _conversionResult.value = ConversionResult.Error("Ошибка конвертации: ${e.localizedMessage}")
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
