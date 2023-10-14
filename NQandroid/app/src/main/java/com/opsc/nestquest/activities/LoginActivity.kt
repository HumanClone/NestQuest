package com.opsc.nestquest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.nestquest.NQAPI
import com.opsc.nestquest.api.nestquest.models.NQRetro
import com.opsc.nestquest.api.nestquest.models.Observation
import com.opsc.nestquest.api.nestquest.models.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                    val userf = Firebase.auth.currentUser
                    getUser(userf!!.uid.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Prevents user from returning to login screen using back button
                } else {
                    Toast.makeText(baseContext, "Invalid Email or Password.", Toast.LENGTH_SHORT).show()
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


    private fun getUser(id:String)
    {
        val nqApi = NQRetro.getInstance().create(NQAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            val call: Call<User> = nqApi.getUser(id)
            call.enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    try {
                        if (response.isSuccessful()) {
                            Log.d("testing",response.body()!!.toString())
                            val root: User = response.body()!!
                            UserData.user=root

                        }
                    }
                    catch(e:java.lang.NullPointerException)
                    {
                        Log.d("testing","no data")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable?) {
                    // displaying an error message in toast
//                    Toast.makeText(this@Test, "Fail to get the data..", Toast.LENGTH_SHORT)
//                        .show()
                    Log.d("testing","no data")
                }
            })
        }
    }



    private fun getOb()
    {
        val timeWiseApi = NQRetro.getInstance().create(NQAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            try {


                val call:List<Observation> = timeWiseApi.getObserve(UserData.user.UserId!!)
                if (call.isEmpty())
                {
                    Log.d("testing","no values ")
                }

                Log.d("testing", call.toString())
                UserData.observations.clear()
                UserData.observations+call

            }
            catch (e:kotlin.KotlinNullPointerException)
            {
                Log.d("testing","no data")
            }

        }
    }
}