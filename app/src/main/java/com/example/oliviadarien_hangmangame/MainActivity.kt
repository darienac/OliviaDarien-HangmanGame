package com.example.oliviadarien_hangmangame

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
        OutlinedButton(
            onClick=onClick,
            enabled=enabled,
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(0),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            modifier=Modifier
                .fillMaxSize()
                .padding(2.dp)
                .size(width = 8.dp, height =32.dp)) {
            Text(label,
                fontSize=15.sp)
        }
    }

    Column(Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text("Choose a Letter",
            fontSize=32.sp,
            fontWeight = FontWeight.Bold,
            modifier=Modifier.padding(8.dp, 0.dp, 0.dp, 8.dp))
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 64.dp),
                modifier = Modifier
                    .padding(8.dp, 0.dp, 0.dp, 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                items(26) { index ->
                    LetterInputButton(
                        'A' + index + "",
                        enabled && !usedLetters.contains('A' + index)
                    ) {
                        onLetterClick('A' + index)
                    }
                }
            }
        }
        Spacer(Modifier.height(2.dp))
    }
}

@Composable
fun HintPanel(hint: String, enabled: Boolean, onHintClick: ()->Unit) {
    ElevatedCard(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(hint, fontSize=24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick=onHintClick, enabled=enabled, border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(0),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
                    Text("Hint")
                }
            }
        }
    }
}

@Composable
fun GamePlayPanel(gameWord: String, livesLeft: Int, usedLetters: Set<Char>, gameWon: Boolean, onReset: () -> Unit, newGame: () -> Unit) {
    //  list of # of lives + corresponding images
    val images = listOf(
        Pair(6, R.drawable.p6),
        Pair(5, R.drawable.p5),
        Pair(4, R.drawable.p4),
        Pair(3, R.drawable.p3),
        Pair(2, R.drawable.p2),
        Pair(1, R.drawable.p1),
        Pair(0, R.drawable.p0)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {

        // Playing game

            // depending on livesLeft -> different image
            Image(
                painter = painterResource(images.find {it.first == livesLeft}?.second?: R.drawable.p0),
                contentDescription=null,
                modifier = Modifier
                    // makes images same size on screen
                    .weight(1f)
                    .fillMaxWidth()
            )


        // Won Game
        if (gameWon){
            Text("Congrats! You Won!", fontWeight = FontWeight.Bold, fontSize = 24.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onReset,
                border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(0),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
                Text("Reset Game")
            }

        }

        // Lost Game
        else if (livesLeft <= 0){
            Text(text="You lost... Try again next time!", fontWeight = FontWeight.Bold, fontSize = 24.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onReset,border = BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(0),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
                Text("Reset Game")
            }
        }
        else {
            Row(){
                Spacer(Modifier.width(5.dp))
                for (char in gameWord) {
                    if (usedLetters.contains(char)){
                        Text(text = char.toString(),
                            fontSize = 50.sp,
                            textDecoration = TextDecoration.Underline)

                        Spacer(Modifier.width(5.dp))
                    }

                    else{
                        Text(text = "   ",
                            fontSize = 50.sp,
                            textDecoration = TextDecoration.Underline)
                        Spacer(Modifier.width(5.dp))
                    }
                }
            }
        }
        OutlinedButton(onClick = newGame,border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(0),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
            Text("New Game")
        }
    }
}

@Composable
fun AppLayout(modifier: Modifier = Modifier) {
    var gameNum by rememberSaveable {mutableIntStateOf(1)}

    var gameWord by rememberSaveable {mutableStateOf("APPLE")}
    if (gameNum ==1) gameWord = "APPLE" else gameWord = "ELEPHANT"
    var hint by rememberSaveable {mutableStateOf("Hint: Something you can eat")}
    if (gameNum ==1) hint = "Hint: Something you can eat" else hint = "Hint: A big animal"

    var hintRound by rememberSaveable {mutableStateOf(HintRound.MESSAGE)}
    var livesLeft by rememberSaveable {mutableIntStateOf(6)}
    var usedLetters by rememberSaveable {mutableStateOf(setOf<Char>())} // all used letters, even if they were wrong
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

    fun resetGame() {
        livesLeft = 6
        usedLetters = setOf<Char>()
        hintRound = HintRound.MESSAGE
        gameWon = false
    }

    fun newGame() {
        livesLeft = 6
        usedLetters = setOf<Char>()
        hintRound = HintRound.MESSAGE
        gameWon = false
        gameNum = if (gameNum == 1) 2 else 1
    }

    val context = LocalContext.current
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) { // Portrait
        Column(modifier=modifier) {
            Box(modifier=Modifier.weight(1f), contentAlignment = Alignment.Center) {
                GamePlayPanel(gameWord, livesLeft, usedLetters, gameWon, ::resetGame, ::newGame)
            }
            Box(modifier=Modifier.weight(1f)) {
                ChooseLetterPanel(usedLetters, livesLeft > 0 && !gameWon, ::testLetter)
            }
        }
    } else { // Landscape
        Row(modifier=modifier) {
            Column(modifier=Modifier.weight(1f)) {
                Box(modifier=Modifier.weight(7f)) {
                    ChooseLetterPanel(usedLetters, livesLeft > 0 && !gameWon, ::testLetter)
                }
                Box(modifier=Modifier.weight(3f).padding(8.dp)) {
                    HintPanel(if (hintRound > HintRound.MESSAGE) hint else "Click for hint", livesLeft > 0 && hintRound != HintRound.NO_HINT && !gameWon) {
                        if (hintRound == HintRound.MESSAGE) {
                            hintRound = HintRound.HALF_LETTERS
                        } else if (livesLeft == 1 || hintRound == HintRound.NO_HINT) {
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
            Box(modifier=Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                GamePlayPanel(gameWord, livesLeft, usedLetters, gameWon, ::resetGame, ::newGame)
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