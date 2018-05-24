package com.example.user.Knowhere;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class MapFragment extends Fragment implements LoaderCallbacks<Cursor>, OnConnectionFailedListener {

    private MapFragment mapFragment;
    MapView mMapView;
    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    Place place;
    String s;


    public MapFragment() {

    }


    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private StreetViewPanoramaView mStreetViewPanoramaView;
    private LatLng sydney = new LatLng(-33.8767308, 151.2097581);


    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        getLocationPermission();


        mMapView.onResume();
        getLoaderManager().initLoader(0, null, this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity().getApplicationContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap mMap) {


                googleMap = mMap;


                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(final Marker marker) {
                        Log.d(TAG, "onInfoWindowClick: ");
                        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
                        options.position(marker.getPosition());
                        mStreetViewPanoramaView = new StreetViewPanoramaView(getActivity().getApplicationContext(), options);
                        ViewGroup vg = (ViewGroup) rootView;
                        final Button btok = new Button(getActivity().getApplicationContext());
                        btok.setText("HIDE");
                        ;
                        btok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mStreetViewPanoramaView.animate().translationY(50000).setDuration(3000);
                                ((ViewGroup) rootView).removeView(btok);
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ViewGroup) rootView).removeView(mStreetViewPanoramaView);
                                    }
                                };
                                handler.postDelayed(runnable, 1000);
                            }
                        });

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.END | Gravity.BOTTOM;
                        vg.setLayoutParams(params);
                        vg.addView(mStreetViewPanoramaView);
                        mStreetViewPanoramaView.onCreate(savedInstanceState);
                        vg.addView(btok, params);
                    }
                });


                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                getDeviceLocation();




                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng point) {
                        Log.d(TAG, "onInfoWindowClick: ");
                        StreetViewPanoramaOptions options = new StreetViewPanoramaOptions();
                        options.position(point);
                        mStreetViewPanoramaView = new StreetViewPanoramaView(getActivity().getApplicationContext(), options);
                        ViewGroup vg = (ViewGroup) rootView;
                        final Button btok = new Button(getActivity().getApplicationContext());
                        final Button btadd = new Button(getActivity().getApplicationContext());
                        btok.setText("HIDE");
                        btadd.setText("ADD TO BASE");
                        ;
                        btok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mStreetViewPanoramaView.animate().translationY(50000).setDuration(3000);
                                ((ViewGroup) rootView).removeView(btok);
                                ((ViewGroup) rootView).removeView(btadd);
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ViewGroup) rootView).removeView(mStreetViewPanoramaView);
                                    }
                                };
                                handler.postDelayed(runnable, 1500);
                            }
                        });

                        btadd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mStreetViewPanoramaView.animate().translationY(50000).setDuration(3000);
                                ((ViewGroup) rootView).removeView(btok);
                                ((ViewGroup) rootView).removeView(btadd);
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        drawMarker(point);

                                        ContentValues contentValues = new ContentValues();


                                        contentValues.put(LocationsDB.FIELD_LAT, point.latitude);


                                        contentValues.put(LocationsDB.FIELD_LNG, point.longitude);


                                        contentValues.put(LocationsDB.FIELD_ZOOM, googleMap.getCameraPosition().zoom);


                                        contentValues.put(LocationsDB.FIELD_ADDRESS, getFormattedAddress(point));


                                        LocationInsertTask insertTask = new LocationInsertTask();


                                        insertTask.execute(contentValues);

                                        Toast.makeText(getActivity().getApplicationContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
                                        ((ViewGroup) rootView).removeView(mStreetViewPanoramaView);
                                    }
                                };
                                handler.postDelayed(runnable, 1500);
                            }
                        });

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.END | Gravity.BOTTOM;
                        params1.gravity = Gravity.START | Gravity.BOTTOM;
                        vg.setLayoutParams(params);
                        vg.addView(mStreetViewPanoramaView);
                        mStreetViewPanoramaView.onCreate(savedInstanceState);
                        vg.addView(btok, params);
                        vg.addView(btadd, params1);
                    }
                });

            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    protected String getAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        String fullAddress = null;
        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.forLanguageTag("RU"));
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String area = addresses.get(0).getLocality();
            String city = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalcode = addresses.get(0).getPostalCode();

            fullAddress = address + ", " + area + " , \n" + city + ", " + country + ", " + postalcode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullAddress;
    }

    protected String getFormattedAddress(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        String fullAddress = null;
        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.forLanguageTag("RU"));
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);

            fullAddress = address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullAddress;
    }


    private void drawMarker(LatLng point) {

        MarkerOptions markerOptions = new MarkerOptions();


        markerOptions.position(point);

        markerOptions.title(getFormattedAddress(point));
        


        googleMap.addMarker(markerOptions);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {


            getActivity().getApplicationContext().getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {


            getActivity().getApplicationContext().getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0,
                                         Bundle arg1) {


        Uri uri = LocationsContentProvider.CONTENT_URI;


        return new CursorLoader(getActivity().getApplicationContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0,
                               Cursor arg1) {
        int locationCount = 0;
        double lat = 0;
        double lng = 0;
        float zoom = 0;


        locationCount = arg1.getCount();

        arg1.moveToFirst();

        for (int i = 0; i < locationCount; i++) {


            lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LAT));


            lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LNG));


            zoom = arg1.getFloat(arg1.getColumnIndex(LocationsDB.FIELD_ZOOM));


            LatLng location = new LatLng(lat, lng);


            drawMarker(location);


            arg1.moveToNext();
        }

        if (locationCount > 0) {

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));


            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    private String getResponse(String urlString) {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            urlConnection.setReadTimeout(5000);
            urlConnection.connect();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder s = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                s.append(line + "\n");
            }
            return s.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}