package com.projects.melih.helpcity.ui;

import android.Manifest;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projects.melih.helpcity.DataManager;
import com.projects.melih.helpcity.R;
import com.projects.melih.helpcity.ui.base.BaseActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by melih on 30.11.2017
 */

@RuntimePermissions
public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 18f;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String BUNDLE_LOCATION = "BUNDLE_LOCATION";

    private ImageButton buttonFindLocation;
    private MapFragment mapFragment;
    private PlaceAutocompleteFragment autocompleteFragment;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private Location lastKnownLocation;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        final FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        autocompleteFragment = (PlaceAutocompleteFragment) fragmentManager.findFragmentById(R.id.place_autocomplete_fragment);
        buttonFindLocation = findViewById(R.id.vote);
        snackView = findViewById(R.id.container);

        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(BUNDLE_LOCATION);
        }
        mapFragment.getMapAsync(this);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (googleMap != null) {
                    final LatLng latLng = place.getLatLng();
                    animateAndPutMarker(latLng, "", "");
                }
            }

            @Override
            public void onError(Status status) {
                showSnackBar(getString(R.string.error_get_location));
            }
        });
        buttonFindLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivityPermissionsDispatcher.getDeviceLocationWithPermissionCheck(MapsActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                MapsActivityPermissionsDispatcher.getDeviceLocationWithPermissionCheck(MapsActivity.this);
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(BUNDLE_LOCATION, lastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        buttonFindLocation.setVisibility(View.VISIBLE);
        if (googleMap != null) {
            UiSettings settings = googleMap.getUiSettings();
            settings.setZoomControlsEnabled(true);
            settings.setCompassEnabled(true);
            settings.setZoomGesturesEnabled(true);

            if (currentLocation != null) {
                animateAndPutMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), "", "");
            }
            //googleMap.setOnMarkerClickListener(this);
        }
    }

    @Override
    public void onDestroy() {
        getFragmentManager().beginTransaction()
                .remove(mapFragment)
                .commitAllowingStateLoss();
        getFragmentManager().beginTransaction()
                .remove(autocompleteFragment)
                .commitAllowingStateLoss();
        super.onDestroy();
    }

    @SuppressWarnings("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void getDeviceLocation() {
        updateLocationUI();

        try {
            Task<Location> locationResult = getFusedLocationProviderClient().getLastLocation();
            locationResult.addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        lastKnownLocation = (Location) task.getResult();
                    }
                    if (lastKnownLocation != null) {
                        DataManager.getInstance(MapsActivity.this).saveLastLocation(lastKnownLocation);
                    } else {
                        lastKnownLocation = DataManager.getInstance(MapsActivity.this).getLastLocation();
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                    if (lastKnownLocation != null) {
                        animateAndPutMarker(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), "", "");
                    }
                }
            });
        } catch (SecurityException e) {
            showSnackBar(getString(R.string.error_get_location));
        }
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAskLocation() {
        if (!isFinishing()) {
            new android.support.v7.app.AlertDialog.Builder(MapsActivity.this)
                    .setTitle(R.string.permission_location_title)
                    .setMessage(R.string.permission_location_message)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent androidSetting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(androidSetting, REQUEST_CHECK_SETTINGS);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .setCancelable(true)
                    .show();
        }
    }

    //TODO remove this and use your own button for this implementation
    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            UiSettings settings = googleMap.getUiSettings();
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                settings.setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                settings.setMyLocationButtonEnabled(false);
                //lastKnownLocation = null;
                //getLocationPermission();
            }
        } catch (SecurityException e) {
            showSnackBar(getString(R.string.error_get_location));
        }
    }

    //TODO change marker icon
    private void animateAndPutMarker(@NonNull LatLng latLng, @NonNull String title, @NonNull String snippet) {
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        googleMap.addMarker(new MarkerOptions()
                .title(title)
                .snippet(snippet)
                .position(latLng));
    }

    @NonNull
    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        }
        return fusedLocationProviderClient;
    }
}