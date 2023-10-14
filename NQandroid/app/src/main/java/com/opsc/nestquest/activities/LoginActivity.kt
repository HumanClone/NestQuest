package com.opsc.nestquest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.opsc.nestquest.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_reg)
        actionBar?.hide()
        auth = FirebaseAuth.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.editText_username)
        val passwordEditText = findViewById<EditText>(R.id.editText_password)
        val viewPasswordImageView = findViewById<ImageView>(R.id.imageView_viewPassword)
        val loginButton = findViewById<Button>(R.id.button_login)
        val registerTextView = findViewById<TextView>(R.id.textView_register)

        viewPasswordImageView.setOnClickListener {
            togglePasswordVisibility(passwordEditText)
        }

        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Bypass the login process
            if (email == "test" && password == "test") {
                Toast.makeText(baseContext, "Login Bypassed.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            } // Bypass the login process
            if (validateForm(email, password)) {
                loginUser(email, password)
            }
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }



    private fun validateForm(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(baseContext, "Fields cannot be empty.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Login Successful.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Prevents user from returning to login screen using back button
                } else {
                    Toast.makeText(baseContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun togglePasswordVisibility(passwordEditText: EditText) {
        if (passwordEditText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordEditText.setSelection(passwordEditText.text.length)
    }
}