package co.edu.udea.compumovil.gr09_20251.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.edu.udea.compumovil.gr09_20251.lab1.ui.theme.LabsCM20251Gr09Theme

class PersonalDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabsCM20251Gr09Theme {
                // Aquí llamaremos a PersonalDataScreen() más adelante
            }
        }
    }
}
