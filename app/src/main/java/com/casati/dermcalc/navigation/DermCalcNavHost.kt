package com.casati.dermcalc.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.casati.dermcalc.DermCalcApplication
import com.casati.dermcalc.R
import com.casati.dermcalc.data.local.MisurazioneEntity
import com.casati.dermcalc.data.local.PazienteEntity
import com.casati.dermcalc.data.local.TipoCalcolo
import com.casati.dermcalc.data.referto.GeneratoreReferto
import com.casati.dermcalc.domain.formattaDataFile
import com.casati.dermcalc.ui.components.Avviso
import com.casati.dermcalc.ui.components.BarraNavigazione
import com.casati.dermcalc.ui.components.SezionePrincipale
import com.casati.dermcalc.ui.components.rememberStatoAvviso
import com.casati.dermcalc.ui.screens.auth.AuthViewModel
import com.casati.dermcalc.ui.screens.auth.CreaPinScreen
import com.casati.dermcalc.ui.screens.bmi.BmiScreen
import com.casati.dermcalc.ui.screens.bmi.BmiViewModel
import com.casati.dermcalc.ui.screens.bsa.BsaScreen
import com.casati.dermcalc.ui.screens.bsa.BsaViewModel
import com.casati.dermcalc.ui.screens.calcolatore.CalcolatoreScreen
import com.casati.dermcalc.ui.screens.calcolatore.CalcolatoreViewModel
import com.casati.dermcalc.ui.screens.calcolatore.RisultatoScreen
import com.casati.dermcalc.ui.screens.home.HomeScreen
import com.casati.dermcalc.ui.screens.home.HomeViewModel
import com.casati.dermcalc.ui.screens.impostazioni.ImpostazioniScreen
import com.casati.dermcalc.ui.screens.impostazioni.ImpostazioniViewModel
import com.casati.dermcalc.ui.screens.pazienti.PazienteDettaglioScreen
import com.casati.dermcalc.ui.screens.pazienti.PazienteDettaglioViewModel
import com.casati.dermcalc.ui.screens.pazienti.PazienteFormScreen
import com.casati.dermcalc.ui.screens.pazienti.PazienteListScreen
import com.casati.dermcalc.ui.screens.pazienti.PazienteViewModel
import com.casati.dermcalc.ui.screens.profilo.ProfiloViewModel
import com.casati.dermcalc.ui.screens.profilo.RegistrazioneScreen
import com.casati.dermcalc.ui.shared.SelezionePazienteViewModel
import java.io.File

// Le tre schermate radice mostrano la barra di navigazione; le schermate di
// dettaglio la nascondono per lasciare spazio al contenuto e al piede d'azione.
private val ROTTE_CON_NAVIGAZIONE = setOf(
    Screen.Home.route,
    Screen.Pazienti.route,
    Screen.Impostazioni.route
)

@Composable
fun DermCalcNavHost(
    authViewModel: AuthViewModel,
    profiloViewModel: ProfiloViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val app = context.applicationContext as DermCalcApplication
    val statoAvviso = rememberStatoAvviso()

    val selezioneViewModel: SelezionePazienteViewModel = viewModel(
        factory = SelezionePazienteViewModel.Factory(app.pazienteRepository, app.misurazioneRepository)
    )
    val calcolatoreViewModel: CalcolatoreViewModel = viewModel(
        factory = CalcolatoreViewModel.Factory(app.misurazioneRepository, app.sessione)
    )
    val bmiViewModel: BmiViewModel = viewModel(
        factory = BmiViewModel.Factory(app.misurazioneRepository, app.sessione)
    )
    val bsaViewModel: BsaViewModel = viewModel(
        factory = BsaViewModel.Factory(app.misurazioneRepository, app.sessione)
    )
    val pazienteViewModel: PazienteViewModel = viewModel(
        factory = PazienteViewModel.Factory(app.pazienteRepository, app.sessione)
    )
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(app.pazienteRepository, app.misurazioneRepository)
    )
    val impostazioniViewModel: ImpostazioniViewModel = viewModel(
        factory = ImpostazioniViewModel.Factory(
            app.backupRepository,
            app.impostazioniManager,
            app.pazienteRepository,
            app.misurazioneRepository
        )
    )

    val profiloState by profiloViewModel.uiState.collectAsState()
    val pazienteCollegatoId by app.sessione.pazienteCollegatoId.collectAsState()
    val pazienti by selezioneViewModel.pazienti.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rottaCorrente = backStackEntry?.destination?.route

    val condividiReferto: (Long) -> Unit = { pazienteId ->
        val voce = pazienti.firstOrNull { it.paziente.id == pazienteId }
        if (voce != null) {
            condividiRefertoPdf(
                context = context,
                paziente = voce.paziente,
                misurazioni = selezioneViewModel.misurazioniDi(pazienteId),
                profilo = profiloState.profilo,
                onErrore = { statoAvviso.mostra(context.getString(R.string.referto_fallito)) }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        selezioneViewModel = selezioneViewModel,
                        profilo = profiloState.profilo,
                        pazienteCollegatoId = pazienteCollegatoId,
                        onCollegaPaziente = { app.sessione.collega(it) },
                        onApriCalcolatore = { tipo ->
                            when (tipo) {
                                TipoCalcolo.BMI -> navController.navigate(Screen.Bmi.route)
                                TipoCalcolo.BSA -> navController.navigate(Screen.Bsa.route)
                                else -> {
                                    calcolatoreViewModel.impostaTipo(tipo)
                                    navController.navigate(Screen.Calcolatore.creaRoute(tipo))
                                }
                            }
                        },
                        onApriPaziente = { navController.navigate(Screen.PazienteDettaglio.creaRoute(it)) },
                        onApriImpostazioni = { navController.navigate(Screen.Impostazioni.route) }
                    )
                }

                composable(
                    route = Screen.Calcolatore.route,
                    arguments = listOf(navArgument(Screen.ARG_TIPO) { type = NavType.StringType })
                ) { entry ->
                    val tipo = runCatching {
                        TipoCalcolo.valueOf(entry.arguments?.getString(Screen.ARG_TIPO).orEmpty())
                    }.getOrDefault(TipoCalcolo.PASI)
                    LaunchedEffect(tipo) { calcolatoreViewModel.impostaTipo(tipo) }
                    CalcolatoreScreen(
                        viewModel = calcolatoreViewModel,
                        selezioneViewModel = selezioneViewModel,
                        onIndietro = { navController.popBackStack() },
                        onVediRisultato = { navController.navigate(Screen.Risultato.route) }
                    )
                }

                composable(Screen.Risultato.route) {
                    RisultatoScreen(
                        viewModel = calcolatoreViewModel,
                        selezioneViewModel = selezioneViewModel,
                        onIndietro = { navController.popBackStack() },
                        onChiudi = { navController.popBackStack(Screen.Home.route, false) },
                        onRefertoPdf = condividiReferto,
                        onSalvato = { pazienteId, messaggio ->
                            statoAvviso.mostra(messaggio)
                            navController.navigate(Screen.PazienteDettaglio.creaRoute(pazienteId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(Screen.Bmi.route) {
                    BmiScreen(
                        viewModel = bmiViewModel,
                        selezioneViewModel = selezioneViewModel,
                        onIndietro = { navController.popBackStack() },
                        onSalvato = { pazienteId, dettaglio ->
                            statoAvviso.mostra("BMI $dettaglio")
                            navController.navigate(Screen.PazienteDettaglio.creaRoute(pazienteId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(Screen.Bsa.route) {
                    BsaScreen(
                        viewModel = bsaViewModel,
                        selezioneViewModel = selezioneViewModel,
                        onIndietro = { navController.popBackStack() },
                        onSalvato = { pazienteId, dettaglio ->
                            statoAvviso.mostra("BSA $dettaglio")
                            navController.navigate(Screen.PazienteDettaglio.creaRoute(pazienteId)) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(Screen.Pazienti.route) {
                    PazienteListScreen(
                        viewModel = pazienteViewModel,
                        selezioneViewModel = selezioneViewModel,
                        pazienteCollegatoId = pazienteCollegatoId,
                        onPazienteClick = { navController.navigate(Screen.PazienteDettaglio.creaRoute(it)) },
                        onNuovoPaziente = { navController.navigate(Screen.NuovoPaziente.route) }
                    )
                }

                composable(Screen.NuovoPaziente.route) {
                    PazienteFormScreen(
                        viewModel = pazienteViewModel,
                        selezioneViewModel = selezioneViewModel,
                        pazienteEsistente = null,
                        onIndietro = { navController.popBackStack() },
                        onSalvato = { messaggio ->
                            statoAvviso.mostra(messaggio)
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.ModificaPaziente.route,
                    arguments = listOf(navArgument(Screen.ARG_PAZIENTE_ID) { type = NavType.LongType })
                ) { entry ->
                    val pazienteId = entry.arguments?.getLong(Screen.ARG_PAZIENTE_ID) ?: 0L
                    val paziente = pazienti.firstOrNull { it.paziente.id == pazienteId }?.paziente
                    PazienteFormScreen(
                        viewModel = pazienteViewModel,
                        selezioneViewModel = selezioneViewModel,
                        pazienteEsistente = paziente,
                        onIndietro = { navController.popBackStack() },
                        onSalvato = { messaggio ->
                            statoAvviso.mostra(messaggio)
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.PazienteDettaglio.route,
                    arguments = listOf(navArgument(Screen.ARG_PAZIENTE_ID) { type = NavType.LongType })
                ) { entry ->
                    val pazienteId = entry.arguments?.getLong(Screen.ARG_PAZIENTE_ID) ?: 0L
                    val dettaglioViewModel: PazienteDettaglioViewModel = viewModel(
                        key = "paziente-$pazienteId",
                        factory = PazienteDettaglioViewModel.Factory(
                            pazienteId,
                            app.pazienteRepository,
                            app.misurazioneRepository
                        )
                    )
                    PazienteDettaglioScreen(
                        viewModel = dettaglioViewModel,
                        onIndietro = { navController.popBackStack() },
                        onModificaPaziente = {
                            navController.navigate(Screen.ModificaPaziente.creaRoute(it))
                        },
                        onNuovaMisurazione = { id ->
                            app.sessione.collega(id)
                            calcolatoreViewModel.impostaTipo(TipoCalcolo.PASI)
                            navController.navigate(Screen.Calcolatore.creaRoute(TipoCalcolo.PASI))
                        },
                        onImpostaAttivo = { id ->
                            app.sessione.collega(id)
                            val nome = pazienti.firstOrNull { it.paziente.id == id }?.paziente
                            statoAvviso.mostra(
                                context.getString(
                                    R.string.scheda_collegato_formato,
                                    nome?.let { "${it.nome} ${it.cognome}" }.orEmpty()
                                )
                            )
                        },
                        onEsportaScheda = { paziente, misurazioni ->
                            condividiSchedaJson(
                                context = context,
                                nomeFile = context.getString(
                                    R.string.file_scheda_formato,
                                    paziente.cognome.lowercase()
                                ),
                                contenuto = app.backupRepository.schedaPaziente(paziente, misurazioni),
                                onErrore = {
                                    statoAvviso.mostra(context.getString(R.string.referto_fallito))
                                }
                            )
                        },
                        onRefertoPdf = condividiReferto,
                        onEliminato = { messaggio ->
                            statoAvviso.mostra(messaggio)
                            navController.popBackStack()
                        },
                        onMisurazioneEliminata = { statoAvviso.mostra(it) }
                    )
                }

                composable(Screen.Impostazioni.route) {
                    ImpostazioniScreen(
                        viewModel = impostazioniViewModel,
                        profilo = profiloState.profilo,
                        onModificaProfilo = { navController.navigate(Screen.ModificaProfilo.route) },
                        onCambiaPin = { navController.navigate(Screen.CambioPin.route) },
                        onAvviso = { statoAvviso.mostra(it) }
                    )
                }

                composable(Screen.ModificaProfilo.route) {
                    RegistrazioneScreen(
                        viewModel = profiloViewModel,
                        inModifica = true,
                        onIndietro = { navController.popBackStack() },
                        onCompletato = {
                            statoAvviso.mostra(context.getString(R.string.registrazione_profilo_aggiornato))
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.CambioPin.route) {
                    CreaPinScreen(
                        viewModel = authViewModel,
                        onIndietro = { navController.popBackStack() },
                        onPinImpostato = {
                            statoAvviso.mostra(context.getString(R.string.auth_pin_aggiornato))
                            navController.popBackStack()
                        }
                    )
                }
            }

            if (rottaCorrente in ROTTE_CON_NAVIGAZIONE) {
                BarraNavigazione(
                    sezioneAttiva = when (rottaCorrente) {
                        Screen.Pazienti.route -> SezionePrincipale.PAZIENTI
                        Screen.Impostazioni.route -> SezionePrincipale.IMPOSTAZIONI
                        else -> SezionePrincipale.CALCOLI
                    },
                    onSezioneClick = { sezione ->
                        val destinazione = when (sezione) {
                            SezionePrincipale.CALCOLI -> Screen.Home.route
                            SezionePrincipale.PAZIENTI -> Screen.Pazienti.route
                            SezionePrincipale.IMPOSTAZIONI -> Screen.Impostazioni.route
                        }
                        navController.navigate(destinazione) {
                            popUpTo(Screen.Home.route) { inclusive = destinazione == Screen.Home.route }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        Avviso(statoAvviso)
    }
}

private fun condividiRefertoPdf(
    context: android.content.Context,
    paziente: PazienteEntity,
    misurazioni: List<MisurazioneEntity>,
    profilo: com.casati.dermcalc.data.local.ProfiloMedico?,
    onErrore: () -> Unit
) {
    val uri = GeneratoreReferto.generaPdf(context, paziente, misurazioni, profilo)
    if (uri == null) {
        onErrore()
        return
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(intent, context.getString(R.string.referto_condividi))
    )
}

private fun condividiSchedaJson(
    context: android.content.Context,
    nomeFile: String,
    contenuto: String,
    onErrore: () -> Unit
) {
    val uri = runCatching {
        val cartella = File(context.cacheDir, "esportazioni").apply { mkdirs() }
        val file = File(cartella, nomeFile)
        file.writeText(contenuto)
        FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    }.getOrNull()
    if (uri == null) {
        onErrore()
        return
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(intent, context.getString(R.string.referto_condividi))
    )
}
