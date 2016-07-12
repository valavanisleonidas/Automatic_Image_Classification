package ImageRepresentationModels.BoVW.BoVW;

import java.io.File;
import org.eclipse.swt.widgets.Shell;
import Utils.Utilities;

/*
 * 
 * Phow Dense Sift
 * 
 * 
 * 
 */

public class BoVWMatlab implements Runnable{
	private String projectPath,DBSourcePath,descriptorChoice,CentroidsPath;
	private int ClusterNum;
	private Shell shell;

	@Override
	public void run() {
		
		File dir = new File(DBSourcePath);//o fakelos me tis eikones
		String[] fileNames=null;
		try {
	   		//[0] : train  , [1] test
			fileNames =Utilities.getTrainTest(dir);
       		
	   		if ( !Utilities.collectionFilesAreCorrect(fileNames,shell) ) return;

		  
		} catch (Exception e1) {	}
		String cd_to_files = "cd([ pwd '\\matlab_files' ])";
		if(descriptorChoice.equals("Phow")){	
			try {
				
				String command_train = "BoVW_PHOW( '"+projectPath+"' , '"+fileNames[0]+"' , '"+CentroidsPath+"', "+ClusterNum+" , 'train' );";
				String command_test = "BoVW_PHOW( '"+projectPath+"' , '"+fileNames[1]+"' , '"+CentroidsPath+"', "+ClusterNum+" , 'test' );";
				
				Utilities.VLFeatMatlab(new String[] { cd_to_files ,command_train , command_test   });
			} catch (Exception e) {
				System.out.println(e);
				Utilities.showMessage("Matlab VLFeat BoVW Phow "+ClusterNum+" failed",shell,true);
				return;
			}
			
			Utilities.showMessage("VLFeat BoVW Phow "+ClusterNum+" completed",shell,false);
		}
		else if(descriptorChoice.equals("Dense Sift")){
			try {
				String command_train = "BoVW_DSift( '"+projectPath+"' ,  '"+fileNames[0]+"' , '"+CentroidsPath+"' , "+ClusterNum+" , 'train');";
				String command_test = "BoVW_DSift( '"+projectPath+"' ,  '"+fileNames[1]+"' , '"+CentroidsPath+"' , "+ClusterNum+" , 'test' );";
				
				Utilities.VLFeatMatlab(new String[] { cd_to_files ,command_train , command_test   });

			} catch (Exception e) {
				System.out.println(e);
				Utilities.showMessage("Matlab VLFeat BoVW Dense Sift "+ClusterNum+" failed",shell,true);
				return;
			}
			Utilities.showMessage("VLFeat Bovw Dense Sift  "+ClusterNum+" completed",shell,false);
		}
			
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public void setDBSourcePath(String dBSourcePath) {
		DBSourcePath = dBSourcePath;
	}	
	public void setClusterNum(int clusterNum) {
		ClusterNum = clusterNum;
	}	
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	public void setDescriptorChoice(String descriptorChoice) {
		this.descriptorChoice = descriptorChoice;
	}
	public void setCentroidsPath(String centroidsPath) {
		CentroidsPath = centroidsPath;
	}
}
