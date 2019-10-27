package com.google.android.gms.location.sample.locationupdates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RecommendCafeActivity extends AppCompatActivity {

    private static final String TAG ="";
    private List<Place.Field> placeFields;
    private int N = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TextView TextViewplaces = (TextView) findViewById(R.id.textViewplaces);

        Intent coordinates = this.getIntent();
        String coordinatesInfull = coordinates.getDoubleExtra("Latitude", 0)+","
                +coordinates.getDoubleExtra("Longitude", 0);
        //coordinates.getDoubleExtra()
        Log.i(TAG, "Parsed user coordinates: "+coordinatesInfull);

        if (!Places.isInitialized()) {
            String gApiKey = RecommendCafeActivity.this.getString(R.string.api_key_places);
            Places.initialize(RecommendCafeActivity.this, gApiKey);
        }
        PlacesClient placesClient = Places.createClient(RecommendCafeActivity.this);
        // Use fields to define the data types to return.
        //List<Place.Field> placeFields = Collections.singletonList(Place.Field.LAT_LNG);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.TYPES);

// Fetch Place by ID
        //FetchPlaceRequest detailsRequest = FetchPlaceRequest.builder(placeId, fields).build();



// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                        if (placeLikelihood.getPlace().getTypes().toString().contains("FOOD") ){
                        Log.i(TAG, String.format("Filtered nearest CAFE "+placeLikelihood.getPlace().getName()+
                                placeLikelihood.getPlace().getLatLng().toString()+" "+
                                placeLikelihood.getPlace().getTypes()));

                            final Button[] myTextViews = new Button[N]; // create an empty array;
                            LinearLayout lLayout = (LinearLayout) findViewById(R.id.searchedPlaces);
                            // create a new textview
                            final Button rowTextView = new Button(this);

                            // set some properties of rowTextView or something
                            rowTextView.setText(placeLikelihood.getPlace().getName());
                            rowTextView.setTag(placeLikelihood.getPlace().getName());
                            rowTextView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //Log.i("TAG", "The index is" + index);
                                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=:"+ v.getTag());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");

                                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                }});




                            // add the textview to the linearlayout
                            lLayout.addView(rowTextView);


                            // save a reference to the textview for later
                            //myTextViews[i] = rowTextView;

                        TextView textViewplaces = (TextView) findViewById(R.id.textViewplaces);
                            textViewplaces.setText("Nearest CAFE for you is "+ placeLikelihood.getPlace().getName());




                            }

                        }}
                else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            //getLocationPermission();
        }


        setContentView(R.layout.activity_main2);
    }



    private boolean checkPermission(String permission) {
        boolean hasPermission =
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 0);
        }
        return hasPermission;
    }

}
