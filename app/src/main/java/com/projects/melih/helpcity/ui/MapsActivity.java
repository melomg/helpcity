package com.projects.melih.helpcity.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.firebase.ui.auth.AuthUI;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.projects.melih.helpcity.DataManager;
import com.projects.melih.helpcity.R;
import com.projects.melih.helpcity.common.ResourcesUtil;
import com.projects.melih.helpcity.model.Vote;
import com.projects.melih.helpcity.ui.base.BaseActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by melih on 30.11.2017
 */

@RuntimePermissions
public class MapsActivity extends BaseActivity implements View.OnTouchListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    private static final float DEFAULT_ZOOM = 18f;
    private static final long ANIMATION_DURATION = 400;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String BUNDLE_LOCATION = "BUNDLE_LOCATION";

    private ImageButton buttonFindLocation;
    private MapFragment mapFragment;
    private PlaceAutocompleteFragment autocompleteFragment;
    private View layoutEmojis;
    private View emoji1;
    private View emoji2;
    private View emoji3;
    private View emoji4;
    private View emoji5;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private Location lastKnownLocation;
    private Location currentLocation;

    public static Intent newIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        autocompleteFragment = (PlaceAutocompleteFragment) fragmentManager.findFragmentById(R.id.place_autocomplete_fragment);
        buttonFindLocation = findViewById(R.id.findLocation);
        layoutEmojis = findViewById(R.id.layoutEmojis);
        snackView = findViewById(R.id.container);
        emoji1 = findViewById(R.id.emoji1);
        emoji2 = findViewById(R.id.emoji2);
        emoji3 = findViewById(R.id.emoji3);
        emoji4 = findViewById(R.id.emoji4);
        emoji5 = findViewById(R.id.emoji5);

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
        emoji1.setOnTouchListener(this);
        emoji2.setOnTouchListener(this);
        emoji3.setOnTouchListener(this);
        emoji4.setOnTouchListener(this);
        emoji5.setOnTouchListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout: {
                logout();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rating;
        switch (v.getId()) {
            case R.id.emoji1: {
                rating = Vote.VoteType.EXTREME_SAD;
                animateViewWhenPressed(emoji1);
                break;
            }
            case R.id.emoji2: {
                rating = Vote.VoteType.SAD;
                animateViewWhenPressed(emoji2);
                break;
            }
            case R.id.emoji3: {
                rating = Vote.VoteType.NORMAL;
                animateViewWhenPressed(emoji3);
                break;
            }
            case R.id.emoji4: {
                rating = Vote.VoteType.HAPPY;
                animateViewWhenPressed(emoji4);
                break;
            }
            case R.id.emoji5: {
                rating = Vote.VoteType.EXTREME_HAPPY;
                animateViewWhenPressed(emoji5);
                break;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            makeViewGoneWithAnim(layoutEmojis);
            //TODO take location, rating and send to server
        }
        return true;
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
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnCameraMoveListener(this);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        makeViewVisibleWithAnim(layoutEmojis);
        return false;
    }

    @Override
    public void onCameraMove() {
        makeViewGoneWithAnim(layoutEmojis);
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

    private void animateAndPutMarker(@NonNull LatLng latLng, @NonNull String title, @NonNull String snippet) {
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        Bitmap bitmap = ResourcesUtil.getBitmap(MapsActivity.this, R.drawable.vote_focussed);
        if (bitmap != null) {
            googleMap.addMarker(new MarkerOptions()
                    .title(title)
                    .snippet(snippet)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .position(latLng));
        }
    }

    @NonNull
    private FusedLocationProviderClient getFusedLocationProviderClient() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        }
        return fusedLocationProviderClient;
    }

    private void makeViewVisibleWithAnim(@NonNull View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0.0f);

            scaleView(view, .5f, 1f);

            view.animate()
                    .alpha(1.0f)
                    .setDuration(ANIMATION_DURATION);
        }
    }

    private void makeViewGoneWithAnim(@NonNull final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.animate()
                    .alpha(0.0f)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.animate().setListener(null);
                            view.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void scaleView(@NonNull View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }

    private void animateViewWhenPressed(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setDuration(200);
        scaleAnimation.setInterpolator(new OvershootInterpolator(6f));
        view.startAnimation(scaleAnimation);

        scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.5);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setDuration(200);
        view.startAnimation(scaleAnimation);
    }

    private void logout() {
        new AlertDialog.Builder(MapsActivity.this).setMessage(R.string.are_you_sure_to_logout)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthUI.getInstance().signOut(MapsActivity.this);
                        startActivity(LoginActivity.newIntent(MapsActivity.this));
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}