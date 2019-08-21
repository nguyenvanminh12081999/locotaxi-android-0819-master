package com.suusoft.locoindia.utils.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suusoft.locoindia.globals.Constants;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MapsUtil {

    private static final String TAG = MapsUtil.class.getSimpleName();

    public static void getMyLocation(final GoogleApiClient googleApiClient, final IMaps iMaps) {
        // Create the LocationRequest object
        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                iMaps.processFinished(location);

                // Be sure to remove the updates if you only need one location without constant monitoring
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        });
    }

    public static void getMyPlace(final GoogleApiClient googleApiClient, final IMaps iMaps) {
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(googleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                Place place = null;
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    // If you don't  freeze place object then you can't use it anymore
                    place = placeLikelihood.getPlace().freeze();
                }
                iMaps.processFinished(place);

                likelyPlaces.release();
            }
        });
    }
    public static void moveCameraShowAllLatLng(GoogleMap googleMap, List<LatLng> markers){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (LatLng latLng : markers) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();

        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        googleMap.animateCamera(cu);
    }
    public static boolean locationIsEnable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    public static void moveCameraTo(GoogleMap googleMap, LatLng latLng, int zoomLevel) {
        if (googleMap != null && latLng != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }

    public static void moveCameraTo(GoogleMap googleMap, LatLng latLng) {
        if (googleMap != null && latLng != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    public static Marker addMarker(GoogleMap googleMap, LatLng latLng, String title, int icon, boolean draggable) {
        // create marker
        MarkerOptions marker = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(icon)).draggable(draggable);

        // adding marker
        if(marker ==null) {
            return null;
        }else
        return googleMap.addMarker(marker);
    }

    public static void clearMarkers(GoogleMap googleMap) {
        googleMap.clear();
    }

    public static void startAddressService(Context context, AddressResultReceiver resultReceiver, LatLng latLng) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, latLng);
        context.startService(intent);
    }

    public static void displayLocationSettingsRequest(final AppCompatActivity activity, final int reqCode) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(activity, reqCode);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    public static void getAutoCompletePlaces(Activity activity, int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity);
            activity.startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // Handle the error.
        }
    }

    public static String getAddressFromLatLng(Context context, LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size()>0)
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;

    }
    public static void getAutoCompletePlaces(Fragment fragment, int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(fragment.getActivity());
            fragment.startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // Handle the error.
        }
    }


    public static double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####.#");
        double kmInDec = Double.parseDouble(newFormat.format(km));
        double meter = valueResult % 1000;
        double meterInDec = Double.parseDouble(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return kmInDec;
    }
}
