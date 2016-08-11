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
package ImageRepresentationModels.BoC.LBOC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Utils.cluster.CPoint;
import net.semanticmetadata.lire.utils.cv.KMeans;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class Clusterer {
    
    private String file;
    private List<CPoint> clusters=new ArrayList<CPoint>();
           
    public Clusterer(String file, boolean loadNow){
        this.file=file;
        if(loadNow){load();}                    
    }
        
    public void save(){
        writeClusters(clusters,file);
    }
    public void save(String file){
        writeClusters(clusters,file);
    }
    
    
    public void load(){
        clusters=readClusters(file);
    }
    
    public void cluster(List<CPoint> points, int nClusters){
        clusters.clear();
        List<double[]> pts=new ArrayList<double[]>();
        for(CPoint p:points){
            pts.add(p.getVector());
        }
        KMeans km=new KMeans(pts, nClusters);
        for(double[] centers:km.getMeans()){
            clusters.add(new CPoint(centers));
        }        
    }
               
    public List<CPoint> getClusters(){
        return clusters;
    }
    
    public int clusterIndexOf(CPoint p){
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
    
    public static int[][] getPaletteFromClusters(List<CPoint> clusters, boolean sort){                 
         if(sort){Collections.sort(clusters);}
         
         int[][] ret = new int[clusters.size()][3];
         for(int i=0;i<clusters.size();i++){
             double[] v = clusters.get(i).getVector();
             ret[i][0] = (int)Math.round(v[0]);
             ret[i][1] = (int)Math.round(v[1]);
             ret[i][2] = (int)Math.round(v[2]);
         }
         return ret;                 
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
    
    public static List<double[]> ConvertToDoubleList(List<CPoint> clusters){
    	List<double[]> ret = new ArrayList<double[]>();
    	for (CPoint cPoint : clusters) 
    		ret.add(cPoint.getVector());
		
    	return ret;
    	
    }
    
       
}
