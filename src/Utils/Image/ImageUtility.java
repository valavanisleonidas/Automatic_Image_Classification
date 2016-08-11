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
package Utils.Image;


import Utils.Image.ColorConversion.ColorSpace;
import ij.ImagePlus;
import ij.process.ImageConverter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class ImageUtility {
    
	public enum Format{ JPEG, PNG, BMP, WBMP, GIF };
	
	//get buffered image from path 'imageFile'
	public static BufferedImage getImage(String imageFile) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imageFile));
        } catch (Exception ex) {
            //Failed to read try with ImageJ.ImagePlus
            try{
                ImagePlus imgPlus = new ImagePlus(imageFile);
                // converting the image to RGB
                ImageConverter ic = new ImageConverter(imgPlus);               
                ic.convertToRGB();               
                img = imgPlus.getBufferedImage();
                // returning the BufferedImage instance
            }catch(Exception iex){
                System.out.println("Failed to read: " + imageFile);
                ex.printStackTrace();
            }                      
        }
        return img;
    }
	
	public static void saveImage(BufferedImage img, Format format, String file){
        File outputfile = new File(file);
        try {
            ImageIO.write(img, format.toString(), outputfile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }      
	
    //resizes image to given width and height
    public static BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight,
            boolean keepAspectRatio) {
        int w = img.getWidth();
        int h = img.getHeight();

        if (keepAspectRatio) {
            newHeight = h * newWidth / w;
        }
        int type = img.getType();
        if (type == 0) {
            type = TYPE_INT_RGB;
        }
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
    //splits image into rows X cols and returns array of bufferedImage 
    public static BufferedImage[] splitImage(BufferedImage img, int cols, int rows) {
        int w = img.getWidth() / cols;
        int h = img.getHeight() / rows;
        int num = 0;
        BufferedImage imgs[] = new BufferedImage[cols * rows];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                imgs[num] = new BufferedImage(w, h, img.getType());
                // Tell the graphics to draw only one block of the image
                Graphics2D g = imgs[num].createGraphics();
                g.drawImage(img, 0, 0, w, h, w * x, h * y, w * x + w, h * y + h, null);
                g.dispose();
                num++;
            }
        }
        return imgs;
    }
    //splits image into rows X cols and returns array of bufferedImage 
    public static BufferedImage[] splitImage(BufferedImage img, int[] cols, int[] rows) {
        List<BufferedImage> imgs = new ArrayList<BufferedImage>();
        for (int i = 0; i < cols.length; i++) {
            BufferedImage blocks[] = splitImage(img, cols[i], rows[i]);
            for (BufferedImage b : blocks) {
                imgs.add(b);
            }
        }
        return imgs.toArray(new BufferedImage[0]);
    }
    //concatenate images into one
    public static BufferedImage conCatImages(BufferedImage[] imgs, int cols, int rows) {
        if (imgs.length > 0) {
            int w = imgs[0].getWidth();
            int h = imgs[0].getHeight();
            int num = 0;
            BufferedImage dimg = new BufferedImage(w * cols, h * rows, imgs[0].getType());
            Graphics2D g = dimg.createGraphics();
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    g.drawImage(imgs[num], w * x, h * y, w * x + w, h * y + h, 0, 0, w, h, null);
                    g.drawRect(w * x, h * y, w, h);
                    num++;
                }
            }
            g.dispose();
            return dimg;
        } else {
            return null;
        }
    }
    //get dominant color of image into colorspace cs
    public static int[] getDominantColor(BufferedImage img, ColorSpace cs){         
       int ret[] = new int[3];
       Map<String,Integer> domC = new HashMap<String,Integer>();
       for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color cl = new Color(img.getRGB(i, j));                                
                int r=cl.getRed();
                int g=cl.getGreen();
                int b=cl.getBlue();
                int color[] = ColorConversion.convertFromRGB(cs, r,g,b);
                String key="";
                for(int c=0;c<color.length;c++){key+=color[c]+";";}
                if(domC.containsKey(key)){                    
                    domC.put(key, domC.get(key) + 1);
                }else{
                    domC.put(key, 1);
                }
            }
       }       
       
       String keyMax="";
       int max=-1;       
       for(Map.Entry<String,Integer> entry:domC.entrySet()){
           if(max<entry.getValue()){
               max = entry.getValue();
               keyMax = entry.getKey();                       
           }
       }      
       
       String[] scolors = keyMax.split(";");
       ret[0] = Integer.valueOf(scolors[0]);
       ret[1] = Integer.valueOf(scolors[1]);
       ret[2] = Integer.valueOf(scolors[2]);                      
       return ret;
    }
    //get dominant color of image into colorspace cs
    public static int[][] getDominantColors(BufferedImage img,int cols, int rows,
                                            ColorSpace cs){
        int[][] ret = new int[cols*rows][3];
        BufferedImage imgs[] = splitImage(img,cols,rows);
        for(int i=0;i<imgs.length;i++){
            int[] cl = getDominantColor(imgs[i],cs);
            for(int j=0;j<3;j++){ret[i][j] = cl[j];}
        }
        return ret;
    }
    //get dominant color of image into colorspace cs
    public static BufferedImage getDominantColorsImage(BufferedImage img,int cols, int rows,
                                            ColorSpace cs){
        int[][] colors = getDominantColors(img,cols,rows,cs);
        BufferedImage imgs[] = splitImage(img,cols,rows);
        int w = imgs[0].getWidth();
        int h = imgs[0].getHeight();
        BufferedImage dimg = new BufferedImage(w * cols, h * rows, imgs[0].getType());
        Graphics2D g = dimg.createGraphics();
        int num = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int[] cl = ColorConversion.convertToRGB(cs,colors[num][0],colors[num][1],colors[num][2]);                                                
                g.setColor(new Color(cl[0],cl[1],cl[2]));
                g.fillRect(w * x, h * y, w, h);
//                g.setColor(Color.white);
//                g.drawRect(w * x, h * y, w, h);                
                num++;
            }
        }
        colors=null;
        return dimg;        
    }
    //get image  from palete   
    public static BufferedImage getImageFromPalette(BufferedImage img, int[][] palette, ColorConversion.ColorSpace cs){
       
    	/*
    	
        KDTreeing kdTree = new KDTreeing();
        List<double[]> paletteList = Utilities.intArray2DoubleList(palette);
        KDTree tree=KDTreeing.createTree(paletteList);
       */
    	BufferedImage ret = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        Graphics2D g = ret.createGraphics();
        for(int x=0;x<img.getWidth();x++){
            for(int y=0;y<img.getHeight();y++){
                Color color = new Color(img.getRGB(x, y));
                int[] cl = new int[]{color.getRed(),color.getGreen(),color.getBlue()};
                cl = ColorConversion.convertFromRGB(cs, cl[0], cl[1], cl[2]);
             
                //closest color with L2 distance
                 cl = getClosestColor(palette,cl);
                
        		/* closest color with kd-tree	
                int index= kdTree.SearchTree(new double[]{cl[0],cl[1],cl[2]},tree);
        		cl=palette[index];*/
        		
                cl = ColorConversion.convertToRGB(cs, cl[0], cl[1], cl[2]);
                color = new Color(cl[0],cl[1],cl[2]);
                g.setColor(color);
                ret.setRGB(x, y, color.getRGB());
            }
        }
      
        return ret;
    }
     
    private static int[] getClosestColor(int[][] palette, int[] cl){                
       return palette[getClosestColorIndx(palette,cl)];
   }
    //get closest color to index using L2 distance
    private static int getClosestColorIndx(int[][] palette, int[] cl){        
        double minDist=getL2Distance(palette[0], cl);
        int minIndx = 0;
        
        for(int i=1;i<palette.length;i++){
            double d = getL2Distance(palette[i], cl);
            if(minDist>d){
                minDist = d;
                minIndx = i;
            }            
        }                
        return minIndx;
    }
    //returns L2 distance between 2 arrays
     public static double getL2Distance(int[] point1, int[] point2) {
        double dist = 0;
        for (int i = 0; i < point1.length; i++) {
            dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        return Math.sqrt(dist);
    }
    
    public static BufferedImage getPaletteImg(int[][] palette,int imgSize, ColorConversion.ColorSpace cs){
         //int rows = sizeHW / colsPerRow;
         int colsPerRow=(int)Math.round((Math.sqrt(palette.length)));
         int sizeHW=imgSize;
         int w =(int) Math.round(sizeHW/colsPerRow);//Math.round(Math.sqrt(sizeHW)/colsPerRow);
         int h = w;
                  
         BufferedImage dimg = new BufferedImage(sizeHW,sizeHW,BufferedImage.TYPE_INT_RGB);
         Graphics2D g = dimg.createGraphics();
         int col = 0;
         int row = 0;
         
         //int[][] palete = getPalleteFromClusters(clusterFile, true, cs);
         for(int[] cl:palette){
             cl = ColorConversion.convertToRGB(cs, cl[0], cl[1], cl[2]);
             g.setColor(new Color(cl[0],cl[1],cl[2]));
             g.fillRect(w * row, h * col, w, h);
             g.setColor(Color.white);
             g.drawRect(w * row, h * col, w, h);    
                
             row++;
             if(row>=colsPerRow){ 
                 col++;
                 row=0;
             }
         }         
         return dimg;
    }  
}
