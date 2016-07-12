package Classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.eclipse.swt.widgets.Shell;
import Classification.libSVM.svm_train;
import Utils.Utilities;


public class TrainClassifier implements Runnable{

    private String trainDataPath,command,saveModelPath;
    private Shell shell;
 
	public TrainClassifier(String command,String trainDataPath,String saveModelPath,Shell shell){
		this.command = command;
		this.saveModelPath=saveModelPath;
		this.trainDataPath = trainDataPath;
		this.shell = shell;
	}
	
	@Override
	public void run(){
		try {
			if( trainData() )
				Utilities.showMessage("Train completed Successfully", shell, false);
		} catch (IOException e) {
			Utilities.showMessage("Something is wrong in the parameters or the train file!\nSee help", shell, true);
			e.printStackTrace();
		}
		
	}
	
	//trains data with given command and saves model into saveModelPath
	public boolean trainData() throws IOException{
		File trainFile=new File(trainDataPath);
    	//if train file does not exist in datapath given
		if(!trainFile.exists()){
			Utilities.showMessage("Can't find train file!", shell, true);
 			return false;
		}
				String[] trainArgs;
				//default parameter settings
				if(command.trim().equals("")){
					trainArgs=new String[2];
					trainArgs[0]=trainDataPath;
					trainArgs[1]=saveModelPath;
				}else{    //if there are parameters parse them
					
					//remove leading and traling whitespaces with regex
					command = command.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
					String delims = "[ ,]+";
					//split command into tokens by space (osa spaces theloume vazoume kai to pairnei)
					String[] parameters = command.split(delims);
				
					trainArgs=new String[parameters.length + 2];
					for(int i=0;i<parameters.length;i++){
						trainArgs[i]=parameters[i];
					}
					
					trainArgs[parameters.length]=trainDataPath;
					trainArgs[parameters.length + 1]=saveModelPath;		
				} 

				
			try{	
				svm_train st = new svm_train();
				st.run(trainArgs);
			}catch(NumberFormatException e){
				Utilities.showMessage("Wrong input/train file given!\nCheck if it is LibSVM format!\nCheck if it exists!", shell, true);
				e.printStackTrace();
				return false;
			}catch(FileNotFoundException e1){
				e1.printStackTrace();
				Utilities.showMessage("Give correct commands", shell, true);
				return false;
			}
			return true;
	}
	
	public String getTrainDataPath() {
		return trainDataPath;
	}
	public void setTrainDataPath(String trainDataPath) {
		this.trainDataPath = trainDataPath;
	}
	
}
