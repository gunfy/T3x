package com.example.eymard.t3x;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eymard on 28/12/2015.
 */
public class myService extends Service implements ServiceBinder.IMyServiceMethod {
    private IBinder mBinder; //l'instance du binder correspondant à notre service
    private ArrayList<JSONObject> list = new ArrayList<JSONObject>();


    @Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        //au démarrage du service, on créé le binder en envoyant le service
        mBinder = new ServiceBinder(this);
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

        JSONObject json = new JSONObject();
        try {
            json.put("ctrl","allcourse");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HTTPRequest myReq= new HTTPRequest(json.toString());
        myReq.execute();

        return Service.START_STICKY;

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.i("service----", "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("service----", "Service onDestroy");
    }

    public ArrayList<JSONObject> getDataFromService() {
        return list;
    }
}
