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
import android.widget.EditText;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        LocationListener {

    private GoogleMap mMap;
    private Button addQRButton;
    private Button backButton;
    private Button myLocationButton;
    private Button searchButton;
    private Button searchGoButton;
    private EditText searchBar;
    private ToggleButton followLocationButton;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager locationManager;
    private Location currentLocation;
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private boolean FollowUserLocation = false;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Radius of the visible area in meters
    private int visibleRadius = 200;

    // List of markers
    private List<Marker> markerList = new ArrayList<Marker>();


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

        // Set the callback for when the map is ready
        mapFragment.getMapAsync(this);

        // Back Button
        // Goes back to Profile
        backButton = findViewById(R.id.map_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



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


        // TODO add functionality to the locate button to reset rotate

        // TODO add functionality that by clicking on markers, users should be navigated to the QR code page

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     * @param googleMap
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
                        // Move the camera to the user's location only if FollowUserLocation is false:
                        if (location != null) {
                            // Logic to handle location object
                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                        }
                    }
                });

        // Retrieve the QR codes from the database
        db.collection("QrCodes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {

                            QRCode qrCode = create_QR_Object(d);

                            // Mark the QR code on the map if it's within the range of visibility
                            mFusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                                        @Override
                                        public void onSuccess(Location location) {
                                            // Got last known location. In some rare situations this can be null.
                                            if (location != null) {
                                                currentLocation = location;
                                                Double distance = distance(
                                                        currentLocation.getLatitude(),
                                                        currentLocation.getLongitude(),
                                                        qrCode.getLocation().getLatitude(),
                                                        qrCode.getLocation().getLongitude());
                                                if (distance <= visibleRadius) {
                                                    LatLng qrLocation = new LatLng(qrCode.getLocation().getLatitude(), qrCode.getLocation().getLongitude());
                                                    Marker marker = mMap.addMarker(new MarkerOptions().position(qrLocation).title(qrCode.getName()));
                                                    markerList.add(marker);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    @Override
    public void onLocationChanged(Location location) {
        // Update the current location
        currentLocation = location;
        // Update the map camera to the new location
        if (FollowUserLocation) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
        updateMarkers(currentLocation);
    }

    /**
     * This method calculate the distance between two points, given their latitude and longitude
     * it uses the Haversine formula to calculate the distance between two points
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return
     */
    // Use the Haversine formula to calculate the distance between two points
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    /**
     * This method updates the markers on the map, removing the ones that are out of range
     * @param currentLocation
     */
    // Update the markers on the map, removing the ones that are out of range
    private void updateMarkers(Location currentLocation) {
        Iterator<Marker> iterator = markerList.iterator();
        while (iterator.hasNext()) {
            Marker marker = iterator.next();
            double distance = distance(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    marker.getPosition().latitude,
                    marker.getPosition().longitude);
            if (distance > visibleRadius) {
                // Remove marker from the map
                marker.remove();
                // Remove marker from the list
                iterator.remove();
            }
        }
    }

    /**
     * This method creates a QRCode object from a DocumentSnapshot retrieved from the database
     * @param d the document snapshot retrieved from the database
     * @return the QRCode object created from the document snapshot
     */
    // Create a QRCode object from a DocumentSnapshot which is retrieved from the database
    private QRCode create_QR_Object(DocumentSnapshot d) {

        // Since attributes from db are longitude and latitude, we need to create a new Location object
        Location QrLocation = new Location("");
        QrLocation.setLatitude((Double)d.get("latitude"));
        QrLocation.setLongitude((Double)d.get("longitude"));

        // Create a new QR code object
        QRCode qrCode = new QRCode(
                d.get("hash").toString(),
                d.get("name").toString(),
                QrLocation,
                ((Long)d.get("score")).intValue());
        return qrCode;
    }
}