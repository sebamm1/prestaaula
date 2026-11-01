@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.prestaaula.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prestaaula.model.Rol
import com.example.prestaaula.ui.components.BotonPrincipal
import com.example.prestaaula.ui.components.CampoTexto
import com.example.prestaaula.ui.components.PrestaTopBar
import com.example.prestaaula.validation.Validador
import com.example.prestaaula.viewmodel.SesionViewModel

/** Login: nombre, correo y RUT con validación por campo. El rol lo trae el usuario. */
@Composable
fun LoginScreen(
    sesionViewModel: SesionViewModel,
    onTema: () -> Unit,
    onIngresado: (List<Rol>) -> Unit,
    onRestablecer: () -> Unit
) {
    val estado by sesionViewModel.login.collectAsState()

    Scaffold(
        topBar = { PrestaTopBar(titulo = "", onTema = onTema, plana = true) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo "PA"
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "PA",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            Text("PrestaAula", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Colegio Marcela Paz · CRA\nIngresa con tus datos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            CampoTexto(
                valor = estado.nombre,
                onCambio = sesionViewModel::cambiarNombre,
                etiqueta = "Nombre completo",
                icono = Icons.Filled.Person,
                ayuda = "Ej: María Soto",
                error = estado.errores[Validador.NOMBRE],
                tocado = Validador.NOMBRE in estado.tocados,
                onSalir = { sesionViewModel.marcarTocado(Validador.NOMBRE) }
            )
            CampoTexto(
                valor = estado.correo,
                onCambio = sesionViewModel::cambiarCorreo,
                etiqueta = "Correo",
                icono = Icons.Filled.Email,
                ayuda = "Ej: docente@colegio.cl",
                tipoTeclado = KeyboardType.Email,
                error = estado.errores[Validador.CORREO],
                tocado = Validador.CORREO in estado.tocados,
                onSalir = { sesionViewModel.marcarTocado(Validador.CORREO) }
            )
            CampoTexto(
                valor = estado.rut,
                onCambio = sesionViewModel::cambiarRut,
                etiqueta = "RUT",
                icono = Icons.Filled.Badge,
                ayuda = "Ej: 12.345.678-5",
                error = estado.errores[Validador.RUT],
                tocado = Validador.RUT in estado.tocados,
                onSalir = { sesionViewModel.marcarTocado(Validador.RUT) }
            )

            // Error general (datos que no coinciden con un usuario)
            AnimatedVisibility(
                visible = estado.errorGeneral != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Error, contentDescription = null)
                        Text(estado.errorGeneral ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            BotonPrincipal(
                texto = if (estado.cargando) "Ingresando..." else "Ingresar",
                onClick = { sesionViewModel.ingresar(onIngresado) },
                sacudir = estado.sacudir,
                habilitado = !estado.cargando
            )
            TextButton(onClick = onRestablecer, modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Restablecer datos de prueba")
            }
        }
    }
}
