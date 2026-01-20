package io.github.mucute.qwq.lsp.demo.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.mucute.qwq.lsp.demo.screen.MainScreen
import io.github.mucute.qwq.lsp.demo.ui.theme.TypeScriptLSPDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TypeScriptLSPDemoTheme {
                MainScreen()
            }
        }
    }
}