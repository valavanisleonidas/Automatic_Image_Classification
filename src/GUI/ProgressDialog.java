package GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class ProgressDialog implements Runnable 
{
    private Display display;
    private Label updateLabel,wholeLabel;
    private Shell shell;
    private volatile boolean running = true;
    
    public void terminate() {
        running = false;
    }
    public void close()
    {
    	shell.close();    	
    }
    
    public Display getDisplay(){
        return display;
    }
  
    
    /*
    *//**
     * @wbp.parser.entryPoint
     *//*
*/    public void run() 
    {
		if (running==true) {
	        display = new Display();
	        shell = new Shell(display);
	        shell.setSize(410, 188);
	        shell.setText("Progress window");
	        GridLayout gl_shell = new GridLayout();
	        gl_shell.numColumns = 6;
	        shell.setLayout(gl_shell);
	        shell.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
	        
	        //show in center of the screen
	        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
	        shell.setLocation((screenSize.width - shell.getBounds().width) / 2, (screenSize.height - shell.getBounds().height) / 2);
	      		
	        updateLabel = new Label(shell, SWT.RIGHT);
	        GridData gd_updateLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	        gd_updateLabel.widthHint = 60;
	        updateLabel.setLayoutData(gd_updateLabel);
	        updateLabel.setText("  0");
	        
	        Label divLabel = new Label(shell, SWT.CENTER);
	        GridData gd_divLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	        gd_divLabel.widthHint = 14;
	        divLabel.setLayoutData(gd_divLabel);
	        divLabel.setText("/");
	        
	        wholeLabel = new Label(shell, SWT.NONE);
	        GridData gd_wholeLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
	        gd_wholeLabel.widthHint = 38;
	        wholeLabel.setLayoutData(gd_wholeLabel);
	        wholeLabel.setText("0");
	        
	        Label completedLabel = new Label(shell, SWT.NONE);
	        completedLabel.setText("Completed!");
	        shell.open();
	     
	        while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
	     
	        try{
	        	display.dispose();
	        }
	        catch(Exception e){
	        	
	        }
        
		}
        
    }
	public synchronized void setWholeImageSize(final int value){
	    if (display == null || display.isDisposed()) 
	        return;
	    display.asyncExec(new Runnable() {
	
	        public void run() {
	            wholeLabel.setText(""+value);
	        }
	    });
	
	}
    public synchronized void updateImageProcessed(final int value){
        if (display == null || display.isDisposed()) 
            return;
        display.asyncExec(new Runnable() {

            public void run() {
            	updateLabel.setText("  "+value);
            }
        });

    }
    
  

}




