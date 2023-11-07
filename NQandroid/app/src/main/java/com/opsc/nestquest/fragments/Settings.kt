package com.opsc.nestquest.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.activities.LoginActivity
import com.opsc.nestquest.api.nestquest.NQAPI
import com.opsc.nestquest.api.nestquest.models.NQRetro
import com.opsc.nestquest.api.nestquest.models.User
import com.opsc.nestquest.classes.BackgroundLocal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Settings.newInstance] factory method to
 * create an instance of this fragment.
 */
class Settings : Fragment() {


    private lateinit var usernameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var  system: TextInputLayout
    private lateinit var systemField:MaterialAutoCompleteTextView
    private lateinit var distance:Slider
    private lateinit var unit:TextView
    private lateinit var notif:SwitchMaterial
    val CHANNEL_ID = "Location"
    val CHANNEL_NAME = "Near Hotspots"
    val NOTIF_ID = 101
    var requiredPermission = android.Manifest.permission.POST_NOTIFICATIONS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if(currentUser==null)
        {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
        usernameEditText=view.findViewById(R.id.usernameEditText)
        emailEditText=view.findViewById(R.id.emailEditText)
        notif=view.findViewById(R.id.Notif_Switch)


        unit=view.findViewById(R.id.unit)
        distance=view.findViewById(R.id.distance)


        usernameEditText.setText(UserData.user.name)
        emailEditText.setText(UserData.user.email)
        distance.value=UserData.user.maxDistance!!
        notif.isChecked=UserData.user.notifications!!
        var checkVal = requireContext().checkCallingOrSelfPermission(requiredPermission)
        if(checkVal!= PackageManager.PERMISSION_GRANTED)
        {
            notif.isChecked=false
        }

        notif.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                notifPermiss.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            else{
                val serviceIntent = Intent(requireContext(), BackgroundLocal::class.java)
                requireActivity().stopService(serviceIntent)

            }
        }


        system=view.findViewById(R.id.system)
        systemField=view.findViewById(R.id.systemfield)
        val adapterSys = ArrayAdapter(requireContext(), R.layout.dropdown_list,listOf<String>("Metric","Imperial"))
        systemField.setAdapter(adapterSys)

        if (UserData.user.metricSystem!!) {
            systemField.setText("Metric",false)
            unit.setText("KM")
        } else {
            systemField.setText("Imperial",false)
            unit.setText("MI")
        }



        systemField.setOnItemClickListener { parent, view, position, id ->
            if (systemField.text.toString() == "Metric") {
                unit.setText("KM")
            } else {
                unit.setText("MI")
            }
        }

        val fab=view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_save)
        fab.setOnClickListener {
            save()
        }

        val fab1=view.findViewById<ExtendedFloatingActionButton>(R.id.extended_fab_logout)
        fab1.setOnClickListener {
            logout()
        }
    }

    private fun save()
    {
        UserData.user.maxDistance=distance.value
        UserData.user.notifications=notif.isChecked
        UserData.user.metricSystem = systemField.text.toString() == "Metric"
        Log.d("testing","${UserData.user.maxDistance}\t${UserData.user.metricSystem}")
        saveUser(UserData.user.userId!!,UserData.user)
        Toast.makeText(requireContext(),"Settings Saved",Toast.LENGTH_SHORT).show()
    }

    private fun logout()
    {
        UserData.observations.clear()
        val user: User = User(null,null,null,null,null,null)
        UserData.user=user
        val serviceIntent = Intent(requireContext(), BackgroundLocal::class.java)
        requireActivity().stopService(serviceIntent)
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)

    }
    private fun saveUser(id:String,user: User)
    {
        val nqApi = NQRetro.getInstance().create(NQAPI::class.java)

        // passing data from our text fields to our model class.
        Log.d("testing","String of Object  "+ user.toString())
        GlobalScope.launch{
            nqApi.editUser(id,user).enqueue(
                object : Callback<User> {

                    override fun onFailure(call: Call<User>, t: Throwable) {

                        Log.d("testing", "It worked")
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


    val notifPermiss = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissions ->
        if(permissions)
        {

            notif.isChecked=true
            val serviceIntent = Intent(requireContext(), BackgroundLocal::class.java)
            requireActivity().startForegroundService(serviceIntent)
        }
        else{
            notif.isChecked=false
            AlertDialog.Builder(requireContext())
                .setTitle("Notification Permissions Required")
                .setMessage("Please enable notification permissions before you enable this notification")
                .setPositiveButton("OK") { dialog, _ ->
                    // You can optionally add code here to take the user to the settings screen to enable permissions.
                    val intent = Intent(android.provider.Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS)
                    startActivity(intent)
                    dialog.dismiss()

                }
                .show()


        }

    }

    override fun onDetach() {
        super.onDetach()
        if(notif.isChecked && !UserData.user.notifications!!)
        {
            val serviceIntent = Intent(requireContext(), BackgroundLocal::class.java)
            requireActivity().stopService(serviceIntent)

        }
        if(!notif.isChecked && UserData.user.notifications!!)
        {
            val serviceIntent = Intent(requireContext(), BackgroundLocal::class.java)
            requireActivity().startForegroundService(serviceIntent)
        }
    }

}