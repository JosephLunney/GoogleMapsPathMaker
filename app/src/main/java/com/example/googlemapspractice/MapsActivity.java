package com.example.googlemapspractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.googlemapspractice.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationProviderClient ;
    private static final float DEFAULT_ZOOM = 15f ;
    private static final String TAG = "MapsActivity"; //To make a tag just quikcly type in 'logt' and hit enter



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MapsActivity", "Succeeded to get into maps") ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("MapsActivty", "Made it here") ;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location1 = new LatLng(43.6452778, -79.4467202) ;
        mMap = googleMap;

        //Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, -90);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (true /*when unsure if location permission is granted or not*/) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Either coarse or fine location is disabled") ;
                return ;
            } else {
                Log.d(TAG, "Both coarse and fine location seem to be enabled") ;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false); // Disables the button that centers the map back onto your location

        }

    }


    private void getDeviceLocation() {
        Log.d(TAG, "Getting the devices location part 1") ;
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this) ;

        try {
            if (true) {
                Task location = mLocationProviderClient.getLastLocation() ;
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found Location!");

                            Location currentLocation = (Location) task.getResult() ;

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                            mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Marker in my current location"));

                        } else {
                            Log.d(TAG, "Unable to get current location") ;
                        }
                    }
                });
            }

        }
        catch (SecurityException e) {
            Log.e(TAG, "SecurityException:" + e.getMessage()) ;
        }

    }


    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moving the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude) ;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

}