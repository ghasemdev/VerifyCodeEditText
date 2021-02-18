package com.jakode.verifycodeedittexttest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakode.verifycodeedittext.VerifyCodeEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val verifyCodeEditText = findViewById<VerifyCodeEditText>(R.id.verifyCodeEditText)
        verifyCodeEditText.setCompleteListener { complete ->
            if (complete) {
                Toast.makeText(this@MainActivity, verifyCodeEditText.text, Toast.LENGTH_SHORT).show()
                verifyCodeEditText.setCodeItemErrorLineDrawable()
            }
        }
        verifyCodeEditText.text = "99999"
    }
}