package com.example.googlemapspractice;

import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/*
* This is an object that is based off of the ground overlay class.
* It calculates its angle and the locations of its corners.
* */
public class Hallway {
    private static final String TAG = "HallwayTesting";
    public final GroundOverlayOptions display ;
    public final LatLng[] boundaries ;
    public final LatLng center ;
    public final LatLng upperBound, lowerBound, leftBound, rightBound ;
    public final LatLng upperRight, upperLeft, lowerRight, lowerLeft ;
    public final float hallwayLength, hallwayWidth ;
    public float bearing, oppositeAngle ;

    /*
    * This constructor takes the start and end of the hall and
    * also the width.
    *
    * From there, it calculates its length as well as the locations of its
    * four corners.
    *
    * */
    Hallway(LatLng start, LatLng end, float hallwayWidth) {

        this.hallwayWidth = hallwayWidth ;
        center = new LatLng((end.latitude + start.latitude)/2, (end.longitude + start.longitude)/2) ;
        hallwayLength = (float) getHallwayLength(start, end);
        bearing = getAngle(start, end) ;

        upperBound = new LatLng(end.latitude, end.longitude) ;
        lowerBound = new LatLng(start.latitude, start.longitude) ;

        leftBound = getOverlayBoundaries(center, hallwayWidth/2, 90 + bearing) ;
        rightBound = getOverlayBoundaries(center, hallwayWidth/2, 270 + bearing) ;

        upperRight = getOverlayBoundaries(upperBound, hallwayWidth/2, 90 + bearing) ;
        upperLeft = getOverlayBoundaries(upperBound, hallwayWidth/2, 270 + bearing) ;
        lowerRight = getOverlayBoundaries(lowerBound, hallwayWidth/2, 90 + bearing) ;
        lowerLeft = getOverlayBoundaries(lowerBound, hallwayWidth/2, 270 + bearing) ;

        if (bearing >= 180) {
            oppositeAngle = bearing - 180 ;
        } else {
            oppositeAngle = bearing + 180 ;
        }

        boundaries = new LatLng[9] ;
        boundaries[0] = center ;
        boundaries[1] = lowerBound ;
        boundaries[2] = upperBound ;
        boundaries[3] = rightBound ;
        boundaries[4] = leftBound ;
        boundaries[5] = upperRight ;
        boundaries[6] = upperLeft;
        boundaries[7] = lowerRight ;
        boundaries[8] = lowerLeft ;



        if (start.latitude == end.latitude && start.longitude == end.longitude) {
            display = new GroundOverlayOptions()
                    .visible(false)
                    .position(start, 0, 0)
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.blue));
        } else {
            display = new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.blue))
                    .position(center, hallwayWidth, hallwayLength)
                    .bearing(bearing);
        }

    }


    private static double toRad(double x) {
        return (x * Math.PI / 180) ;
    }

    private static double toDeg(double x) {
        return (x * 180 / Math.PI) ;
    }

    /*
    * This function takes a point, a distance and an angle and then calculates the location
    * of a point that is as far as the distance specifies.
    *
    * */
    private LatLng getOverlayBoundaries(LatLng center, double distance, double bearing) {
        //width and height are in meters
        double lat1 = toRad(center.latitude), lon1 = toRad(center.longitude) ;
        double lat2, lon2 ;

        distance = distance / 6371000 ;

        bearing = toRad(bearing) ;

        lat2 = Math.asin(Math.sin(lat1)*Math.cos(distance) +
                Math.cos(lat1) * Math.sin(distance) * Math.cos(bearing)) ;

        lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(distance) * Math.cos(lat1),
                Math.cos(distance) - Math.sin(lat1) * Math.sin(lat2));

        return new LatLng(toDeg(lat2), toDeg(lon2));

    }

    /*
    * This function uses the haversine equation to calculate distances between points on the earth.
    *It takes two points as parameters and returns the distance between them in meters.
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

    /*
    * This function takes the start and end point of a hallway and calulates the angle between them.
    * It returns the angle as a bearing (clockwise from north) in degrees
    * */
    private float getAngle(LatLng start, LatLng end) {
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


        return ((float) angle) ;
    }
}