package com.ghanshyam.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String api_key = "your-api-key";
    final String weather_url = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    String location_provider = LocationManager.GPS_PROVIDER;
    TextView nameOfCity, weatherState, temperature;
    ImageView weatherIcon;

    RelativeLayout cityFinder;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherState = findViewById(R.id.weatherCondition);
        temperature = findViewById(R.id.temperature);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityFinder = findViewById(R.id.cityFinder);
        nameOfCity = findViewById(R.id.cityName);

        cityFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CityFinder.class);
                startActivity(intent);
            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        getWeatherOfCurrentLocation();
//    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent = getIntent();
        String city = mIntent.getStringExtra("City");
        if (city != null) {
            getWeatherForNewCity(city);
        } else {
            getWeatherOfCurrentLocation();
        }


    }


    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", api_key);
        letsdosomething(params);

    }

    private void getWeatherOfCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());


                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", api_key);

                letsdosomething(params);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }
        locationManager.requestLocationUpdates(location_provider, MIN_TIME, MIN_DISTANCE, locationListener);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Location setup successful", Toast.LENGTH_SHORT).show();
                getWeatherOfCurrentLocation();
            } else {
                //user denied the permission
            }
        }

    }

    private void letsdosomething(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(weather_url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(getApplicationContext(), "Data Get Success", Toast.LENGTH_SHORT).show();

                WeatherData weatherData = WeatherData.fromJson(response);
                updateUI(weatherData);

//                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void updateUI(WeatherData weatherData) {
        temperature.setText(weatherData.getTemp());
        nameOfCity.setText(weatherData.getCity());
        weatherState.setText(weatherData.getWeatherType());

        int resourceID = getResources().getIdentifier(weatherData.getIcon(), "drawable", getPackageName());
        weatherIcon.setImageResource(resourceID);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}