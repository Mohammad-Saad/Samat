package com.example.locationupdate;

import java.util.List;

/**
 * Created by AbdulRahim on 3/19/2017.
 */

public class OpeningHours {
    public List<Object> exceptional_date ;
    public Boolean open_now;
    public List<Object> weekday_text;

    public List<Object> getExceptional_date() {
        return exceptional_date;
    }

    public void setExceptional_date(List<Object> exceptional_date) {
        this.exceptional_date = exceptional_date;
    }

    public Boolean getOpen_now() {
        return open_now;
    }

    public void setOpen_now(Boolean open_now) {
        this.open_now = open_now;
    }

    public List<Object> getWeekday_text() {
        return weekday_text;
    }

    public void setWeekday_text(List<Object> weekday_text) {
        this.weekday_text = weekday_text;
    }
}
