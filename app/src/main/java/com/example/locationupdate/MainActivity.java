package com.example.locationupdate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.locationupdate.db.Realm$Helper;
import com.example.locationupdate.shared_pref.SaveInSharedPreference;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {

    private Location location;
    //private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    List<FilterData> Filter;
    List<FilterData> datafilter;
    private Handler mHandler = new Handler();
    TextView textView;
    ListView listView;
    private Button button;
    String Changedistance;
    private int STORAGE_PERMISSION_CODE = 1;
    AlertDialog progressBar;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.edit_settings);

        progressBar = new SpotsDialog.Builder()
                .setContext(this)
                .build();

        //locationTv = findViewById(R.id.location);
        // we add permissions we need to request location of the users

        try {

            example = new ArrayList<>();
            Filter = new ArrayList<FilterData>();
            //textView = (TextView) findViewById(R.id.textView);
            listView = (ListView) findViewById(R.id.listView);

          /*  IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            BroadcastReceiver mReceiver = new ScreenReceiver();
            registerReceiver(mReceiver, filter);
*/
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                /*Toast.makeText(MainActivity.this, "You have already granted this permission!",
                        Toast.LENGTH_SHORT).show();
*/
                Intent startIntent = new Intent(this, LocationService.class);
                startService(startIntent);

                getdbData();


            } else {
                requestStoragePermission();
                statusCheck();
            }

            /*Intent startIntent = new Intent(this, LocationService.class);
            startService(startIntent);

            getdbData();*/
        } catch (Exception ex) {
            Log.e("OnCreateException", ex.toString());
        }


        /*permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        try {
            Thread.sleep(5000);
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this).
                    addApi(LocationServices.API).
                    addConnectionCallbacks(MainActivity.this).
                    addOnConnectionFailedListener(MainActivity.this).build();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        // we build google api client

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("OnDestroy", "OnDestroyEventwascalled");

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("You need this permission to get Masjid Locations")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();

                Intent startIntent = new Intent(this, LocationService.class);
                startService(startIntent);

                getdbData();

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    OkHttpClient client = new OkHttpClient();
    List<RootObject> example;

    private final Gson gson = new Gson();

  

    public void run(String token) throws Exception {
        try {
            Request request;
            Log.e("RUN1", "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + SaveInSharedPreference.getInSharedPreference(this).getLat() + "," + SaveInSharedPreference.getInSharedPreference(this).getLng() + "&radius=1000&type=mosque&key=");
            //if(token.equalsIgnoreCase("")) {
            request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + SaveInSharedPreference.getInSharedPreference(this).getLat() + "," + SaveInSharedPreference.getInSharedPreference(this).getLng() + "&radius=1000&type=mosque&key=")
                    .build();
            //}


            //ResponseBody responseBody = client.newCall(request).execute().body();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);


                    //RootObject entity = gson.fromJson(response.body().string(), RootObject.class);
                    /*List<Object> getdata = null;
                    getdata.add(gson.fromJson(response.body().charStream(), RootObject.class));
                    example.add(gson.fromJson(response.body().charStream(), RootObject.class));
*/
                    /*Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
*/
                    try {

                        //example.add(gson.fromJson(response.body().charStream(), RootObject.class));
                        String temp = response.body().string();
                        RootObject entity = gson.fromJson(temp, RootObject.class);
                        //gson.fromJson(response.toString(), RootObject.class);
                        //example = new ArrayList<>();
                        example.add(entity);
                        //gson.fromJson(response.toString(), RootObject.class);*/
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    // if (example.get(example.size() - 1).getNext_page_token() == null) {
                    for (int index = 0; index <= example.size() - 1; index++) {
                        for (int i = 0; i < example.get(index).getResults().size(); i++) {
                            Log.e("lat", "" + example.get(index).getResults().get(i).getGeometry().getLocation().getLat());
                            Log.e("long", "" + example.get(index).getResults().get(i).getGeometry().getLocation().getLng());
                            Log.e("Count", "" + i);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run2(String token) throws Exception {
        try {
            final Request request;
            //if(token.equalsIgnoreCase("")) {
            Log.e("2nd Url", "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + SaveInSharedPreference.getInSharedPreference(this).getLat() + "," + SaveInSharedPreference.getInSharedPreference(this).getLng() + "&radius=1000&type=mosque&key=w&pagetoken=" + token);

            request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + SaveInSharedPreference.getInSharedPreference(this).getLat() + "," + SaveInSharedPreference.getInSharedPreference(this).getLng() + "&radius=1000&type=mosque&key=w&pagetoken=" + token)
                    .build();
            //}

            /*else{
                request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + token + "&key=")
                        .build();
            }*/

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);


                    /*Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
*/
                        try {

                            Log.e("BK-201 URL: ", responseBody.charStream().toString());
                            String temp = response.body().string();
                            RootObject entity = gson.fromJson(temp, RootObject.class);
                            example.add(entity);
                        } catch (Exception ex) {
                            Log.e("Run2Exception", ex.toString());
                        }
                    }
                    //RootObject entity = gson.fromJson(temp, RootObject.class);
                    //gson.fromJson(response.toString(), RootObject.class);
                    //example = new ArrayList<>();
                    //example.add(entity);
                    //gson.fromJson(response.toString(), RootObject.class);

                    // if (example.get(example.size() - 1).getNext_page_token() == null) {
                    for (int index = 0; index <= example.size() - 1; index++) {
                        for (int i = 0; i < example.get(index).getResults().size(); i++) {
                            Log.e("lat", "" + example.get(index).getResults().get(i).getGeometry().getLocation().getLat());
                            Log.e("long", "" + example.get(index).getResults().get(i).getGeometry().getLocation().getLng());
                            Log.e("Count", "" + i);
                        }
                    }
                    // }

                    /*else {
                        Log.e("Token", "" + example.get(example.size() - 1).getNext_page_token());
                        try {
                            run(example.get(example.size() - 1).getNext_page_token().toString());
                        } catch (Exception ex) {
                        }

                    }*/
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void GetJson(View view) throws Exception {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if(SaveInSharedPreference.getInSharedPreference(this).getCurrentLat() == 0.0 || SaveInSharedPreference.getInSharedPreference(this).getCurrentLat() == 0.0)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Unable to Find Location please try Syncing Again")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
                //Toast.makeText(MainActivity.this, "Unable to Find Location please try Syncing Again", Toast.LENGTH_LONG).show();
            }

            else {
                try {
                    SaveInSharedPreference.getInSharedPreference(this).setLatLong(
                            SaveInSharedPreference.getInSharedPreference(this).getCurrentLat(),
                            SaveInSharedPreference.getInSharedPreference(this).getCurrentLng());

                    run("");
                    Log.e("Loading", "First Run");

                    progressBar.show();
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {


                            String next_page_token = "";
                            try {
                                next_page_token = example.get(example.size() - 1).getNextPageToken().toString();

                                Log.e("Page Token", next_page_token);
                            } catch (Exception e) {
                                Log.e("Exception", "" + e.getMessage());
                            }
                            if (example.get(example.size() - 1).getNextPageToken() != null) {
                                try {
                                    run2(next_page_token);
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e(" Index after 5 Second", "" + example.size());
                                            for (int index = 0; index <= example.size() - 1; index++) {

                                                for (int i = 0; i < example.get(index).getResults().size(); i++) {
                                                    Log.e("Inside Loop", "Inside Loop");
                                                    String ID = example.get(index).getResults().get(i).getId();
                                                    String Name = example.get(index).getResults().get(i).getName();
                                                    double lat = example.get(index).getResults().get(i).getGeometry().getLocation().getLat();
                                                    double lng = example.get(index).getResults().get(i).getGeometry().getLocation().getLng();

                                                    Filter.add(new FilterData(ID, Name, lat, lng));
                                                }


                                            }

                                            Collections.sort(Filter, FilterData.LatCompare);
                                            Realm$Helper.getLocation$Module(MainActivity.this).save$AllData(Filter);

                                            datafilter = Realm$Helper.getLocation$Module(MainActivity.this).getAllData();

                                            int count = 0;
                                            for (FilterData str : datafilter) {
                                                count++;
                                                Log.e("Filter Name", str.getName());
                                                Log.e("Filter Lat", "" + str.getLat());
                                                Log.e("Filter Lat", "" + str.getlng());
                                            }

                                            Log.e("Total Count", "" + count);
                                            getdbData();
                                            progressBar.dismiss();

                                        }
                                    }, 10000);

                                } catch (Exception e) {
                                    Log.e("Exception", "" + e.getMessage());
                                }
                            } else {
                                for (int index = 0; index <= example.size() - 1; index++) {

                                    for (int i = 0; i < example.get(index).getResults().size(); i++) {
                                        Log.e("Inside Loop", "Inside Loop");
                                        String ID = example.get(index).getResults().get(i).getId();
                                        String Name = example.get(index).getResults().get(i).getName();
                                        double lat = example.get(index).getResults().get(i).getGeometry().getLocation().getLat();
                                        double lng = example.get(index).getResults().get(i).getGeometry().getLocation().getLng();

                                        Filter.add(new FilterData(ID, Name, lat, lng));
                                        progressBar.dismiss();
                                    }

                                }

                                Collections.sort(Filter, FilterData.LatCompare);
                                Realm$Helper.getLocation$Module(MainActivity.this).save$AllData(Filter);
                            }
                            Log.e("Example Index", "" + example.size());
                            getdbData();
                        }

                    }, 20000);

                    //Log.e("Index",""+example.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            requestStoragePermission();
            statusCheck();
        }
    }

    public void getdbData() {
        datafilter = Realm$Helper.getLocation$Module(MainActivity.this).getAllData();
        List<String> placeslist = new ArrayList<>();

/*        AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(this)
                .build();
*/

        if (datafilter != null) {
            /*textView.setText("" + datafilter.size());*/
            int count = 0;
            for (FilterData std : datafilter) {
                count = count + 1;
                placeslist.add("(" + count + ")  " + std.getName());
                //textView.setText(textView.getText().toString() + "\n \n" + std.getID() + "\n" + std.getName() + "\n" + std.getlng() + "\n" + std.getLat());

            }

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placeslist);
            listView.setAdapter(adapter);
            //dialog.dismiss();
        } else {

            textView.setText("No Data Found");
        }
      /*  if (adapter != null && adapter.getCount() == 0) {
            try {
                GetJson(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    public void getDataFromDB(View view) {
       /* datafilter = Realm$Helper.getLocation$Module(MainActivity.this).getAllData();
        textView.setText(""+datafilter.size());
        for (FilterData std: datafilter ) {
            textView.setText(textView.getText().toString() + "\n \n"+ std.getID()+ "\n" + std.getName()+ "\n" + std.getlng()+ "\n" + std.getLat());

        }*/
    }

    public void ExitService(View view) {
        stopService(new Intent(this, LocationService.class));
        finish();
    }

    public void EditSettings(View view) {
        openDialog();
        //Log.e("ChangeDistance ", Changedistance);
    }

    public void openDialog() {
        SettingsDialog exampleDialog = new SettingsDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyTexts(String Distance) {
        SaveInSharedPreference.getInSharedPreference(this).setPrefDistance(Distance);
    }
}
