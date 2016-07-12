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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class CPoint implements Comparable<CPoint>, com.stromberglabs.cluster.Clusterable {

    private float[] vector;

    public CPoint(double[] vector) {
        this.vector = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            this.vector[i] = (float) vector[i];
        }
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
        double d = getL2Distance(this.vector, t.vector);
        //System.out.println("dist(p1,p2)=>\ndist(["+ ArrayUtil.arrayToString(vector)+"], ["+ArrayUtil.arrayToString(t.vector)+"]= "+d);
        return d;
    }

    private static double getL2Distance(float[] point1, float[] point2) {
        double dist = 0;
        for (int i = 0; i < point1.length; i++) {
            dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        return Math.sqrt(dist);
    }

    @Override
    public float[] getLocation() {
        return this.vector;
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

    /**
     * Writes a cluster centers to a binary file with the following format:
     * [0]<integer>: number of centers (integer) [1]<integer>: size of center
     * point (integer) [...]<double>: array data
     *
     * @param clusters
     * @param file
     */
    public static void writeClusters(List<CPoint> clusters, String file) {
        if (clusters.size() > 0) {
            try {
                // Create an output stream to the file.
                FileOutputStream fileOutput = new FileOutputStream(file);
                // Wrap the FileOutputStream with a DataOutputStream
                DataOutputStream dataOut = new DataOutputStream(fileOutput);
                // Write the data to the file
                // [0]<integer>: number of centers (integer) 
                dataOut.writeInt(clusters.size());
                // [1]<integer>: size of center
                dataOut.writeInt(clusters.get(0).getVector().length);
                // [...]<double>: array data
                for (CPoint p : clusters) {
                    double[] vector = p.getVector();
                    for (int i = 0; i < vector.length; i++) {
                        dataOut.writeDouble(vector[i]);
                    }
                    dataOut.flush();
                }
                fileOutput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static List<CPoint> readClusters(String file) {
        List<CPoint> ret = new ArrayList<CPoint>();
        try {
            // Wrap the FileInputStream with a DataInputStream
            FileInputStream fileInput = new FileInputStream(file);
            DataInputStream dataIn = new DataInputStream(fileInput);
            // [0]<integer>: number of centers (integer) 
            int nClusters = dataIn.readInt();
            // [1]<integer>: size of center
            int vLen = dataIn.readInt();
            // [...]<double>: array data            
            for (int i = 0; i < nClusters; i++) {
                double[] cVector = new double[vLen];
                for (int j = 0; j < vLen; j++) {
                    cVector[j] = dataIn.readDouble();
                }
                ret.add(new CPoint(cVector));
            }
            //Close the file.
            dataIn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static int clusterIndexOf(List<CPoint> clusters, CPoint p) {
        int ret = 0;
        if (clusters.size() > 0) {
            double distance = clusters.get(0).distanceFrom(p);
            double tmp;
            for (int i = 1; i < clusters.size(); i++) {
                tmp = clusters.get(i).distanceFrom(p);
                if (tmp < distance) {
                    distance = tmp;
                    ret = i;
                }
            }
        }
        return ret;
    }
}
