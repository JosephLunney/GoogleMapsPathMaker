package com.example.googlemapspractice;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
* */
public class MapFileGenerator1 extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapFileGenerator1Test";
    BufferedReader mapFileReader ;
    int path ; //= getResources().getIdentifier("JosephMapPracticeFile.txt", "raw", getPackageName()) ;
    InputStream IS ; //= getResources().openRawResource(path) ;
    private boolean firstLine = true ;

    private GoogleMap mMap;

    TextView distanceDisplay ;
    private double distanceTravelled = 0;


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

        distanceDisplay = (TextView) findViewById(R.id.distanceDisplay) ;


    }

    //This function triggers when the map is ready
    //It reads the file and then displays the total distance travelled
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        readMapFile2();
        distanceTravelled = ((double)((int)(distanceTravelled*100)))/100 ;
        distanceDisplay.setText("Distance travelled: " + distanceTravelled + " meters");

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

                    //This is used to move the camera once to first part of the path
                    if (firstLine) {
                        moveCamera(temp.center, 15f);
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

        //This makes sure that the file is not making halls with the same start and end points
        if (start.latitude != end.latitude && start.longitude != end.longitude) {
           // mMap.addPolyline(new PolylineOptions().add(start).add(end).width(100).color(R.color.PURPLE));
            mMap.addGroundOverlay(hallway.display) ;
        }

        Log.d(TAG, "Successful hallway made") ;

        distanceTravelled += getHallwayLength(hallway.lowerBound, hallway.upperBound) ;

        return hallway ;
    }

    //This moves the map
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moving the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /*
    * This uses the haversine equation to calculate distances on the earth.
    * It takes the start and end locations.
    * It returns the distance between them in meters.
    * */
    private double getHallwayLength(LatLng lower, LatLng upper) {
        final double EARTH_RADIUS = 6378.137 ; //Units are KM
        double dLat, dLon, a, c,d ;

        dLat = toRad(upper.latitude) - toRad(lower.latitude) ;
        dLon = toRad(upper.longitude - lower.longitude) ;

        a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lower.latitude * Math.PI / 180) * Math.cos(upper.latitude * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        d = EARTH_RADIUS * c ;


        return d * 1000 ;
    }

    //This function converts angles from degrees to radians
    private static double toRad(double x) {
        return (x * Math.PI / 180) ;
    }



}
