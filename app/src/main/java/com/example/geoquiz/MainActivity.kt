package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_score.*
import org.w3c.dom.Text
import java.text.DecimalFormat
import kotlin.math.round
import kotlin.math.truncate
import android.R.attr.data
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    val TAG = "ivy"


    private lateinit var trueButton : Button
    private lateinit var falseButton : Button
    private lateinit var nextButton : ImageButton
    private lateinit var prevButton : ImageButton
    private lateinit var questionTextView : TextView
    private lateinit var questionImage : ImageView
    private lateinit var cheatButton : Button


    private val KEY_INDEX = "currentIndex"
    private val KEY_NUM_CORRECT = "numCorrect"


    private val quizViewModel : QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val exit = intent.getBooleanExtra("EXIT", false)
        Log.i(TAG, "Exit is: $exit")
        if (exit) {
            Log.i(TAG, "finish")
            finish()
        }


        Log.i(TAG, "onCreate() called")
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        cheatButton = findViewById(R.id.cheatButton)

        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        questionTextView = findViewById(R.id.question_text_view)

        questionImage = findViewById(R.id.imageView2)


        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }

        falseButton.setOnClickListener{ view: View ->
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            if (quizViewModel.currentIndex == 5) {
                Log.i(TAG, "Last Q")
                showScoring()
            }
            else {
                quizViewModel.moveToNext()
                updateQuestion()
            }
        }


        cheatButton.setOnClickListener{
            Log.i(TAG, "Cheat clicked")
            quizViewModel.isCheater = true
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }


        var currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        var numCorrect : Double = savedInstanceState?.getDouble(KEY_NUM_CORRECT, 0.0) ?: 0.0
        quizViewModel.numCorrect = numCorrect

        var size = quizViewModel.questionBank.size
        val questionTextResId = quizViewModel.currentQuestionText


        questionTextView.setText(questionTextResId)
        questionTextView.setOnClickListener {
            currentIndex = (currentIndex + 1) % size
            updateQuestion()
        }


        Log.i(TAG, "max: $size")
        prevButton.setOnClickListener{
            currentIndex = if (currentIndex != 0 ) {
                (currentIndex - 1) % size
            }
            else {
                size - 1
            }

            updateQuestion()
        }

        updateQuestion()
    }


    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        if (resultCode == RESULT_CLOSE_ALL) {
//                setResult(RESULT_CLOSE_ALL)
//                finish()
//
//        }
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }


    private fun checkAnswer(userAnswer : Boolean) {
        enableButtons(false)

        val correctAnswer = quizViewModel.currentQuestionAnswer

        if (userAnswer == correctAnswer && !quizViewModel.isCheater) {
            quizViewModel.numCorrect++
            Log.i(TAG, "Num correct: " + quizViewModel.numCorrect)
        }

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        var currentScore : Int =  quizViewModel.numCorrect.toInt()
        txtScoreCurrent.text = currentScore.toString()
        txtScoreTotalAnsweredQs.text = (quizViewModel.currentIndex +1).toString()



        var toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.show()
        toast.setGravity(Gravity.TOP, 0, 250)
    }



    fun showScoring() {
        val newIntent = Intent(this, Score::class.java)
        newIntent.putExtra("score", calcScore())
        startActivity(newIntent)
    }



    private fun calcScore() : Int {
        var score : Int = (round((quizViewModel.numCorrect/6)*100)).toInt()
        //Toast.makeText(this, "Quiz score: " + score + "%" , Toast.LENGTH_SHORT).show()
        //var score : Int = quizViewModel.numCorrect.toInt()
        quizViewModel.numCorrect = 0.0
        return score;
    }

    private fun enableButtons(option : Boolean) {
        trueButton.isEnabled = option
        falseButton.isEnabled = option
        nextButton.isEnabled = !option
        cheatButton.isEnabled = option
    }

    private fun updateQuestion() {

        enableButtons(true)

        var currentIndex = quizViewModel.currentIndex

        Log.d(TAG, "Current question index: $currentIndex")

        try {
            val question = quizViewModel.currentIndex
        } catch (ex: ArrayIndexOutOfBoundsException) {
            Log.e(TAG, "Index was out of bounds", ex)
        }


        val questionTextResId = quizViewModel.currentQuestionText

        val drawableResource = when (quizViewModel.currentQuestionText) {
            R.string.question_australia -> R.drawable.question_australia
            R.string.question_oceans -> R.drawable.question_oceans
            R.string.question_mideast -> R.drawable.question_mideast
            R.string.question_africa -> R.drawable.question_africa
            R.string.question_americas -> R.drawable.question_americas
            else -> R.drawable.question_asia
        }

        questionImage.setImageResource(drawableResource)
        questionTextView.setText(questionTextResId)
        prevButton.isEnabled = true
    }

    override fun onStart()  {
        super.onStart()

        Log.i(TAG, "onStart() called")
        if (intent.getBooleanExtra("exit", false)) {
            Log.i(TAG, "finish")
            finish()
        }

    }

    override fun onPause() {
        super.onPause()

        Log.i(TAG, "onPause() method called")
    }

    override fun onStop() {
        super.onStop()

        Log.i(TAG, "onStop() method called")
    }

    override fun onResume() {
        super.onResume()

        Log.i(TAG, "onResume() method called")


    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart() method called")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(TAG, "onDestroy() method called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState() method called")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putDouble(KEY_NUM_CORRECT, quizViewModel.numCorrect)
        Log.i(TAG, "Current Index: " +  quizViewModel.currentIndex  +" | Current score: " + quizViewModel.numCorrect + "/6")
    }


}
