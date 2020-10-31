package com.example.geoquiz

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "ivy"

class QuizViewModel : ViewModel() {


    val questionBank = listOf(
    Question(R.string.question_australia, true),
    Question(R.string.question_oceans, true),
    Question(R.string.question_mideast, false),
    Question(R.string.question_africa, false),
    Question(R.string.question_americas, true),
    Question(R.string.question_asia, true))

    var currentIndex = 0
    var isCheater = false
    var numCorrect : Double = 0.0

    val currentQuestionAnswer : Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText : Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        isCheater = false
        currentIndex = (currentIndex + 1) % questionBank.size
    }




}