package com.jakode.verifycodeedittexttest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jakode.verifycodeedittext.VerifyCodeEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val verifyCodeEditText = findViewById<VerifyCodeEditText>(R.id.verify)
        verifyCodeEditText.setCompleteListener { complete ->
            if (complete) {
                Toast.makeText(this@MainActivity, verifyCodeEditText.text, Toast.LENGTH_SHORT)
                    .show()
                verifyCodeEditText.setCodeItemErrorLineDrawable()
            }
        }
        verifyCodeEditText.text = "99999"

        /*val verifyCodeEditText = VerifyCodeEditText.Builder {
            text {
                size = 20F
                color = Color.parseColor("#000000")
            }
            bottomIcon {
                iconHeight = 5
                iconWidth = 60
                selectedIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.bottom_selected_icon)
                unSelectedIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.bottom_unselected_icon)
                errorIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.bottom_error_icon)
            }
            verifyCell {
                count = VerifyCodeEditText.Builder.ViewCount.Five
                spaceSize = 48
            }
        }.build(context = this)
        findViewById<ConstraintLayout>(R.id.layout).addView(verifyCodeEditText)*/
    }
}