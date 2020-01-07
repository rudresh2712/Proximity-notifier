package com.example.thelasttempl;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.MapMaker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.TodoApi;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static final float DEFAULT_ZOOM=15f;
    public static final String TAG="activity";
    public static final String FINE_LOCATION= Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION=Manifest.permission.ACCESS_COARSE_LOCATION;

    private Boolean mlocationPgranted=false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE=1234;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private EditText searchbar;
    private Button mark,save;
    private Marker marker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();
        Log.d("USER info on create",TodoApi.getInstance().getUsename()
                +TodoApi.getInstance().getUserid());

        searchbar=findViewById(R.id.searchbar);
        mark=findViewById(R.id.mark);
        save=findViewById(R.id.save);

//        mark.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Search();
//            }
//        });



    }

    private void Search(){
        searchbar.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
               if(actionid== EditorInfo.IME_ACTION_SEARCH
               ||actionid==EditorInfo.IME_ACTION_DONE
               || keyEvent.getAction()==KeyEvent.ACTION_DOWN
               || keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){
                   geolocate();
               }

                return false;
            }
        });


    }



    private void geolocate() {

          String searchstring= searchbar.getText().toString();

        Geocoder geocoder= new Geocoder(MapsActivity.this);
        List<Address> list=new ArrayList<>();
        try {
            list.add((Address) geocoder.getFromLocationName(searchstring,1));
        }catch (IOException e)
        {
            Log.d(TAG,"IO EXCEPTION"+e.getMessage());
        }
        if(list.size()>0) {
            final Address address = list.get(0);
            Toast.makeText(MapsActivity.this,"Location found",Toast.LENGTH_LONG).show();
            Log.d(TAG,"  LOCATION acquired  "+address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude())
                    ,DEFAULT_ZOOM,address.getAddressLine(0));

//            save.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d(TAG,"ON click of save button");
//
//                    try {
//                        Intent in=new Intent(MapsActivity.this,Markerclicked.class);
////                        in.putExtra("Latlng",address);
//                        TodoApi todo=new TodoApi();
//                        todo.setAddress(address);
//                       // Toast.makeText(this,)
//                        Log.d("USER info",TodoApi.getInstance().getUsename()
//                                +TodoApi.getInstance().getUserid());
//                        startActivity(in);
//                    }catch (NullPointerException e)
//                    {
//                        Log.d(TAG,"NULL POINTER!!!!");
//
//                    }
//                }
//            });
        }else
            Toast.makeText(MapsActivity.this,"Location not found",Toast.LENGTH_LONG).show();

    }




    private void getDeviceLocation(){
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mlocationPgranted)
            {
                final Task location=fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Location currentlocation = (Location) task.getResult();
                           moveCamera(new LatLng(currentlocation.getLatitude(),
                                   currentlocation.getLongitude()),DEFAULT_ZOOM,"My location");
                        }else
                            Toast.makeText(MapsActivity.this,"Unable to find current location",
                                    Toast.LENGTH_LONG).show();
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void moveCamera(LatLng latlng,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,zoom));

        MarkerOptions options=new MarkerOptions() // Marker options helps in creating Marker object
                .position(latlng).title(title);
    }



    public void  getLocationPermission(){
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mlocationPgranted=true;
            }
        }else
        {
            ActivityCompat.requestPermissions(this,
                    permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mlocationPgranted=false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0)
                for(int i=0; i<grantResults.length;i++){
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                    {
                        mlocationPgranted=false;
                    return;
                    }
                }mlocationPgranted=true;
        }
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

        if(mlocationPgranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this,SavingTodo.class));
//                Log.d("USER info on create",TodoApi.getInstance().getUsename()
//                        +TodoApi.getInstance().getUserid());
            }
        });






    }
}
