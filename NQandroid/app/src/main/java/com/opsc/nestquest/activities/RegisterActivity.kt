package com.opsc.nestquest.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.nestquest.NQAPI
import com.opsc.nestquest.api.nestquest.models.NQRetro
import com.opsc.nestquest.api.nestquest.models.User
import com.opsc.nestquest.databinding.ActivityRegisterBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameEditText:EditText
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText:EditText
    private lateinit var registerButton:Button
    private lateinit var loginTextView:TextView

private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        usernameEditText = findViewById(R.id.editText_username)
        emailEditText = findViewById(R.id.editText_email)
        passwordEditText = findViewById(R.id.editText_password)
        registerButton = findViewById(R.id.button_register)
        loginTextView = findViewById(R.id.textView_login)

        //getUserNorm()
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val username=usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateForm(username, password,email)) {
                createAccount(email, password)
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }


    private fun validateForm(username: String, password: String,email:String): Boolean {
        if (username.isEmpty() || password.isEmpty()||email.isEmpty()) {
            Toast.makeText(baseContext, "Fields cannot be empty.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userf = Firebase.auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                    displayName = usernameEditText.text.toString()
                     }
                    userf!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("register", "User profile updated.")
                            }
                        }

                    val user=User(
                        userId =userf!!.uid.toString(),
                        name=usernameEditText.text.toString(),
                        email = email,
                        birdSightingIds = listOf(),
                        darkTheme = false,
                        maxDistance = 10.0.toFloat(),
                        metricSystem = true
                    )

                    UserData.user=user
                    addUser(user)
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(baseContext, "Registration Successful.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                    else {
                    Toast.makeText(baseContext, "Invalid Email or Password.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun addUser(user: User)
    {
        val nqApi = NQRetro.getInstance().create(NQAPI::class.java)

        // passing data from our text fields to our model class.
        Log.d("testing","String of Object  "+ user.toString())
        GlobalScope.launch{
            nqApi.addUser(user).enqueue(
                object : Callback<User> {

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.d("testing", "Failure")
                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        val addedUser = response.body()
                        if (response.isSuccessful)
                        {
                            Log.d("testing", addedUser.toString()+"worked!!")
                        }
                        Log.d("testing", addedUser.toString()+" fail")
                    }

                })
        }
    }



}