package GUI;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;

public class Help {

	protected Shell shell;
	private Text text;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Help window = new Help();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents(display);
		
	
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
		shell = new Shell();
		shell.setSize(800, 506);
		shell.setText("Help");
		
		//show in center of the screen
		Rectangle screenSize = display.getPrimaryMonitor().getBounds();
		shell.setLocation((screenSize.width - shell.getBounds().width) / 2, (screenSize.height - shell.getBounds().height) / 2);
		
		shell.setLayout(new FillLayout());
	
		
		text = new Text(shell,  SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		text.setBounds(10, 194, 429, 212);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		text.setText("  options:\r\n      "
				+ "      -s svm_type : set type of SVM (default 0)\r\n   "
				+ "                 0 -- C-SVC\r\n        "
				+ "            1 -- nu-SVC\r\n           "
				+ "         2 -- one-class SVM\r\n        "
				+ "            3 -- epsilon-SVR\r\n       "
				+ "             4 -- nu-SVR\r\n           "
				+ " -t kernel_type : set type of kernel function (default 2)\r\n "
				+ "                 "
				+ "  0 -- linear: u'*v\r\n        "
				+ "            1 -- polynomial: (gamma*u'*v + coef0)^degree\r\n     "
				+ "               2 -- radial basis function: exp(-gamma*|u-v|^2)\r\n          "
				+ "          3 -- sigmoid: tanh(gamma*u'*v + coef0)\r\n        "
				+ "    -d degree : set degree in kernel function (default 3)\r\n    "
				+ "        -g gamma : set gamma in kernel function (default 1/num_features)\r\n     "
				+ "       -r coef0 : set coef0 in kernel function (default 0)\r\n      "
				+ "      -c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\r\n  "
				+ "          -n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\r\n   "
				+ "         -p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\r\n        "
				+ "    -m cachesize : set cache memory size in MB (default 100)\r\n      "
				+ "      -e epsilon : set tolerance of termination criterion (default 0.001)\r\n      "
				+ "      -h shrinking: whether to use the shrinking heuristics, 0 or 1 (default 1)\r\n   "
				+ "         -b probability_estimates: whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\r\n     "
				+ "       -wi weight: set the parameter C of class i to weight*C, for C-SVC (default 1)\r\n\t\t"
				+ "-v n : n-fold cross validation mode\r\n\t\t"
				+ "-q : quiet mode (no outputs)\r\n\r\n      "
				+ "      The k in the -g option means the number of attributes in the input data.");
		text.setEditable(false);
		
	}
}
