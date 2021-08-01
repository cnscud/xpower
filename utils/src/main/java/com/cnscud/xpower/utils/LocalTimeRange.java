package com.cnscud.xpower.utils;

import java.time.LocalTime;

public class LocalTimeRange extends Tuple<LocalTime,LocalTime> {
    
    public LocalTimeRange() {
    }

    public LocalTimeRange(LocalTime start, LocalTime end) {
        super(start,end);
    }

}