@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Buscador con lupa (corrección #3). Busca sin importar tildes ni mayúsculas (lo resuelve el ViewModel). */
@Composable
fun BarraBusqueda(
    texto: String,
    onCambio: (String) -> Unit,
    sugerencia: String,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    TextField(
        value = texto,
        onValueChange = onCambio,
        placeholder = { Text(sugerencia) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
        trailingIcon = if (texto.isNotEmpty()) {
            {
                IconButton(onClick = { onCambio("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                }
            }
        } else null,
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = cs.surfaceContainerHigh,
            unfocusedContainerColor = cs.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

/** Desplegable "Tipo" del inventario (corrección #1) y la cantidad de equipos encontrados. */
@Composable
fun FiltroTipo(
    tipo: String,
    opciones: List<String>,
    onElegir: (String) -> Unit,
    cantidad: Int,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tipo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Box {
            OutlinedButton(
                onClick = { abierto = true },
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(start = 12.dp, end = 4.dp)
            ) {
                Text(tipo)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Elegir tipo")
            }
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        leadingIcon = if (opcion == tipo) {
                            { Icon(Icons.Filled.Check, contentDescription = null) }
                        } else null,
                        onClick = {
                            abierto = false
                            onElegir(opcion)
                        }
                    )
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            "$cantidad ${if (cantidad == 1) "equipo" else "equipos"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
