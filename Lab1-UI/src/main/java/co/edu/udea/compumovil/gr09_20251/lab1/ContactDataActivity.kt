package co.edu.udea.compumovil.gr09_20251.lab1

// Importaciones necesarias
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.KeyboardCapitalization

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class ContactDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactDataScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDataScreen() {
    // Variables con rememberSaveable (para conservar datos si rota la pantalla)
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }

    // Variables de error para validaciones
    var phoneError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var countryError by rememberSaveable { mutableStateOf(false) }
    var cityError by rememberSaveable { mutableStateOf(false) }

    val countries = listOf(*stringArrayResource(id = R.array.latam_countries))
    val cities = listOf(*stringArrayResource(id = R.array.colombia_cities))

    var countryExpanded by rememberSaveable { mutableStateOf(false) }
    var cityExpanded by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.contact_data_title),
            style = MaterialTheme.typography.headlineMedium
        )

        // Teléfono
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                phoneError = false
            },
            label = { RequiredFieldLabel(stringResource(R.string.phone)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            isError = phoneError,
            singleLine = true
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { RequiredFieldLabel(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = emailError,
            singleLine = true
        )

        // País (Dropdown)
        ExposedDropdownMenuBox(
            expanded = countryExpanded,
            onExpandedChange = { countryExpanded = !countryExpanded }
        ) {
            OutlinedTextField(
                value = country,
                onValueChange = {},
                readOnly = true,
                label = { RequiredFieldLabel(stringResource(R.string.country)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = countryExpanded,
                onDismissRequest = { countryExpanded = false }
            ) {
                countries.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            country = item
                            countryExpanded = false
                        }
                    )
                }
            }
        }

        // Dirección
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.address)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        // Ciudad (Dropdown)
        ExposedDropdownMenuBox(
            expanded = cityExpanded,
            onExpandedChange = { cityExpanded = !cityExpanded }
        ) {
            OutlinedTextField(
                value = city,
                onValueChange = {},
                readOnly = true,
                label = { RequiredFieldLabel(stringResource(R.string.city)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = cityExpanded,
                onDismissRequest = { cityExpanded = false }
            ) {
                cities.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            city = item
                            cityExpanded = false
                        }
                    )
                }
            }
        }

        // Botón de Siguiente
        Button(
            onClick = {
                phoneError = phone.isBlank()
                emailError = email.isBlank()
                countryError = country.isBlank()
                cityError = city.isBlank()

                if (!phoneError && !emailError && !countryError && !cityError) {
                    // Mostrar datos en Logcat
                    Log.d("ContactData", "------------------------------------")
                    Log.d("ContactData", "Teléfono: $phone")
                    Log.d("ContactData", "Email: $email")
                    Log.d("ContactData", "País: $country")
                    Log.d("ContactData", "Dirección: $address")
                    Log.d("ContactData", "Ciudad: $city")
                    Log.d("ContactData", "------------------------------------")
                } else {
                    Log.w("ContactData", "⚠️ Faltan datos requeridos")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.next))
        }
    }
}
