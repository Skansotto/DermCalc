package com.casati.dermcalc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.casati.dermcalc.R

val Jakarta = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

// I numeri sono sempre in monospaziato: le cifre non "ballano" mentre il punteggio cambia.
val PlexMono = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_mono_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlexMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 60.sp,
        lineHeight = 62.sp,
        letterSpacing = (-3).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlexMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 54.sp,
        lineHeight = 56.sp,
        letterSpacing = (-2.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = PlexMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 38.sp,
        lineHeight = 40.sp,
        letterSpacing = (-2).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 25.sp,
        lineHeight = 29.sp,
        letterSpacing = (-0.7).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 21.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.Bold,
        fontSize = 14.5f.sp,
        lineHeight = 19.sp,
        letterSpacing = (-0.2).sp
    ),
    titleSmall = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5f.sp,
        lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.Normal,
        fontSize = 14.5f.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Jakarta,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.5f.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlexMono,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 1.3.sp
    )
)

// Stili monospaziati per punteggi e metadati numerici, usati fuori dalla scala Material.
val MonoGrande = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.SemiBold,
    fontSize = 26.sp,
    lineHeight = 30.sp,
    letterSpacing = (-1).sp
)

val MonoMedio = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.SemiBold,
    fontSize = 17.sp,
    lineHeight = 22.sp
)

val MonoPiccolo = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

val MonoMinuto = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.Medium,
    fontSize = 10.5f.sp,
    lineHeight = 14.sp
)

// Etichetta di sezione: monospaziata, maiuscola e molto spaziata.
val EtichettaSezione = TextStyle(
    fontFamily = PlexMono,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    lineHeight = 15.sp,
    letterSpacing = 1.3.sp
)
