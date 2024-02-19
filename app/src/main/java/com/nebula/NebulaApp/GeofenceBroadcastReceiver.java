package com.nebula.NebulaApp;


import static android.content.ContentValues.TAG;

import static com.nebula.NebulaApp.HomeFragment.locationParam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_GEOFENCE_TRANSITION = "com.nebula.NebulaApp.GeofenceBroadcastReceiver.ACTION_GEOFENCE_TRANSITION";

//    public GeofenceBroadcastReceiver(HomeFragment homeFragment) {
//        this.homeFragment = homeFragment;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        assert geofencingEvent != null; // Assertion <<
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        int transition = geofencingEvent.getGeofenceTransition();

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            HomeFragment.setLocationText("Inside 🏡");
            Toast.makeText(context.getApplicationContext(), "Inside 🏡", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceive: ENTER");
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            HomeFragment.setLocationText("Outside 🚗");
            Toast.makeText(context.getApplicationContext(), "Outside 🚗", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onReceive: EXIT");
        }
    }
//    public void onReceive(Context context, Intent intent) {
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        if (geofencingEvent.hasError()) {
//            Log.d(TAG, "onReceive: Error receiving geofence event...");
//            return;
//        }
//
//        int transition = geofencingEvent.getGeofenceTransition();
//
//        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
//            Log.d(TAG, "onReceive: ENTER");
//            locationParam.setText("You are inside the geofence");
//        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//            Log.d(TAG, "onReceive: EXIT");
//            locationParam.setText("You are outside the geofence");
//        }
//    }
}

