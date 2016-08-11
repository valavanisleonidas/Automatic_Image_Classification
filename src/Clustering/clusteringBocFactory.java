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
package Clustering;

import ImageRepresentationModels.BoC.BoCLibrary;
import Utils.Image.ImageFilter;
import Utils.Image.ColorConversion.ColorSpace;
import Utils.cluster.CPoint;
import Utils.Utilities;

import java.io.File;

import org.eclipse.swt.widgets.Shell;


public class clusteringBocFactory implements Runnable {

	 private String clusterDBPath, projectPath,comboKMeansChoice,colorspace,model;
	 private static int resize = 256;
	 private static int patches = 64;
	 private int numOfColors,sampleLimit;
	 private int [][]palete;
	 private Shell shell;
	 private ColorSpace cs;
	 private boolean isDistinctColors;
	  @Override
	  public void run() {
		  try {
			performCluster();
		} catch (Exception e) {
			e.printStackTrace();
		}
	  }	
	
	  public void performCluster() throws Exception{
	   
	   cs = Utilities.findColorSpace(colorspace);
       String paleteFileDat= isDistinctColors 
	    		? projectPath+"\\distinct_palete"+numOfColors+"-"+cs.toString()+".dat" 
	    		: projectPath+"\\palete"+numOfColors+"-"+colorspace+".dat";
    		   
    		   
	   //[0] : train  , [1] test
	   String []fileNames =Utilities.getTrainTest(new File(clusterDBPath));
       String[] sampleImages= ImageFilter.getDirFiles(fileNames[0],sampleLimit, true);
       System.out.println("lenght of sample images : "+sampleImages.length);
       for(String images : sampleImages){
    	   System.out.println(images.toString());
       }
       String outputPaleteName = isDistinctColors 
 	    		? projectPath+"\\distinct_palete"+numOfColors+"-"+cs.toString()+".txt" 
 	    		: projectPath+"\\palete"+numOfColors+"-"+cs.toString()+".txt";
        if(comboKMeansChoice.equals("Lire")){
        	 try {
        		 palete = BoCLibrary.createPalette(paleteFileDat, sampleImages, resize, patches, numOfColors, isDistinctColors, cs);
        		 Utilities.writePalete2TXT(palete, outputPaleteName,shell);	    

 	        	Utilities.showMessage("Lire Clustering with "+model+" , number of colors : "+numOfColors +" "
			    			+ " , color space : "+colorspace+"  completed successfully",shell,false);
 	        	
 		        sampleImages=null;

 	        } catch (Exception e) {
 	        	Utilities.showMessage("Lire Clustering with "+model+" , number of colors : "+numOfColors +" "
		    			+ " , color space : "+colorspace+" failed",shell,true);
				e.printStackTrace();
				 sampleImages=null;

 	        }
        }

	}
 
public void setComboKMeansChoice(String comboKMeansChoice) {
	this.comboKMeansChoice = comboKMeansChoice;
}

public void setClusterDBPath(String clusterDBPath) {
	this.clusterDBPath = clusterDBPath;
}

public void setProjectPath(String projectPath) {
	this.projectPath = projectPath;
}

public void setShell(Shell shell) {
	this.shell = shell;
}
public int getNumOfColors() {
	return numOfColors;
}
public void setNumOfColors(int numOfColors) {
	this.numOfColors = numOfColors;
}
public int [][] getPalette() {
	return palete;
}
public void setPalette(int [][] palete) {
	this.palete = palete;
}
public String getColorspace() {
	return colorspace;
}
public void setColorspace(String colorspace) {
	this.colorspace = colorspace;
}
public ColorSpace getCs() {
	return cs;
}
public void setCs(ColorSpace cs) {
	this.cs = cs;
}
public void setModel(String model) {
	this.model = model;
}

public int getSampleLimit() {
	return sampleLimit;
}

public void setSampleLimit(int sampleLimit) {
	this.sampleLimit = sampleLimit;
}

public boolean isDistinctColors() {
	return isDistinctColors;
}

public void setDistinctColors(boolean isDistinctColors) {
	this.isDistinctColors = isDistinctColors;
}

}
