package com.example.geolocationbasis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button showMapButton;
    TextView latText, lonText, timeText;

    LocationManager locationManager;
    Location location;


    //granted var shows "were ALL permissions agreed by user?"
    private boolean granted = false;
    //this constant is used by us like id for permission requesting procedure with dialog window
    //It's like intent key
    private final int LOCATION_PERMISSION = 1111; //just constant with unique value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showMapButton = findViewById(R.id.toMapButton);
        lonText = findViewById(R.id.lon);
        latText = findViewById(R.id.lat);
        timeText = findViewById(R.id.timeText);

        //TODO подключить менеджер местоположения
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //below we can set minimal time between requests to device about our location - in milliseconds
        //we also can set navigation system for ex. GPS
        //other popular provider is Network - get location info by internet
        //we can set minimal distance or pogreshnost - so I don't know
        //and finally we set locationListener
        if(granted||checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*5, 20f, listener);
        }

        showMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    //TODO описать LocationListener
    LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //when user changed his location
            //checking of location by locationListener can happen every few seconds
            if(location!=null) {
                //we use it only when user changed his location
                showLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //when the status of permission was changed by user on his device
            // TODO: 15.03.2022 you should turn off permission in program
        }

        @Override
        public void onProviderEnabled(String s) {
            //when user turned on service of location in his device
        }

        @Override
        public void onProviderDisabled(String s) {
            //when user turned off service of location in his device
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        //TODO реализовать получение координат с запросом разрешения
        // we have already done it in onCreate but user can pause his activity and we also have to check it here
        if(granted||checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*5, 20f, listener);
        }
        if(locationManager!=null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null) {
                showLocation(location);
            }
        }

    }

    //TODO переопределить функцию обратного вызова для обработки ответа пользователя
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //int array with permission that were agreed by user
        //if request code equals our constant LOCATION_PERMISSION then user is agree with permission

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==LOCATION_PERMISSION) {
            granted = true;
            if(grantResults.length > 0) {
                for(int permission : grantResults) {
                    if(permission != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                    }
                }
            }
            else {
                //here we can show text to user "sorry we can't work without permissions"
                granted = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(listener);
    }

    private void showLocation(Location location) {
        //it's not override method
        //actually we would can not to write that function
        latText.setText(String.valueOf(location.getLatitude()));
        lonText.setText(String.valueOf(location.getLongitude()));
        //we can create time text like completed toString-showed Data object
        timeText.setText(new Date(location.getTime()).toString());
    }

    private boolean checkPermission() {
        //checkSelfPermission is only for dangerous permissions for ex. location, personal data
        //PERMISSION_GRANTED = permission is turned on
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            //the method below create window "do you want to enable permission for user"
            //and it also automatically calls method onRequestPermissionsResult
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            return false;
        }
        else {
            return true;
        }
    }
}
