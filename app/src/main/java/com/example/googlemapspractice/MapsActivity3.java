package com.example.googlemapspractice;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity3 extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity3Testing";

    private GoogleMap mMap;
    private LatLng northEast = new LatLng(43.649121, -79.443117);
    private LatLng southWest = new LatLng(43.6480309, -79.4437996) ;
    private LatLng northWest = new LatLng(43.6489513, -79.4441560);
    private LatLng southEast = new LatLng(43.6484465, -79.4429430) ;
    private LatLng hopefulCenter ;
    private float hallwayLength ;
    private float angle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        testCase4();




    }

    private static double toRad(double x) {
        return (x * Math.PI / 180) ;
    }

    private static double toDeg(double x) {
        return (x * 180 / Math.PI) ;
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moving the camera to: lat: " + latLng.latitude + "lng: " + latLng.longitude) ;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private double getAngle(LatLng start, LatLng end) {
        LatLng rightAnglePoint = new LatLng(start.latitude, end.longitude) ;
        double hypotenuse, adjacent, angle ;

        hypotenuse = getHallwayLength(start, end) ;
        adjacent = getHallwayLength(start, rightAnglePoint) ;

        Log.d(TAG, "hypotenuse length: " + hypotenuse) ;
        Log.d(TAG, "adjacent length: " + adjacent) ;

        angle = Math.acos(adjacent/hypotenuse) ;

        if (end.latitude > start.latitude && end.longitude > start.longitude) { // case 1
            angle = 90 - toDeg(angle) ;
            Log.d(TAG, "Bearing value case 1: " + angle) ;
        } else if (end.latitude > start.latitude && end.longitude < start.longitude) { //case 2
            angle = toDeg(angle) + 270 ;
            Log.d(TAG, "Bearing value case 2: " + angle) ;
        } else if (end.latitude < start.latitude && end.longitude > start.longitude) { // case 3
            angle = toDeg(angle) + 90 ;
            Log.d(TAG, "Bearing value case 3: " + angle) ;
        } else { // case 4
            angle =  270 - toDeg(angle) ;
            Log.d(TAG, "Bearing value case 4: " + angle) ;
        }


        return angle ;
    }

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

    private LatLng getCenter(LatLng start, LatLng end) {
        return new LatLng((start.latitude + end.latitude)/2, (start.longitude + end.longitude)/2) ;
    }

    private  void testCase4() { // check
        mMap.addMarker(new MarkerOptions()
                .position(southWest)
                .title("")) ;

        mMap.addMarker(new MarkerOptions()
                .position(northEast)
                .title("")) ;

        Log.d(TAG, "North is great than south: " + (northEast.latitude > southWest.latitude)) ; // true
        Log.d(TAG, "East is greater than west: " + (northEast.longitude > southWest.longitude)) ; // true

        hopefulCenter = getCenter(northEast, southWest) ;
        hallwayLength = (float) getHallwayLength(northEast, southWest) ;
        angle = (float) getAngle(northEast, southWest) ;

        GroundOverlayOptions hallway = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue))
                .position(hopefulCenter, 1, hallwayLength)
                .bearing(angle);

        mMap.addGroundOverlay(hallway) ;
        moveCamera(hopefulCenter, 15);
    }

    private  void testCase2() { // check
        mMap.addMarker(new MarkerOptions()
                .position(southEast)
                .title("")) ;

        mMap.addMarker(new MarkerOptions()
                .position(northWest)
                .title("")) ;

        hopefulCenter = getCenter(southEast, northWest) ;
        hallwayLength = (float) getHallwayLength(southEast, northWest) ;
        angle = (float) getAngle(southEast, northWest) ;

        GroundOverlayOptions hallway = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue))
                .position(hopefulCenter, 1, hallwayLength)
                .bearing(angle);

        mMap.addGroundOverlay(hallway) ;
        moveCamera(hopefulCenter, 15);
    }

    private  void testCase3() { // check
        mMap.addMarker(new MarkerOptions()
                .position(southEast)
                .title("")) ;

        mMap.addMarker(new MarkerOptions()
                .position(northWest)
                .title("")) ;

        hopefulCenter = getCenter(southEast, northWest) ;
        hallwayLength = (float) getHallwayLength(southEast, northWest) ;
        angle = (float) getAngle(northWest, southEast) ;

        GroundOverlayOptions hallway = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.blue))
                .position(hopefulCenter, 1, hallwayLength)
                .bearing(angle);

        mMap.addGroundOverlay(hallway) ;
        moveCamera(hopefulCenter, 15);
    }

}
