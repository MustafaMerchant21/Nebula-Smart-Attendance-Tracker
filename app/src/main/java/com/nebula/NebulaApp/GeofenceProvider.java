package com.nebula.NebulaApp;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class GeofenceProvider {

    public GeofenceProvider() {
    }

    public Geofence createGeofence(LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setRequestId(generateRequestId())
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(transitionTypes)
                .build();
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
