package com.example.homework1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.homework1.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private Boolean isRecording = false;
    private List<Location> locations = new ArrayList<Location>();
    private Location lastLocation = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding
                .inflate(inflater, container, false);

        locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> {
            if (ActivityCompat.checkSelfPermission(
                    getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                                getActivity(),
                                "Access to storage denied!",
                                Toast.LENGTH_SHORT)
                        .show();

                return;
            }

            binding.buttonFirst.setEnabled(false);
            if (!isRecording) {
                locations.clear();
                if (lastLocation != null)
                    locations.add(lastLocation);
                isRecording = true;
                binding.buttonFirst.setText("Stop Recording");
            }
            else {
                isRecording = false;
                if (GeolocationSaver.savePath(getActivity(), locations))
                    Toast.makeText(
                        getActivity(),
                        "Path successfully saved!",
                        Toast.LENGTH_SHORT)
                            .show();
                else
                    Toast.makeText(
                        getActivity(),
                        "Path saving failed!",
                        Toast.LENGTH_SHORT)
                            .show();
                locations.clear();
                binding.buttonFirst.setText("Start Recording");
            }
            binding.buttonFirst.setEnabled(true);
        });

        if (!isGpsEnabled()) {
            Toast.makeText(
                    getActivity(),
                    "GPS is OFF! Use Settings to turn it on",
                    Toast.LENGTH_SHORT)
                 .show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        if (ActivityCompat.checkSelfPermission(
                getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }

        locationListener = new MyLocationListener();
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    1,
                    locationListener);
        }
        catch (SecurityException ex) {
            Toast.makeText(
                            getActivity(),
                            "Access to location denied!",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            lastLocation = loc;
            if (isRecording)
                locations.add(lastLocation);
            String longitude = String.format(
                    "Long.: % .2f",
                    loc.getLongitude());
            String latitude = String.format(
                    "Lat.: % .2f",
                    loc.getLatitude());
            binding.textviewFirst.setText(longitude+"\n"+latitude);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}