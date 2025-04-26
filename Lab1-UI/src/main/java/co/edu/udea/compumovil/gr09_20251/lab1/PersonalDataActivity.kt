package co.edu.udea.compumovil.gr09_20251.lab1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DatePickerState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

// Clase de actividad que despliega la pantalla de datos personales
class PersonalDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalDataScreen() // Llama al Composable principal
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen() {

    // Estados para almacenar los datos personales
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("") }
    var escolaridad by rememberSaveable { mutableStateOf("") }

    // Estados para el control de errores de validación
    var nombresError by rememberSaveable { mutableStateOf(false) }
    var apellidosError by rememberSaveable { mutableStateOf(false) }
    var fechaError by rememberSaveable { mutableStateOf(false) }

    // Estado para controlar la expansión del menú de escolaridad
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Carga de opciones de escolaridad desde recursos
    val escolaridades = listOf(*stringArrayResource(R.array.education_levels))

    // Estado para mostrar el diálogo de selección de fecha
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // Estado del DatePicker (para seleccionar fecha de nacimiento)
    val datePickerState = rememberDatePickerState()

    // Estado para almacenar la fecha seleccionada
    var selectedDate by rememberSaveable(
        stateSaver = Saver<Date?, Bundle>(
            save = { date ->
                Bundle().apply {
                    putLong("date_key", date?.time ?: -1)
                }
            },
            restore = { bundle ->
                bundle.getLong("date_key").takeIf { it != -1L }?.let { Date(it) }
            }
        )
    ) { mutableStateOf<Date?>(null) }

    // Formateador de fecha a dd/MM/yyyy
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    // Detecta si el dispositivo está en orientación horizontal
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Obtiene el contexto de la aplicación
    val context = LocalContext.current

    // Función para validar que los campos obligatorios estén diligenciados
    fun validateFields(): Boolean {
        nombresError = nombres.isBlank()
        apellidosError = apellidos.isBlank()
        fechaError = selectedDate == null

        return !nombresError && !apellidosError && !fechaError
    }

    fun logUserData(context: Context, nombres: String, apellidos: String, sexo: String,
                    selectedDate: Date?, escolaridad: String, dateFormatter: SimpleDateFormat) {
        Log.d("UserData", "***********************************")
        Log.d("UserData", context.getString(R.string.log_personal_data_title))
        Log.d("UserData", "$nombres $apellidos")
        if (sexo.isNotBlank()) {
            Log.d("UserData", sexo)
        }
        val dateText = selectedDate?.let { dateFormatter.format(it) }
            ?: context.getString(R.string.log_no_date_selected)
        val birthDateMessage = context.getString(R.string.log_birth_date_format, dateText)
        Log.d("UserData", birthDateMessage)
        if (escolaridad.isNotBlank()) {
            Log.d("UserData", escolaridad)
        }
        Log.d("UserData", "***********************************")
    }

    // Función que maneja el evento de presionar el botón "Siguiente"
    fun handleNextButton() {
        if (validateFields()) {

            // Si todos los campos son válidos, registra los datos en Logcat
            logUserData(
                context = context,
                nombres = nombres,
                apellidos = apellidos,
                sexo = sexo,
                selectedDate = selectedDate,
                escolaridad = escolaridad,
                dateFormatter = dateFormatter
            )

            // Navega a la pantalla de datos de contacto
            context.startActivity(Intent(context, ContactDataActivity::class.java))
        } else {
            // Si falla la validación, muestra un warning en Logcat
            Log.w("UserData", "Validación fallida - Complete los campos obligatorios")
        }
    }

    // Sección de UI adaptada a orientación horizontal (landscape)
    if (isLandscape) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Título principal
            Text(
                text = stringResource(R.string.personal_data_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            // Fila para nombres y apellidos, cada uno ocupa la mitad del ancho
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {

                    // Campo de texto para nombres
                    OutlinedTextField(
                        value = nombres,
                        onValueChange = {
                            nombres = it
                            nombresError = false
                        },
                        label = { RequiredFieldLabel(stringResource(R.string.first_names)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "firstNames"
                            )
                        },
                        isError = nombresError,
                        supportingText = {
                            if (nombresError) ErrorText(stringResource(R.string.required_field))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        singleLine = true
                    )
                }

                Column(modifier = Modifier.weight(1f)) {

                    // Campo de texto para apellidos
                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = {
                            apellidos = it
                            apellidosError = false
                        },
                        label = { RequiredFieldLabel(stringResource(R.string.last_names)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "lastNames"
                            )
                        },
                        isError = apellidosError,
                        supportingText = {
                            if (apellidosError) ErrorText(stringResource(R.string.required_field))
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        singleLine = true
                    )
                }
            }

            // Composable  para seleccionar género
            GenderSelection(sexo) { sexo = it }

            // Composable  para seleccionar la fecha de nacimiento
            DateSelectionField(
                selectedDate = selectedDate,
                isError = fechaError,
                onDateSelected = { date ->
                    selectedDate = date
                    fechaError = false
                },
                showDatePicker = showDatePicker,
                onShowDatePickerChanged = { showDatePicker = it },
                datePickerState = datePickerState,
                dateFormatter = dateFormatter
            )

            // Composable para seleccionar nivel educativo
            EducationLevelDropdown(
                escolaridad = escolaridad,
                escolaridades = escolaridades,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onLevelSelected = { escolaridad = it }
            )

            // Botón "Siguiente" alineado a la derecha
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

        // Sección de UI adaptada a orientación vertical (portrait)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Título principal
            Text(
                text = stringResource(R.string.personal_data_title),
                style = MaterialTheme.typography.headlineMedium
            )

            // Campo de texto para nombres
            OutlinedTextField(
                value = nombres,
                onValueChange = {
                    nombres = it
                    nombresError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.first_names)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "firstNames"
                    )
                },
                isError = nombresError,
                supportingText = {
                    if (nombresError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true
            )

            // Campo de texto para apellidos
            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    apellidos = it
                    apellidosError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.last_names)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "lastNames"
                    )
                },
                isError = apellidosError,
                supportingText = {
                    if (apellidosError) ErrorText(stringResource(R.string.required_field))
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true
            )

            // Muestra el componente de selección de género
            GenderSelection(sexo) { sexo = it }

            // Muestra el campo de selección de fecha de nacimiento
            DateSelectionField(
                selectedDate = selectedDate,
                isError = fechaError,
                onDateSelected = { date ->
                    selectedDate = date
                    fechaError = false
                },
                showDatePicker = showDatePicker,
                onShowDatePickerChanged = { showDatePicker = it },
                datePickerState = datePickerState,
                dateFormatter = dateFormatter
            )

            // Muestra el campo desplegable para seleccionar nivel de escolaridad
            EducationLevelDropdown(
                escolaridad = escolaridad,
                escolaridades = escolaridades,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onLevelSelected = { escolaridad = it }
            )

            // Botón para avanzar al siguiente formulario
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

@Composable
fun GenderSelection(
    selectedSex: String,
    onSexSelected: (String) -> Unit
) {

    // Traduce a los textos localizados "Masculino" y "Femenino"
    val male = stringResource(R.string.male)
    val female = stringResource(R.string.female)

    // Layout principal: fila centrada horizontalmente
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        // Icono de cara representando género
        Icon(
            imageVector = Icons.Default.Face,
            contentDescription = "gender",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Texto "Género:"
        Text(
            text = "${stringResource(R.string.gender)} :",
            modifier = Modifier.padding(end = 16.dp)
        )

        // Opciones masculinas y femeninas en una fila separada
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Opción masculino
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedSex == male,
                    onClick = { onSexSelected(male) }
                )
                Text(
                    text = male,
                    modifier = Modifier
                        .clickable { onSexSelected(male) }
                        .padding(start = 4.dp)
                )
            }

            // Opción femenino
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedSex == female,
                    onClick = { onSexSelected(female) }
                )
                Text(
                    text = stringResource(R.string.female),
                    modifier = Modifier
                        .clickable { onSexSelected(female) }
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionField(
    selectedDate: Date?,                       // Fecha seleccionada actualmente
    isError: Boolean,                          // Indica si el campo presenta error
    onDateSelected: (Date) -> Unit,             // Función a ejecutar cuando se selecciona fecha
    showDatePicker: Boolean,                    // Indica si el DatePicker está abierto
    onShowDatePickerChanged: (Boolean) -> Unit, // Función para abrir/cerrar DatePicker
    datePickerState: DatePickerState,           // Estado interno del DatePicker
    dateFormatter: SimpleDateFormat             // Formato para mostrar la fecha
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column {

        // Etiqueta indicando campo obligatorio
        RequiredFieldLabel(text = stringResource(R.string.birth_date))

        // Campo de texto que muestra la fecha seleccionada
        OutlinedTextField(
            value = selectedDate?.let { dateFormatter.format(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            trailingIcon = {

                // Botón con ícono de calendario para abrir DatePicker
                IconButton(onClick = { onShowDatePickerChanged(true) }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            placeholder = { Text(stringResource(R.string.select_date)) }
        )

        // Si hay error, se muestra mensaje
        if (isError) {
            ErrorText(
                text = stringResource(R.string.required_field),
                modifier = Modifier
                    .padding(top = 4.dp, start = 16.dp)
            )
        }

        // Si el DatePicker está abierto:
        if (showDatePicker) {
            if (isLandscape) {

                // Para orientación horizontal: más espacio vertical en el DatePicker
                DatePickerDialog(
                    onDismissRequest = { onShowDatePickerChanged(false) },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    onDateSelected(Date(it))
                                }
                                onShowDatePickerChanged(false)
                            }
                        ) {
                            Text(text = stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { onShowDatePickerChanged(false) }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .heightIn(max = 400.dp)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            colors = DatePickerDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            title = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {

                // Para orientación vertical: diálogo de tamaño normal
                DatePickerDialog(
                    onDismissRequest = { onShowDatePickerChanged(false) },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    onDateSelected(Date(it))
                                }
                                onShowDatePickerChanged(false)
                            }
                        ) {
                            Text(text = stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { onShowDatePickerChanged(false) }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationLevelDropdown(
    escolaridad: String,                      // Nivel educativo seleccionado
    escolaridades: List<String>,               // Lista de opciones de niveles educativos
    expanded: Boolean,                         // Indica si el menú desplegable está abierto
    onExpandedChange: (Boolean) -> Unit,        // Función para cambiar el estado expandido
    onLevelSelected: (String) -> Unit           // Función que se llama al seleccionar un nivel
) {
    Column {

        // Título simple encima del menú
        Text(text = stringResource(R.string.education_level))

        // Componente de menú desplegable expuesto
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {

            // Campo de texto donde se muestra el nivel seleccionado (solo lectura)
            OutlinedTextField(
                value = escolaridad,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "educationalLevel"
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                label = { Text(stringResource(R.string.label_educational_level)) }
            )

            // Menú desplegable que muestra las opciones de escolaridad
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {

                // Por cada nivel de escolaridad disponible, crea un ítem de menú
                escolaridades.forEach { nivel ->
                    DropdownMenuItem(
                        text = { Text(nivel) },
                        onClick = {
                            onLevelSelected(nivel)    // Actualiza selección
                            onExpandedChange(false)   // Cierra el menú al seleccionar
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RequiredFieldLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        // Texto del campo
        Text(text)

        // Asterisco rojo para indicar que es obligatorio
        Text(
            text = "*",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

@Composable
fun ErrorText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text, // Mensaje de error
        color = MaterialTheme.colorScheme.error, // Color de error definido por el tema
        style = MaterialTheme.typography.bodySmall, // Estilo de texto pequeño
        modifier = modifier
    )
}
