package com.cnscud.xpower.utils;

import java.time.LocalTime;

public class LocalDateTimeRange {
    
    public LocalTime start;
    public LocalTime end;

    public LocalDateTimeRange() {
    }

    public LocalDateTimeRange(LocalTime start, LocalTime end) {
        super();
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return start + "-" + end;
    }
}