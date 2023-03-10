package com.example.qrhunter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener {

    private GoogleMap mMap;
    private Button addQRButton;
    private Button backButton;
    private Button myLocationButton;
    private ToggleButton followLocationButton;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean FollowUserLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Create a new SupportMapFragment
        SupportMapFragment mapFragment = new SupportMapFragment();

        // Replace the RelativeLayout with the SupportMapFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        // Back Button
        // Goes back to Profile
        backButton = findViewById(R.id.map_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set the callback for when the map is ready
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSIONS_REQUEST_LOCATION);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        myLocationButton = findViewById(R.id.map_locate_button);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                                }
                            }
                        });
            }
        });
        followLocationButton = findViewById(R.id.map_follow_button);
        followLocationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FollowUserLocation = true;
                } else {
                    FollowUserLocation = false;
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        // Add QR Button implementation + onClick
        // TODO Add Camera activity
//        addQRButton = findViewById(R.id.map_add_button);
//
//        addQRButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        // TODO Add QR code marker on map based on its location from db
        // All QR codes should be marked on the map and the user should be able to click on them
        // By clicking the marker, user should be navigated to the QR code's page



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
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        // move camera to current location when map is ready
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        //  ove the camera to the user's location only if FollowUserLocation is false:
                        if (location != null) {
                            // Logic to handle location object
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                        }
                    }
                });
    }
    @Override
    public void onLocationChanged(Location location) {
        // Update the map camera to the new location
        if (FollowUserLocation) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }

}