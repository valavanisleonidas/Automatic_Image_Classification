/* 
 * Copyright (C) 2016 Spyridon Stathopoulos
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import Clustering.KMeans.KDTreeImplementation;
import ImageRepresentationModels.BoC.LBOC.ArrayIO;
import ImageRepresentationModels.BoC.LBOC.Clusterer;
import ImageRepresentationModels.BoC.LBOC.Processor;
import Utils.Image.ColorConversion;
import Utils.cluster.CPoint;

import net.sf.javaml.core.kdtree.KDTree;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class BoCLibrary {            
    
    public static int[][] createPalette(String file, String[] sampleImgs, int resize, 
                                        int patches, int noOfColors,boolean uniqueColors, 
                                        ColorConversion.ColorSpace cs) {
        Clusterer cl = new Clusterer(file, false);

        //Gather colors      
        System.out.println("Gathering colors...");
        List<CPoint> cpoints = new ArrayList<CPoint>();
        for (int i=0;i<sampleImgs.length;i++) {           
            BufferedImage img = Utils.Image.ImageUtility.getImage(sampleImgs[i]);
            img = Processor.resizeImage(img, resize, resize, false);
            int[][] domColors = Processor.getDominantColors(img, patches, patches, cs);
            for (int c[] : domColors) {
                CPoint cp=new CPoint(new double[]{c[0], c[1], c[2]});
                if(!uniqueColors){
                    cpoints.add(cp);
                }else if(!cpoints.contains(cp)){
                    cpoints.add(cp);
                }
            }
        }
        //Clusters
        System.out.print("Clustering...");
        cl.cluster(cpoints, noOfColors);
        int[][] palette = Clusterer.getPaletteFromClusters(cl.getClusters(),true);
        ArrayIO.arrayToBinaryFile(palette, file);
        //ArrayIO.toFile(palette, file);
        System.out.println("[OK]");
        return palette;
    }
    
    public static Clusterer createDictionary(String file, String[] sampleImgs, int resize, 
                                           int patches, int noOfVWords, int[][] palete, 
                                           ColorConversion.ColorSpace cs,KDTree tree){
        Clusterer cl = new Clusterer(file, false);

        //Gather colors
        System.out.println("Gathering colors...");
        List<CPoint> cpoints = new ArrayList<CPoint>();
        for (int i=0;i<sampleImgs.length;i++) {           
            BufferedImage img = Utils.Image.ImageUtility.getImage(sampleImgs[i]);
            img = Processor.resizeImage(img, resize, resize, false);
            BufferedImage blocks[] = Processor.splitImage(img, patches, patches);
            for(BufferedImage b:blocks){
                cpoints.add(new CPoint(getBoC(b, palete, cs,tree)));
            }
        }
        //Clusters
        System.out.print("Clustering...");
        cl.cluster(cpoints, noOfVWords);                
        cl.save();        
        System.out.println("[OK]");
        return cl;
    }
    
    public static double[] getBoC(String fimg, int[][] palette, int resize, int patches, ColorConversion.ColorSpace cs,KDTree tree) {
        BufferedImage img = Utils.Image.ImageUtility.getImage(fimg);        
        img = Processor.resizeImage(img, resize, resize, false);
        return getBoC(img, palette, cs,tree);
    }
    
    public static double[] getBoC(BufferedImage img, int[][] palette, ColorConversion.ColorSpace cs) {        
        
        double[] vector = new double[palette.length];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color color = new Color(img.getRGB(x, y));
                int[] cl = ColorConversion.convertFromRGB(cs, color.getRed(), color.getGreen(), color.getBlue());
                int indx = Processor.getClosestColorIndx(palette, cl);
                vector[indx]++;
            }
        }
        return vector;
    }        
    
	public static double[] getBoC(BufferedImage img, int[][] palette, ColorConversion.ColorSpace cs,KDTree tree){
		
		double[] vector = new double[palette.length];           
        //bimg = ImageUtility.resizeImage(img, reScaleDim, reScaleDim,false);//fast scale using java 1.4
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color color = new Color(img.getRGB(x, y));                    
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
  
    
    //Default values for resize=256, patches=64
    public static double[] getLBoC(BufferedImage img, int[][] palette,Clusterer dictionary,
                                   int resize, int patches,ColorConversion.ColorSpace cs, KDTree tree) {   
        img = Processor.resizeImage(img, resize, resize, false);                
        double[] vector = new double[dictionary.getClusters().size()];
        BufferedImage blocks[] = Processor.splitImage(img, patches, patches);
        for(BufferedImage b:blocks){
            int indx = dictionary.clusterIndexOf(new CPoint(getBoC(b, palette, cs,tree)));
            vector[indx]++;
        }
        return vector;
    }
        
    public static double[][] list2DoubleArray(List<double[]> A){
    	
        double[][] ret=new double[A.size()][];
        for(int i=0;i<ret.length;i++){
            ret[i]=A.get(i);
        }
        return ret;
    }
}
