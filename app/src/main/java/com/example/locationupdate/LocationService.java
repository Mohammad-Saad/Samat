package com.example.locationupdate;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.locationupdate.db.Realm$Helper;
import com.example.locationupdate.shared_pref.SaveInSharedPreference;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    //private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000;
    //private int DelayAfterSilent = 60*1000; // 1 MINUTE
    private int DelayAfterSilent = 600*1000; //10 minutes
    int count = 0;

    List<FilterData> datafromDB;
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {


            for (Location location : locationResult.getLocations()) {
                //Log.e("MainActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

                if (SaveInSharedPreference.getInSharedPreference(LocationService.this).getLat() == 0.0 && SaveInSharedPreference.getInSharedPreference(LocationService.this).getLng() == 0.0) {
                    SaveInSharedPreference.getInSharedPreference(LocationService.this).setLatLong(location.getLatitude(), location.getLongitude());
                }
                SaveInSharedPreference.getInSharedPreference(LocationService.this).setCurrentLatLong(location.getLatitude(), location.getLongitude());

                count = count + 1;

                Log.e("mLocationCallbackLat", String.valueOf(location.getLatitude()));
                Log.e("mLocationCallbackLong", String.valueOf(location.getLongitude()));
                LocationMatch(location);
            }

           /* datafromDB = Realm$Helper.getLocation$Module(LocationService.this).getAllData();

            for (FilterData str : datafromDB) {

                String name = str.getName();

            }*/
        };

    };


    public void LocationMatch(Location location) {
        final Location loc1 = new Location("");
        final Location loc2 = new Location("");
        //final double SilentDistance = 300;

        final double SilentDistance = SaveInSharedPreference.getInSharedPreference(getApplicationContext()).getPrefDistance();
        Log.e("SilentDistanceValue" , " " + SilentDistance);
        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final int previousAudioState = am.getRingerMode();

        if(location.getLongitude() != 0.0 && location.getLongitude() != 0.0 )

        {
            datafromDB = Realm$Helper.getLocation$Module(LocationService.this).getAllData();

            for (FilterData str : datafromDB) {

                String name = str.getName();

                loc1.setLatitude(location.getLatitude());
                loc1.setLongitude(location.getLongitude());

                loc2.setLatitude(str.getLat());
                loc2.setLongitude(str.getlng());

                double distanceInMeters = loc1.distanceTo(loc2);
                /*Log.e("Name", name);
                Log.e("Distance", "" + distanceInMeters);*/

                if (distanceInMeters <= SilentDistance) {
                    try {
                        if (am.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
                            // Silent Phone
                            Log.e ("Match", "Silent the Phone");
                            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            onPause();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onResume();
                                    am.setRingerMode(previousAudioState);
                                }
                            }, DelayAfterSilent); //10 minutes

                            break;
                            // Silent Phone
                        } else {
                            // Remain Same
                            //Do Something
                        }

                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

    private void backgroundServiceCall() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(getResources().getColor(R.color.colorPrimaryDark))
                        .setContentTitle("Location Update Service")
                        .setContentText("Updating the location in background while BusyBee driver is online").build();
            } else {
                notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("")
                        .setContentText("").build();
            }

            startForeground(1, notification);
        }
    }
    @Override
    public void onCreate() {

        try {
            /*IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            BroadcastReceiver mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);
*/
            backgroundServiceCall();
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            requestLocationUpdates();
            Log.e("LocationServce", "OnCreate");
        }

        catch (Exception ex)
        {
            Log.e("ServiceOnCreate", ex.toString());
        }
        //onPause();
        //startLocationUpdates();
    }


    //@Override
    /*public void onStart(Intent intent, int startId) {
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {

            Log.e("ServiceScreenOff", "IF");
            requestLocationUpdates();
            // YOUR CODE
        } else {

            Log.e("ServiceScreenOff", "Else");
            backgroundServiceCall();
            // YOUR CODE
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e ("LocationServce", "ONdestroy");
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

    }

    public void onResume() {
        Log.e("OnResume", "OnResumeEvent");
        if (mFusedLocationClient != null) {
            requestLocationUpdates();
        }
    }

    public void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL); // two minute interval
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }


    public void onPause() {
        //super.onPause();
        Log.e("ONPause", "OnPauseEvent");
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }



    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {

        if (SaveInSharedPreference.getInSharedPreference(LocationService.this).getLat() == 0.0 && SaveInSharedPreference.getInSharedPreference(LocationService.this).getLng() == 0.0) {
            SaveInSharedPreference.getInSharedPreference(LocationService.this).setLatLong(location.getLatitude(), location.getLongitude());
        }
        SaveInSharedPreference.getInSharedPreference(LocationService.this).setCurrentLatLong(location.getLatitude(), location.getLongitude());

        Toast.makeText(this,"onLocationChanged",Toast.LENGTH_LONG);
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        Log.e ("onLocationChangedLat", String.valueOf(location.getLatitude()));
        Log.e ("onLocationChangedLong", String.valueOf(location.getLongitude()));
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


            /*loc1.setLatitude(location.getLatitude());
            loc1.setLongitude(location.getLongitude());

            loc2.setLatitude(str.getLat());
            loc2.setLongitude(str.getlng());

            double distanceInMeters = loc1.distanceTo(loc2);
            Log.e("Name", name);
            Log.e("Distance", "" + distanceInMeters);*/
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
