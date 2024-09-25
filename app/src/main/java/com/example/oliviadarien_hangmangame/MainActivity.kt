package com.example.oliviadarien_hangmangame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.oliviadarien_hangmangame.ui.theme.OliviaDarienHangmanGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OliviaDarienHangmanGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppLayout(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ChooseLetterPanel() {
    Column(Modifier.fillMaxSize().background(Color.Red)) {

    }
}

@Composable
fun HintPanel() {
    Column(Modifier.fillMaxSize().background(Color.Green)) {

    }
}

@Composable
fun GamePlayPanel() {
    Column(Modifier.fillMaxSize().background(Color.Blue)) {

    }
}

@Composable
fun AppLayout(modifier: Modifier = Modifier) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) { // Portrait
        Column() {
            Box(modifier=Modifier.weight(2f)) {
                GamePlayPanel()
            }
            Box(modifier=Modifier.weight(1f)) {
                ChooseLetterPanel()
            }
        }
    } else { // Landscape
        Row() {
            Column(modifier=Modifier.weight(1f)) {
                Box(modifier=Modifier.weight(2f)) {
                    ChooseLetterPanel()
                }
                Box(modifier=Modifier.weight(2f)) {
                    HintPanel()
                }
            }
            Box(modifier=Modifier.weight(1f)) {
                GamePlayPanel()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OliviaDarienHangmanGameTheme {
        AppLayout()
    }
}