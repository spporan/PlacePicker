package com.poran.placepicker

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


const val AUTOCOMPLETE_REQUEST_CODE=1
const val REQUEST_PERMISSSION_CODE=0
class MainActivity : AppCompatActivity() {
    var showAddress: TextView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Places.initialize(applicationContext,resources.getString(R.string.YOUR_API_KEY))
        val client=Places.createClient(this)

        showAddress=findViewById(R.id.textView)







        usingFragment()
        findViewById<Button>(R.id.button).setOnClickListener {
            startIntent()

        }









    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {



            } else {

                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSSION_CODE)

            }
        }
    }
    fun usingFragment(){
        val fragment= supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment)as AutocompleteSupportFragment
        fragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.NAME))
        fragment.setCountry("BD")

        fragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Log.i( "Place: " ,p0.statusMessage.toString())
            }

            override fun onPlaceSelected(place: Place) {

                Log.i( "Place: " ,place.name + ", " + place.id)
                showAddress!!.text=place.name.toString()
            }


        })

    }
    private fun startIntent(){
        val fields= listOf(Place.Field.ID, Place.Field.NAME)
        val intent=Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields)
            .setCountry("BD")
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSSION_CODE-> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startIntent()
                    usingFragment()
                }
                return
            }

        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {

                RESULT_OK -> {
                    val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                    Log.i("Place", place?.name!!)
                    showAddress!!.text = place.name.toString()

                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                    Log.i("Error", status?.statusMessage!!)
                }
                Activity.RESULT_CANCELED -> {
                    Log.i("Error", "Cancel")
                }
            }

        }
    }
}


