package com.nebula.NebulaApp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LastNodeKeyNameRetriever {
    String  lastYear, lastMonth, lastDay;
    DatabaseReference attendRef;

    public LastNodeKeyNameRetriever(DatabaseReference r){
        this.attendRef = r;
    }

    public String getLastYear() {
        return this.lastYear;
    }
    public void setLastYear(String lastYear) {
        this.lastYear = lastYear;
    }
    public String getLastMonth() {
        return lastMonth;
    }
    public void setLastMonth(String lastMonth) {
        this.lastMonth = lastMonth;
    }
    public String getLastDay() {
        return lastDay;
    }
    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }

    public void getLastNodeKeyNames() {
        attendRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if there is any data  >>>
                if (dataSnapshot.exists()) {
                    for (DataSnapshot yearSnapshot : dataSnapshot.getChildren()) {
                        String yearKey = String.valueOf(yearSnapshot.getKey());
                        setLastYear(yearKey);
                        Log.d("== Last Year ==",getLastYear());

                        // Get the last month node under the year node  >>>
                        DatabaseReference yearRef = attendRef.child(yearKey);
                        yearRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot yearDataSnapshot) {
                                if (yearDataSnapshot.exists()) {
                                    for (DataSnapshot monthSnapshot : yearDataSnapshot.getChildren()) {
                                        String monthKey = monthSnapshot.getKey();
                                        setLastMonth(monthKey);
                                        Log.d("== Last Month ==",monthKey);

                                        // Get the last day node under the month node  >>>
                                        DatabaseReference monthRef = yearRef.child(monthKey);
                                        monthRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot monthDataSnapshot) {
                                                if (monthDataSnapshot.exists()) {
                                                    for (DataSnapshot daySnapshot : monthDataSnapshot.getChildren()) {
                                                        String dayKey = daySnapshot.getKey();
                                                        setLastDay(dayKey);
                                                        Log.d("== Last Day ==",dayKey);
                                                    }
                                                } else {
                                                    Log.e("Attendance Operations: LastKeyNodeNamesRetreiver","No data fetched for months");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle errors  >>>
                                                Log.e("Attendance Operations: LastKeyNodeNamesRetreiver","Error retrieving data:" + databaseError.getMessage());
                                                System.out.println("Error retrieving data: " + databaseError.getMessage());
                                            }
                                        });
                                    }
                                } else {
                                    // No data found for the year  >>>
                                    Log.e("Attendance Operations: LastKeyNodeNamesRetreiver","No attendance data found for the year");
                                    System.out.println("No attendance data found for the year.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors  >>>
                                Log.e("Attendance Operations: LastKeyNodeNamesRetreiver","Error retrieving data: " + databaseError.getMessage());
                                System.out.println("Error retrieving data: " + databaseError.getMessage());
                            }
                        });
                    }
                } else {
                    // No data found for the attendance data  >>>
                    Log.e("Attendance Operations: LastKeyNodeNamesRetreiver","No attendance data found for the student.");
                    System.out.println("No attendance data found for the student.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors  >>>
                Log.e("Attendance Operations: LastKeyNodeNamesRetreiver","Error retrieving data: " + databaseError.getMessage());
                System.out.println("Error retrieving data: " + databaseError.getMessage());
            }
        });
    }
}

