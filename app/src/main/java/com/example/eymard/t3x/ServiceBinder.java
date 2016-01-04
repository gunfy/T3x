package com.example.eymard.t3x;

import android.os.Binder;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eymard on 29/12/2015.
 */
public class ServiceBinder extends Binder {


    private IMyServiceMethod mService;

    //on recoit l'instance du service
    public ServiceBinder(IMyServiceMethod service) {
        super();
        mService = service;
    }

    /** @return l'instance du service */
    public IMyServiceMethod getService(){
        return mService;
    }

    /** les méthodes de cette interface seront accessibles par l'activité */
    public interface IMyServiceMethod {
        public ArrayList<JSONObject> getDataFromService();

    }
}
