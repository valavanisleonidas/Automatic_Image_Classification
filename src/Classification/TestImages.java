package Classification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import Utils.Utilities;
import Classification.libSVM.svm_predict;

public class TestImages implements Runnable{


    private String testImagesPath,modelPath,resultFilePath,command;
    private Shell testShell;
	private List<double []> probabilities = new ArrayList<double[]>();
	private int[] labels;

	public TestImages(String testImagesPath,String modelPath, String resultFilePath,String command,Shell shell){
		this.testImagesPath = testImagesPath;
		this.modelPath = modelPath;
		this.resultFilePath = resultFilePath;
		this.testShell=shell;
		this.command=command;
	}
	
	@Override
	public void run() {
		try {
			 testingImages();
		} catch (IOException e) {
			Utilities.showMessage("Something is wrong in the parameters or the test file!\nSee help", testShell, true);
			e.printStackTrace();
		}
	}
	
	
	//test images using svm_predict
	public boolean testingImages() throws IOException{
		File testFile=new File(testImagesPath);
		File modelFile=new File(modelPath);

    	//if train file does not exist in datapath given
		if(!testFile.exists()){
			Utilities.showMessage("Can't find model file!", testShell, true);
			return false;
		}
		if(!modelFile.exists()){
			System.out.println("Mpika2");

			Utilities.showMessage("Can't find model file!", testShell, true);
			return false;
		}
		
			String[] testArgs;
			//default parameter settings
			if(command.trim().equals("")){
				testArgs=new String[3];
				testArgs[0]=testImagesPath;
				testArgs[1]=modelPath;
				testArgs[2]=resultFilePath;

			}//if there are parameters parse them
			else{
				//remove leading and traling whitespaces with regex
				command = command.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
				String delims = "[ ,]+";
				//split command into tokens by space (osa spaces theloume vazoume kai to pairnei)
				String[] parameters = command.split(delims);
			
				testArgs=new String[parameters.length + 3];
				for(int i=0;i<parameters.length;i++){
					testArgs[i]=parameters[i];
				}
				
				testArgs[parameters.length]=testImagesPath;
				testArgs[parameters.length + 1]=modelPath;
				testArgs[parameters.length + 2]=resultFilePath;
			}
			
			try{
				//test the model with the given/default parameters settings
				svm_predict predict = new svm_predict();
				predict.main(testArgs);

				if (command.contains("-b 1")){
					probabilities = predict.getProbabilities();
					labels=predict.getLabels();
				}
			}catch(NumberFormatException e){
				e.printStackTrace();

				Utilities.showMessage("Wrong test file given!\nCheck if it is LibSVM format!\nCheck if it exists!", testShell, true);
				return false;
			}
			
			return true;
			
	}

	public List<double []> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(List<double []> probabilities) {
		this.probabilities = probabilities;
	}

	public int[] getLabels() {
		return labels;
	}
	
	public int getLabelsIndex(int i) {
		return labels[i];
	}

	public void setLabels(int[] labels) {
		this.labels = labels;
	}
	
}
