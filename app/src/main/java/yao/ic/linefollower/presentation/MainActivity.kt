package yao.ic.linefollower.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import yao.ic.linefollower.domain.ble.BLEManager
import yao.ic.linefollower.ui.theme.LineFollowerTheme
import yao.ic.linefollower.utils.WithPermissions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LineFollowerTheme {
                WithPermissions {
                    LineFollowerApp(
                        modifier = Modifier.fillMaxSize(),
                        windowSizeClass = calculateWindowSizeClass(this@MainActivity),
                    )
                }
            }
        }
    }
}