package com.example.googlemapspractice;

import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FloorPlanMaker1 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivityTesting";

    private FusedLocationProviderClient mLocationProviderClient;

    private static final float DEFAULT_ZOOM = 15f;
    private static Timer locationTimer;
    private static TimerTask locationInterval;
    private static LatLng currentDeviceLocation, pastLocation ;
    private static Task<Location> locationTask ;
    private static Boolean pathHasStarted = false ;
    Button saveButton ;
    Button viewPathButton ;
    File mapFile ;
    FileOutputStream mapFileFOS ;
    String fileName = "JosephMapPracticeFile.txt";
    boolean ended = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan_maker1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton = (Button) findViewById(R.id.saveButton) ;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endApplication() ;
            }
        });

        viewPathButton = (Button) findViewById(R.id.viewPathButton) ;

        viewPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPathViewer();
            }
        });





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {

        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false); ;
        setLocationTimer();
        initializeFileWriter();
        locationTimer.schedule(locationInterval, 2000, 2000);
    }


    private void getDeviceLocation() {
        Log.d(TAG, "Getting the devices location part 1");
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        int i = 0 ;
        try {
            locationTask = mLocationProviderClient.getLastLocation();
            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    handleLocation(task.getResult());
                }
            }) ;
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException:" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moving the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    /*
    * This function takes the location and stores it into the external file currently being written
    *
    * */

    private void handleLocation(Location location) {
        if (pathHasStarted) {
            pastLocation = new LatLng(currentDeviceLocation.latitude, currentDeviceLocation.longitude) ;
            currentDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude()) ;


        } else {
            currentDeviceLocation = new LatLng(location.getLatitude(), location.getLongitude()) ;
            pastLocation = new LatLng(location.getLatitude(), location.getLongitude()) ;
            moveCamera(currentDeviceLocation, DEFAULT_ZOOM);
            pathHasStarted = true ;
        }

        mMap.addPolyline(new PolylineOptions()
                .width(100f)
                .color(R.color.PURPLE)
                .add(pastLocation)
                .add(currentDeviceLocation)) ;

        try {
            mapFileFOS.write((pastLocation.latitude + "," + pastLocation.longitude + "," +
                    currentDeviceLocation.latitude + "," + currentDeviceLocation.longitude + "\n").getBytes()) ;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //This sets the location timer
    private void setLocationTimer() {
        locationTimer = new Timer() ;

        locationInterval = new TimerTask() {
            @Override
            public void run() {
                getDeviceLocation();
            }
        } ;

    }

    /*
    * This function checks whether the External Storage permission is enabled
    *
    * */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //This goes to the map file generator activity to show the user their path
    public void goToPathViewer() {

        //Checks if the file has been saved
        if (!ended) {
            endApplication();
        }

        Intent intent = new Intent(FloorPlanMaker1.this, MapFileGenerator1.class) ;
        startActivity(intent);

    }


/*
    public void writeFile(View v) {
        String text ;


        if (isExternalStorageWritable()) {
            String fileName = "FirstMapPracticeJoe.txt";
            textFile = new File(Environment.getExternalStorageDirectory(), fileName/*fileName.getText().toString()) ;

            try {
                FileOutputStream fos = new FileOutputStream(textFile);
                fos.write(text.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
  */
    private void endApplication() {
        locationTimer.cancel();
        locationTimer.purge() ;

        try {
            mapFileFOS.close();
            ended = true ;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeFileWriter() {
        mapFile = new File(Environment.getExternalStorageDirectory(), fileName) ; //fileName.getText().toString()) ;

        try {
            mapFileFOS = new FileOutputStream(mapFile) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
