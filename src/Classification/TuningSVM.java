package Classification;

import org.eclipse.swt.widgets.Shell;

import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import Utils.Utilities;

public class TuningSVM implements Runnable {
	
    private double bestc,bestg,bestcv ;
    private String filePath;
    private Shell shell;
    boolean finished;	
    
    public TuningSVM(String filePath,Shell shell){
		this.shell=shell;
		this.filePath=filePath;
		finished=false;
	}
	
    //invokes a grid search in matlab to find best parameters for c , g 
	public void run(){
	 	 //Create a proxy, which we will use to control MATLAB
	       MatlabProxyFactory factory = new MatlabProxyFactory();
	       MatlabProxy proxy;
		try {
			MatlabProxyFactoryOptions builder = new MatlabProxyFactoryOptions.Builder().setMatlabLocation("C:\\Program Files\\MATLAB\\R2010a\\bin\\matlab.exe").setUsePreviouslyControlledSession(true).setHidden(false).build();
	        factory = new MatlabProxyFactory(builder);
			proxy = factory.getProxy();
			
			proxy.eval("cd([ pwd '\\matlab_files' ])");
			
			Object[] parameters= proxy.returningEval(" gridSearch('"+filePath+"')",3);
			bestc = ((double[]) parameters[0])[0];
			bestg = ((double[]) parameters[1])[0];
			bestcv = ((double[]) parameters[2])[0];
			//Disconnect the proxy from MATLAB
			proxy.exit();
			//  proxy.disconnect();
			finished=true;
			Utilities.showMessage("Matlab finished successfully", shell,false);
		} catch (Exception e) {
			Utilities.showMessage("Something happened with Matlab", shell,true);
			e.printStackTrace();
		}
		
	}
	
	public double getBestg() {
		return bestg;
	}
	public double getBestc() {
		return bestc;
	}
	public double getBestcv() {
		return bestcv;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
}
