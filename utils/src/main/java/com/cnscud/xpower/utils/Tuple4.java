package com.cnscud.xpower.utils;



public class Tuple4<X, Y, M, N> {
    public X x;
    public Y y;
    public M m;
    public N n;

    public Tuple4() {
    }

    public Tuple4(X x, Y y, M m, N n) {
        this.x = x;
        this.y = y;
        this.m = m;
        this.n = n;
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

    public M getM() {
        return m;
    }

    public void setM(M m) {
        this.m = m;
    }

    public N getN() {
        return n;
    }

    public void setN(N n) {
        this.n = n;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m == null) ? 0 : m.hashCode());
        result = prime * result + ((n == null) ? 0 : n.hashCode());
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
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
        Tuple4 other = (Tuple4) obj;
        if (m == null) {
            if (other.m != null)
                return false;
        } else if (!m.equals(other.m))
            return false;
        if (n == null) {
            if (other.n != null)
                return false;
        } else if (!n.equals(other.n))
            return false;
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
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tuple4 [");
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
        if (m != null) {
            builder.append("m=");
            builder.append(m);
            builder.append(", ");
        }
        if (n != null) {
            builder.append("n=");
            builder.append(n);
        }
        builder.append("]");
        return builder.toString();
    }

}