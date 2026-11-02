@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prestaaula.ui.theme.coloresExtra

/** Ícono al final del campo: ✓ verde si es válido, ! rojo si tiene error (rúbrica IE 2.1.2). */
@Composable
fun IconoValidacion(conError: Boolean) {
    Crossfade(targetState = conError, label = "iconoValidacion") { error ->
        if (error) {
            Icon(Icons.Filled.Error, contentDescription = "Campo con error", tint = MaterialTheme.colorScheme.error)
        } else {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Campo válido", tint = MaterialTheme.coloresExtra.exito)
        }
    }
}

/** Texto bajo el campo: el error (en rojo) o la ayuda. Cambia con una transición suave. */
@Composable
fun TextoApoyo(texto: String, esError: Boolean) {
    Crossfade(targetState = texto to esError, label = "textoApoyo") { (t, error) ->
        Text(
            t,
            color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Campo de texto con validación visual por campo.
 * - El error solo se muestra cuando el campo ya fue tocado.
 * - onSalir: se llama al salir del campo (para marcarlo como tocado, igual que en la maqueta).
 * - validar = false para campos opcionales (observaciones).
 */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    tocado: Boolean = false,
    icono: ImageVector? = null,
    ayuda: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    onSalir: (() -> Unit)? = null,
    lineas: Int = 1,
    validar: Boolean = true
) {
    val mostrarError = validar && tocado && error != null
    val textoApoyo = if (mostrarError) error else ayuda
    var tuvoFoco by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = if (icono != null) {
            { Icon(icono, contentDescription = null) }
        } else null,
        trailingIcon = if (validar && tocado) {
            { IconoValidacion(conError = mostrarError) }
        } else null,
        isError = mostrarError,
        supportingText = if (textoApoyo != null) {
            { TextoApoyo(textoApoyo, mostrarError) }
        } else null,
        singleLine = lineas == 1,
        minLines = lineas,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { estado ->
                if (estado.isFocused) {
                    tuvoFoco = true
                } else if (tuvoFoco) {
                    tuvoFoco = false
                    onSalir?.invoke()
                }
            }
    )
}

/**
 * Campo desplegable (curso y tipo de equipo), con el mismo estilo y validación que CampoTexto.
 * opciones: lista de (valor que se guarda, texto que se muestra).
 * valorSeleccionado: la opción elegida se marca en el menú.
 * grupoDe: si se entrega, las opciones se agrupan bajo un encabezado (ej. "1° Básico" con A, B y C),
 *          igual que en la maqueta.
 */
@Composable
fun CampoDesplegable(
    valorMostrado: String,
    opciones: List<Pair<String, String>>,
    onElegir: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    tocado: Boolean = false,
    icono: ImageVector? = null,
    valorSeleccionado: String? = null,
    grupoDe: ((String) -> String)? = null
) {
    var abierto by remember { mutableStateOf(false) }
    val mostrarError = tocado && error != null
    // Ancho del campo: el menú se abre con el mismo ancho, en cualquier tamaño de pantalla
    var anchoCampo by remember { mutableStateOf(0) }
    val densidad = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { anchoCampo = it.width }
    ) {
        OutlinedTextField(
            value = valorMostrado,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            leadingIcon = if (icono != null) {
                { Icon(icono, contentDescription = null) }
            } else null,
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (tocado) IconoValidacion(conError = mostrarError)
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Abrir opciones")
                }
            },
            isError = mostrarError,
            supportingText = if (mostrarError && error != null) {
                { TextoApoyo(error, true) }
            } else null,
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente encima del campo: al tocarla se abre el menú
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { abierto = true }
        )
        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false },
            modifier = Modifier
                .width(with(densidad) { anchoCampo.toDp() })
                .heightIn(max = 320.dp)
        ) {
            var grupoAnterior: String? = null
            opciones.forEach { (valor, texto) ->
                // Encabezado del grupo (ej. "1° Básico"), solo cuando cambia de grupo
                val grupo = grupoDe?.invoke(valor)
                if (grupo != null && grupo != grupoAnterior) {
                    Text(
                        grupo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 2.dp)
                    )
                }
                grupoAnterior = grupo
                val elegido = valor == valorSeleccionado
                DropdownMenuItem(
                    text = { Text(texto) },
                    trailingIcon = if (elegido) {
                        { Icon(Icons.Filled.Check, contentDescription = "Elegido") }
                    } else null,
                    onClick = {
                        abierto = false
                        onElegir(valor)
                    },
                    // Con grupos, las opciones van con sangría bajo su encabezado
                    contentPadding = PaddingValues(start = if (grupoDe != null) 32.dp else 16.dp, end = 16.dp),
                    modifier = if (elegido) Modifier.background(MaterialTheme.colorScheme.primaryContainer) else Modifier
                )
            }
        }
    }
}
