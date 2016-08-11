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

import Utils.DistanceFunctions;
import Utils.Utilities;
import Utils.Image.ColorConversion;
import Utils.Image.ColorConversion.ColorSpace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.awt.image.WritableRaster;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.semanticmetadata.lire.utils.ImageUtils;



/**
 *
 * @author Spyridon Stathopoulos
 */
public class Processor {
     
    public static double[][] getImagePixels(BufferedImage img){
        int n=img.getHeight(),m=img.getWidth();
        double[][] ret= new double[3][img.getWidth()*img.getHeight()];
        double[] data = img.getData().getPixels(img.getMinX(),img.getMinY(), img.getWidth(), img.getHeight(),(double[])null);
                        
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){                     
                ret[0][i*n+j] =data[(i*m*3)+(j*3+0)];
                if(img.getRaster().getNumBands()==3){
                    ret[1][i*n+j] =data[(i*m*3)+(j*3+1)];
                    ret[2][i*n+j] =data[(i*m*3)+(j*3+2)];
                }
            }
        }
        return ret;
    }
                
    public static BufferedImage getEmptyImage(){
        BufferedImage img = new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);  
        Graphics2D g2d = (Graphics2D)img.getGraphics();
        Font font = new Font("Arial", Font.PLAIN, 25);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString("Empty", 128, 128);
        g2d.dispose();
        return img;                
    }
    
    public static BufferedImage getTextImage(String text){
        BufferedImage img = new BufferedImage(256,256,BufferedImage.TYPE_INT_RGB);  
        Graphics2D g2d = (Graphics2D)img.getGraphics();
        Font font = new Font("Arial", Font.PLAIN, 25);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawString(text, 128, 128);
        g2d.dispose();
        return img;
    }
    
     public static BufferedImage convertToGrayScale(BufferedImage img) {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return image;
    }

    public static BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight, 
                                            boolean keepAspectRatio) {
        int w = img.getWidth();
        int h = img.getHeight();
        
        if(keepAspectRatio){
            newHeight = h * newWidth / w;
        }
        int type=img.getType();
        if(type==0) type = TYPE_INT_RGB;
        BufferedImage dimg = new BufferedImage(newWidth, newHeight,type);
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
    
    public static BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight){
        return ImageUtils.scaleImage(img, newWidth, newHeight);//fast scale using java 1.4    
    }
     
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
    
    public static BufferedImage[] splitImage(BufferedImage img, int[] cols, int[] rows){
        List<BufferedImage> imgs=new ArrayList<BufferedImage>();
        for(int i=0;i<cols.length;i++){
            BufferedImage blocks[]=splitImage(img,cols[i],rows[i]);
            for(BufferedImage b:blocks){
                imgs.add(b);
            }
        }
        return imgs.toArray(new BufferedImage[0]);
    }
        
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
    
    public static BufferedImage[] centerSplit(BufferedImage img, int centerPercent){
        //center of image
        int centX=img.getWidth()/2;
        int centY=img.getHeight()/2;     
        
        //center part dimensions        
        float centPct= (float)centerPercent/ 100;
        int centW= (int) (img.getWidth() * centPct);
        int centH= (int) (img.getHeight() * centPct);        
        //border offsets
        float brdPct=(1-centPct)/2;
        int offX = (int) (img.getWidth() *brdPct);
        int offY = (int) (img.getHeight()*brdPct);
        //The border pixels are filled with black full TRANSPARENT pixels.
        Color cl = new Color(0, 0, 0);
        Graphics2D g;
        BufferedImage imgs[] = new BufferedImage[5];
        
        //split image int 2 x 2
        BufferedImage bimgs[] = splitImage(img,2,2);
        //upper left
        imgs[0] = bimgs[0];
        g = imgs[0].createGraphics();
        g.setColor(cl);
        g.fillRect(offX, offY, centW/2, centH/2);
        g.dispose();
        //upper right
        imgs[1] = bimgs[1];
        g = imgs[1].createGraphics();
        g.setColor(cl);
        g.fillRect(0, offY, centW/2, centH/2);
        g.dispose();
        //lower left
        imgs[2] = bimgs[2];
        g = imgs[2].createGraphics();
        g.setColor(cl);
        g.fillRect(offX,0, centW/2, centH/2);
        g.dispose();
        //lower right
        imgs[3] = bimgs[3];
        g = imgs[3].createGraphics();
        g.setColor(cl);
        g.fillRect(0, 0, centW/2, centH/2);
        g.dispose();
        //center
        imgs[4] = new BufferedImage(centW,centH,img.getType());        
        g = imgs[4].createGraphics();
        g.setColor(cl);
        g.drawImage(img,0,0,centW,centH,offX,offY,img.getWidth()-offX,img.getHeight()-offY,null);
        g.dispose();
    
        return imgs;
    }
    
    public static BufferedImage conCatCenterSplit(BufferedImage imgs[],int centerPercent, 
                                                  Color borderColor){
        
        if(imgs.length!=5) {throw new InvalidParameterException("imgs.length != 5\nNot a center split");}
        int imgW = (imgs[4].getWidth()*100)/centerPercent;
        int imgH = (imgs[4].getHeight()*100)/centerPercent;
        //center of image
        int centX=imgW/2;
        int centY=imgH/2;
        //center part dimensions        
        float centPct= (float)centerPercent/ 100;
        int centW= (int) (imgW * centPct);
        int centH= (int) (imgH * centPct);        
        //border offsets
        float brdPct=(1-centPct)/2;
        int offX = (int) (imgW*brdPct);
        int offY = (int) (imgH*brdPct);
        
        BufferedImage img=new BufferedImage(imgW,imgH,imgs[4].getType());
        Graphics2D g=img.createGraphics();
        g.setColor(borderColor);                
        g.setStroke(new BasicStroke(2));
        //draw borders
        //upper left
        g.drawImage(imgs[0],0,0,centX,centY,0,0,imgs[0].getWidth(),imgs[0].getHeight(),null);
        g.drawRect(0, 0, imgs[0].getWidth(),imgs[0].getHeight());
        
        //upper right
        g.drawImage(imgs[1],centX,0,imgW,centY,0,0,imgs[1].getWidth(),imgs[1].getHeight(),null);
        g.drawRect(centX, 0, imgs[1].getWidth(),imgs[1].getHeight());
        
        //lower left
        g.drawImage(imgs[2],0,centY,centX,imgH,0,0,imgs[2].getWidth(),imgs[2].getHeight(),null);
        g.drawRect(0, centY, imgs[2].getWidth(),imgs[2].getHeight());
                
        //lower right
        g.drawImage(imgs[3],centX,centY,imgW,imgH,0,0,imgs[0].getWidth(),imgs[3].getHeight(),null);
        g.drawRect(centX,centY, imgs[3].getWidth(),imgs[3].getHeight());
        
//        //center                    
        g.drawImage(imgs[4],offX,offY,imgW-offX,imgH-offY,0,0,imgs[4].getWidth(),imgs[4].getHeight(),null);
        g.drawRect(offX,offY, imgs[4].getWidth(),imgs[4].getHeight());
        return img;                
    }
           
    public static BufferedImage quantizeImage(BufferedImage img, int colors){ 
        int scale = 256/colors;        
        BufferedImage img2 = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());      
        WritableRaster rst =img2.getRaster();
         for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color cl = new Color(img.getRGB(i, j));
                int r=cl.getRed();
                r = (r/scale)*256 / colors;
                int g=cl.getGreen();
                g = (g/scale)*256 / colors;
                int b=cl.getBlue();
                b = (b/scale)*256 / colors;                
                rst.setPixel(i, j, new int[]{r,g,b});
            }
         }                        
        return img2;                 
    }
    
    
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
        
        return dimg;        
    }
    
    public static BufferedImage getImageFromPalette(BufferedImage img, int[][] palette, ColorConversion.ColorSpace cs){
        BufferedImage ret = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        Graphics2D g = ret.createGraphics();
        for(int x=0;x<img.getWidth();x++){
            for(int y=0;y<img.getHeight();y++){
                Color color = new Color(img.getRGB(x, y));
                int[] cl = new int[]{color.getRed(),color.getGreen(),color.getBlue()};
                cl = ColorConversion.convertFromRGB(cs, cl[0], cl[1], cl[2]);
                cl = getPaletteClosestColor(palette,cl);
                cl = ColorConversion.convertToRGB(cs, cl[0], cl[1], cl[2]);
                color = new Color(cl[0],cl[1],cl[2]);
                g.setColor(color);
                ret.setRGB(x, y, color.getRGB());
            }
        }
        return ret;
    }
    
    public static int[] getPaletteClosestColor(int[][] palette, int[] cl){                
        return palette[getClosestColorIndx(palette,cl)];
    }
    
    public static int getClosestColorIndx(int[][] palette, int[] cl){        
        double minDist=DistanceFunctions.getL2Distance(palette[0], cl);
        int minIndx = 0;
        
        for(int i=1;i<palette.length;i++){
            double d = DistanceFunctions.getL2Distance(palette[i], cl);
            if(minDist>d){
                minDist = d;
                minIndx = i;
            }            
        }                
        return minIndx;
    }
            
    public static BufferedImage getPaletteImg(int[][] palette, int colsPerRow, int sizeHW,
                                                   ColorConversion.ColorSpace cs){
         //int rows = sizeHW / colsPerRow;
         int w =(int) Math.round(Math.sqrt(sizeHW));
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
                
             col++;
             if(col>=colsPerRow){ 
                 row++;
                 col=0;
             }
         }         
         return dimg;
    }  
}
