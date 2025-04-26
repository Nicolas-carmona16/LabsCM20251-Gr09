package co.edu.udea.compumovil.gr09_20251.lab1

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
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

// Clase que representa una ciudad obtenida de la API
@Serializable
data class City(
    val id: Int,
    val name: String
)

// Actividad principal para la captura de datos de contacto
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
    // Estados para almacenar los datos ingresados
    var telefono by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pais by rememberSaveable { mutableStateOf("") }
    var ciudad by rememberSaveable { mutableStateOf("") }

    // Estados para manejar la expansión de los menús desplegables
    var paisExpanded by rememberSaveable { mutableStateOf(false) }
    var ciudadExpanded by rememberSaveable { mutableStateOf(false) }

    // Lista de ciudades cargadas desde la API y estado de carga
    var ciudades by rememberSaveable { mutableStateOf<List<City>>(emptyList()) }
    var isLoadingCities by rememberSaveable { mutableStateOf(false) }

    // Estados para validación de campos
    var telefonoError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var paisError by rememberSaveable { mutableStateOf(false) }
    var ciudadError by rememberSaveable { mutableStateOf(false) }

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current
    val paises = listOf(*context.resources.getStringArray(R.array.latin_america_countries))
    val coroutineScope = rememberCoroutineScope()

    // Función para cargar las ciudades desde una API si el país es Colombia
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

    // Cada vez que se cambie el país, se recargan ciudades si aplica
    LaunchedEffect(pais) {
        if (pais == "Colombia") {
            loadCities()
        } else {
            ciudad = ""
            ciudades = emptyList()
        }
    }

    // Función para validar formato de email
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return email.matches(emailRegex)
    }

    // Función para validar todos los campos
    fun validateFields(): Boolean {
        telefonoError = telefono.isBlank()
        emailError = email.isBlank() || !isValidEmail(email)
        paisError = pais.isBlank()
        ciudadError = pais == "Colombia" && ciudad.isBlank()
        return !telefonoError && !emailError && !paisError && !ciudadError
    }

    // Función para registrar en Logcat los datos ingresados
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

    // Función que maneja la acción del botón "Siguiente"
    fun handleNextButton() {
        if (validateFields()) {
            logContactData(
                context = context,
                telefono = telefono,
                direccion = direccion,
                email = email,
                pais = pais
            )
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            context.startActivity(intent)
        } else {
            Log.w("ContactData", "Validación fallida - Complete los campos obligatorios")
        }
    }

    // Verificamos si el dispositivo está en modo horizontal (landscape)
    if (isLandscape) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Título de la pantalla
            Text(
                text = stringResource(R.string.contact_data_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            // Primera fila: Teléfono y Dirección
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Campo Teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it
                        telefonoError = false
                    },
                    label = { RequiredFieldLabel(stringResource(R.string.phone)) },
                    modifier = Modifier.weight(1f),
                    isError = telefonoError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone"
                        )
                    },
                    supportingText = {
                        if (telefonoError) ErrorText(stringResource(R.string.required_field))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )

                // Campo Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text(stringResource(R.string.address)) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true
                )
            }

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
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

            // Segunda fila: País y Ciudad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Selector de País
                CountryDropdown(
                    selectedCountry = pais,
                    countries = paises,
                    expanded = paisExpanded,
                    onExpandedChange = { paisExpanded = it },
                    onCountrySelected = { pais = it },
                    isError = paisError,
                    modifier = Modifier.weight(1f)
                )

                // Si el país es Colombia, muestra selector de Ciudad
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

            // Botón Siguiente
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

        // Layout para modo vertical (portrait)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Título de la pantalla
            Text(
                text = stringResource(R.string.contact_data_title),
                style = MaterialTheme.typography.headlineMedium
            )

            // Campo Teléfono
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    telefonoError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.phone)) },
                modifier = Modifier.fillMaxWidth(),
                isError = telefonoError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone"
                    )
                },
                supportingText = {
                    if (telefonoError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            // Campo Dirección
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text(stringResource(R.string.address)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = false,
                maxLines = 3
            )

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
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

            // Selector de País
            CountryDropdown(
                selectedCountry = pais,
                countries = paises,
                expanded = paisExpanded,
                onExpandedChange = { paisExpanded = it },
                onCountrySelected = { pais = it },
                isError = paisError
            )

            // Selector de Ciudad si aplica
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

            // Botón Siguiente
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

// Composable para mostrar el Dropdown de selección de país
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(
    selectedCountry: String,                  // País actualmente seleccionado
    countries: List<String>,                  // Lista de países disponibles
    expanded: Boolean,                        // Estado de expansión del menú desplegable
    onExpandedChange: (Boolean) -> Unit,       // Función para cambiar el estado de expansión
    onCountrySelected: (String) -> Unit,       // Función que se ejecuta al seleccionar un país
    isError: Boolean,                          // Estado de error para validación
    modifier: Modifier = Modifier              // Modificador para personalizar el componente
) {
    var query by rememberSaveable { mutableStateOf("") } // Texto que escribe el usuario
    val filteredCountries = remember(query, countries) { // Lista de países filtrados
        if (query.isBlank()) {
            countries
        } else {
            countries.filter { it.contains(query, ignoreCase = true) }
        }
    }

    Column(modifier = modifier) {
        // Caja que combina el campo de texto con el menú desplegable
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(it) }
        ) {
            // Campo de texto para mostrar país seleccionado o búsqueda
            OutlinedTextField(
                value = if (expanded) query else selectedCountry,
                onValueChange = {
                    query = it
                    if (it.isNotBlank()) {
                        onExpandedChange(true) // Abre el menú cuando escribe
                    }
                },
                label = { RequiredFieldLabel(stringResource(R.string.country)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Country"
                    )
                },
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

            // Menú desplegable de países
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onExpandedChange(false)
                    query = ""
                }
            ) {
                if (filteredCountries.isEmpty()) {
                    // Mensaje si no se encuentran países
                    DropdownMenuItem(
                        text = { Text("No se encontraron países") },
                        onClick = {}
                    )
                } else {
                    // Lista de países filtrados
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

// Composable que muestra un menú desplegable para seleccionar una ciudad
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    selectedCity: String,                  // Ciudad actualmente seleccionada
    cities: List<String>,                  // Lista completa de ciudades disponibles
    expanded: Boolean,                     // Estado de expansión del menú desplegable
    onExpandedChange: (Boolean) -> Unit,    // Función para cambiar la expansión del menú
    onCitySelected: (String) -> Unit,       // Función que se ejecuta al seleccionar una ciudad
    isError: Boolean,                       // Bandera que indica si hay error de validación
    isLoading: Boolean,                     // Bandera que indica si las ciudades están cargando
    modifier: Modifier = Modifier           // Modificador opcional para personalizar el estilo
) {

    // Variable para almacenar el texto de búsqueda ingresado por el usuari
    var query by rememberSaveable { mutableStateOf("") }

    // Lista de ciudades filtradas según el texto de búsqueda
    val filteredCities = remember(query, cities) {
        if (query.isBlank()) {
            cities
        } else {
            cities.filter { it.contains(query, ignoreCase = true) }
        }
    }

    Column(modifier = modifier) {

        // Caja que integra el TextField y el menú desplegable
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onExpandedChange(it) }
        ) {

            // Campo de texto para mostrar la ciudad seleccionada o búsqueda
            OutlinedTextField(
                value = if (expanded) query else selectedCity,
                onValueChange = {
                    query = it
                    if (it.isNotBlank()) {
                        onExpandedChange(true) // Expande automáticamente cuando el usuario escribe
                    }
                },
                label = { RequiredFieldLabel(stringResource(R.string.city)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "City"  // Descripción accesible del icono
                    )
                },
                trailingIcon = {
                    if (isLoading) {
                        // Indicador de progreso si las ciudades están cargando
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth(),
                isError = isError, // Muestra el borde de error si hay un problema de validación
                supportingText = {
                    if (isError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                enabled = !isLoading // Desactiva la entrada si las ciudades aún están cargando
            )

            // Menú desplegable que muestra las ciudades disponibles
            ExposedDropdownMenu(
                expanded = expanded && !isLoading,
                onDismissRequest = {
                    onExpandedChange(false)
                    query = ""  // Resetea el query cuando el menú se cierra
                }
            ) {
                if (filteredCities.isEmpty()) {

                    // Si no hay resultados después de buscar
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

                    // Lista las ciudades filtradas para que el usuario pueda seleccionarlas
                    filteredCities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                onCitySelected(city) // Asigna la ciudad seleccionada
                                onExpandedChange(false) // Cierra el menú
                                query = "" // Limpia el campo de búsqueda
                            }
                        )
                    }
                }
            }
        }
    }
}
