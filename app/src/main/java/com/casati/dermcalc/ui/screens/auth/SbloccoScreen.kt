package com.casati.dermcalc.ui.screens.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.casati.dermcalc.R

@Composable
fun SbloccoScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var pin by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometriaDisponibile = remember {
        BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
            BiometricManager.BIOMETRIC_SUCCESS
    }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(text = stringResource(R.string.titolo_sblocco), style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text(stringResource(R.string.auth_pin_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )
            uiState.messaggioErrore?.let { messaggio ->
                Text(text = messaggio, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = { viewModel.verificaPin(pin) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.auth_azione_sblocca))
            }
            if (biometriaDisponibile && activity != null) {
                OutlinedButton(
                    onClick = { mostraPromptBiometrico(activity, onSuccesso = viewModel::sbloccaConBiometria) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.auth_azione_biometria))
                }
            }
        }
    }
}

private fun mostraPromptBiometrico(activity: FragmentActivity, onSuccesso: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccesso()
            }
        }
    )
    val infoPrompt = BiometricPrompt.PromptInfo.Builder()
        .setTitle(activity.getString(R.string.auth_biometria_titolo))
        .setSubtitle(activity.getString(R.string.auth_biometria_sottotitolo))
        .setNegativeButtonText(activity.getString(R.string.auth_biometria_annulla))
        .build()
    prompt.authenticate(infoPrompt)
}
