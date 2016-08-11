/*
 * Copyright (C) 2015 Spyridon Stathopoulos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Utils.cluster;

import java.util.Arrays;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class CPoint implements Comparable<CPoint>{
    private float[] vector;

    public CPoint(double[] vector) {
        this.vector = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            this.vector[i] = (float) vector[i];
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.vector);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CPoint other = (CPoint) obj;
        if (!Arrays.equals(this.vector, other.vector)) {
            return false;
        }
        return true;
    }

    public CPoint(float[] vector) {
        this.vector = vector;
    }

    public double[] getVector() {
        double[] ret = new double[vector.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = (double) vector[i];
        }
        return ret;
    }
    
    
    public double distanceFrom(CPoint t) {
        double d =Utils.DistanceFunctions.getL2Distance(this.vector, t.vector);
        //System.out.println("dist(p1,p2)=>\ndist(["+ ArrayUtil.arrayToString(vector)+"], ["+ArrayUtil.arrayToString(t.vector)+"]= "+d);
        return d;
    }   
    
    @Override
    public int compareTo(CPoint o) {
        float myTotal = 0;
        for (int i = 0; i < this.vector.length; i++) {
            myTotal += this.vector[i];
        }
        float oTotal = 0;
        for (int i = 0; i < this.vector.length; i++) {
            oTotal += o.vector[i];
        }

        return (myTotal > oTotal ? -1
                : (myTotal == oTotal ? 0 : 1));
    }
   
}
