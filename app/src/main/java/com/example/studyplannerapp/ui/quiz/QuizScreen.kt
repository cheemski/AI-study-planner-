package com.example.studyplannerapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyplannerapp.network.models.QuizQuestion
import com.example.studyplannerapp.ui.components.GradientHero
import com.example.studyplannerapp.ui.components.HeroBrushes
import com.example.studyplannerapp.ui.components.ScreenBackground
import com.example.studyplannerapp.ui.components.SectionHeader
import com.example.studyplannerapp.ui.theme.AccentGreen
import com.example.studyplannerapp.ui.theme.AccentOrange
import com.example.studyplannerapp.ui.theme.AccentRed
import com.example.studyplannerapp.ui.theme.BrandPurple
import com.example.studyplannerapp.ui.theme.CardWhite
import com.example.studyplannerapp.ui.theme.GreenTint
import com.example.studyplannerapp.ui.theme.InkMuted
import com.example.studyplannerapp.ui.theme.InkNavy
import com.example.studyplannerapp.ui.theme.OrangeTint
import com.example.studyplannerapp.ui.theme.PurpleTint
import com.example.studyplannerapp.ui.theme.RedTint

@Composable
fun QuizScreen(viewModel: QuizViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    ScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
        ) {
            item {
                Column {
                    Text("Career-driven course", color = InkMuted, fontSize = 14.sp)
                    Text("AI Quiz", color = InkNavy, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
            item { QuizHero(uiState, viewModel) }
            item { Spacer(Modifier.height(16.dp)) }

            when {
                uiState.quiz == null -> {
                    item { RecentQuizzes() }
                }
                uiState.isFinished -> {
                    item { QuizResultCard(uiState, onRestart = viewModel::reset) }
                }
                else -> {
                    item { QuestionCard(uiState, viewModel) }
                }
            }
        }
    }
}

@Composable
private fun QuizHero(uiState: QuizUiState, viewModel: QuizViewModel) {
    GradientHero(brush = HeroBrushes.quiz, modifier = Modifier.height(220.dp)) {
        Column {
            Text("Test Your Knowledge", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Before the Exam", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            if (uiState.quiz == null) {
                OutlinedTextField(
                    value = uiState.subject,
                    onValueChange = viewModel::onSubjectChange,
                    placeholder = { Text("Topic e.g. Data Structures", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f)
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text("Questions: ${uiState.numQuestions}", color = Color.White, fontSize = 13.sp)
                Slider(
                    value = uiState.numQuestions.toFloat(),
                    onValueChange = { viewModel.onNumQuestionsChange(it.toInt()) },
                    valueRange = 1f..20f, steps = 18,
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = Color.White, activeTrackColor = Color.White
                    )
                )
                uiState.errorMessage?.let { Text(it, color = Color.White, fontSize = 13.sp) }
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    modifier = Modifier.clickable(enabled = !uiState.isLoading) { viewModel.generateQuiz() }
                ) {
                    Row(
                        Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = AccentOrange)
                        } else {
                            Text("Start Quiz ⚡", color = AccentOrange, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HeroChip("${uiState.quiz?.questions?.size ?: 0} Questions")
                    HeroChip(uiState.subject.ifBlank { "Quiz" })
                }
            }
        }
    }
}

@Composable
private fun HeroChip(text: String) {
    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.22f)) {
        Text(text, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
private fun QuestionCard(uiState: QuizUiState, viewModel: QuizViewModel) {
    val quiz = uiState.quiz ?: return
    val question: QuizQuestion = quiz.questions[uiState.currentIndex]
    val letters = listOf("A", "B", "C", "D")

    Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(shape = RoundedCornerShape(50), color = OrangeTint) {
                    Text(
                        "Q${uiState.currentIndex + 1} of ${quiz.questions.size}",
                        color = AccentOrange, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                ProgressDashes(current = uiState.currentIndex + 1, total = quiz.questions.size)
            }
            Spacer(Modifier.height(16.dp))
            Text(question.question, color = InkNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(18.dp))

            question.options.forEachIndexed { idx, option ->
                AnswerOption(
                    letter = letters.getOrElse(idx) { "?" },
                    text = option,
                    isSelected = uiState.selectedAnswer == option,
                    isRevealed = uiState.isAnswerRevealed,
                    isCorrect = option == question.correctAnswer,
                    onClick = { viewModel.selectAnswer(option) }
                )
                Spacer(Modifier.height(12.dp))
            }

            if (uiState.isAnswerRevealed) {
                Surface(shape = RoundedCornerShape(12.dp), color = PurpleTint, modifier = Modifier.fillMaxWidth()) {
                    Text(question.explanation, color = InkNavy, fontSize = 13.sp, modifier = Modifier.padding(14.dp))
                }
                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = viewModel::nextQuestion,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InkNavy)
                    ) {
                        Text(
                            if (uiState.currentIndex + 1 == quiz.questions.size) "See results" else "Next Question",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressDashes(current: Int, total: Int) {
    val shown = total.coerceAtMost(6)
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(shown) { i ->
            Box(
                Modifier.width(20.dp).height(5.dp).clip(RoundedCornerShape(50))
                    .background(if (i < current.coerceAtMost(shown)) AccentOrange else PurpleTint)
            )
        }
    }
}

@Composable
private fun AnswerOption(
    letter: String, text: String, isSelected: Boolean, isRevealed: Boolean, isCorrect: Boolean, onClick: () -> Unit
) {
    val bg: Color; val border: Color; val badge: Color
    when {
        !isRevealed -> { bg = Color(0xFFF6F5FF); border = Color.Transparent; badge = InkMuted.copy(alpha = 0.3f) }
        isCorrect -> { bg = GreenTint; border = AccentGreen; badge = AccentGreen }
        isSelected -> { bg = RedTint; border = AccentRed; badge = AccentRed }
        else -> { bg = Color(0xFFF6F5FF); border = Color.Transparent; badge = InkMuted.copy(alpha = 0.3f) }
    }
    val textColor = when {
        !isRevealed -> InkNavy
        isCorrect -> AccentGreen
        isSelected -> AccentRed
        else -> InkNavy
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bg,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, border, RoundedCornerShape(14.dp))
            .clickable(enabled = !isRevealed, onClick = onClick)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(30.dp).clip(RoundedCornerShape(50)).background(badge),
                contentAlignment = Alignment.Center
            ) {
                Text(letter, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.size(12.dp))
            Text(text, color = textColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun QuizResultCard(uiState: QuizUiState, onRestart: () -> Unit) {
    val total = uiState.quiz?.questions?.size ?: 0
    Surface(shape = RoundedCornerShape(20.dp), color = CardWhite, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quiz complete! 🎉", color = InkNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("${uiState.correctCount} / $total", color = BrandPurple, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
            Text("correct answers", color = InkMuted, fontSize = 14.sp)
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple)
            ) {
                Text("Try another topic", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RecentQuizzes() {
    Column {
        SectionHeader("Recent Quizzes 📝")
        Spacer(Modifier.height(12.dp))
        RecentRow("Operating Systems", "10 questions", "85%", AccentGreen)
        Spacer(Modifier.height(12.dp))
        RecentRow("Calculus Chapter 4", "10 questions", "72%", AccentOrange)
    }
}

@Composable
private fun RecentRow(title: String, sub: String, score: String, color: Color) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardWhite, shadowElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(18.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, color = InkNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(sub, color = InkMuted, fontSize = 13.sp)
            }
            Text(score, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
