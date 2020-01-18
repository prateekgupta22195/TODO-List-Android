package com.prateek.todo.landing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.compat.Place
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete
import com.prateek.todo.R
import com.prateek.todo.app.AppDatabase
import com.prateek.todo.base.BaseFragment
import com.prateek.todo.db.Location
import com.prateek.todo.util.MapUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingFragment : BaseFragment() {


    val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1

    private val onMapReadyCallback = OnMapReadyCallback { this.setUpMaps(it) }


    var googleMap : GoogleMap?=null

    override fun onViewInflated(view: View, savedInstanceState: Bundle?) {
        initMap()
        setHasOptionsMenu(true)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_landing
    }


    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun setUpMaps(googleMap: GoogleMap) {
        this.googleMap = googleMap
        context?.let {
            GlobalScope.launch(Dispatchers.Main) {

                val savedLocations = getAllSavedLocations()

                val builder = LatLngBounds.Builder()
                for(location in savedLocations) {
                    val latLng = LatLng(location.lat, location.lng)
                    builder.include(latLng)
                    MapUtil.putMarkerOnDestination(latLng, googleMap)
                }

                if(savedLocations.isNotEmpty())
                    MapUtil.moveCameraToBound(builder.build(), it, googleMap)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_landing_fragment, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        activity?.let{
            if(item.itemId == R.id.search_place)
                openPlacePicker(it)
        }
        return super.onOptionsItemSelected(item)
    }




    fun openPlacePicker(activity: Activity) {
        try {
            // to search places of india only
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            // Ref : https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability#isGooglePlayServicesAvailable(android.content.Context)
            //if google play services are absent then it will crash
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        googleMap?.let {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(context, data)
                GlobalScope.launch(Dispatchers.IO) {
                    insertNewPlace(place)
                }
                MapUtil.putMarkerOnDestination(place.latLng, it)
                MapUtil.moveCameraToPosition(place.latLng, it)
            }
        }
    }



    private fun insertNewPlace(place : Place) {
        context?.let {
            val appDatabase = AppDatabase.invoke(it)
            val currentCoordinate = Location(place.latLng.latitude, place.latLng.longitude, MapUtil.getAddressFromLatLng(place.latLng, it))
            appDatabase.locationDao().insertAll(currentCoordinate)
        }?:kotlin.run {
            throw NullPointerException("Context is Null")
        }
    }

    @Throws(NullPointerException::class)
    private suspend fun getAllSavedLocations() : List<Location>   {
        return withContext(Dispatchers.IO) {
            // make network call
            // return user
            context?.let { context ->
                val appDatabase = AppDatabase.invoke(context)
                val locations = appDatabase.locationDao().getAll()
                return@withContext locations
            }?:run {
                throw NullPointerException("Context is Null")
            }
        }
    }
}