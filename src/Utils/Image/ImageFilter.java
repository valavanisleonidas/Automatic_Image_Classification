package Utils.Image;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageFilter  extends javax.swing.filechooser.FileFilter implements FileFilter{
		
	String GIF = "gif";
	static String PNG = "png";
	String JPG = "jpg";
	String BMP = "bmp";
	String JPEG = "jpeg";
	   @Override
	    public boolean accept(File pathname) {
	        String fname = pathname.getName().toLowerCase();
	        if(fname.endsWith(".jpeg"))
	            return true;
	        if(fname.endsWith(".jpg"))
	            return true;
	        if(fname.endsWith(".png"))
	            return true;
	        if(fname.endsWith(".bmp"))
	            return true;
	        if(fname.endsWith(".gif"))
	            return true;        
	        
	        return false;
	    }

	    @Override
	    public String getDescription() {
	        return "Image files (*.jpg,*.jpeg, *.png, *.bmp, *.gif)";
	    }
	  
	    public static FileNameExtensionFilter[] getImageFileFilters(){
	        FileNameExtensionFilter[] ret = new FileNameExtensionFilter[4];
	        ret[0] = new FileNameExtensionFilter("PNG","png");
	        ret[1] = new FileNameExtensionFilter("JPG","jpg","jpeg");        
	        ret[2] = new FileNameExtensionFilter("BMP","bmp");
	        ret[3] = new FileNameExtensionFilter("GIF","gif");
	        return ret;
	    }
	    
	    //returns all images of folder 'dirPath' and subfolders
	     public static String[] getDirFiles(String dirPath, int sampleSize, boolean searchSubDirs){  
	        ImageFilter ffilt=new ImageFilter();
	        List<String> retFiles = new ArrayList<String>();
	        int sampleLimit=0;
	        File dir = new File(dirPath);                                
	        File[] files = dir.listFiles();
	        for(File f:files){
	            if(f.isFile() && ffilt.accept(f) && sampleLimit<sampleSize){
	                retFiles.add(f.getPath());
	                sampleLimit++;
	            }
	            else if (f.isDirectory() && searchSubDirs){
	                String[] subfiles = getDirFiles(f.getPath(),sampleSize,true);
	                retFiles.addAll(Arrays.asList(subfiles));                
	            }
	        }               
	        return retFiles.toArray(new String[0]);
	    }	
	     	
	     //returns true if file is .PNG
		public static boolean isPNG(File file) {
				if(file != null) {
						if(file.isDirectory())
							return false;
						String extension = getExtension(file);
						if(extension != null && isPngExtension(extension))
							return true;
				}
				return false;
			}	
			
		//returns true if file is Image
		public boolean isImage(File file) {
			if(file != null) {
					if(file.isDirectory())
						return false;
					String extension = getExtension(file);
					if(extension != null && isSupported(extension))
						return true;
			}
			return false;
		}
		//returns extension of file
		public static String getExtension(File file) {
			if(file != null) {
				String filename = file.getName();
				int dot = filename.lastIndexOf('.');
				if(dot > 0 && dot < filename.length()-1)
					return filename.substring(dot+1).toLowerCase();
			}
			return null;
		}
		//returns true if extension is png
		private static boolean isPngExtension(String ext) {
			return  ext.equalsIgnoreCase(PNG);
					
			}
		
		private boolean isSupported(String ext) {
			return ext.equalsIgnoreCase(GIF) || ext.equalsIgnoreCase(PNG) ||
					ext.equalsIgnoreCase(JPG) || ext.equalsIgnoreCase(BMP) ||
					ext.equalsIgnoreCase(JPEG);
			}
}