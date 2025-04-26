package co.edu.udea.compumovil.gr09_20251.lab1

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class City(
    val id: Int,
    val name: String
)

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
    var telefono by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pais by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }
    var paisExpanded by rememberSaveable { mutableStateOf(false) }
    var ciudadExpanded by rememberSaveable { mutableStateOf(false) }
    var ciudades by rememberSaveable { mutableStateOf<List<City>>(emptyList()) }
    var isLoadingCities by rememberSaveable { mutableStateOf(false) }

    var telefonoError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var paisError by rememberSaveable { mutableStateOf(false) }
    var ciudadError by rememberSaveable { mutableStateOf(false) }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val paises = listOf(*context.resources.getStringArray(R.array.latin_america_countries))
    val coroutineScope = rememberCoroutineScope()

    fun loadCities() {
        if (pais != "Colombia") {
            ciudades = emptyList()
            return
        }

        coroutineScope.launch {
            isLoadingCities = true
            try {
                val response = withContext(Dispatchers.IO) {
                    HttpClient {
                        install(ContentNegotiation) {
                            json(Json {
                                ignoreUnknownKeys = true
                                isLenient = true
                            })
                        }
                    }.use { client ->
                        client.get("https://api-colombia.com/api/v1/City")
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    ciudades = response.body<List<City>>()
                    Log.d("API Success", "Cities loaded: ${ciudades.size}")
                } else {
                    Log.e("API Error", "Failed to fetch cities: ${response.status}")
                    ciudades = emptyList()
                }
            } catch (e: Exception) {
                Log.e("API Error", "Error fetching cities: ${e.message}")
                ciudades = emptyList()
            } finally {
                isLoadingCities = false
            }
        }
    }

    LaunchedEffect(pais) {
        if (pais == "Colombia") {
            loadCities()
        } else {
            ciudad = ""
            ciudades = emptyList()
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return email.matches(emailRegex)
    }

    fun validateFields(): Boolean {
        telefonoError = telefono.isBlank()
        emailError = email.isBlank() || !isValidEmail(email)
        paisError = pais.isBlank()
        ciudadError = pais == "Colombia" && ciudad.isBlank()
        return !telefonoError && !emailError && !paisError && !ciudadError
    }

    fun logContactData(context: Context, telefono: String, direccion: String, email: String, pais: String) {
        Log.d("ContactData", "***********************************")
        Log.d("ContactData", context.getString(R.string.log_contact_data_title))
        Log.d("ContactData", context.getString(R.string.log_phone_format, telefono))
        if (direccion.isNotBlank()) {
            Log.d("ContactData", context.getString(R.string.log_address_format, direccion))
        }
        Log.d("ContactData", context.getString(R.string.log_email_format, email))
        Log.d("ContactData", context.getString(R.string.log_country_format, pais))
        if (pais == "Colombia") {
            Log.d("ContactData", context.getString(R.string.log_city_format, ciudad))
        }
        Log.d("ContactData", "***********************************")
    }

    fun handleNextButton() {
        if (validateFields()) {
            logContactData(
                context = context,
                telefono = telefono,
                direccion = direccion,
                email = email,
                pais = pais
            )
            // Navegar a la siguiente actividad cuando la implementemos
            // context.startActivity(Intent(context, NextActivity::class.java))
        } else {
            Log.w("ContactData", "Validación fallida - Complete los campos obligatorios")
        }
    }

    if (isLandscape) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.contact_data_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it
                        telefonoError = false
                    },
                    label = { RequiredFieldLabel(stringResource(R.string.phone)) },
                    modifier = Modifier.weight(1f),
                    isError = telefonoError,
                    supportingText = {
                        if (telefonoError) ErrorText(stringResource(R.string.required_field))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text(stringResource(R.string.address)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                supportingText = {
                    if (emailError) {
                        if (email.isBlank()) {
                            ErrorText(stringResource(R.string.required_field))
                        } else {
                            ErrorText(stringResource(R.string.invalid_email))
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CountryDropdown(
                    selectedCountry = pais,
                    countries = paises,
                    expanded = paisExpanded,
                    onExpandedChange = { paisExpanded = it },
                    onCountrySelected = { pais = it },
                    isError = paisError,
                    modifier = Modifier.weight(1f)
                )

                if (pais == "Colombia") {
                    CityDropdown(
                        selectedCity = ciudad,
                        cities = ciudades.map { it.name },
                        expanded = ciudadExpanded,
                        onExpandedChange = { ciudadExpanded = it },
                        onCitySelected = { ciudad = it },
                        isError = ciudadError,
                        isLoading = isLoadingCities,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { handleNextButton() },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(text = stringResource(R.string.next))
                }
            }
        }
    } else {
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

            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    telefonoError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.phone)) },
                modifier = Modifier.fillMaxWidth(),
                isError = telefonoError,
                supportingText = {
                    if (telefonoError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text(stringResource(R.string.address)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = false,
                maxLines = 3
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                supportingText = {
                    if (emailError) {
                        if (email.isBlank()) {
                            ErrorText(stringResource(R.string.required_field))
                        } else {
                            ErrorText(stringResource(R.string.invalid_email))
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            CountryDropdown(
                selectedCountry = pais,
                countries = paises,
                expanded = paisExpanded,
                onExpandedChange = { paisExpanded = it },
                onCountrySelected = { pais = it },
                isError = paisError
            )

            if (pais == "Colombia") {
                CityDropdown(
                    selectedCity = ciudad,
                    cities = ciudades.map { it.name },
                    expanded = ciudadExpanded,
                    onExpandedChange = { ciudadExpanded = it },
                    onCitySelected = { ciudad = it },
                    isError = ciudadError,
                    isLoading = isLoadingCities
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { handleNextButton() },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(text = stringResource(R.string.next))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(
    selectedCountry: String,
    countries: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCountrySelected: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredCountries = remember(query, countries) {
        if (query.isBlank()) {
            countries
        } else {
            countries.filter { it.contains(query, ignoreCase = true) }
        }
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(it) }
        ) {
            OutlinedTextField(
                value = if (expanded) query else selectedCountry,
                onValueChange = {
                    query = it
                    if (it.isNotBlank()) {
                        onExpandedChange(true)
                    }
                },
                label = { RequiredFieldLabel(stringResource(R.string.country)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth(),
                isError = isError,
                supportingText = {
                    if (isError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onExpandedChange(false)
                    query = ""
                }
            ) {
                if (filteredCountries.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No se encontraron países") },
                        onClick = {}
                    )
                } else {
                    filteredCountries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                onCountrySelected(country)
                                onExpandedChange(false)
                                query = ""
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    selectedCity: String,
    cities: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCitySelected: (String) -> Unit,
    isError: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredCities = remember(query, cities) {
        if (query.isBlank()) {
            cities
        } else {
            cities.filter { it.contains(query, ignoreCase = true) }
        }
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(it) }
        ) {
            OutlinedTextField(
                value = if (expanded) query else selectedCity,
                onValueChange = {
                    query = it
                    if (it.isNotBlank()) {
                        onExpandedChange(true)
                    }
                },
                label = { RequiredFieldLabel(stringResource(R.string.city)) },
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth(),
                isError = isError,
                supportingText = {
                    if (isError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                enabled = !isLoading
            )

            ExposedDropdownMenu(
                expanded = expanded && !isLoading,
                onDismissRequest = {
                    onExpandedChange(false)
                    query = ""
                }
            ) {
                if (filteredCities.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            if (isLoading) {
                                Text("Cargando ciudades...")
                            } else {
                                Text("No se encontraron ciudades")
                            }
                        },
                        onClick = {}
                    )
                } else {
                    filteredCities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onCitySelected(city)
                                onExpandedChange(false)
                                query = ""
                            }
                        )
                    }
                }
            }
        }
    }
}
