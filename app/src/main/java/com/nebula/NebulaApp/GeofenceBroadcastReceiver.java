package com.nebula.NebulaApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.location.Geofence;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";
    HomeFragment homeFragment;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the intent action is for a geofence transition
        Log.d(TAG,"Entered onRecieve");
        if (intent.getAction() != null && intent.getAction().equals("com.example.geofence.ACTION_RECEIVE_GEOFENCE")) {
            // Extract the geofence transition type
            int geofenceTransition = intent.getIntExtra("transition_type", -1);

            switch (geofenceTransition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    // User entered the geofence
                    homeFragment.changeQRCodeButtonState(true);
                    Toast.makeText(context, "You are inside the geofence", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User entered the geofence");
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    // User exited the geofence
                    homeFragment.changeQRCodeButtonState(true);
                    Toast.makeText(context, "You are outside the geofence", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User exited the geofence");
                    break;
                default:
                    // Unknown geofence transition
                    homeFragment.changeQRCodeButtonState(false);
                    Log.e(TAG, "Unknown geofence transition");
                    break;
            }
        }
    }
}
