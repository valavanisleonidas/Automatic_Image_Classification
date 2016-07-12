package ImageRepresentationModels.GBoC;

import java.io.File;
import org.eclipse.swt.widgets.Shell;
import Utils.Utilities;
import Utils.Image.ColorConversion.ColorSpace;

public class GBocDescriptor{
	 
	private String dbSourcePath,projectPath,FeaturesFormat;
	private int numberOfColors, numberOfPatches;
	private ColorSpace cs;
	private Shell shell;
		
	public void createFeatureVectors() throws Exception{
		String PaletePath = projectPath+"\\palete"+numberOfColors+"-"+cs.toString()+".txt";
		if (!new File(PaletePath).exists()){
			Utilities.showMessage("Palete given does not exist in project path", shell, true);
			return;
		}
		//get path of test/train original images 
		String[]  fileNamesBoc =Utilities.getTrainTest(new File(dbSourcePath));
   		
		if ( !Utilities.collectionFilesAreCorrect(fileNamesBoc,getShell()) ) return;
   		
		String cdCurrentFolder = "cd(['"+projectPath+"' '\\..\\matlab_files\\GBoC_Improved' ]);";
 		PaletePath = PaletePath.replace(".dat", ".txt");
		projectPath = projectPath+"\\";  
		
		String RunGBoC = "exportGBoCImprov('"+fileNamesBoc[0]+"' , '"+fileNamesBoc[1]+"'  , '"+projectPath+"' , '"+PaletePath+"' , "+getNumberOfBlocks()+" , '"+cs.toString()+"' , '"+FeaturesFormat+"'  );";		
   		
		//run matlab commands   
		Utilities.VLFeatMatlab(new String[]{ cdCurrentFolder ,RunGBoC });
				
		
		Utilities.showMessage("Gboc number of colors: "+numberOfColors
				+"  number of blocks: "+getNumberOfBlocks()+" , Completed successfully", shell, false);

	}
	 
  
	
public String getProjectPath(){
	return projectPath;
}

public int getNumberOfBlocks(){
	return (256/numberOfPatches);
}
public int getNumberOfPatches(){
	return numberOfPatches;
}
public void setNumberOfPatches(int numberOfPatches) {
	this.numberOfPatches = numberOfPatches;
}
public void setProjectPath(String projectPath) {
	this.projectPath = projectPath;
}
public void setDBSourcePath(String dbSourcePath) {
	this.dbSourcePath = dbSourcePath;
}	
public String getDBsourcePath(){
	return dbSourcePath;
}
public int getNumberOfColors(){
	return numberOfColors;
}
public void setNumberOfColors(int numberOfColors) {
	this.numberOfColors = numberOfColors;
}	
public String getColorSpace(){
	return cs.toString();
}
public void setCS(ColorSpace cs) {
	this.cs = cs;
}
public Shell getShell() {
	return shell;
}

public void setShell(Shell shell) {
	this.shell = shell;
}
public String getFeaturesFormat() {
	return FeaturesFormat;
}
public void setFeaturesFormat(String FeaturesFormat) {
	this.FeaturesFormat = FeaturesFormat;
}	
}
