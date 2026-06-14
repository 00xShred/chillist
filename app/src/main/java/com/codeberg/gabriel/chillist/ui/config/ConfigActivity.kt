package com.codeberg.gabriel.chillist.ui.config

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.codeberg.gabriel.chillist.ui.theme.ChillistTheme

class ConfigActivity : ComponentActivity() {

    private val viewModel: ConfigViewModel by viewModels {
        ConfigViewModel.Factory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            ChillistTheme(darkTheme = true, dynamicColor = false) {
                ConfigScreen(viewModel = viewModel)
            }
        }
    }
}

