package com.opsc.nestquest.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
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


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_log_reg)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

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
    { Log.d("testing","click")

        val timeWiseApi = NQRetro.getInstance().create(NQAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            try {

                val call:User = timeWiseApi.getUser(id)
                UserData.user=call
                getOb()
                Log.d("testing", call.toString())
            }
            catch (e:kotlin.KotlinNullPointerException)
            {
                Log.d("testing","no data")
            }
            catch(e:Exception)
            {
                Toast.makeText(this@LoginActivity, "Fail to get the data..", Toast.LENGTH_SHORT).show()

            }
        }

    }



    private fun getOb()
    {
        val timeWiseApi = NQRetro.getInstance().create(NQAPI::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            try {



                val call:List<Observation> = timeWiseApi.getObserve(UserData.user.userId!!)

                if (call.isEmpty())
                {
                    Log.d("testing","no values ")
                }

                Log.d("testing", call.toString())
                UserData.observations.clear()
                UserData.observations=call.toMutableList()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()


            }
            catch (e:kotlin.KotlinNullPointerException)
            {
                Log.d("testing","no data")
            }

        }
    }

}