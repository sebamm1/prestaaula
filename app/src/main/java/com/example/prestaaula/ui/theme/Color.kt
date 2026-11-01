package com.example.prestaaula.ui.theme

import androidx.compose.ui.graphics.Color

// =====================================================================
// Paleta de PrestaAula (Material 3, índigo).
// Igual que en la clase "Color Scheme de Material 3": cada color tiene
// un ROL (primary, secondary, error, surface...) y su versión "On"
// (el color del texto o ícono que va encima).
// Las pantallas NUNCA usan estos colores directo: usan MaterialTheme.colorScheme.
// =====================================================================

// ---------- Modo claro ----------
val IndigoPrimary = Color(0xFF4F5BD5)
val IndigoOnPrimary = Color(0xFFFFFFFF)
val IndigoPrimaryContainer = Color(0xFFE0E0FF)
val IndigoOnPrimaryContainer = Color(0xFF00105C)
val IndigoInversePrimary = Color(0xFFBEC2FF)

val SecondaryLight = Color(0xFF5C5D72)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE1E0F9)
val OnSecondaryContainerLight = Color(0xFF191A2C)

val TertiaryLight = Color(0xFF78536B)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFD8EE)
val OnTertiaryContainerLight = Color(0xFF2E1126)

val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

val BackgroundLight = Color(0xFFFBF8FF)
val OnBackgroundLight = Color(0xFF1B1B21)
val SurfaceVariantLight = Color(0xFFE3E1EC)
val OnSurfaceVariantLight = Color(0xFF46464F)
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF5F2FA)
val SurfaceContainerLight = Color(0xFFEFEDF4)
val SurfaceContainerHighLight = Color(0xFFE9E7EF)
val SurfaceContainerHighestLight = Color(0xFFE4E1E9)
val SurfaceDimLight = Color(0xFFDBD9E0)
val OutlineLight = Color(0xFF777680)
val OutlineVariantLight = Color(0xFFC7C5D0)
val InverseSurfaceLight = Color(0xFF303036)
val InverseOnSurfaceLight = Color(0xFFF3EFF7)
val ScrimColor = Color(0xFF000000)

// ---------- Modo oscuro ----------
val IndigoPrimaryDark = Color(0xFFBDC2FF)
val IndigoOnPrimaryDark = Color(0xFF1B2779)
val IndigoPrimaryContainerDark = Color(0xFF36429F)
val IndigoOnPrimaryContainerDark = Color(0xFFE0E0FF)
val IndigoInversePrimaryDark = Color(0xFF4F5BD5)

val SecondaryDark = Color(0xFFC5C4DD)
val OnSecondaryDark = Color(0xFF2E2F42)
val SecondaryContainerDark = Color(0xFF444559)
val OnSecondaryContainerDark = Color(0xFFE1E0F9)

val TertiaryDark = Color(0xFFE8B9D5)
val OnTertiaryDark = Color(0xFF46263C)
val TertiaryContainerDark = Color(0xFF5E3C53)
val OnTertiaryContainerDark = Color(0xFFFFD8EE)

val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

val BackgroundDark = Color(0xFF131318)
val OnBackgroundDark = Color(0xFFE4E1E9)
val SurfaceVariantDark = Color(0xFF46464F)
val OnSurfaceVariantDark = Color(0xFFC7C5D0)
val SurfaceContainerLowestDark = Color(0xFF0E0E13)
val SurfaceContainerLowDark = Color(0xFF1B1B21)
val SurfaceContainerDark = Color(0xFF1F1F25)
val SurfaceContainerHighDark = Color(0xFF2A292F)
val SurfaceContainerHighestDark = Color(0xFF35343A)
val SurfaceBrightDark = Color(0xFF39383F)
val OutlineDark = Color(0xFF91909A)
val OutlineVariantDark = Color(0xFF46464F)
val InverseSurfaceDark = Color(0xFFE4E1E9)
val InverseOnSurfaceDark = Color(0xFF303036)

// =====================================================================
// Acento por rol (corrección #11): cambia primary y primaryContainer.
// Docente usa el índigo base (arriba).
// =====================================================================
// Encargado(a) CRA: violeta
val EncargadoPrimary = Color(0xFF7E4AAE)
val EncargadoOnPrimary = Color(0xFFFFFFFF)
val EncargadoPrimaryContainer = Color(0xFFF2DAFF)
val EncargadoOnPrimaryContainer = Color(0xFF2E004E)
val EncargadoPrimaryDark = Color(0xFFE0B6FF)
val EncargadoOnPrimaryDark = Color(0xFF4A1670)
val EncargadoPrimaryContainerDark = Color(0xFF64328F)
val EncargadoOnPrimaryContainerDark = Color(0xFFF2DAFF)

// UTP / Administración: malva
val UtpPrimary = Color(0xFF8E4467)
val UtpOnPrimary = Color(0xFFFFFFFF)
val UtpPrimaryContainer = Color(0xFFFFD8E6)
val UtpOnPrimaryContainer = Color(0xFF3B0723)
val UtpPrimaryDark = Color(0xFFFFB0CE)
val UtpOnPrimaryDark = Color(0xFF561D3B)
val UtpPrimaryContainerDark = Color(0xFF722D50)
val UtpOnPrimaryContainerDark = Color(0xFFFFD8E6)

// =====================================================================
// Colores de estado (Disponible / Prestado) y de "campo válido".
// Material 3 no trae roles para esto, así que se definen como colores
// extra del tema (ver ColoresExtra en Theme.kt).
// =====================================================================
val DisponibleContainerLight = Color(0xFFD3F5D9)
val OnDisponibleContainerLight = Color(0xFF0F5223)
val PrestadoContainerLight = Color(0xFFFFDDB3)
val OnPrestadoContainerLight = Color(0xFF5E3700)
val ExitoLight = Color(0xFF1E6B35)

val DisponibleContainerDark = Color(0xFF1F4D2B)
val OnDisponibleContainerDark = Color(0xFFB4F2C1)
val PrestadoContainerDark = Color(0xFF5A3B00)
val OnPrestadoContainerDark = Color(0xFFFFDDB0)
val ExitoDark = Color(0xFF8DD99E)
