package com.casati.dermcalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Il tema è volutamente fisso: la palette clinica non segue il dynamic color di sistema,
// perché i colori di severità devono restare identici su ogni dispositivo.
private val SchemaColori = lightColorScheme(
    primary = Indaco,
    onPrimary = Superficie,
    primaryContainer = IndacoTenue,
    onPrimaryContainer = Indaco,
    secondary = TestoMedio,
    onSecondary = Superficie,
    secondaryContainer = SuperficieCalda,
    onSecondaryContainer = TestoMedio,
    tertiary = Verde,
    onTertiary = Superficie,
    tertiaryContainer = VerdeTenue,
    onTertiaryContainer = Verde,
    error = Rosso,
    onError = Superficie,
    errorContainer = RossoTenue,
    onErrorContainer = Rosso,
    background = Sfondo,
    onBackground = Inchiostro,
    surface = Superficie,
    onSurface = Inchiostro,
    surfaceVariant = SuperficieCalda,
    onSurfaceVariant = TestoMedio,
    outline = BordoForte,
    outlineVariant = Bordo
)

@Composable
fun DermCalcTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SchemaColori,
        typography = Typography,
        content = content
    )
}
