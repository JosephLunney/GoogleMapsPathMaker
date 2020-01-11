package com.example.googlemapspractice;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
/*
* This Activity loads the file saved to exernal storage for later viewing.
* It requires the external storage permission to work.
*
* */
public class MapFileGenerator1 extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapFileGenerator1Test";
    BufferedReader mapFileReader ;
    int path ; //= getResources().getIdentifier("JosephMapPracticeFile.txt", "raw", getPackageName()) ;
    InputStream IS ; //= getResources().openRawResource(path) ;
    private boolean firstLine = true ;

    private GoogleMap mMap;

    //Initializes the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_file_generator1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        path = getResources().getIdentifier("JosephMapPracticeFile.txt", "raw", getPackageName()) ;
        IS = getResources().openRawResource(R.raw.josephmappracticefile) ;




    }

    //This function triggers when the map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        readMapFile2();

    }

    //This is the function that reads from internal storage
    private void readMapFile() {
        String mFRLine ;
        try {
            mapFileReader = new BufferedReader(new InputStreamReader(IS)) ;

            mFRLine = mapFileReader.readLine() ;
            Hallway temp = createHallway(mFRLine) ;
            moveCamera(temp.center, 15);
            Log.d(TAG, mFRLine) ;

            while (mapFileReader.ready()) {
                mFRLine = mapFileReader.readLine() ;
                createHallway(mFRLine) ;
                Log.d(TAG, mFRLine) ;

            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //This function reads from external storage
    private void readMapFile2() {
        String mFRLine ;
        Hallway temp ;
        try {
            File textFile = new File(Environment.getExternalStorageDirectory(), "JosephMapPracticeFile.txt") ;
            FileInputStream fis = new FileInputStream(textFile) ;

            if (fis != null) {
                InputStreamReader isr = new InputStreamReader(fis) ;
                BufferedReader buff = new BufferedReader(isr) ;

                while (buff.ready()) {
                    mFRLine = buff.readLine() ;
                    temp = createHallway(mFRLine) ;

                    if (firstLine) {
                        moveCamera(temp.center, 15);
                        firstLine = false ;
                    }

                }

            }

        } catch (Exception e) {

        }


    }

    //This takes the data from the file and then converts it into a hallway object
    //The hallway object is essetnailly a polyline that stays the same size and calculates its dimensions
    public Hallway createHallway(String sampleData) {
        StringTokenizer lineDeconstructer = new StringTokenizer(sampleData, ",");
        LatLng start = new LatLng(Double.parseDouble(lineDeconstructer.nextToken()), Double.parseDouble(lineDeconstructer.nextToken())) ;
        LatLng end = new LatLng(Double.parseDouble(lineDeconstructer.nextToken()), Double.parseDouble(lineDeconstructer.nextToken())) ;
        Hallway hallway = new Hallway(start, end, 3) ;

        Log.d(TAG, "start: " + start.latitude + ", " + start.longitude) ;
        Log.d(TAG, "end: " + end.latitude + ", " + end.longitude) ;

        if (start.latitude != end.latitude && start.longitude != end.longitude) {
           // mMap.addPolyline(new PolylineOptions().add(start).add(end).width(100).color(R.color.PURPLE));
            mMap.addGroundOverlay(hallway.display) ;
        }

        Log.d(TAG, "Successful hallway made") ;
        return hallway ;
    }

    //This moves the map
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moving the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }



}
