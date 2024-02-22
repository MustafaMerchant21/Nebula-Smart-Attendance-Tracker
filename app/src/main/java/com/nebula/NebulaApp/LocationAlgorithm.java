package com.nebula.NebulaApp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class LocationAlgorithm {


    private final List<PointF> polygonPoints;

    public LocationAlgorithm(List<PointF> polygonPoints) {
        this.polygonPoints = polygonPoints;
    }
    public boolean abc(PointF point) {
        int crossings = 0;
        int count = polygonPoints.size();
        for (int i = 0; i < count; i++) {

            PointF a = polygonPoints.get(i);
            PointF b = polygonPoints.get((i + 1) % count);

            if (MyY(point, a, b)) {
                float crossProduct = (point.x - a.x) * (b.y - a.y) - (point.y - a.y) * (b.x - a.x);

                if (crossProduct == 0 && MyX(point, a, b)) {
                    return true;
                }
                if (a.y <= point.y && b.y > point.y && crossProduct > 0) {
                    crossings++;
                }
                if (a.y > point.y && b.y <= point.y && crossProduct < 0) {
                    crossings--;
                }
            }
        }
        return crossings != 0;
    }

    //Helper Methods >>>
    private static boolean MyY(PointF point, PointF a, PointF b) {
        return (a.y <= point.y && point.y < b.y) || (b.y <= point.y && point.y < a.y);
    }
    private static boolean MyX(PointF point, PointF a, PointF b) {
        return Math.min(a.x, b.x) <= point.x && point.x <= Math.max(a.x, b.x);
    }

    // Display a notification Main>>>
    public void abcNotification(Context context, boolean isInside) {
        if (isInside) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("idLocation",
                        "Location Notification Channel", NotificationManager.IMPORTANCE_HIGH);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "idLocation")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Inside Target Area")
                    .setContentText("You are inside the target area.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Display the notification >>>
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManagerCompat.notify(123, builder.build());
        }
    }

    //TODO: Use this function only to process location with notification display >>>
    public void finalOutput(Context context, PointF point){
        abcNotification(context, abc(point));
    }
}

