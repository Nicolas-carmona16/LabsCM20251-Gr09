package co.edu.udea.compumovil.gr09_20251.lab1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr09_20251.lab1.ui.theme.LabsCM20251Gr09Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabsCM20251Gr09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    // Llama a la pantalla de inicio
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    // Contenedor vertical que centra los elementos
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla
            .padding(16.dp), // Agrega margen
        verticalArrangement = Arrangement.Center, // Centra verticalmente
        horizontalAlignment = Alignment.CenterHorizontally  // Centra horizontalmente
    ) {
        // Texto de bienvenida
        Text(
            text = stringResource(R.string.welcome), // Carga texto desde strings.xml
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        // Botón que navega a la actividad PersonalDataActivity
        Button(
            onClick = {
                val intent = Intent(context, PersonalDataActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text( text = stringResource(R.string.button_to_personal_data ))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LabsCM20251Gr09Theme {
        HomeScreen()
    }
}
