package com.opsc.nestquest.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import java.time.LocalDateTime


class CreateObservation : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    lateinit var imageView: ShapeableImageView
    var link:String=""
    var storageRef= Firebase.storage.reference
    lateinit var des: TextInputEditText
    lateinit var deslay: TextInputLayout
    lateinit var recycler: RecyclerView
    lateinit var address:String

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
                val ob:Observation= Observation(null,UserData.user.userId,null,
                    LocalDateTime.now().toString(),"${UserData.lat};${UserData.lng};${address}", description =des.text.toString() , picture = null)
                if(!link.isNullOrEmpty())
                {
                    //val picture = Picture(null,userId = UserData.user.userId, description = link)
                    //addPicture(picture)
                    ob.picture=link
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
    private fun close ()
    {
        this.dismiss()
    }

    private fun genRecycleView(data:List<Observation>, recyclerView: RecyclerView)
    {
        Log.d("testing","at recycler")
        activity?.runOnUiThread(Runnable {
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = observationAdapter(data)
            recyclerView.adapter = adapter

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
                val uploadTask = storageRef.child("${UserData.user.userId}/$sd").putFile(imageUri)

                // On success, download the file URL and display it
                uploadTask.addOnSuccessListener {
                    // using glide library to display the image
                    storageRef.child("${UserData.user.userId}/$sd").downloadUrl.addOnSuccessListener {

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


//    private fun addPicture(pic: Picture)
//    {
//        val nqAPI = NQRetro.getInstance().create(NQAPI::class.java)
//
//        // passing data from our text fields to our model class.
//        Log.d("testing", "String of Object  $pic")
//        GlobalScope.launch{
//            nqAPI.addPic(pic).enqueue(
//                object : Callback<Picture> {
//
//                    override fun onFailure(call: Call<Picture>, t: Throwable) {
//                        Log.d("testing", "Failure")
//                    }
//
//                    override fun onResponse(call: Call<Picture>, response: Response<Picture>) {
//                        val addedUser = response.body()
//                        if (response.isSuccessful)
//                        {
//                            Log.d("testing", addedUser.toString()+"worked!!")
//                        }
//                        Log.d("testing", addedUser.toString()+" fail pic")
//                    }
//
//                })
//        }
//    }

    private fun addObservation(ob:Observation)
    {
        val nqAPI = NQRetro.getInstance().create(NQAPI::class.java)

        // passing data from our text fields to our model class.
        Log.d("testing", "String of Object  $ob")
        Log.d("testing", Gson().toJson(ob))
        GlobalScope.launch{
            nqAPI.addObserve(ob).enqueue(
                object : Callback<Observation> {

                    override fun onFailure(call: Call<Observation>, t: Throwable) {
                        UserData.observations.add(ob)
                        Log.d("testing", "it worked")
                        genRecycleView(UserData.observations,recycler)
                        getOb()
                        Toast.makeText(requireContext(), "Observation Saved", Toast.LENGTH_LONG).show()

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


    companion object {
        const val TAG = "CreateObservation"

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
                UserData.observations=call.toMutableList<Observation>()
                Log.d("testing", UserData.observations.toString())
                genRecycleView(UserData.observations,recycler)
                close()

            }
            catch (e:kotlin.KotlinNullPointerException)
            {
                Log.d("testing","no data")
            }

        }
    }

}