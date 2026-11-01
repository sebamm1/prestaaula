package com.example.prestaaula.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Los 5 tamaños de esquinas de Material 3 (clase "Shapes y Surface Elevation").
val PrestaAulaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),  // campos de texto, snackbar
    small = RoundedCornerShape(8.dp),       // chips y etiquetas de estado
    medium = RoundedCornerShape(12.dp),     // tarjetas
    large = RoundedCornerShape(16.dp),      // calendario, tarjetas grandes
    extraLarge = RoundedCornerShape(28.dp)  // diálogos
)
