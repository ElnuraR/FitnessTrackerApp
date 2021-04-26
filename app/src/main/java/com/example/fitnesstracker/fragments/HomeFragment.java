package com.example.fitnesstracker.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fitnesstracker.MainActivity;
import com.example.fitnesstracker.R;
import com.example.fitnesstracker.db.Route;
import com.example.fitnesstracker.service.LocationService;
import com.example.fitnesstracker.viewModel.RouteViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    static final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    static final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

    private Button startButton;
    private Button finishButton;
    private TextView length, speed;
    private Chronometer chronometer;

    private GoogleMap map;
    private Marker marker;
    private RouteViewModel routeViewModel;
    private ArrayList<LatLng> arrayList;
    private LatLng latLng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }

        startButton = view.findViewById(R.id.button_start);
        finishButton = view.findViewById(R.id.button_finish);
        length = view.findViewById(R.id.text_runLength);
        speed = view.findViewById(R.id.text_runSpeed);
        chronometer = view.findViewById(R.id.chronometer_time);

        finishButton.setEnabled(false);
        length.setText(R.string.km);
        speed.setText(R.string.km_h);

        arrayList = new ArrayList<LatLng>();
        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                LatLng bishkek = new LatLng(42.871318, 74.568512);

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                    marker = map.addMarker(new MarkerOptions().position(bishkek));
                }
                map.moveCamera(CameraUpdateFactory.newLatLng(bishkek));

                LocationService.locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            double latitude = locationResult.getLastLocation().getLatitude();
                            double longitude = locationResult.getLastLocation().getLongitude();

                            latLng = new LatLng(latitude, longitude);
                            arrayList.add(latLng);
                            polyline();
                        }
                    }
                };
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    startButton.setEnabled(false);
                    finishButton.setEnabled(true);

                    arrayList.clear();
                    startLocationService();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                stopLocationService();
                float currentTime = (SystemClock.elapsedRealtime() - chronometer.getBase());

                @SuppressLint("DefaultLocale") final String routeLength = String.format("%.2f км", calculateLength(arrayList));
                @SuppressLint("DefaultLocale") final String routeSpeed = String.format("%.2f км/ч", calculateSpeed(currentTime));

                final String routeTime = makeReadable((int) currentTime);
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
                speed.setText(routeSpeed);
                length.setText(routeLength);

                try {
                    Route routes = new Route(routeLength, routeSpeed, routeTime);
                    routeViewModel.insert(routes);
                } catch (NullPointerException e) {

                }

                startButton.setEnabled(true);
                finishButton.setEnabled(false);

                length.setText(R.string.km);
                speed.setText(R.string.km_h);
            }
        });

        return view;
    }

    private float calculateSpeed(float timeInMillisecToHours) {
        float HH = ((timeInMillisecToHours / (1000 * 60 * 60)) % 24);
        return calculateLength(arrayList) / HH;
    }

    private static String makeReadable(int milliseconds) {
        int SS = (milliseconds / 1000) % 60;
        int MM = ((milliseconds / (1000 * 60)) % 60);
        int HH = ((milliseconds / (1000 * 60 * 60)) % 24);

        return String.format(Locale.getDefault(), "%d:%02d:%02d", HH, MM, SS);
    }

    @SuppressLint("DefaultLocale")
    private static float calculateLength(ArrayList<LatLng> points) {
        float tempTotalDistance = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            LatLng pointA = points.get(i);
            LatLng pointB = points.get(i + 1);
            float[] results = new float[3];
            Location.distanceBetween(pointA.latitude, pointA.longitude, pointB.latitude, pointB.longitude, results);
            tempTotalDistance += results[0];
        }
        return tempTotalDistance / 1000;
    }

    private void polyline() {
        map.clear();
        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.RED).geodesic(true);
        for (int i = 0; i < arrayList.size(); i++) {
            LatLng point = arrayList.get(i);
            polylineOptions.add(point);
        }
        map.addMarker(new MarkerOptions().position(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        map.addPolyline(polylineOptions);
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getActivity().getApplicationContext(), LocationService.class);
            intent.setAction(ACTION_START_LOCATION_SERVICE);
            getActivity().startService(intent);
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getActivity().getApplicationContext(), LocationService.class);
            intent.setAction(ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(intent);
        }
    }
}