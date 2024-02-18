package com.nebula.NebulaApp;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class EventDecolored implements DayViewDecorator {
    private CalendarDay currentDate;

    public EventDecolored(CalendarDay currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(currentDate);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#443D54"));
        view.addSpan(span);
    }
}


