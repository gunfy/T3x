package com.example.eymard.t3x;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by eymard on 24/12/2015.
 */
public class AddressLocation extends AsyncTask<String, String, String> {


    private static final String TAG = "com.example.eymard.t3x.AddressLocation";
    private Context mainContxt;
    private Double latitude,longitude;


    public AddressLocation(Double lat,Double longi,Context con){
        latitude=lat;
        longitude=longi;
        mainContxt=con;
    }

    @Override
    protected String doInBackground(String... params) {


        Geocoder geocoder = new Geocoder(mainContxt);
        String result = null;

        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList .size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                //for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                sb.append(address.getAddressLine(0)).append("\n");
                //}
                // sb.append(address.getLocality());
                result = sb.toString();
            }
        }  catch (IOException e) {
            Log.e(TAG, "Unable connection Geocoder", e);
        }

        return result;

    }


    @Override
    protected void onPostExecute(String address) {

        if (!(address=="")){
            Toast.makeText(mainContxt, address,
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(mainContxt, "No Location found",
                    Toast.LENGTH_LONG).show();
        }

    }














    }
