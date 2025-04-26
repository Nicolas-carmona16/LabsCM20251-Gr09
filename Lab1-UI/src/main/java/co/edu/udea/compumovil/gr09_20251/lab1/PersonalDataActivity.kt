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
import androidx.compose.material3.DatePickerState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

class PersonalDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalDataScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen() {
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("") }
    var escolaridad by rememberSaveable { mutableStateOf("") }
    var nombresError by rememberSaveable { mutableStateOf(false) }
    var apellidosError by rememberSaveable { mutableStateOf(false) }
    var fechaError by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val escolaridades = listOf(*stringArrayResource(R.array.education_levels))
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
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
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val context = LocalContext.current

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

    fun handleNextButton() {
        if (validateFields()) {
            logUserData(
                context = context,
                nombres = nombres,
                apellidos = apellidos,
                sexo = sexo,
                selectedDate = selectedDate,
                escolaridad = escolaridad,
                dateFormatter = dateFormatter
            )
            context.startActivity(Intent(context, ContactDataActivity::class.java))
        } else {
            Log.w("UserData", "Validación fallida - Complete los campos obligatorios")
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
                text = stringResource(R.string.personal_data_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = nombres,
                        onValueChange = {
                            nombres = it
                            nombresError = false
                        },
                        label = { RequiredFieldLabel(stringResource(R.string.first_names)) },
                        modifier = Modifier.fillMaxWidth(),
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
                    OutlinedTextField(
                        value = apellidos,
                        onValueChange = {
                            apellidos = it
                            apellidosError = false
                        },
                        label = { RequiredFieldLabel(stringResource(R.string.last_names)) },
                        modifier = Modifier.fillMaxWidth(),
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

            GenderSelection(sexo) { sexo = it }

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

            EducationLevelDropdown(
                escolaridad = escolaridad,
                escolaridades = escolaridades,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onLevelSelected = { escolaridad = it }
            )

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
                text = stringResource(R.string.personal_data_title),
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = nombres,
                onValueChange = {
                    nombres = it
                    nombresError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.first_names)) },
                modifier = Modifier.fillMaxWidth(),
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

            OutlinedTextField(
                value = apellidos,
                onValueChange = {
                    apellidos = it
                    apellidosError = false
                },
                label = { RequiredFieldLabel(stringResource(R.string.last_names)) },
                modifier = Modifier.fillMaxWidth(),
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

            GenderSelection(sexo) { sexo = it }

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

            EducationLevelDropdown(
                escolaridad = escolaridad,
                escolaridades = escolaridades,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onLevelSelected = { escolaridad = it }
            )

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
    val male = stringResource(R.string.male)
    val female = stringResource(R.string.female)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${stringResource(R.string.gender)} :",
            modifier = Modifier.padding(end = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
    selectedDate: Date?,
    isError: Boolean,
    onDateSelected: (Date) -> Unit,
    showDatePicker: Boolean,
    onShowDatePickerChanged: (Boolean) -> Unit,
    datePickerState: DatePickerState,
    dateFormatter: SimpleDateFormat
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column {
        RequiredFieldLabel(text = stringResource(R.string.birth_date))
        OutlinedTextField(
            value = selectedDate?.let { dateFormatter.format(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            trailingIcon = {
                IconButton(onClick = { onShowDatePickerChanged(true) }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            placeholder = { Text(stringResource(R.string.select_date)) }
        )
        if (isError) {
            ErrorText(
                text = stringResource(R.string.required_field),
                modifier = Modifier
                    .padding(top = 4.dp, start = 16.dp)
            )
        }

        if (showDatePicker) {
            if (isLandscape) {
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
    escolaridad: String,
    escolaridades: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onLevelSelected: (String) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.education_level))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = escolaridad,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                label = { Text(stringResource(R.string.label_educational_level)) }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                escolaridades.forEach { nivel ->
                    DropdownMenuItem(
                        text = { Text(nivel) },
                        onClick = {
                            onLevelSelected(nivel)
                            onExpandedChange(false)
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
        Text(text)
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
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}
