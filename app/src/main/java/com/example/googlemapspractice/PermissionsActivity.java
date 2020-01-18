package com.example.googlemapspractice;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.googlemapspractice.MapsActivity;
import com.example.googlemapspractice.PermissionsAdapter;
import com.example.googlemapspractice.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import javax.net.ssl.ManagerFactoryParameters;

public class PermissionsActivity extends AppCompatActivity {

    private static final String TAG = "PermissionsActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final int LOCATION_PERMISSION_CODE = 1 ;

    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1 ;

    private  static final int READ_EXTERNAL_CODE = 1 ;

    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE} ;

    private boolean[] permissionsEnabled ;

    private ListView permissionsCheckList ;

    private PermissionsAdapter permissionsAdapter ;

    private boolean permissionGuard ;



    /*
    * this generates the list and its functionality for diaplying which permissions need to be turned on
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        permissionsEnabled = checkEnabledPermissions() ;

        permissionsCheckList = (ListView) findViewById(R.id.permissionsCheckList) ;
        permissionsAdapter = new PermissionsAdapter(this, getResources().getStringArray(R.array.permissions), permissionsEnabled) ;
        permissionsCheckList.setAdapter(permissionsAdapter);

        permissionsCheckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityCompat.requestPermissions(PermissionsActivity.this, permissions, LOCATION_PERMISSION_CODE);
                ActivityCompat.requestPermissions(PermissionsActivity.this, permissions, EXTERNAL_STORAGE_PERMISSION_CODE);
                ActivityCompat.requestPermissions(PermissionsActivity.this, permissions, READ_EXTERNAL_CODE);

            }
        });

        if (isServicesOK()) {
            init() ;
        }
            //Toast.makeText(this, "All permissions must be enabled", Toast.LENGTH_LONG).show(); ;


    }

    /*This creates the functionality for the button to begin tracking the user */
    private void init () {
        Button buttonMap = (Button) findViewById(R.id.btnMap) ;
        buttonMap.setText("Start tracking") ;
        if (allPermissionEnabled()) {
            buttonMap.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PermissionsActivity.this, FloorPlanMaker1.class);
                    startActivity(intent);
                }

            });
        } else {
            Toast.makeText(this, "All permissions must be enabled", Toast.LENGTH_LONG).show(); ;
        }
    }

    /*this function checks if the app can reach google play services */
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PermissionsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google play servieces is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "Error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PermissionsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /*This loops through the permissions array and updates teh endabledPermissions array on which are enabled */
    private boolean[] checkEnabledPermissions() {
        boolean[] enabledPermissions = new boolean[permissions.length] ;

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug", permissions[i] + " came true") ;
                enabledPermissions[i] = true ;
            } else {
                Log.d("works", permissions[i] + " came false") ;
                enabledPermissions[i] = false ;
            }
        }

        return enabledPermissions ;
    }
    /*
    * This returns true if all the permissions are enabled and false otherwise
    * */
    private boolean allPermissionEnabled() {

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                return false ;
            }
        }
        return true ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsEnabled = checkEnabledPermissions() ;
        permissionsAdapter = new PermissionsAdapter(this, getResources().getStringArray(R.array.permissions), permissionsEnabled) ;
        permissionsCheckList.setAdapter(permissionsAdapter);

        if (isServicesOK() && allPermissionEnabled()) {
            init() ;
        }
    }
}
