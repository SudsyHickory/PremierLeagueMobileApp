package com.example.premierleagueapp.domain.model

/**
 * Informacje o rozgrywkach i bieżącym sezonie.
 *
 * @property code kod rozgrywek w API, np. „PL”
 * @property currentMatchday numer bieżącej kolejki
 * @property totalMatchdays liczba kolejek w sezonie – zakres strzałek na ekranie Mecze
 */
data class Competition(
    val name: String,
    val code: String,
    val seasonStartYear: Int,
    val seasonEndYear: Int,
    val currentMatchday: Int,
    val totalMatchdays: Int
)
