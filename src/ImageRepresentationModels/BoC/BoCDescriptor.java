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
package ImageRepresentationModels.BoC;

import Utils.cluster.CPoint;
import Clustering.clusteringBocFactory;
import Utils.cluster.ColorClusterComparator;
import Clustering.KMeans.KDTreeImplementation;
import Utils.Utilities;
import Utils.Image.ColorConversion;
import Utils.Image.ColorConversion.ColorSpace;
import Utils.Image.ImageUtility;
import net.semanticmetadata.lire.utils.cv.KMeans;
import net.sf.javaml.core.kdtree.KDTree;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class BoCDescriptor{
    public static int rescaleDim=256;
    public static int segRowCols=64;
    
    public static int[][] createPaletteStrombergLab(String[] sampleImages,ColorSpace cs,int noOfColors, 
                                          String paletteFile, boolean debug, boolean distinct_colors) throws Exception{
        if(debug){System.out.print("Collecting colors...");}
        
        // Create a list of sample CPoints to cluster
        List<CPoint> cpoints = new ArrayList<CPoint>();
        for(String fimg:sampleImages){
            BufferedImage img = ImageUtility.getImage(fimg);
            img=ImageUtility.resizeImage(img, rescaleDim, rescaleDim, false);
            int[][] domColors = ImageUtility.getDominantColors(img, segRowCols, segRowCols, cs);                        
            for(int c[] : domColors){
            	double[] color = new double[]{c[0],c[1],c[2]};
            	if(distinct_colors){
            		if(!CpointListContainsArray(cpoints,color) )
						cpoints.add(new CPoint( color ));				
				}
				else{
					cpoints.add(new CPoint( color ));
				}
            	
            }            
        }
        if(debug){System.out.print("[ΟΚ] Collected: "+cpoints.size()+" points.\n");}

        // Cluster the Sample points
        if(debug){System.out.print("Creating palette...");}
        List<CPoint> clusters = clusteringBocFactory.KMeansStromer(cpoints, noOfColors);                
        int[][] palette =getPaletteFromClusters(clusters,true,cs);
        if(debug){System.out.print("[OK] Saving palette to: "+paletteFile);}        
        arrayToBinaryFile(palette, paletteFile);
        if(debug){System.out.print("[OK]\n");}              
        return palette;
    }    
    	

    

	public static int[][] createPaletteLire(String[] sampleImages,ColorSpace cs,int noOfColors, 
            String paletteFile, boolean debug, boolean distinct_colors) throws Exception{
		
    	if(debug){System.out.print("Collecting colors...");}                         
		// Create a list of sample CPoints to cluster
    	List<double[]> lire = new ArrayList<double[]>();
        
		for(String fimg:sampleImages){
			BufferedImage img = ImageUtility.getImage(fimg);
			img=ImageUtility.resizeImage(img, rescaleDim, rescaleDim, false);
			int[][] domColors = ImageUtility.getDominantColors(img, segRowCols, segRowCols, cs);  
			for(int c[] : domColors){
				double[] color = new double[]{c[0],c[1],c[2]};
				
				if(distinct_colors){
					if(!DoubleListContainsArray(lire,color) )
						lire.add(color);
				}
				else{
					lire.add(color);
				}
			}            
		}
		//Utilities.write2TXT(lire,"C:\\Users\\leonidas\\Eclipse\\ImageClassification\\Clef2\\colors.txt",null);
		
		if(debug){System.out.print("[ΟΚ] Collected: "+lire.size()+" points.\n");}
		
		// Cluster the Sample points
		if(debug){System.out.print("Creating palette...");}
		
		List<CPoint> clusters = new ArrayList<CPoint>();
		KMeans mean=new KMeans(lire, noOfColors);
    	List<double[]> descriptorCenters= mean.getMeans();
    	
    	for(double[] centers:descriptorCenters){
    		clusters.add(new CPoint(centers));
    	}
		
		
		int[][] palette =getPaletteFromClusters(clusters,true,cs);
		if(debug){System.out.print("[OK] Saving palette to: "+paletteFile);}        
		arrayToBinaryFile(palette, paletteFile);
		if(debug){System.out.print("[OK]\n");}              
		return palette;
	}    
    
	
	
    private static boolean DoubleListContainsArray(List<double[]> list , double[] array){
    	for(double[] colors : list){
    		if(colors[0] == array[0] 
			&& colors[1] == array[1] 
			&& colors[2] == array[2]  )
    			return true;
    	}
    	
    	return false;
    }
    
    private static boolean CpointListContainsArray(List<CPoint> list, double[] array) {
    	for(CPoint color : list){
    		if(color.getVector()[0] == array[0] 
			&& color.getVector()[1] == array[1] 
			&& color.getVector()[2] == array[2]  )
    			return true;
    	}
    	
    	return false;
	}
 
    private static int[][] getPaletteFromClusters(List<CPoint> clusters, boolean sort, 
                                                 ColorSpace cs){                 
         if(sort){Collections.sort(clusters,new ColorClusterComparator(cs));}
         
         int[][] ret = new int[clusters.size()][3];
         for(int i=0;i<clusters.size();i++){
             float[] v = clusters.get(i).getLocation();
             ret[i][0] = Math.round(v[0]);
             ret[i][1] = Math.round(v[1]);
             ret[i][2] = Math.round(v[2]);
         }
         return ret;                 
    }   
   
  
    public static void arrayToBinaryFile(int[][] array, String filePath) {
        int rows = array.length;
        int cols = array[0].length;
        try {
            // Create an output stream to the file.
            FileOutputStream fileOutput = new FileOutputStream(filePath);
            // Wrap the FileOutputStream with a DataOutputStream
            DataOutputStream dataOut = new DataOutputStream(fileOutput);
            // Write the data to the file
            // [0]<integer>: number of rows (integer)
            dataOut.writeInt(rows);
            // [1]<integer>: number of columns (integer)
            dataOut.writeInt(cols);
            // [...]<int>: array data
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    dataOut.writeInt(array[i][j]);
                }
                dataOut.flush();
            }
            //Close the file.
            fileOutput.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private int palette[][];
    private ColorSpace cs;
    
    public int reScaleDim = 128;
    
    public BoCDescriptor() {
	}
    
    public BoCDescriptor(int[][] palette, ColorSpace cs) {
        this.palette=palette;
        this.cs=cs;                   
    }      
    
    public BoCDescriptor(String palleteFile, ColorSpace cs){
    	this.palette=Utilities.binaryFileTo2DIntArray(palleteFile);
        this.cs=cs;       
    }


	public double[] extract(BufferedImage bimg){
		
        List<double[]> paletteList = Utilities.intArray2DoubleList(palette);
        KDTree tree=KDTreeImplementation.createTree(paletteList);
     	
		double[] vector = new double[palette.length];           
        bimg = ImageUtility.resizeImage(bimg, reScaleDim, reScaleDim,false);//fast scale using java 1.4
        for (int x = 0; x < bimg.getWidth(); x++) {
            for (int y = 0; y < bimg.getHeight(); y++) {
                Color color = new Color(bimg.getRGB(x, y));                    
                int[] cl  = ColorConversion.convertFromRGB(cs,color.getRed(),color.getGreen(), color.getBlue());
                
                //convert using L2 distance
                //int indx = getClosestColorIndx(palette, cl);
                
                //convert using KDtrees
                int indx= KDTreeImplementation.SearchTree(new double[]{cl[0],cl[1],cl[2]},tree);
                
                vector[indx]++;
            }
        }
        return vector;
    }
        
    private int getClosestColorIndx(int[][] palette, int[] cl){        
        double minDist=ImageUtility.getL2Distance(palette[0], cl);
        int minIndx = 0;
        
        for(int i=1;i<palette.length;i++){
            double d = ImageUtility.getL2Distance(palette[i], cl);           
            if(minDist>d){
                minDist = d;
                minIndx = i;
            }            
        }                
        return minIndx;
    }      
}
