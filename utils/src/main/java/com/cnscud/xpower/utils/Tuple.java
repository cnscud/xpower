package com.cnscud.xpower.utils;

import java.time.LocalDate;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Tuple<K,V> {
    
    public K start;
    public V end;

    public Tuple() {
    }

    public Tuple(K start, V end) {
        super();
        this.start = start;
        this.end = end;
    }

    public K getStart() {
        return start;
    }
    public V getEnd() {
        return end;
    }
    @JsonIgnore
    public K getX() {
        return start;
    }
    @JsonIgnore
    public V getY() {
        return end;
    }
    
    public void setStart(K start) {
        this.start = start;
    }

    public void setEnd(V end) {
        this.end = end;
    }

    
    @Override
    public String toString() {
        return start + "-" + end;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple other = (Tuple) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        return true;
    }
    
}