import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Shell;

import Utils.Utilities;

public class test_palette {

	public static void main(String[] args) throws Exception{
		
		String filepath = "C:\\Users\\leonidas\\Desktop\\Palette\\palete-64-RGB_vlfeat_distinct_300it_++.txt";
		String datPath = "C:\\Users\\leonidas\\Desktop\\Palette\\palete-64-RGB_vlfeat_distinct_300it_++.dat";

		List<int[]> palete = readFromFile(filepath, null);
		for (int[] colors : palete){
			System.out.println(colors[0] +" "+ colors[1] +" "+ colors[2]);
		}
		arrayToBinaryFile((int[][]) palete.toArray(new int[palete.size()][palete.get(0).length]), datPath);
		
	}
	
	public static List<int[]> readFromFile(String filePath,Shell shell) throws Exception {

		   List<int[]> centroid=new ArrayList<int[]>();
		   BufferedReader reader = null;	    
		 	try {
		 		reader = new BufferedReader(new FileReader(new File(filePath)));
			} catch (FileNotFoundException e1) {
				Utilities.showMessage("Could not Read File!", shell, true);
				e1.printStackTrace();

				return null;
			}
			
			String line=null;
			   try {
				   while ((line = reader.readLine()) != null){ 
					  int count = 0;
					  StringTokenizer tokenizedLine = new StringTokenizer(line," ");
					  int[] feature = new int[tokenizedLine.countTokens()];
					  
					  System.out.println(tokenizedLine.countTokens());
					  
					  while(tokenizedLine.hasMoreTokens()){
						   int color = Integer.parseInt(tokenizedLine.nextToken());
						   feature[count]=color;
						   count++;
					  }
					  centroid.add(feature);				 
				   }
			   }catch(Exception e){
					Utilities.showMessage("Error reading file!", shell, true);
					e.printStackTrace();
		 			return null;
			   }
			   try {
				   reader.close();
			   }catch (FileNotFoundException e) {
					Utilities.showMessage("Could not close File!", shell, true);
					e.printStackTrace();

		 			return null;
			   }
			   return centroid;
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

	
}
