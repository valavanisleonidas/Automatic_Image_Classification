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

import Utils.Image.ColorConversion;
import Utils.Image.ColorConversion.ColorSpace;
import java.util.Comparator;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class ColorClusterComparator implements Comparator<CPoint>{
    private ColorConversion.ColorSpace cs;

    public ColorClusterComparator(ColorSpace cs) {
        this.cs = cs;
    }
    
    @Override
    public int compare(CPoint o1, CPoint o2){
        if(o1.getLocation().length != 3){ throw new UnsupportedOperationException("Color components must be = 3");}
        float[] v1 = o1.getLocation();
        int[] c1 = ColorConversion.convertToRGB(cs, Math.round(v1[0]), Math.round(v1[1]), Math.round(v1[2]));
                        
        float[] v2 = o2.getLocation();
        int[] c2 = ColorConversion.convertToRGB(cs, Math.round(v2[0]), Math.round(v2[1]), Math.round(v2[2]));
               
        double bW=0.6, cW=0.4;
        double p1 = cW*(0.7*c1[0]+0.2*c1[1]+0.1*c1[2]); //getPixelValue(c1);
        p1+=bW*(c1[0]+c1[1]+c1[2]);
        double p2 = cW*(0.7*c2[0]+0.2*c2[1]+0.1*c2[2]); //getPixelValue(c2);
        p2+=bW*(c2[0]+c2[1]+c2[2]);
                
        return (p1 > p2 ? -1
                : (p1 == p2 ? 0 : 1));        
    }
    
    private int getPixelValue(int rgb[]){
        int pixel = (rgb[0] & 0xFF) << 16 |
            (rgb[1] & 0xFF) << 8 |
            (rgb[2] & 0xFF);
        return pixel;
    }
    
}
