
# Конвертер валют

 Это Android-приложение для конвертации валют, разработанное с использованием Kotlin и реактивного подхода.
## Особенности

- Конвертация между различными валютами
- Использование актуальных курсов валют через API
- Кэширование данных для оффлайн-использования
- Простой и интуитивно понятный пользовательский интерфейс

## Технологии

- Kotlin
- MVVM архитектура
- Coroutines для асинхронных операций
- Flow для реактивного программирования
- Retrofit для сетевых запросов
- View Binding для безопасной работы с UI


## Использование

1. Введите сумму для конвертации
2. Выберите исходную валюту из выпадающего списка
3. Выберите целевую валюту из выпадающего списка
4. Нажмите кнопку "Конвертировать"
5. Результат конвертации отобразится на экране

## Структура проекта

- `MainActivity`: Основной экран приложения
- `MainViewModel`: ViewModel для обработки бизнес-логики
- `RetrofitClient`: Настройка Retrofit для сетевых запросов
- `ExchangeRateApi`: Интерфейс для API валютных курсов
- `ExchangeRatesResponse`: Data class для ответа API
- `ConversionResult`: Sealed class для представления результатов конвертации

## Автор: Чистяков Алексей Олегоыич

