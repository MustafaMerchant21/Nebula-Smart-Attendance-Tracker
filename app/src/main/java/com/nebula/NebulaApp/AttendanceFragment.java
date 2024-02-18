package com.nebula.NebulaApp;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

public class AttendanceFragment extends Fragment {
    private MaterialCalendarView materialCalendarView;
    Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attendance, container, false);
        materialCalendarView = (MaterialCalendarView) v.findViewById(R.id.calenderAttendance);
        Toolbar toolbar = (Toolbar) requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Attendance");

        // Get data from HomeFragment after successful scan
        assert getArguments() != null;
        String isMarked = getArguments().getString("isMarked");

        // Get the current year, month, and day
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a CalendarDay object from the current date
        CalendarDay currentDate = CalendarDay.from(year, month, day);
        if(isMarked.equals("True")) {
            // TODO: Reflect to the Database the value of isMarked
            EventDecorator eventDecorator = new EventDecorator(currentDate);
            materialCalendarView.addDecorator(eventDecorator);
        }else{
            EventDecolored eventDecorator = new EventDecolored(currentDate);
            materialCalendarView.addDecorator(eventDecorator);
        }

        return v;
    }
}