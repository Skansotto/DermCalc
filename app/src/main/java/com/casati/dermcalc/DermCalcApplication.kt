package com.casati.dermcalc

import android.app.Application
import com.casati.dermcalc.data.local.DermCalcDatabase
import com.casati.dermcalc.data.local.ImpostazioniManager
import com.casati.dermcalc.data.local.OnboardingManager
import com.casati.dermcalc.data.local.PinManager
import com.casati.dermcalc.data.local.ProfiloManager
import com.casati.dermcalc.data.local.SessioneCorrente
import com.casati.dermcalc.data.repository.BackupRepository
import com.casati.dermcalc.data.repository.MisurazioneRepository
import com.casati.dermcalc.data.repository.PazienteRepository

class DermCalcApplication : Application() {

    private val database by lazy { DermCalcDatabase.getInstance(this) }
    val pazienteRepository by lazy { PazienteRepository(database.pazienteDao()) }
    val misurazioneRepository by lazy { MisurazioneRepository(database.misurazioneDao()) }
    val backupRepository by lazy { BackupRepository(pazienteRepository, misurazioneRepository) }
    val pinManager by lazy { PinManager(this) }
    val onboardingManager by lazy { OnboardingManager(this) }
    val profiloManager by lazy { ProfiloManager(this) }
    val impostazioniManager by lazy { ImpostazioniManager(this) }
    val sessione by lazy { SessioneCorrente() }
}
