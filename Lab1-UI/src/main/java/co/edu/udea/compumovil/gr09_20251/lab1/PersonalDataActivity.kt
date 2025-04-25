package co.edu.udea.compumovil.gr09_20251.lab1

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

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
    var expanded by rememberSaveable { mutableStateOf(false) }
    val escolaridades = listOf(*stringArrayResource(R.array.education_levels))
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var selectedDate by rememberSaveable(
        stateSaver = Saver <Date?, Bundle>(
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
                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { RequiredFieldLabel(stringResource(R.string.first_names)) },
                    modifier = Modifier.weight(1f),
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
                    onValueChange = { apellidos = it },
                    label = { RequiredFieldLabel(stringResource(R.string.last_names)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "${stringResource(R.string.gender)} :")

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = sexo == "Masculino",
                            onClick = { sexo = "Masculino" }
                        )
                        Text(
                            text = stringResource(R.string.male),
                            modifier = Modifier.clickable { sexo = "Masculino" }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = sexo == "Femenino",
                            onClick = { sexo = "Femenino" }
                        )
                        Text(
                            text = stringResource(R.string.female),
                            modifier = Modifier.clickable { sexo = "Femenino" }
                        )
                    }
                }
            }

            Column {
                RequiredFieldLabel(text = stringResource(R.string.birth_date))
                OutlinedTextField(
                    value = selectedDate?.let { dateFormatter.format(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    placeholder = { Text(stringResource(R.string.select_date)) }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        selectedDate = Date(it)
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text(text = stringResource(R.string.confirm))
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDatePicker = false }
                            ) {
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
                }
            }

            Column {
                Text(text = stringResource(R.string.education_level))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
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
                        onDismissRequest = { expanded = false }
                    ) {
                        escolaridades.forEach { nivel ->
                            DropdownMenuItem(
                                text = { Text(nivel) },
                                onClick = {
                                    escolaridad = nivel
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.next))
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
                onValueChange = { nombres = it },
                label = { RequiredFieldLabel(stringResource(R.string.first_names)) },
                modifier = Modifier.fillMaxWidth(),
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
                onValueChange = { apellidos = it },
                label = { RequiredFieldLabel(stringResource(R.string.last_names)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true
            )

            Text(text = stringResource(R.string.gender))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = sexo == "Masculino",
                        onClick = { sexo = "Masculino" }
                    )
                    Text(
                        text = stringResource(R.string.male),
                        modifier = Modifier.clickable { sexo = "Masculino" }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = sexo == "Femenino",
                        onClick = { sexo = "Femenino" }
                    )
                    Text(
                        text = stringResource(R.string.female),
                        modifier = Modifier.clickable { sexo = "Femenino" }
                    )
                }
            }

            RequiredFieldLabel(text = stringResource(R.string.birth_date))
            OutlinedTextField(
                value = selectedDate?.let { dateFormatter.format(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                },
                placeholder = { Text(stringResource(R.string.select_date)) }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    selectedDate = Date(it)
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text(text = stringResource(R.string.confirm))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDatePicker = false }
                        ) {
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

            Text(text = stringResource(R.string.education_level))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
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
                    onDismissRequest = { expanded = false }
                ) {
                    escolaridades.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text(nivel) },
                            onClick = {
                                escolaridad = nivel
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.next))
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
