package com.casati.dermcalc.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import com.casati.dermcalc.ui.components.PulsanteAzione
import com.casati.dermcalc.ui.theme.Ambra
import com.casati.dermcalc.ui.theme.AmbraTenue
import com.casati.dermcalc.ui.theme.BordoMarcato
import com.casati.dermcalc.ui.theme.Indaco
import com.casati.dermcalc.ui.theme.IndacoTenue
import com.casati.dermcalc.ui.theme.MonoMedio
import com.casati.dermcalc.ui.theme.Sfondo
import com.casati.dermcalc.ui.theme.Superficie
import com.casati.dermcalc.ui.theme.TestoMedio
import com.casati.dermcalc.ui.theme.TestoTenue
import com.casati.dermcalc.ui.theme.Verde
import com.casati.dermcalc.ui.theme.VerdeTenue
import kotlinx.coroutines.launch

private data class PaginaOnboarding(
    val titoloRes: Int,
    val descrizioneRes: Int,
    val glifo: String,
    val colore: Color,
    val tinta: Color
)

private val PAGINE = listOf(
    PaginaOnboarding(
        R.string.onboarding_pagina1_titolo,
        R.string.onboarding_pagina1_descrizione,
        "4",
        Indaco,
        IndacoTenue
    ),
    PaginaOnboarding(
        R.string.onboarding_pagina2_titolo,
        R.string.onboarding_pagina2_descrizione,
        "∑",
        Ambra,
        AmbraTenue
    ),
    PaginaOnboarding(
        R.string.onboarding_pagina3_titolo,
        R.string.onboarding_pagina3_descrizione,
        "↗",
        Verde,
        VerdeTenue
    )
)

@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel, modifier: Modifier = Modifier) {
    val statoPager = rememberPagerState(pageCount = { PAGINE.size })
    val scope = rememberCoroutineScope()
    val ultima = statoPager.currentPage == PAGINE.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Sfondo)
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = stringResource(R.string.onboarding_azione_salta),
                style = MaterialTheme.typography.titleSmall,
                color = TestoTenue,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.completaOnboarding() }
                    .padding(horizontal = 4.dp, vertical = 10.dp)
            )
        }

        HorizontalPager(
            state = statoPager,
            modifier = Modifier.weight(1f)
        ) { indice ->
            val pagina = PAGINE[indice]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(34.dp, Alignment.CenterVertically)
            ) {
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .clip(RoundedCornerShape(38.dp))
                        .background(pagina.tinta),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(pagina.colore.copy(alpha = 0.10f))
                    )
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(pagina.colore.copy(alpha = 0.16f))
                    )
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(pagina.colore),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = pagina.glifo, style = MonoMedio, color = Superficie)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = stringResource(pagina.titoloRes),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(pagina.descrizioneRes),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TestoMedio,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PAGINE.indices.forEach { indice ->
                val attivo = indice == statoPager.currentPage
                val larghezza by animateDpAsState(if (attivo) 22.dp else 6.dp, tween(250), label = "dotW")
                val colore by animateColorAsState(if (attivo) Indaco else BordoMarcato, tween(250), label = "dotC")
                Box(
                    modifier = Modifier
                        .width(larghezza)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(colore)
                )
            }
        }

        PulsanteAzione(
            testo = stringResource(
                if (ultima) R.string.onboarding_azione_inizia else R.string.onboarding_azione_avanti
            ),
            onClick = {
                if (ultima) {
                    viewModel.completaOnboarding()
                } else {
                    scope.launch { statoPager.animateScrollToPage(statoPager.currentPage + 1) }
                }
            }
        )
    }
}
