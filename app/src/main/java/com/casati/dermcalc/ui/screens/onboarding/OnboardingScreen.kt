package com.casati.dermcalc.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casati.dermcalc.R
import kotlinx.coroutines.launch

private data class OnboardingPagina(val titoloRes: Int, val descrizioneRes: Int)

private val PAGINE = listOf(
    OnboardingPagina(R.string.onboarding_pagina1_titolo, R.string.onboarding_pagina1_descrizione),
    OnboardingPagina(R.string.onboarding_pagina2_titolo, R.string.onboarding_pagina2_descrizione),
    OnboardingPagina(R.string.onboarding_pagina3_titolo, R.string.onboarding_pagina3_descrizione),
    OnboardingPagina(R.string.onboarding_pagina4_titolo, R.string.onboarding_pagina4_descrizione)
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { PAGINE.size })
    val scope = rememberCoroutineScope()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TextButton(
                onClick = { viewModel.completaOnboarding() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.onboarding_azione_salta))
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pagina ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(PAGINE[pagina].titoloRes),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(PAGINE[pagina].descrizioneRes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(PAGINE.size) { indice ->
                    val selezionato = indice == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (selezionato) 10.dp else 8.dp)
                            .background(
                                color = if (selezionato) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
            Button(
                onClick = {
                    if (pagerState.currentPage == PAGINE.lastIndex) {
                        viewModel.completaOnboarding()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == PAGINE.lastIndex) {
                        stringResource(R.string.onboarding_azione_inizia)
                    } else {
                        stringResource(R.string.onboarding_azione_avanti)
                    }
                )
            }
        }
    }
}
