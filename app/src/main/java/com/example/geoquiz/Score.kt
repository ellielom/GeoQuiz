package com.example.geoquiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_score.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log


class Score : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        txtCurrentScore.text = getCurrentScore().toString() + "%"
        txtHighestScore.text = getHighestScore().toString() + "%"

    }

    fun getCurrentScore() : Int {
        val intent = getIntent()
        val result = intent.getIntExtra("score", 0);
        return result;
    }

    fun getHighestScore() : Int {
        val pref = getPreferences(Context.MODE_PRIVATE);
        var currentScore = getCurrentScore()
        var highestScore = pref.getInt("Highest Score", 0);

        if (currentScore > highestScore) {
            saveCurrentScoreToFile(currentScore)
            return currentScore
        }
        else
            return highestScore

    }

    fun saveCurrentScoreToFile(score: Int) {
        val pref = getPreferences(Context.MODE_PRIVATE);
        val editor = pref.edit();

        editor.putInt("Highest Score", score);
        editor.commit();
    }

    fun onTryAgain(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
    fun onExit(view: View) {
        Log.i("ivy", "Exit clicked")
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("EXIT", true)
        startActivity(intent)

    }

}
