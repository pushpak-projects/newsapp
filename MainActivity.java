package com.example.newsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsapp.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private TextView textLatLong;
    private ProgressBar progressBar;
    private static final String weatherURL="https://api.openweathermap.org/data/2.5/weather";
    private static final String bingUrl="https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=";
    FrameLayout frameLayout;
    View view;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            view= getLayoutInflater().inflate(R.layout.fragment_home,null);

        BottomNavigationView navView=findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(navListener);

        frameLayout=findViewById(R.id.fragment_container);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        }else{
            Log.d("MainActivity","inside else");
            getCurrentLocation();
        }


        Log.d("MainActivity","before calling home fragment");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("MainActivity","onRequestPermissionResult");
                getCurrentLocation();
            }
            else
            {
                Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        //progressBar.setVisibility(View.VISIBLE);
        Log.d("MainActivity","inside getCurrentLocation");
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size()>0)
                        {
                            int latestLocationIndex=locationResult.getLocations().size()-1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
//                            textLatLong.setText(
//                                    String.format(
//                                            "Latitude: %s\nLongitude: %s",
//                                            latitude,
//                                            longitude
//                                    )
//                            );
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                final String cityName = addresses.get(0).getLocality();
                                final String stateName = addresses.get(0).getAdminArea();
                                String countryName = addresses.get(0).getCountryName();
                                Log.d("city",cityName);
                                Log.d("state",stateName);
                                Log.d("country",countryName);

                                final TextView textView = (TextView) findViewById(R.id.text);
// ...

// Instantiate the RequestQueue.
                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                StringRequest stringRequest = new StringRequest(weatherURL + "?q=" + cityName, new Response.Listener<String>() {
                                    @SuppressLint("ResourceType")
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("weather response",response);
                                        GsonBuilder weatherGsonBuilder = new GsonBuilder();
                                        Gson weatherGson = weatherGsonBuilder.create();
                                        try {
                                            JSONObject jsonOb = new JSONObject(response);
                                            JSONArray jsonArray = jsonOb.getJSONArray("weather");
                                            JSONObject obj = jsonArray.getJSONObject(0);
                                            String type = obj.getString("main");
                                            Log.d("type",type);

                                            System.out.println(view);
                                            CardView weatherCard = (CardView)view.findViewById(R.id.card_view);
                                            ImageView image1 = (ImageView)weatherCard.findViewById(R.id.image1);
                                            switch(type){
                                                case "Clear":
                                                    image1.setImageResource(R.drawable.clear_weather);
                                                    break;
                                                case "Clouds":
                                                    image1.setImageResource(R.drawable.cloudy_weather);
                                                    break;
                                                case "Snow":
                                                    image1.setImageResource(R.drawable.snowy_weather);
                                                    break;
                                                default:
                                                    System.out.println("in sunny");
                                                    image1.setImageResource(R.drawable.sunny_weather);
                                                    break;
                                            }

                                            JSONObject mainObj = jsonOb.getJSONObject("main");
                                            String temp=mainObj.getString("temp");
                                            temp=String.valueOf(Math.round(Double.valueOf(temp)));
                                            TextView tempView=(TextView)view.findViewById(R.id.textView2);
                                            tempView.setText(temp);
                                            System.out.println(tempView);
                                            TextView cityView=(TextView)view.findViewById(R.id.textView3);
                                            cityView.setText(cityName);
                                            TextView stateView=(TextView)view.findViewById(R.id.textView4);
                                            stateView.setText(stateName);
                                            TextView typeView=(TextView)view.findViewById(R.id.textView5);
                                            System.out.println(typeView.getText());
                                            typeView.setText(type);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //String area = jsonOb.getString("AREA");;

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                    }

                                });

// Add the request to the RequestQueue.
                                //queue.add(stringRequest);
                                //RequestQueue queue = Volley.newRequestQueue(this);
                                queue.add(stringRequest);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                        //Bar.setVisibility(View.GONE);
                    }


                }, Looper.getMainLooper());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment=null;
                    switch(item.getItemId())
                    {
                        case R.id.nav_home:
                            //findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                            selectedFragment=new HomeFragment();

                            //RecyclerView newsList = (RecyclerView) findViewById(R.id.newsList);
                            break;
                        case R.id.nav_headlines:
                            //findViewById(R.id.card_view).setVisibility(View.GONE);
                            //findViewById(R.id.fragment_container).getLayoutParams().height=getParent().get;
                            selectedFragment=new HeadlinesFragment();

                            break;
                        case R.id.nav_trending:
                            //findViewById(R.id.card_view).setVisibility(View.GONE);
                            selectedFragment=new TrendingFragment();
                            break;
                        case R.id.nav_bookmark:
                            //findViewById(R.id.card_view).setVisibility(View.GONE);
                            selectedFragment=new BookmarksFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}
