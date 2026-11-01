package com.example.prestaaula.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.prestaaula.model.Rol

// ===== ColorScheme claro y oscuro (clase "Theme.kt: ColorScheme y MaterialTheme completo") =====
private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = IndigoOnPrimary,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoOnPrimaryContainer,
    inversePrimary = IndigoInversePrimary,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = BackgroundLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceTint = IndigoPrimary,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimColor,
    surfaceBright = BackgroundLight,
    surfaceDim = SurfaceDimLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoOnPrimaryDark,
    primaryContainer = IndigoPrimaryContainerDark,
    onPrimaryContainer = IndigoOnPrimaryContainerDark,
    inversePrimary = IndigoInversePrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = BackgroundDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceTint = IndigoPrimaryDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimColor,
    surfaceBright = SurfaceBrightDark,
    surfaceDim = BackgroundDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark
)

// ===== Colores extra del tema (estados Disponible / Prestado y campo válido) =====
@Immutable
data class ColoresExtra(
    val disponible: Color,
    val onDisponible: Color,
    val prestado: Color,
    val onPrestado: Color,
    val exito: Color
)

private val ColoresExtraClaros = ColoresExtra(
    disponible = DisponibleContainerLight,
    onDisponible = OnDisponibleContainerLight,
    prestado = PrestadoContainerLight,
    onPrestado = OnPrestadoContainerLight,
    exito = ExitoLight
)

private val ColoresExtraOscuros = ColoresExtra(
    disponible = DisponibleContainerDark,
    onDisponible = OnDisponibleContainerDark,
    prestado = PrestadoContainerDark,
    onPrestado = OnPrestadoContainerDark,
    exito = ExitoDark
)

private val LocalColoresExtra = staticCompositionLocalOf { ColoresExtraClaros }

/** Se usa igual que MaterialTheme.colorScheme: MaterialTheme.coloresExtra.disponible */
val MaterialTheme.coloresExtra: ColoresExtra
    @Composable
    @ReadOnlyComposable
    get() = LocalColoresExtra.current

// ===== Acento por rol (corrección #11) =====
private data class Acento(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color
)

private fun acentoDe(rol: Rol?, oscuro: Boolean): Acento = when (rol) {
    Rol.ENCARGADO -> if (oscuro)
        Acento(EncargadoPrimaryDark, EncargadoOnPrimaryDark, EncargadoPrimaryContainerDark, EncargadoOnPrimaryContainerDark)
    else
        Acento(EncargadoPrimary, EncargadoOnPrimary, EncargadoPrimaryContainer, EncargadoOnPrimaryContainer)

    Rol.UTP -> if (oscuro)
        Acento(UtpPrimaryDark, UtpOnPrimaryDark, UtpPrimaryContainerDark, UtpOnPrimaryContainerDark)
    else
        Acento(UtpPrimary, UtpOnPrimary, UtpPrimaryContainer, UtpOnPrimaryContainer)

    // Docente y pantallas sin rol (login): índigo base
    else -> if (oscuro)
        Acento(IndigoPrimaryDark, IndigoOnPrimaryDark, IndigoPrimaryContainerDark, IndigoOnPrimaryContainerDark)
    else
        Acento(IndigoPrimary, IndigoOnPrimary, IndigoPrimaryContainer, IndigoOnPrimaryContainer)
}

/**
 * Tema de PrestaAula.
 * @param darkTheme true = modo oscuro (lo decide el selector Claro / Oscuro / Sistema).
 * @param rol rol activo; cambia el color de acento (barra superior y botones).
 *
 * No se usa Dynamic Color (Material You) a propósito: reemplazaría la paleta
 * índigo y los colores por rol que se definieron en la maqueta.
 */
@Composable
fun PrestaAulaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    rol: Rol? = null,
    content: @Composable () -> Unit
) {
    val base = if (darkTheme) DarkColorScheme else LightColorScheme
    val acento = acentoDe(rol, darkTheme)

    // Animación: al cambiar de rol o de tema, los colores de acento cambian suavemente
    val duracion = tween<Color>(durationMillis = 400)
    val primary by animateColorAsState(acento.primary, duracion, label = "primary")
    val onPrimary by animateColorAsState(acento.onPrimary, duracion, label = "onPrimary")
    val primaryContainer by animateColorAsState(acento.primaryContainer, duracion, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(acento.onPrimaryContainer, duracion, label = "onPrimaryContainer")

    val colorScheme = base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        surfaceTint = primary
    )
    val extra = if (darkTheme) ColoresExtraOscuros else ColoresExtraClaros

    CompositionLocalProvider(LocalColoresExtra provides extra) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,   // definida en Type.kt (archivo de la plantilla)
            shapes = PrestaAulaShapes,
            content = content
        )
    }
}
