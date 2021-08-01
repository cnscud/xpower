package com.cnscud.xpower.utils;

import java.time.LocalDate;

public class Tuple3<X, Y, Z> {

    public X x;
    public Y y;
    public Z z;

    public Tuple3() {
    }

    public Tuple3(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }

    public Z getZ() {
        return z;
    }

    public void setZ(Z z) {
        this.z = z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        result = prime * result + ((z == null) ? 0 : z.hashCode());
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
        Tuple3 other = (Tuple3) obj;
        if (x == null) {
            if (other.x != null)
                return false;
        } else if (!x.equals(other.x))
            return false;
        if (y == null) {
            if (other.y != null)
                return false;
        } else if (!y.equals(other.y))
            return false;
        if (z == null) {
            if (other.z != null)
                return false;
        } else if (!z.equals(other.z))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tuple3 [");
        if (x != null) {
            builder.append("x=");
            builder.append(x);
            builder.append(", ");
        }
        if (y != null) {
            builder.append("y=");
            builder.append(y);
            builder.append(", ");
        }
        if (z != null) {
            builder.append("z=");
            builder.append(z);
        }
        builder.append("]");
        return builder.toString();
    }

}