package com.prateek.todo.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.ArrayList


object MapUtil {


    fun putMarkerOnDestination(source: LatLng, googleMap: GoogleMap): Marker {
        val drawable = android.R.drawable.ic_menu_mylocation
        val markerOptions = MarkerOptions().icon(BitmapDescriptorFactory.fromResource(drawable)).position(source)
        return googleMap.addMarker(markerOptions)
    }


    fun moveCameraToPosition(coordinate: LatLng, googleMap: GoogleMap) {
        val location = CameraUpdateFactory.newLatLngZoom(
            coordinate, 16f
        )
        googleMap.animateCamera(location)
    }


    fun getAddressFromLatLng(latLng: LatLng, context: Context): String {

        val addressList = getAddressList(latLng, context)

        if (addressList.size != 0) {
            val address = addressList.get(0).getAddressLine(0)
            val premises = addressList.get(0).getPremises()
            val featureName = if (addressList.get(0).getFeatureName() == null)
                ""
            else
                addressList.get(0).getFeatureName()
            val sublocality = if (addressList.get(0).getSubLocality() == null)
                ""
            else
                addressList.get(0).getSubLocality()
            val locality =
                if (addressList.get(0).getLocality() == null) "" else addressList.get(0).getLocality()
            val adminArea =
                if (addressList.get(0).getAdminArea() == null) "" else addressList.get(0).getAdminArea()
            val pincode = if (addressList.get(0).getPostalCode() == null)
                ""
            else
                " - " + addressList.get(0).getPostalCode()

            return String.format("%s, %s %s, %s", featureName, sublocality, locality, adminArea)
        } else {
            return "Fetching Location..."
        }
    }


    fun getAddressList(latLng: LatLng, context: Context): List<Address> {
        try {
            return Geocoder(context).getFromLocation(latLng.latitude, latLng.longitude, 1)
        } catch (e: IOException) {
            return ArrayList()
        }
    }


    fun moveCameraToBound(bounds: LatLngBounds,
                          context: Context,
                          googleMap: GoogleMap) {
        context.let {
            val width = it.resources.displayMetrics.widthPixels
            val height = it.resources.displayMetrics.heightPixels
            val padding = (height * 0.01).toInt() // offset from edges of the map 10% of screen
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
            googleMap.moveCamera(cu)
        }
    }

}