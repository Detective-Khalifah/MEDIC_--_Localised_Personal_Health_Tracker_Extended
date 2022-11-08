package com.blogspot.thengnet.medic

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.blogspot.thengnet.medic.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MapFragment : Fragment() {
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            googleMap = map
            val bay = LatLng(10.516858757587592, 7.450502906745651)
            map.moveCamera(CameraUpdateFactory.zoomTo(17f))
            map.moveCamera(CameraUpdateFactory.newLatLng(bay))

            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isTiltGesturesEnabled = false

//            mapViewModel.allLocations.observe(viewLifecycleOwner, Observer {
//                for (location in it) {
//                    val point = LatLng(map.myLocation.latitude, map.myLocation.longitude)
//
//                    val marker = map.addMarker(
//                        MarkerOptions()
//                            .position(point)
//                            .title(map.myLocation.title)
//                            .snippet("Hours: ${map.myLocation.hours}")
//                            .icon(
//                                getBitmapFromVector(
//                                    R.drawable.twotone_local_hospital_black_24,
//                                    R.color.colorAccent
//                                )
//                            )
//                            .alpha(.75f)
//                    )
//                    marker?.tag = location.locationId

//            if (BuildConfig.DEBUG) {
//                map.addCircle(
//                    CircleOptions()
//                        .center(point)
//                        .radius(location.geofenceRadius.toDouble())
//                )
//            }
//                }
//            })


//            map.setOnInfoWindowClickListener { marker ->
//                val action = MapFragmentDirections.actionNavigationMapToNavigationLocation()
//                action.locationId = marker.tag as Int
//                val navigationController = Navigation.findNavController(requireView())
//                navigationController.navigate(action)
//            }

            enableMyLocation()
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(ENABLE_LOCATION)
    private fun enableMyLocation() {
        if (EasyPermissions.hasPermissions(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            Snackbar.make(
                requireView(),
                "Show distance to locations?",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    EasyPermissions.requestPermissions(
                        this,
                        "We need access to your location to show your location relative to recreation opportunities near you. This data is not stored or shared",
                        ENABLE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }.show()
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(GET_LOCATION)
    private fun getCurrentLocation() {
        if (EasyPermissions.hasPermissions(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
//                    adapter.setCurrentLocation(location)
                }
            }
        } else {
            Snackbar.make(
                requireView(),
                "Show distance to locations?",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok) {
                    EasyPermissions.requestPermissions(
                        this,
                        "your location to show you recreation opportunities near your current location. This data is not stored or shared",
                        GET_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun getBitmapFromVector(
        @DrawableRes vectorResourceId: Int,
        @ColorRes colorResourceId: Int
    ): BitmapDescriptor {
        val vectorDrawable = resources.getDrawable(vectorResourceId, requireContext().theme)
            ?: return BitmapDescriptorFactory.defaultMarker()

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(
            vectorDrawable,
            ResourcesCompat.getColor(
                resources,
                colorResourceId, requireContext().theme
            )
        )
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    companion object {
        const val ENABLE_LOCATION = 123;
        const val GET_LOCATION = 231;
    }
}