package com.opsc.nestquest.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.opsc.nestquest.Objects.UserData
import com.opsc.nestquest.R
import com.opsc.nestquest.api.ebird.adapters.HotspotListAdapter
import com.opsc.nestquest.api.ebird.models.HotspotView
import com.opsc.nestquest.api.nestquest.NQAPI
import com.opsc.nestquest.api.nestquest.adapters.observationAdapter
import com.opsc.nestquest.api.nestquest.models.NQRetro
import com.opsc.nestquest.api.nestquest.models.Observation
import com.opsc.nestquest.api.nestquest.models.Picture
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class CreateObservation : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    lateinit var imageView: ShapeableImageView
    var link:String=""
    val storageRef= Firebase.storage.reference
    lateinit var des: TextInputEditText
    lateinit var deslay: TextInputLayout
    private lateinit var locationManager: LocationManager
    private val permissionId = 2
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_observation, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationManager=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        imageView=view.findViewById(R.id.ImageField)
        deslay=view.findViewById(R.id.DesLay)
        des=view.findViewById(R.id.DesField)

        imageView.setOnClickListener{

            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"

            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)


        }
        val fab2=view.findViewById<ExtendedFloatingActionButton>(R.id.extended_save)
        fab2.setOnClickListener {
            if(!des.text.isNullOrEmpty())
            {
                getLocation()
                val ob:Observation= Observation(null,UserData.user.UserId,null,LocalDate.now(),"${UserData.lat},${UserData.lng}",null)
                if(!link.isNullOrEmpty())
                {
                    val picture = Picture(null,UserData.user.UserId,link)
                    addPicture(picture)
                    var pl:List<Picture> = listOf( picture)
                    ob.pictures=pl
                }
                addObservation(ob)
                genRecycleView(UserData.observations,recycler)
            }
            else
            {
                deslay.error="Enter a Description"
            }
        }
        
        
    }


    private fun genRecycleView(data:List<Observation>, recyclerView: RecyclerView)
    {
        activity?.runOnUiThread(Runnable {
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = observationAdapter(data)
            recyclerView.adapter = adapter
//            adapter.setOnClickListener(object : observationAdapter.OnClickListener {
//                override fun onClick(position: Int, model: HotspotView) {
//
//
//                }
//            })
        })
    }
    
    
    
    //Code attributed
    //https://www.geeksforgeeks.org/android-upload-an-image-on-firebase-storage-with-kotlin/
    //used as a reference to add image file to firebase
    //changed for all formats and t also get download link
    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
    // lambda expression to receive a result back, here we
        // receive single item(photo) on selection

        registerForActivityResult( ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_CANCELED) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data
                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(requireContext(), imageUri!!)

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                val uploadTask = storageRef.child("${UserData.user.UserId}/$sd").putFile(imageUri)

                // On success, download the file URL and display it
                uploadTask.addOnSuccessListener {
                    // using glide library to display the image
                    storageRef.child("${UserData.user.UserId}/$sd").downloadUrl.addOnSuccessListener {

                        Glide.with(this@CreateObservation)
                            .load(it)
                            .into(imageView)
                        link=it.toString()
                        Log.d("testing",link)
                        Log.e("Firebase", "download passed")
                    }.addOnFailureListener {
                        Log.e("Firebase", "Failed in downloading")
                    }
                }.addOnFailureListener {
                    Log.e("Firebase", "Image Upload fail")
                }
            }
        }


    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if(cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }


    private fun addPicture(pic: Picture)
    {
        val timewiseapi = NQRetro.getInstance().create(NQAPI::class.java)

        // passing data from our text fields to our model class.
        Log.d("testing", "String of Object  $pic")
        GlobalScope.launch{
            timewiseapi.addPic(pic).enqueue(
                object : Callback<Picture> {

                    override fun onFailure(call: Call<Picture>, t: Throwable) {
                        Log.d("testing", "Failure")
                    }

                    override fun onResponse(call: Call<Picture>, response: Response<Picture>) {
                        val addedUser = response.body()
                        if (response.isSuccessful)
                        {
                            Log.d("testing", addedUser.toString()+"worked!!")
                        }
                        Log.d("testing", addedUser.toString()+" fail pic")
                    }

                })
        }
    }

    private fun addObservation(ob:Observation)
    {
        val timewiseapi = NQRetro.getInstance().create(NQAPI::class.java)

        // passing data from our text fields to our model class.
        Log.d("testing", "String of Object  $ob")
        Log.d("testing", Gson().toJson(ob))
        GlobalScope.launch{
            timewiseapi.addObserve(ob).enqueue(
                object : Callback<Observation> {

                    override fun onFailure(call: Call<Observation>, t: Throwable) {
                        UserData.observations+ob
                        Log.d("testing", "Failure")

                    }

                    override fun onResponse(call: Call<Observation>, response: Response<Observation>) {
                        val addedUser = response.body()
                        if (response.isSuccessful)
                        {
                            Log.d("testing", addedUser.toString()+"worked!!")
                        }
                        Log.d("testing", addedUser.toString()+" fail ob")
                    }

                })
        }
    }
    


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        UserData.lat=location.latitude
                        UserData.lng=location.longitude
                       
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean
    {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


    private fun requestPermissions()
    {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        Log.d("testing",requestCode.toString())
        if (requestCode == permissionId)
        {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                getLocation()
            }
        }
    }
    companion object {
        const val TAG = "CreateObservation"

    }


}