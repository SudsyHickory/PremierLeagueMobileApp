package com.example.premierleagueapp.ui.components

import androidx.annotation.StringRes
import com.example.premierleagueapp.R
import com.example.premierleagueapp.domain.model.RefreshException

/**
 * Jedno miejsce, w którym powód nieudanego odświeżenia zamienia się na tekst.
 * ViewModele przekazują [RefreshException.Reason], a nie gotowe komunikaty (decyzja 11).
 */

/** Tytuł pełnoekranowego błędu (brak danych). */
@StringRes
fun RefreshException.Reason.errorTitleRes(): Int = when (this) {
    RefreshException.Reason.NO_CONNECTION -> R.string.error_no_connection_title
    RefreshException.Reason.RATE_LIMITED -> R.string.error_rate_limited_title
    RefreshException.Reason.UNKNOWN -> R.string.error_unknown_title
}

/** Opis pełnoekranowego błędu (brak danych). */
@StringRes
fun RefreshException.Reason.errorMessageRes(): Int = when (this) {
    RefreshException.Reason.NO_CONNECTION -> R.string.error_no_connection_message
    RefreshException.Reason.RATE_LIMITED -> R.string.error_rate_limited_message
    RefreshException.Reason.UNKNOWN -> R.string.error_unknown_message
}

/** Tekst snackbara, gdy odświeżenie się nie udało, ale dane zostały na ekranie. */
@StringRes
fun RefreshException.Reason.refreshFailedRes(): Int = when (this) {
    RefreshException.Reason.RATE_LIMITED -> R.string.refresh_failed_rate_limited
    RefreshException.Reason.NO_CONNECTION,
    RefreshException.Reason.UNKNOWN -> R.string.refresh_failed
}
