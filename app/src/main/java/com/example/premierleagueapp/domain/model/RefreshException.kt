package com.example.premierleagueapp.domain.model

/**
 * Nieudane odświeżenie danych ligowych. Jedyny błąd, jaki zgłasza `refresh()` repozytoriów –
 * ViewModel nie musi znać wyjątków sieci ani HTTP.
 */
class RefreshException(
    val reason: Reason,
    cause: Throwable? = null
) : Exception("Refresh failed: $reason", cause) {

    enum class Reason {
        /** Brak sieci lub serwer nie odpowiada. */
        NO_CONNECTION,

        /** Przekroczony limit zapytań API (HTTP 429). */
        RATE_LIMITED,

        UNKNOWN
    }
}
