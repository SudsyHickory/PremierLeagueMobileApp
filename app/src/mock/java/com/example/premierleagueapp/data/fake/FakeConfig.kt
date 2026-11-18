package com.example.premierleagueapp.data.fake

import com.example.premierleagueapp.domain.model.RefreshException
import kotlinx.coroutines.delay
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Wspólna konfiguracja fake repozytoriów. Pozwala celowo wywołać stany ładowania i błędu
 * bez wyłączania sieci. [refreshDelay] i [failRefresh] można zmieniać w trakcie działania.
 *
 * @property refreshDelay ile trwa symulowane pobieranie
 * @property failRefresh powód, z jakim każde odświeżenie ma się nie udać; `null` – odświeżanie działa
 * @property startEmpty `true` – repozytoria startują bez danych (jak pusta baza przy pierwszym
 *   uruchomieniu) i wypełniają się przy pierwszym udanym odświeżeniu; czytane raz, przy ich tworzeniu
 * @property now źródło bieżącego czasu dla „Zaktualizowano X min temu”
 */
class FakeConfig(
    @Volatile var refreshDelay: Duration = 800.milliseconds,
    @Volatile var failRefresh: RefreshException.Reason? = null,
    val startEmpty: Boolean = true,
    val now: () -> Instant = Instant::now
) {
    /** Wspólna część `refresh()` wszystkich fake'ów: opóźnienie, a potem ewentualny błąd. */
    suspend fun simulateRefresh() {
        delay(refreshDelay)
        failRefresh?.let { throw RefreshException(it) }
    }
}
