package com.example.premierleagueapp.domain.model

/**
 * Drużyna ligowa.
 *
 * @property shortName nazwa skrócona do wierszy meczów i tabeli, np. „Man City”
 * @property tla trzyliterowy skrót, np. „MCI”
 * @property crestUrl adres herbu; `null`, gdy herbu nie ma (wtedy UI pokazuje sam skrót)
 */
data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crestUrl: String? = null
)
