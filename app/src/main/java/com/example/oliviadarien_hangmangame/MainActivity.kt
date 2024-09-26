package com.example.oliviadarien_hangmangame

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

enum class HintRound {
    MESSAGE,
    HALF_LETTERS,
    SHOW_VOWELS,
    NO_HINT
}

@Composable
fun ChooseLetterPanel(usedLetters: Set<Char>, enabled: Boolean, onLetterClick: (letter: Char)->Unit) {
    @Composable
    fun LetterInputButton(label: String, enabled: Boolean, onClick: ()->Unit) {
        FilledTonalButton(onClick=onClick, enabled=enabled, modifier=Modifier.fillMaxSize().padding(2.dp)) {
            Text(label)
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text("Choose a Letter", fontSize=32.sp, modifier=Modifier.padding(16.dp, 8.dp, 0.dp, 8.dp))
        LazyVerticalGrid(columns=GridCells.Adaptive(minSize=64.dp), modifier=Modifier.padding(2.dp).weight(1f)) {
            items(26) {index->
                LetterInputButton('A' + index + "", enabled && !usedLetters.contains('A' + index)) {
                    onLetterClick('A' + index)
                }
            }
        }
        Spacer(Modifier.height(2.dp))
    }
}

@Composable
fun HintPanel(hint: String, enabled: Boolean, onHintClick: ()->Unit) {
    ElevatedCard(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment=Alignment.CenterHorizontally) {
            Spacer(Modifier.height(8.dp))
            Text(hint, fontSize=24.sp)
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick=onHintClick, enabled=enabled) {
                Text("Hint")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun GamePlayPanel(gameWord: String, livesLeft: Int, usedLetters: Set<Char>, gameWon: Boolean) {
    // Feel free to get rid of this, this was just for me to test stuff
    Column(Modifier.fillMaxSize().background(Color.Blue)) {
        Text("word: $gameWord")
        Text("Lives left: $livesLeft")
        Text("Game won: $gameWon")
    }
}

@Composable
fun AppLayout(modifier: Modifier = Modifier) {
    val gameWord = "APPLE"
    val hint = "Hint: Something you can eat"

    var hintRound by remember {mutableStateOf(HintRound.MESSAGE)}
    var livesLeft by remember {mutableIntStateOf(6)}
    var usedLetters by remember {mutableStateOf(setOf<Char>())} // all used letters, even if they were wrong
    var gameWon = true
    for (letter in gameWord) {
        if (!usedLetters.contains(letter)) {
            gameWon = false
            break
        }
    }

    fun testLetter(letter: Char) {
        if (usedLetters.contains(letter) || livesLeft == 0) {
            return
        }
        if (!gameWord.contains(letter)) {
            livesLeft--
        }
        usedLetters = usedLetters.plusElement(letter)
    }

    fun disableHalfRemaining() {
        val lettersNotUsed = mutableSetOf<Char>()
        for (letter in 'A'..'Z') {
            if (!usedLetters.contains(letter) && !gameWord.contains(letter)) {
                lettersNotUsed.add(letter)
            }
        }
        val halfRemaining = lettersNotUsed.size / 2
        for (i in 1..halfRemaining) {
            val letter = lettersNotUsed.random()
            lettersNotUsed.remove(letter)
            usedLetters = usedLetters.plus(letter)
        }
    }

    fun showAllVowels() {
        usedLetters = usedLetters.plus(setOf('A', 'E', 'I', 'O', 'U'))
    }

    val context = LocalContext.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) { // Portrait
        Column(modifier=modifier) {
            Box(modifier=Modifier.weight(1f)) {
                GamePlayPanel(gameWord, livesLeft, usedLetters, gameWon)
            }
            Box(modifier=Modifier.weight(1f)) {
                ChooseLetterPanel(usedLetters, livesLeft > 0 && !gameWon, ::testLetter)
            }
        }
    } else { // Landscape
        Row(modifier=modifier) {
            Column(modifier=Modifier.weight(1f)) {
                Box(modifier=Modifier.weight(2f)) {
                    ChooseLetterPanel(usedLetters, livesLeft > 0 && !gameWon, ::testLetter)
                }
                Box(modifier=Modifier.weight(1f)) {
                    HintPanel(if (hintRound > HintRound.MESSAGE) hint else "Click for hint", livesLeft > 0 && hintRound != HintRound.NO_HINT && !gameWon) {
                        if (hintRound == HintRound.MESSAGE) {
                            hintRound = HintRound.HALF_LETTERS
                        } else if (livesLeft == 0 || hintRound == HintRound.NO_HINT) {
                            Toast.makeText(context, "Hint not available", Toast.LENGTH_SHORT).show()
                        } else if (hintRound == HintRound.HALF_LETTERS) {
                            disableHalfRemaining()
                            hintRound = HintRound.SHOW_VOWELS
                            livesLeft--
                        } else if (hintRound == HintRound.SHOW_VOWELS) {
                            showAllVowels()
                            hintRound = HintRound.NO_HINT
                            livesLeft--
                        }
                    }
                }
            }
            Box(modifier=Modifier.weight(1f)) {
                GamePlayPanel(gameWord, livesLeft, usedLetters, gameWon)
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