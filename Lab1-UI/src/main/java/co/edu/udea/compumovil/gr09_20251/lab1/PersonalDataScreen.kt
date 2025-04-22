package co.edu.udea.compumovil.gr09_20251.lab1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Composable
fun PersonalDataScreen() {
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("Masculino") }
    var fechaNacimiento by rememberSaveable { mutableStateOf("") }
    var escolaridad by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val opciones = listOf("Primaria", "Secundaria", "Universitaria", "Otro")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Información Personal", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos *") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Sexo:")
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = sexo == "Masculino",
                onClick = { sexo = "Masculino" }
            )
            Text("Hombre", modifier = Modifier.padding(start = 4.dp))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = sexo == "Femenino",
                onClick = { sexo = "Femenino" }
            )
            Text("Mujer", modifier = Modifier.padding(start = 4.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // Aquí va el DatePicker (todavía pendiente de agregar)
        }) {
            Text(if (fechaNacimiento.isEmpty()) "Fecha de Nacimiento *" else fechaNacimiento)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedTextField(
                value = escolaridad,
                onValueChange = {},
                label = { Text("Grado de escolaridad") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            escolaridad = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Acción para "Siguiente"
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Siguiente")
        }
    }
}
