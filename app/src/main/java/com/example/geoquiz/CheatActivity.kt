package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

private const val EXTRA_ANSWER_IS_TRUE =
    "com.example.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "answer shown"

private lateinit var answerTextView: TextView
private lateinit var showAnswerButton: Button


private var answerIsTrue = false

class CheatActivity : AppCompatActivity() {

    private val cheatViewModel : CheatViewModel by lazy {
        ViewModelProviders.of(this).get(CheatViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)


        //answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        cheatViewModel.answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)



        var answerText = savedInstanceState?.getInt(EXTRA_ANSWER_SHOWN, 0) ?: 0
        cheatViewModel.answerText = answerText
        if (savedInstanceState != null)
            updateText()


        showAnswerButton.setOnClickListener{

            //val answerText = when {
            cheatViewModel.answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            updateText()
        }

    }

    private fun updateText() {
        answerTextView.setText(cheatViewModel.answerText)
        setAnswerShownResult(true)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply{
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext : Context, answerIsTrue : Boolean) : Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(EXTRA_ANSWER_SHOWN, cheatViewModel.answerText)
    }
}
