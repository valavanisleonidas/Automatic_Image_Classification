package GUI;
/*
 * 
 * Contains main
 * Graphic User Interface for Content Based Image Retrieval
 * 
 */

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import ImageRepresentationModels.IRMFactory;
import Classification.TuningSVM;
import Classification.TestImages;
import Classification.TrainClassifier;
import Clustering.clusteringBocFactory;
import Clustering.clusteringBovwFactory;
import Utils.Image.ColorConversion.ColorSpace;
import Utils.Image.ImageUtility;
import Utils.Image.display.ImageViewer;
import ImageRepresentationModels.BoVW.BoVW.BoVWMatlab;
import Utils.Statistics;
import Utils.Utilities;
import ImageRepresentationModels.BoVW.BoVW.BoVWModel;
import ImageRepresentationModels.BoVW.Descriptors.Features;
import ImageRepresentationModels.BoVW.Descriptors.SiftFeatures;
import ImageRepresentationModels.BoVW.Descriptors.SurfFeatures;
import Utils.CreateXml;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;

public class GUI {

	private Shell shlClassification;
	private Text projectNameText,DBSourceText;
    private String testImagesPath,ModelPathTest,resultFilePath;
    private clusteringBovwFactory cluster1= new clusteringBovwFactory();
    private clusteringBovwFactory cluster2= new clusteringBovwFactory();
    private clusteringBocFactory clusterBoc= new clusteringBocFactory();
    private TuningSVM bestParameters;
    private String DBSourcePath,KeywordsPath,CentroidsPath,CentroidsPath2,PaletePath,
    trainDataPath,saveModelPathTrain;
    private int statisticsCounter,statisticsLabelCounter;
	private List <double[]> descriptorCenters1=new ArrayList<double[]>(); 
	private List <double[]> descriptorCenters2=new ArrayList<double[]>();
	private	Statistics metrics;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI window = new GUI();			
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Open the window.
	 * @throws IOException 
	 */
	public void open() throws IOException {
		Display display = Display.getDefault();
		createContents(display);
		shlClassification.open();
		
 	    Image image = new Image(display, System.getProperty("user.dir")+"\\images\\backgroundImage.jpg"); // vazoume photo sto background
		shlClassification.setBackground(display.getSystemColor(SWT.COLOR_WHITE));//vazoume xrwma aspro
		Composite composite = new Composite(shlClassification, SWT.NONE);
	    composite.setBackgroundImage(image);
	    composite.setBackgroundMode(SWT.INHERIT_FORCE);
	    composite.setBounds(0, 0, image.getBounds().width, image.getBounds().height);//vazoume tin eikona ekei pou theloume me ena composite
		ReadMetadata();

		while (!shlClassification.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents(Display display) {
				
//--------------------GUI Contents-----------------------//
		shlClassification = new Shell(display);
		shlClassification.setModified(true);
		shlClassification.setSize(675, 777);	
		shlClassification.setText("Image Classification");
		//show in center of the screen
		Rectangle screenSize = display.getPrimaryMonitor().getBounds();
		shlClassification.setLocation((screenSize.width - shlClassification.getBounds().width) / 2, (screenSize.height - shlClassification.getBounds().height) / 2);
	    
		//create menu bar
		Menu m = new Menu(shlClassification, SWT.BAR);
	    // create a file menu and add an exit item
	    final MenuItem file = new MenuItem(m, SWT.CASCADE);
	    file.setText("&Project");
	    final Menu filemenu = new Menu(shlClassification, SWT.DROP_DOWN);
	    file.setMenu(filemenu);
	    
	     final MenuItem exitItem = new MenuItem(filemenu, SWT.PUSH);
	     exitItem.setText("E&xit");    
	     
	      //action for exit button in menu bar
	      exitItem.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(SelectionEvent e) {
	      	  ExitProgram();
	        }
	      });
	   //Help
	    MenuItem help = new MenuItem(m, SWT.CASCADE);
	    help.setText("Help");
	    final Menu filemenu2 = new Menu(shlClassification, SWT.DROP_DOWN);
	    help.setMenu(filemenu2);   
	    final MenuItem helpLibsvm = new MenuItem(filemenu2, SWT.PUSH);
	    helpLibsvm.setText("&libSVM\tCTRL+L");
	    helpLibsvm.setAccelerator(SWT.CTRL + 'L');  	
	    shlClassification.setMenuBar(m);

		 //action for help button in menu bar
	    class help implements SelectionListener {
		      public void widgetSelected(SelectionEvent event) {
		     	  	Help help=new Help();
		     		help.open();
		       }
		      public void widgetDefaultSelected(SelectionEvent event) {      }
		    }
	    helpLibsvm.addSelectionListener(new help());
  //-------------------------------GUI Contents-----------------------//	
	
	//------------------CREATE TABS and GROUPS-----------------------------//
		TabFolder tabFolder_1 = new TabFolder(shlClassification, SWT.None);
	    tabFolder_1.setBounds(0, 103, 659, 616);	    
	    
	    TabItem homeTab = new TabItem(tabFolder_1, SWT.NONE);
	    TabItem preprocessTab = new TabItem(tabFolder_1, SWT.NONE);
	    TabItem trainTab = new TabItem(tabFolder_1, SWT.NONE);
	    TabItem testTab = new TabItem(tabFolder_1, SWT.NONE);
	    TabItem resultsTab = new TabItem(tabFolder_1, SWT.NONE);

	    Group preprocessGroup = new Group(tabFolder_1, SWT.NONE);
	    Group homeGroup = new Group(tabFolder_1, SWT.NONE);
	    Group trainGroup = new Group(tabFolder_1, SWT.NONE);
	    Group testGroup = new Group(tabFolder_1, SWT.NONE);
	    Group resultsGroup = new Group(tabFolder_1, SWT.NONE);
	    Group settingsGroup = new Group(tabFolder_1, SWT.NONE);
	    TabItem settingsTab = new TabItem(tabFolder_1, 0);
		settingsTab.setText("Settings");
		
		settingsTab.setControl(settingsGroup);
		
	    homeTab.setText("Home");
	    testTab.setText("Test");
	    trainTab.setText("Train");
	    preprocessTab.setText("Preprocess");
	    resultsTab.setText("Results");
	    preprocessTab.setControl(preprocessGroup);
	    trainTab.setControl(trainGroup);
	    testTab.setControl(testGroup);
	    homeTab.setControl(homeGroup);
	    resultsTab.setControl(resultsGroup);

		//-----------------PreprocessTab-----------------------------//
	    Button buttonPalette = new Button(preprocessGroup, SWT.NONE);
	    Text textpalete = new Text(preprocessGroup, SWT.BORDER);

	    buttonPalette.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		PaletePath= fileDialog(textpalete);
	    	}
	    });
	    buttonPalette.setVisible(false);
	    buttonPalette.setText("Palette");
	    buttonPalette.setBounds(348, 444, 139, 30);
	    
	    textpalete.setVisible(false);
	    textpalete.setEditable(false);
	    textpalete.setBounds(493, 444, 156, 30);
	
	    Text centroids2Text = new Text(preprocessGroup, SWT.BORDER);

	    Button buttonCentroids_2 = new Button(preprocessGroup, SWT.NONE);
	    buttonCentroids_2.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		CentroidsPath2= fileDialog(centroids2Text);
	    	}
	    });
	    buttonCentroids_2.setText("Centroids2");
	    buttonCentroids_2.setBounds(10, 500, 150, 30);
	    buttonCentroids_2.setVisible(false);
		
	    centroids2Text.setEditable(false);
	    centroids2Text.setBounds(166, 500, 176, 30);
	    centroids2Text.setVisible(false);
		   
	    Text centroidsText = new Text(preprocessGroup, SWT.BORDER);
	    centroidsText.setEditable(false);
	    centroidsText.setBounds(166, 444, 176, 30);
	    centroidsText.setVisible(false);
	    
	   Button buttonCentroids = new Button(preprocessGroup, SWT.NONE);
	   buttonCentroids.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		CentroidsPath= fileDialog(centroidsText);
	    	}
	    });
	   buttonCentroids.setBounds(10, 444, 150, 30);
	   buttonCentroids.setVisible(false);
	   buttonCentroids.setText("Centroids");
		
	   
	   
	   Label lblCreateXml = new Label(preprocessGroup, SWT.CENTER);
	   lblCreateXml.setText("1) Create XML");
	   lblCreateXml.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
	   lblCreateXml.setBounds(0, 30, 639, 29);
		
	   Label lblCluster = new Label(preprocessGroup, SWT.CENTER);
	   lblCluster.setText("2) Cluster");
	   lblCluster.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
	   lblCluster.setBounds(0, 164, 641, 29);
		
		Label lblBovw = new Label(preprocessGroup, SWT.CENTER);
		lblBovw.setText("3) Model");
		lblBovw.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblBovw.setBounds(0, 307, 641, 29);
		
		Text bestParametersText = new Text(trainGroup, SWT.BORDER | SWT.MULTI);
		bestParametersText.setEditable(false);
		bestParametersText.setBounds(211, 197, 325, 52);
	
		Text testPathText = new Text(testGroup, SWT.BORDER);
		testPathText.setEditable(false);
		testPathText.setBounds(202, 75, 447, 28);
	
		Text saveModelTextTest = new Text(testGroup, SWT.BORDER);
		saveModelTextTest.setEditable(false);
		saveModelTextTest.setBounds(202, 138, 447, 28);

		
		Button btnCreateXml = new Button(preprocessGroup, SWT.NONE);
		btnCreateXml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				CreateXML();
			}
		});
		btnCreateXml.setText("Create Xml");
		btnCreateXml.setBounds(240, 75, 164, 56);
		
	 	Text KmeansSampleText = new Text(settingsGroup, SWT.BORDER);
   	   	KmeansSampleText.setVisible(true);
   	    KmeansSampleText.setBounds(154, 330, 182, 23);
		
   	    Combo KMeansCombo = new Combo(settingsGroup, SWT.NONE);
		KMeansCombo.setBounds(153, 287, 182, 23);
				   
		Combo descriptorCombo = new Combo(settingsGroup, SWT.NONE);
		descriptorCombo.setBounds(462, 181, 182, 23);
	   
		Combo modelCombo = new Combo(settingsGroup, SWT.NONE);
		modelCombo.setBounds(154, 181, 160, 23);
	   
		Combo comboColorSpace = new Combo(settingsGroup, SWT.NONE);
		comboColorSpace.setBounds(154, 373, 182, 23);
		  
		
		Button isDistinctColorsBtn = new Button(preprocessGroup, SWT.CHECK);
		isDistinctColorsBtn.addSelectionListener(new SelectionAdapter() {
   	   		@Override
   	   		public void widgetSelected(SelectionEvent e) {
   	   		}
   	   	});
		isDistinctColorsBtn.setBounds(424, 273, 139, 16);
		isDistinctColorsBtn.setText("Distinct Palete Colors");
		
		Combo patchesCombo = new Combo(settingsGroup, SWT.BORDER);
		patchesCombo.setBounds(484, 373, 160, 23);
		Label lblNewLabel_1 = new Label(resultsGroup, SWT.NONE);
		     
		patchesCombo.add("1");
		patchesCombo.add("2");
		patchesCombo.add("4");
		patchesCombo.add("8");
		patchesCombo.add("16");
		patchesCombo.add("32");
		patchesCombo.setVisible(false);
	   	   	
		Text weightText = new Text(settingsGroup, SWT.BORDER);
		weightText.setBounds(154, 536, 182, 23);
		weightText.setVisible(false);
	   	   	
		DBSourceText = new Text(settingsGroup, SWT.BORDER);
		DBSourceText.setEditable(false);
		DBSourceText.setBounds(154, 79, 490, 23);
	
		Label libSVMLabel = new Label(settingsGroup, SWT.NONE);
		libSVMLabel.setText("Features Format");
		libSVMLabel.setBounds(358, 333, 95, 18);
		
		Combo comboFeaturesFormat = new Combo(settingsGroup, SWT.NONE);
		comboFeaturesFormat.setBounds(484, 330, 160, 23);
		comboFeaturesFormat.add("LibSVM Format");
		comboFeaturesFormat.add("Non-LibSVM Format");
		comboFeaturesFormat.add("Both");   	
		
		
		Text keywordsText = new Text(settingsGroup, SWT.BORDER);
		keywordsText.setEditable(false);
		keywordsText.setBounds(154, 113, 490, 23);
		
	 	Text KmeansClusterNumText = new Text(settingsGroup, SWT.BORDER);
   	   	KmeansClusterNumText.setBounds(484, 287, 160, 23);
   	 
   	   	
   	 	Button btnClusterAndExtract = new Button(preprocessGroup, SWT.CHECK);
   	   	btnClusterAndExtract.addSelectionListener(new SelectionAdapter() {
   	   		@Override
   	   		public void widgetSelected(SelectionEvent e) {
   	   		
   	   		}
   	   	});
   	   	btnClusterAndExtract.setBounds(424, 242, 176, 16);
   	   	btnClusterAndExtract.setText("Cluster And Extract Features");
		
   	   	Button extractFeaturesButton = new Button(preprocessGroup, SWT.NONE);
   	   	
		Button btnCluster = new Button(preprocessGroup, SWT.NONE);
		btnCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if not all components are filled in order to cluster then return
				if(!check2Cluster())
					return;
				
				boolean isDistinctColors = isDistinctColorsBtn.getSelection();
				//parse sampleLimit for clustering if given by user
				int sampleLimit = KmeansSampleText.getText()!="" 
						? Integer.parseInt(KmeansSampleText.getText()) 
						: Integer.MAX_VALUE;

			    String descriptorChoice=descriptorCombo.getText();
				String model = modelCombo.getText();
				String colorSpace = comboColorSpace.getText();
		
				try {
					if (modelCombo.getText().equals("Mixed (Bovw-Boc)")  
							|| modelCombo.getText().equals("Mixed (GraphBoc-Bovw)") ){
						
						//parse textBoxes and get values of Kmeans and number of clusters for models
						StringTokenizer KMeanstokens = new StringTokenizer(KMeansCombo.getText(),"-");
						StringTokenizer clusterNumTokens = new StringTokenizer(KmeansClusterNumText.getText()," ");
						
						if(clusterNumTokens.countTokens()!=2){
							MessageDialog.openError(shlClassification, "Error","Give two cluster numbers.\nFormat: Cluster1 Cluster2\nExample: 10 10");
							return;
						}
						//run cluster with BoVW
						Cluster(KMeanstokens.nextToken(),descriptorChoice,"Bag of Visual Words",colorSpace
								,Integer.parseInt(clusterNumTokens.nextToken()),sampleLimit,isDistinctColors);
						//run cluster with BoC || GBoC
						Cluster(KMeanstokens.nextToken(),"Bag of Colors","Bag of Colors",colorSpace
								,Integer.parseInt(clusterNumTokens.nextToken()),sampleLimit,isDistinctColors);	
					}	
					else{
						Cluster(KMeansCombo.getText(),descriptorChoice,model,colorSpace
								,Integer.parseInt(KmeansClusterNumText.getText()),sampleLimit,isDistinctColors);
					}
				} catch (Exception e1) {
					MessageDialog.openError(shlClassification, "Error","Clustering Failed");
					e1.printStackTrace();
				}
				
				// cluster and extract features
				if (btnClusterAndExtract.getSelection() )
					extractFeaturesButton.notifyListeners(SWT.Selection, new Event());
				
				
				
			}
			//returns false if not all mandatory information has been given
			private boolean check2Cluster(){
				if(!checkSettings(modelCombo,KmeansClusterNumText,descriptorCombo,KMeansCombo,comboColorSpace))
					return false;
				
				if(descriptorCombo.getText().equals("ColorCorrelogram")){
					MessageDialog.openError(shlClassification, "Error","Can't cluster using color correlogram.\nExtract features only");
					return false;
				}
				
				return true;
				
			}
		});
		btnCluster.setText("Cluster");
		btnCluster.setBounds(240, 233, 164, 56);

		Combo comboTFIDF = new Combo(settingsGroup, SWT.NONE);
		comboTFIDF.setBounds(154, 437, 182, 23);
		comboTFIDF.add("TF");
		comboTFIDF.add("TFIDF");
		
		Combo comboNorm = new Combo(settingsGroup, SWT.NONE);
		comboNorm.setBounds(487, 437, 157, 23);
		comboNorm.add("L1");
		comboNorm.add("L2");
		
		Combo FusionCombo = new Combo(settingsGroup, SWT.NONE);
		FusionCombo.setBounds(484, 536, 160, 23);
		FusionCombo.setVisible(false);     
		FusionCombo.add("Early Fusion");
		FusionCombo.add("Late Fusion");
		
		extractFeaturesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Mpikaaa");
				extractFeatures();
			}
			
			
			private void extractFeatures(){
				//if not all components are filled in order to extract features then return
				if(!check2ExtractFeatures())
					return;
					
				String descriptorChoice=descriptorCombo.getText();
				String colorSpace = comboColorSpace.getText();
				String featuresFormat = comboFeaturesFormat.getText();
				String normalization = comboNorm.getText();
				try {
					//factory for all extraction feature models
					IRMFactory factory= new IRMFactory();
					//variables same for all models
					factory.setDBSourcePath(DBSourcePath);
					factory.setDescriptorChoice(descriptorChoice);
					factory.setKMeansChoice(KMeansCombo.getText());
					factory.setModel(modelCombo.getText());
					factory.setProjectPath(createProjectPath());
					factory.setFusionType(FusionCombo.getText());
					factory.setFeaturesFormat(featuresFormat);
					factory.setShell(shlClassification);
					
					if (modelCombo.getText().equals("Mixed (Bovw-Boc)")){
						StringTokenizer KMeanstokens = new StringTokenizer(KMeansCombo.getText(),"-");
						StringTokenizer clusterNumTokens = new StringTokenizer(KmeansClusterNumText.getText()," ");
						
						if(clusterNumTokens.countTokens()!=2){
							MessageDialog.openError(shlClassification, "Error","Give two cluster numbers.\nFormat: Cluster1 Cluster2\nExample: 10 10");
							return;
						}
						if(Double.parseDouble(weightText.getText() ) < 0 || Double.parseDouble(weightText.getText() ) > 1 ){
							MessageDialog.openError(shlClassification, "Error","Give Weight between [ 0 , 1 ]");
							return;
						}
						factory.getMixedBovwBoc().setWeight1(Double.parseDouble(weightText.getText()));
						
						//set Bovw object in factory
						Modeling(factory,descriptorChoice,KMeanstokens.nextToken(),"Bag of Visual Words",colorSpace
								,Integer.parseInt(clusterNumTokens.nextToken()),0, normalization);
						//set Boc object in factory
						Modeling(factory,descriptorChoice,KMeanstokens.nextToken(),"Bag of Colors",colorSpace
								,Integer.parseInt(clusterNumTokens.nextToken()),0 ,normalization);
							
						//set objects for the combined model
						factory.getMixedBovwBoc().setBovw(factory.getBovw());
						factory.getMixedBovwBoc().setBoc(factory.getBoc());
						//free unnecessary objects from memory
						if (FusionCombo.getText().equals("Early Fusion")){
							factory.setBovw(null);
							factory.setBoc(null);
						}
					}else if (modelCombo.getText().equals("Mixed (GraphBoc-Bovw)")){
						
						StringTokenizer KMeanstokens = new StringTokenizer(KMeansCombo.getText(),"-");
						StringTokenizer clusterNumTokens = new StringTokenizer(KmeansClusterNumText.getText()," ");
						
						if(clusterNumTokens.countTokens()!=2){
							MessageDialog.openError(shlClassification, "Error","Give two cluster numbers.\nExample: '10 10'");
							return;
						}
						
						if(Double.parseDouble(weightText.getText() ) < 0 || Double.parseDouble(weightText.getText() ) > 1 ){
							MessageDialog.openError(shlClassification, "Error","Give Weight between [ 0 , 1 ]");
							return;
						}
						//variables required in Graph Boc Bovw 
						factory.getMixedgBocBovw().setWeight1(Double.parseDouble(weightText.getText()));
						
						//set Bovw object in factory
						Modeling(factory,descriptorChoice,KMeanstokens.nextToken(),"Bag of Visual Words",colorSpace
								,Integer.parseInt(clusterNumTokens.nextToken() ),0 , normalization);
						//set Gboc object in factory
						Modeling(factory,descriptorChoice,KMeanstokens.nextToken(),"Graph BoC",colorSpace
								,Integer.parseInt(clusterNumTokens.nextToken() ), getNumberOfPatches(patchesCombo.getText() ) , normalization );
						
						//set objects for the combined model
						factory.getMixedgBocBovw().setBovw(factory.getBovw());
						//free unnecessary objects from memory
						if (FusionCombo.getText().equals("Early Fusion")){
							factory.setBovw(null);
						}
					}else{	//bovw , boc , graph boc
						
						
						Modeling(factory,descriptorChoice,KMeansCombo.getText(),modelCombo.getText(),colorSpace
								,Integer.parseInt(KmeansClusterNumText.getText()),getNumberOfPatches(patchesCombo.getText() ), normalization );
					}	
					//factory is not needed for this descriptors
					if(descriptorChoice.equals("Phow") || descriptorChoice.equals("Dense Sift") ) return;
					
					//start factory using a thread
					Thread threadFactory = new Thread(factory);
					threadFactory.start();  
					
				} catch (Exception e1) {
					MessageDialog.openError(shlClassification, "Error","Extraction of feature failed Failed");
					e1.printStackTrace();
				}	
			}

			private boolean check2ExtractFeatures(){
				if(!checkSettings(modelCombo,KmeansClusterNumText,descriptorCombo,KMeansCombo,comboColorSpace))
					return false;
				
				 if(weightText.getText()=="" && (modelCombo.getText().equals("Mixed (Bovw-Boc)")
						||modelCombo.getText().equals("Mixed (GraphBoc-Bovw)") ) ) {
					MessageDialog.openError(shlClassification, "Error","Choose weight for models");
					return false;
				 }else if(patchesCombo.getText()=="" && modelCombo.getText().equals("Mixed (GraphBoc-Bovw)") ){
						MessageDialog.openError(shlClassification, "Error","Choose patches for graph Boc");
						return false;
				 }
				return true;
			}
			
			//return number of patches(1x1 patches are 256 patches in a  default: 256x256 picture )
			private int getNumberOfPatches(String numberOfPatches) {
				if(!Utilities.isNumeric(numberOfPatches)) return -1;				

				int patches = Integer.parseInt(numberOfPatches);

				if(patches == 1)
					return 256;
				else if(patches == 2)
					return 128;
				else if(patches == 4)
					return 64;
				else if(patches == 8)
					return 32;
				else if(patches == 16)
					return 16;
				else 
					return 8;
			}
		});
		extractFeaturesButton.setBounds(240, 360, 164, 56);
		extractFeaturesButton.setText("Model");
		
		//-----------------PreprocessTab-----------------------------//

	      
		//-----------------TrainTab-----------------------------//
		Button btnResults = new Button(trainGroup, SWT.RIGHT | SWT.WRAP);
		btnResults.setAlignment(SWT.CENTER);
		btnResults.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (bestParameters==null && bestParameters.isFinished()==false) return;
				   
				//set best parameters for training 
				bestParametersText.setText("-c "+bestParameters.getBestc()+" -g "
						+bestParameters.getBestg()+"\nAccuracy: "+(int)bestParameters.getBestcv()+" %");
			}
		});
		btnResults.setText("Show Results\r\n");
		btnResults.setBounds(546, 197, 102, 52);
		
		Label lblTrainClassifier = new Label(trainGroup, SWT.CENTER);
		lblTrainClassifier.setText("Train Classifier");
		lblTrainClassifier.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblTrainClassifier.setBounds(10, 10, 618, 29);
		
		Text resultFiletext = new Text(testGroup, SWT.BORDER);
		resultFiletext.setEditable(false);
		resultFiletext.setBounds(202, 201, 447, 28);
		
		Text commandTextTest = new Text(testGroup, SWT.BORDER);
		commandTextTest.setBounds(202, 260, 447, 28);
		
		Button trainDataButton = new Button(trainGroup, SWT.NONE);
		trainDataButton.setBounds(30, 68, 150, 34);
		trainDataButton.setText("Open Train Data");
		
		Text trainPathText = new Text(trainGroup, SWT.BORDER);
		trainPathText.setEditable(false);
		trainPathText.setBounds(211, 71, 437, 28);
		
		
		//open filedialog to choose train data
		trainDataButton.addSelectionListener(new SelectionAdapter() {
		     	@Override
		     	public void widgetSelected(SelectionEvent e) {
		     		trainDataPath= fileDialog(trainPathText);
		     	}
		     });
				
		Text commandTextTrain = new Text(trainGroup, SWT.BORDER);
		commandTextTrain.setBounds(211, 280, 437, 28);
		
		Label lblGiveCommand = new Label(trainGroup, SWT.NONE);
		lblGiveCommand.setText("Give command");
		lblGiveCommand.setBounds(68, 285, 112, 20);
		
		Button btnTrain = new Button(trainGroup, SWT.NONE);
		btnTrain.setBounds(485, 322, 135, 52);
		btnTrain.setText("Train");
		
		
		Text saveModelTextTrain = new Text(trainGroup, SWT.BORDER);
		saveModelTextTrain.setEditable(false);
		saveModelTextTrain.setBounds(211, 138, 437, 28);
	
		Button saveModelButtonTrain = new Button(trainGroup, SWT.NONE);
		saveModelButtonTrain.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveModelPathTrain= fileDialog(saveModelTextTrain);
			}
		});
		saveModelButtonTrain.setText("Save Model");
		saveModelButtonTrain.setBounds(30, 136, 150, 34);
		
		btnTrain.addSelectionListener(new SelectionAdapter() {
				     	@Override
				     	public void widgetSelected(SelectionEvent e) {
							if(trainDataPath==null){
								MessageDialog.openError(shlClassification, "Error","Give train source");
								return;
							}
							else if(saveModelPathTrain==null){
								MessageDialog.openError(shlClassification, "Error","Give model to save");
								return;
							}
							//train classifier using the given command 
							TrainClassifier trained=new TrainClassifier(commandTextTrain.getText(),trainDataPath,
									saveModelPathTrain,shlClassification);
							Thread threading = new Thread(trained);
							threading.start();
					  	}
				     });
		
		Button btnFindBestParameters = new Button(trainGroup, SWT.NONE);
		btnFindBestParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(trainDataPath==null){
	     			MessageDialog.openError(shlClassification, "Error","Give train data!!");
					return;
				}
				//use matlab to find best parameters cost,gamma
				bestParameters=new TuningSVM(trainDataPath,shlClassification);
				Thread bestParametersThread = new Thread(bestParameters);
				bestParametersThread.start();
			}
		});
		btnFindBestParameters.setText("Find Best Parameters");
		btnFindBestParameters.setBounds(30, 197, 150, 52);
		
		Label classifyImage = new Label(trainGroup, SWT.CENTER);
		classifyImage.setBounds(30, 322, 412, 239);
		Image image = new Image(display,System.getProperty("user.dir")+"\\images\\train.jpg");
		classifyImage.setImage (image);
		
		Label classifiedImage = new Label(testGroup, SWT.CENTER);
		classifiedImage.setBounds(179, 338, 192, 192);
		Image classifiedImg = new Image(display, System.getProperty("user.dir")+"\\images\\classified.jpg");
		classifiedImage.setImage (classifiedImg);
		
		//-----------------TrainTab-----------------------------//
		
		Button btnHasTrueLabels = new Button(testGroup, SWT.CHECK);
		btnHasTrueLabels.setBounds(179, 304, 104, 16);
		btnHasTrueLabels.setText("Has True Labels");
		btnHasTrueLabels.setSelection(true);
		
		Label categorizedlab = new Label(resultsGroup, SWT.NONE);
		categorizedlab.setBounds(100, 500, 44, 20);
		categorizedlab.setVisible(false);

		Label catelab = new Label(resultsGroup, SWT.NONE);
		catelab.setVisible(false);
		catelab.setText("Category");
		catelab.setBounds(10, 500, 70, 20);
		
		Label lblProbability = new Label(resultsGroup, SWT.NONE);
		lblProbability.setText("Probability");
		lblProbability.setBounds(409, 500, 79, 20);
		lblProbability.setVisible(false);
		
		Label probLabel = new Label(resultsGroup, SWT.NONE);
		probLabel.setBounds(522, 500, 64, 20);
		
		Label lblMetrics = new Label(resultsGroup, SWT.CENTER);
		lblMetrics.setText("Metrics");
		lblMetrics.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblMetrics.setBounds(0, 10, 649, 29);
		
		Label indexLabel = new Label(resultsGroup, SWT.NONE);
		indexLabel.setBounds(72, 158, 109, 28);
		
		Label SafeLabel = new Label(resultsGroup, SWT.CENTER);
		SafeLabel.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.NORMAL));
		SafeLabel.setBounds(291, 496, 94, 29);
		
		Label imageLabel = new Label(resultsGroup, SWT.CENTER);
		imageLabel.setBounds(15, 195, 620, 290);
				
		Label classifiedLabel = new Label(resultsGroup, SWT.NONE);
		classifiedLabel.setBounds(249, 500, 36, 20);
		
		Button previousButton = new Button(resultsGroup, SWT.NONE);
		previousButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(metrics==null ||  statisticsCounter-1<0)
					return;
				
				//if previous button does not exceed zero call statistics to display information 
				//to user(category,image etc)
				statisticsCounter--;
				statistics( indexLabel, imageLabel, display, classifiedLabel, categorizedlab
						, probLabel, SafeLabel,btnHasTrueLabels.getSelection());
			}
		});
		previousButton.setBounds(180, 540, 90, 30);
		previousButton.setText("Previous");
		
		Button nextButton = new Button(resultsGroup, SWT.NONE);
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(metrics==null || statisticsCounter+1>metrics.getImagesSize())
					return;
				
				//if next button does not exceed images size call statistics to display information 
				//to user(category,image etc)
				statisticsCounter++;
				statistics( indexLabel, imageLabel, display, classifiedLabel, categorizedlab
						, probLabel, SafeLabel,btnHasTrueLabels.getSelection());
			  	
			}
		});
		nextButton.setBounds(350, 540, 90, 30);
		nextButton.setText("Next");
		  	
		Label classifiedLab = new Label(resultsGroup, SWT.NONE);
		classifiedLab.setBounds(149, 500, 81, 20);
		classifiedLab.setText("Classified in:");
		classifiedLab.setVisible(false);
		
		Label accuracyLabel = new Label(resultsGroup, SWT.NONE);
		accuracyLabel.setBounds(149, 45, 131, 20);
		accuracyLabel.setText("0");
		
		Label precisionLabel = new Label(resultsGroup, SWT.NONE);
		precisionLabel.setText("0");
		precisionLabel.setBounds(149, 80, 131, 20);
		
		Label recallLabel = new Label(resultsGroup, SWT.NONE);
		recallLabel.setText("0");
		recallLabel.setBounds(149, 114, 131, 20);
		
		Label TPlabel = new Label(resultsGroup, SWT.NONE);
		TPlabel.setText("0");
		TPlabel.setBounds(502, 36, 70, 20);
		
		Label TNlabel = new Label(resultsGroup, SWT.NONE);
		TNlabel.setText("0");
		TNlabel.setBounds(502, 62, 70, 20);
		
		Label FPlabel = new Label(resultsGroup, SWT.NONE);
		FPlabel.setText("0");
		FPlabel.setBounds(502, 88, 70, 20);
		
		Label FNlabel = new Label(resultsGroup, SWT.NONE);
		FNlabel.setText("0");
		FNlabel.setBounds(502, 114, 70, 20);
		
		Label lblTestImages = new Label(testGroup, SWT.CENTER);
		lblTestImages.setText("Test Images");
		lblTestImages.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblTestImages.setBounds(0, 10, 649, 29);
		
		Button testDataButton = new Button(testGroup, SWT.NONE);
		testDataButton.setBounds(30, 71, 150, 34);
		testDataButton.setText("Open Test Data");
		
		//open filedialog to choose test data
		testDataButton.addSelectionListener(new SelectionAdapter() {
		     	@Override
		     	public void widgetSelected(SelectionEvent e) {
		     		testImagesPath= fileDialog(testPathText);
		     	}
		     });
		
		Button testButton = new Button(testGroup, SWT.NONE);
		testButton.setBounds(485, 322, 135, 52);
		testButton.setText("Test DataSet");
		
		//open filedialog to choose model path
		Button saveModelButtonTest = new Button(testGroup, SWT.NONE);
		saveModelButtonTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ModelPathTest= fileDialog(saveModelTextTest);
			}
		});
		saveModelButtonTest.setText("Open Model");
		saveModelButtonTest.setBounds(30, 134, 150, 34);
		
		//open filedialog to choose result file
		Button resultFileButton = new Button(testGroup, SWT.NONE);
		resultFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resultFilePath= fileDialog(resultFiletext);
			}
		});
		resultFileButton.setText("Result File");
		resultFileButton.setBounds(30, 197, 150, 34);
		
		Label label = new Label(testGroup, SWT.NONE);
		label.setText("Give command");
		label.setBounds(68, 263, 112, 20);
		
		
		
		Text indexButtonText = new Text(resultsGroup, SWT.BORDER);
		indexButtonText.setBounds(234, 158, 109, 28);
		indexButtonText.setVisible(false);
		
		Button buttonGetIndexImage = new Button(resultsGroup, SWT.NONE);
		buttonGetIndexImage.setText("Get Image");
		buttonGetIndexImage.setBounds(376, 158, 90, 28);
		buttonGetIndexImage.setVisible(false);
		
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(testImagesPath==null){
					MessageDialog.openError(shlClassification, "Error","Give test source");
					return;
				}else if(ModelPathTest==null){
					MessageDialog.openError(shlClassification, "Error","Give model");
					return;
				}else if(resultFilePath==null){
					MessageDialog.openError(shlClassification, "Error","Give result file to save results");
					return;
				}else if(DBSourcePath==null){
					MessageDialog.openError(shlClassification, "Error","Give database source to test images");
					return;
				}
				
				try {
					boolean hasTrueLabels = btnHasTrueLabels.getSelection();
					
					//test images given with command
					TestImages test=new TestImages(testImagesPath,ModelPathTest,resultFilePath,commandTextTest.getText(),shlClassification);
					Thread threading = new Thread(test);
					threading.start();
					//wait thread to finish so we have the results
					threading.join();
					
				    //create metrics and display
					metrics=new Statistics();
					metrics.createMetrics(createProjectPath(),DBSourcePath,resultFilePath,hasTrueLabels,shlClassification);
					//initialize counter for images
					statisticsCounter=0;
						
					accuracyLabel.setText( metrics.getAccuracy() +" %");
					classifiedLab.setVisible(true);
					categorizedlab.setVisible(true);
					indexButtonText.setVisible(true);
					buttonGetIndexImage.setVisible(true);	
					
					statistics( indexLabel, imageLabel, display, classifiedLabel, categorizedlab
							, probLabel, SafeLabel,btnHasTrueLabels.getSelection());
				
					//if test was not with probabilities don't show labels
					if(metrics.getProbabilitySize()!=0){
						lblProbability.setVisible(true);
						probLabel.setVisible(true);
					}else{
						lblProbability.setVisible(false);
						probLabel.setVisible(false);
					}
					
					if(hasTrueLabels){
						catelab.setVisible(true);
						//if test has more than two categories dont show metrics 
						if(metrics.getCategoriesSize()==2){
							precisionLabel.setText(metrics.getPrecisionBinary() +" %" );
							recallLabel.setText( metrics.getRecallBinary() +" %");
							TPlabel.setText( metrics.getTruePositiveBinary()+"/"+(int)metrics.getPositiveBinary() );
							TNlabel.setText( metrics.getTrueNegativeBinary()+"/"+(int)metrics.getNegativeBinary() );
							FPlabel.setText(metrics.getFalsePositiveBinary()+"/"+(int)metrics.getNegativeBinary() );
							FNlabel.setText(metrics.getFalseNegativeBinary()+"/"+(int)metrics.getPositiveBinary() );
							MessageDialog.openInformation(shlClassification, "Completed", "Test completed Successfully");
						}else{
							precisionLabel.setText("0");
							recallLabel.setText("0");
							TPlabel.setText( "0");
							TNlabel.setText("0");
							FPlabel.setText("0");
							FNlabel.setText("0");
							
							MessageDialog.openInformation(shlClassification, "Completed", "Test completed Successfully!\n"
									+ "Check statistics.txt in project folder for metrics");
						}
					}
					else
						MessageDialog.openInformation(shlClassification, "Completed", "Test completed Successfully");
			    		
				} catch (Exception e1) {
					MessageDialog.openError(shlClassification, "Error", "Test failed");
					e1.printStackTrace();
				}
			}
		});
		// get image in given index		
		buttonGetIndexImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = Integer.valueOf(indexButtonText.getText());

				if(indexButtonText.getText()=="" || metrics == null || !Utilities.isNumeric(indexButtonText.getText())
						|| index>metrics.getImagesSize() || index<0)
					return;
				statisticsCounter=index-1 ;
				statistics( indexLabel, imageLabel, display, classifiedLabel, categorizedlab
						, probLabel, SafeLabel,btnHasTrueLabels.getSelection());
			};
		});
							
		//-----------------SettingsTab-----------------------------//
		
		//-----------------homeTab-----------------------------//
		Label startUpLab = new Label(homeGroup, SWT.NONE);
		startUpLab.setBounds(10, 55, 653, 40);
		startUpLab.setText("Design and Implementation of an online application for content based image classification.");
	
		Label lblSubject = new Label(homeGroup, SWT.CENTER);
		lblSubject.setText("Subject");
		lblSubject.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblSubject.setBounds(0, 20, 663, 29);
		
		Label lblNewLabel_2 = new Label(homeGroup, SWT.NONE);
		lblNewLabel_2.setBounds(10, 172, 639, 160);
		lblNewLabel_2.setText("Goal of this project is to design and implement an application which automatically classifies \r\nimages. The application, given an image,will be able to classify it into one or more categories \r\nbased on its content. Images will automatically be assigned labels that define the category\r\nor categories where the image belongs.There is also the ability to extend the application, by \r\neasily adding new methods for representing and classifying images, for general and research \r\npurposes.\r\n\r\n");
		
		Label lblMixedModelSettings = new Label(settingsGroup, SWT.CENTER);
		lblMixedModelSettings.setText("Mixed Model Settings");
		lblMixedModelSettings.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblMixedModelSettings.setBounds(10, 488, 639, 29);
		lblMixedModelSettings.setVisible(false);

		
		Label lblDescription = new Label(homeGroup, SWT.CENTER);
		lblDescription.setText("Description");
		lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblDescription.setBounds(0, 137, 663, 29);
		
		Button btnExit = new Button(homeGroup, SWT.NONE);
			 btnExit.setBounds(10, 491, 109, 40);
		     btnExit.addSelectionListener(new SelectionAdapter() {
		    	 @Override
		     	public void widgetSelected(SelectionEvent e) {
		     		ExitProgram();
		     	}
		     });
		     btnExit.setText("Exit");

		     Label patchesLabel = new Label(settingsGroup, SWT.NONE);
		     patchesLabel.setText("Blocks");
		     patchesLabel.setBounds(358, 373, 67, 23);
		     patchesLabel.setVisible(false);
						     
			lblNewLabel_1.setBounds(65, 45, 64, 20);
			lblNewLabel_1.setText("Accuracy");
				
			Label lblPrecision = new Label(resultsGroup, SWT.NONE);
			lblPrecision.setText("Precision");
			lblPrecision.setBounds(65, 80, 64, 20);
			
			Label lblRecall = new Label(resultsGroup, SWT.NONE);
			lblRecall.setText("Recall");
			lblRecall.setBounds(65, 114, 64, 20);
				
			Label lblTruePositive = new Label(resultsGroup, SWT.NONE);
			lblTruePositive.setText("True Positive");
			lblTruePositive.setBounds(376, 36, 97, 20);
			
			Label lblTrueNegative = new Label(resultsGroup, SWT.NONE);
			lblTrueNegative.setText("True Negative");
			lblTrueNegative.setBounds(376, 62, 97, 20);
			
			Label lblFalsePositive = new Label(resultsGroup, SWT.NONE);
			lblFalsePositive.setText("False Positive");
			lblFalsePositive.setBounds(376, 88, 97, 20);
			
			Label lblFalseNegative = new Label(resultsGroup, SWT.NONE);
			lblFalseNegative.setText("False Negative");
			lblFalseNegative.setBounds(376, 114, 97, 20);
		
	   	   	Label lblChooseDescriptor = new Label(settingsGroup, SWT.NONE);
	   	   	lblChooseDescriptor.setText("Choose Descriptor");
	   	   	lblChooseDescriptor.setBounds(333, 184, 105, 30);
	  	
	   	   	//-----------------SettingsTab-----------------------------//
	   	   	
	   	   	Label lblFusionType = new Label(settingsGroup, SWT.NONE);
			lblFusionType.setBounds(358, 539, 67, 23);
			lblFusionType.setText("Fusion Type:");
			lblFusionType.setVisible(false);
			
			
			Button btnViewPalete = new Button(preprocessGroup, SWT.NONE);
	   	   	btnViewPalete.addSelectionListener(new SelectionAdapter() {
	   	   		@Override
	   	   		public void widgetSelected(SelectionEvent e) {
	   	   			
	   	   			//if a palete file is given parse fileName and show palette
	   	   			if(!textpalete.getText().equals("")){
	   	   				StringTokenizer str = new StringTokenizer(textpalete.getText(),"-");
	   	   				str.nextToken();
	   	   				int numberOfColors = Integer.parseInt(str.nextToken());
	   	   				System.out.println(numberOfColors);

	   	   				ColorSpace cs =Utilities.findColorSpace(str.nextToken());
	   	   				System.out.println(cs.toString());
	   	   				
	   	   				BufferedImage img = ImageUtility.getPaletteImg(Utilities.binaryFileTo2DIntArray(PaletePath),numberOfColors,cs);
	   	   				ImageViewer imgV = new ImageViewer("Palete",img,true);	
	   	   				imgV=null;
	   	   			}else if(clusterBoc.getPalette()!=null){ 	//if palette is from clustering	
	   	   				BufferedImage img = ImageUtility.getPaletteImg(clusterBoc.getPalette(),clusterBoc.getNumOfColors(),clusterBoc.getCs());
	   	   				ImageViewer imgV = new ImageViewer("Palete",img,true);  
	   	   				imgV=null;
	   	   			}else
		   	   			MessageDialog.openError(shlClassification, "Error", "Cluster or give palete file");

	   	   		}
	   	   	});
	   	   	btnViewPalete.setText("View Palete");
	   	   	btnViewPalete.setBounds(348, 500, 184, 30);
	   	   	btnViewPalete.setVisible(false);
	   	   	
	   	   
	   	   	
	   	   	Label lblColorSpace = new Label(settingsGroup, SWT.NONE);
	   	   	lblColorSpace.setText("Color Space");
	   	   	lblColorSpace.setBounds(10, 376, 85, 23);
	   	   	lblColorSpace.setVisible(false);
	   	   	
	   	   	Label lblClustersSample = new Label(settingsGroup, SWT.NONE);
	   	   	lblClustersSample.setText("Num  Images/category \r\n          for Cluster");
	   	   	lblClustersSample.setVisible(true);
	   	   	lblClustersSample.setBounds(10, 326, 128, 30);
	   	   	
	   	 
	   	   	
	   	   	comboColorSpace.setVisible(false);
	   	   	comboColorSpace.add("RGB");
	   	   	comboColorSpace.add("HSV");
	   	   	comboColorSpace.add("XYZ");
	   	   	comboColorSpace.add("CIELab");
	   	   	comboColorSpace.add("YCbCr");
	   	   	
	   	   	Label weightLabel = new Label(settingsGroup, SWT.NONE);
	   	   	weightLabel.setText("Model's Weight");
	   	   	weightLabel.setBounds(10, 536, 95, 23);
	   	   	weightLabel.setVisible(false);
	   	   	
	   	   	Label lblClustersNum = new Label(settingsGroup, SWT.NONE);
	   	   	lblClustersNum.setText("Number of Clusters");
	   	   	lblClustersNum.setBounds(358, 290, 117, 34);
	   	   	
	   	   	descriptorCombo.addModifyListener(new ModifyListener() {
	   	   		public void modifyText(ModifyEvent e) {
		   	   		if(modelCombo.getText().equals("Bag of Visual Words")) {
		   	   			if(descriptorCombo.getText().equals("Dense Sift") || descriptorCombo.getText().equals("Phow")) {
			   	   			KMeansCombo.removeAll();
		   	   				KMeansCombo.setText("VLFeat");
				   	 		KMeansCombo.add("VLFeat");
		   	   				
		   	   			}
		   	   			else if(descriptorCombo.getText().equals("Sift") || descriptorCombo.getText().equals("Surf")
		   	   					|| descriptorCombo.getText().equals("Sift-Surf") || descriptorCombo.getText().equals("ColorCorrelogram-Sift")
		   	   					|| descriptorCombo.getText().equals("ColorCorrelogram")){
		   	   				KMeansCombo.removeAll();
		   	   				KMeansCombo.setText("Lire");
				   	 		KMeansCombo.add("Lire");
			   				KMeansCombo.add("VLFeat");	
		   	   			}
		   	   		}
	   	   		}
	   		});
	   	   	
	   	   	
	   	   	
	   	 
		   	descriptorCombo.addModifyListener(new ModifyListener() {
	   	   		public void modifyText(ModifyEvent e) {
		   	   		if(descriptorCombo.getText().trim().equals("ColorCorrelogram") &&
		   	   			modelCombo.getText().equals("Bag of Visual Words") ){
		   	   			
						KmeansClusterNumText.setText("1024");
		   	   			KmeansClusterNumText.setEnabled(false);
		   	   		}
		   	   		else if( !descriptorCombo.getText().trim().equals("ColorCorrelogram") &&
			   	   			modelCombo.getText().equals("Bag of Visual Words") ){
		   	   			KmeansClusterNumText.setText("512");
		   	   			KmeansClusterNumText.setEnabled(true);
		   	   		}
	   	   		}	
			});
		   	
	   	   	modelCombo.addModifyListener(new ModifyListener() {
	   	   		public void modifyText(ModifyEvent e) {
	   	   			comboFeaturesFormat.setText("LibSVM Format");
	   	   			KmeansSampleText.setText("10");
	   	   			comboNorm.setText("L1");
	   	   			comboTFIDF.setText("TFIDF");
		   	   		KMeansCombo.removeAll();
		   	   		descriptorCombo.removeAll();
		   	   		comboColorSpace.setText("");
		   	   		patchesCombo.setText("");
	   	   			
		   	   		if(modelCombo.getText().equals("Bag of Visual Words")) {
	   	   				hideShowElements(false,false,false,true,1);
	   	   				extractFeaturesButton.setText("BoVW");
	   	   			}else if(modelCombo.getText().equals("Bag of Colors")){
	   	   				hideShowElements(false,false,true,false,2);
						extractFeaturesButton.setText("BoC");
	   	   			}else if(modelCombo.getText().equals("Graph BoC")){
	   	   				hideShowElements(false,true,true,false,4);		
	   	   				extractFeaturesButton.setText("Graph BoC");
	   	   			}else if(modelCombo.getText().equals("Mixed (Bovw-Boc)")){
	   	   				hideShowElements(true,false,true,true,3);
	   	   				extractFeaturesButton.setText("Bovw-Boc");
					}else if(modelCombo.getText().equals("Mixed (GraphBoc-Bovw)")){
	   	   				hideShowElements(true,true,true,true,5);
	   	   				extractFeaturesButton.setText("GBoc-Bovw");
	   	   			}
	   	   		}
	   	   		
		   	   	private void hideShowElements(boolean isElementVisible,boolean isElementVisible1
		   	   			,boolean isElementVisible2,boolean isElementVisible3,int descriptor){
		   	   		
		   	   	if(descriptor ==1){
		   	   		
		   	   		System.out.println("mpika");
		   	 		KMeansCombo.setText("Lire");
		   	 		KMeansCombo.add("Lire");
	   				KMeansCombo.add("VLFeat");
	   				
		   			KmeansClusterNumText.setText("512");

	   				lblClustersNum.setText("Number of Clusters");
		   	 	}else if (descriptor == 2 || descriptor == 4){
		   	 		if (descriptor ==2)
		   	 			descriptorCombo.setText("Bag of Color");
		   	 		else
		   	 			descriptorCombo.setText("Graph BoC");
		   	 		
		   	 		KMeansCombo.setText("Lire");
	   	   			KMeansCombo.add("Lire");
  					KMeansCombo.add("Stromberg Lab");
  					
  					comboColorSpace.setText("RGB");
   	   				btnViewPalete.setBounds(10, 500, 150, 30);
   	   				buttonPalette.setBounds(10, 444, 150, 30);
   	   				textpalete.setBounds(166, 444, 176, 30);
   	   				
   	   				patchesCombo.setText("2");
   	   				
   	   				KmeansClusterNumText.setText("512");
   	   				lblClustersNum.setText("Number of Colors");
	   				
  				}else{
  					KMeansCombo.setText("Lire-Lire");
	   	   			KMeansCombo.add("Lire-Lire");
	   				KMeansCombo.add("Lire-Stromberg Lab");
	   				KMeansCombo.add("VLFeat-Lire");	
	   				KMeansCombo.add("VLFeat-Stromberg Lab");
	   				
	   				comboColorSpace.setText("RGB");
   	   				btnViewPalete.setBounds(348, 500, 176, 30);
   	   				buttonPalette.setBounds(348, 444, 139, 30);
   	   				
   	   				patchesCombo.setText("2");
	   				
	   				if (descriptor ==3)
	   					KmeansClusterNumText.setText("512 512");
		   	 		else
		   	 			KmeansClusterNumText.setText("512 128");
	   				
   	   				lblClustersNum.setText("Number of Clusters\n,Number Of Colors");
   	   	   		
	   	   		}
		   	   		
		   	   		if(descriptor == 1 || descriptor == 3 || descriptor == 5 ){
		   	   			descriptorCombo.add("Sift");
		   	   			descriptorCombo.add("Dense Sift");
		   	   			descriptorCombo.add("Surf");		
		   	   			descriptorCombo.add("Sift-Surf");
		   	   			descriptorCombo.add("ColorCorrelogram");
		   	   			descriptorCombo.add("ColorCorrelogram-Sift");		
		   	   			descriptorCombo.add("Phow");
		   	   		}else if (descriptor == 2){
		   	   			descriptorCombo.add("Bag of Color");
		   	   			
		   	   		}
		   	   		else
		   	   			descriptorCombo.add("Graph BoC");
		   	   			weightText.setText("0.5");
			   	   		weightText.setVisible(isElementVisible);
		   				weightLabel.setVisible(isElementVisible);
		   				lblMixedModelSettings.setVisible(isElementVisible);
		  
		   				lblFusionType.setVisible(isElementVisible);
		   				FusionCombo.setVisible(isElementVisible);     
		   				FusionCombo.setText("Late Fusion");
		   				
		   				patchesCombo.setText("2");
	   	   				patchesCombo.setVisible(isElementVisible1);
	   	   				patchesLabel.setVisible(isElementVisible1);
	   	   				
	   	   				btnViewPalete.setVisible(isElementVisible2);
		   				lblColorSpace.setVisible(isElementVisible2);
		   				comboColorSpace.setText("RGB");
		   				comboColorSpace.setVisible(isElementVisible2);
		   				buttonPalette.setVisible(isElementVisible2);
		   				textpalete.setVisible(isElementVisible2);
		
		   				buttonCentroids.setVisible(isElementVisible3);
		   				centroidsText.setVisible(isElementVisible3);
		   				buttonCentroids_2.setVisible(isElementVisible3);
			   	   		centroids2Text.setVisible(isElementVisible3);
			   	   		
		   	   	}
	   	 
	   	   	});
	   	   	
	   	   	
	   	   	modelCombo.add("Bag of Visual Words");
	   	   	modelCombo.add("Bag of Colors");
	   	   	modelCombo.add("Mixed (Bovw-Boc)");
	   	   	modelCombo.add("Graph BoC");
	   	   	modelCombo.add("Mixed (GraphBoc-Bovw)");

	   	   	Label lblChooseModel = new Label(settingsGroup, SWT.NONE);
	   	   	lblChooseModel.setText("Choose Model");
	   	   	lblChooseModel.setBounds(10, 184, 95, 23);
	   	   	
	   	   	Label lblProjectName = new Label(settingsGroup, SWT.NONE);
	   	   	lblProjectName.setBounds(10, 48, 128, 20);
	   	   	lblProjectName.setText("Give Project Name ");
	   	   	
	   	   	Button DBSource = new Button(settingsGroup, SWT.NONE);
	   	   	DBSource.addSelectionListener(new SelectionAdapter() {
	   	   		@Override
	   	   		public void widgetSelected(SelectionEvent e) {
	   	   			DBSourcePath= directoryDialog(DBSourceText);
	   	   			try {
						EditMetadata();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	   	   		}
	   	   	});
	   	   	DBSource.setText("DataBase Source");
	   	   	DBSource.setBounds(10, 74, 128, 30);	
	   	   	
		projectNameText = new Text(settingsGroup, SWT.BORDER);
		projectNameText.setBounds(154, 45, 490, 23);	
		projectNameText.addFocusListener(new FocusListener() {
			
	        @Override
	        public void focusLost(FocusEvent e) {
	        	try {
					EditMetadata();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }

	        @Override
	        public void focusGained(FocusEvent e) { }
	    });
		
		
		Button btnKeywords = new Button(settingsGroup, SWT.NONE);
		btnKeywords.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KeywordsPath= fileDialog(keywordsText);
			}
		});
		btnKeywords.setText("Keywords");
		btnKeywords.setBounds(10, 110, 128, 30);
		
		Label lblNewLabel = new Label(settingsGroup, SWT.CENTER);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblNewLabel.setBounds(0, 10, 644, 23);
		lblNewLabel.setText("General-XML Settings");
		
		Label lblClusteringSettings = new Label(settingsGroup, SWT.CENTER);
		lblClusteringSettings.setText("Model Settings");
		lblClusteringSettings.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblClusteringSettings.setBounds(0, 146, 644, 23);
		
		Label lblChooseKmeans = new Label(settingsGroup, SWT.NONE);
		lblChooseKmeans.setText("Choose KMeans");
		lblChooseKmeans.setBounds(10, 290, 105, 30);
		
		Label lblNormalizationSettings = new Label(settingsGroup, SWT.CENTER);
		lblNormalizationSettings.setText("Normalization Settings");
		lblNormalizationSettings.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		lblNormalizationSettings.setBounds(10, 402, 639, 23);
		
		
		
		Label lblTfidf = new Label(settingsGroup, SWT.NONE);
		lblTfidf.setBounds(10, 440, 55, 15);
		lblTfidf.setText("TF-IDF");
		
		Label lblNorms = new Label(settingsGroup, SWT.NONE);
		lblNorms.setText("Norms");
		lblNorms.setBounds(358, 440, 55, 18);
		
		Label label_1 = new Label(settingsGroup, SWT.CENTER);
		label_1.setText("Clustering-Model Settings");
		label_1.setFont(SWTResourceManager.getFont("Segoe UI", 11, SWT.BOLD));
		label_1.setBounds(0, 250, 644, 23);
		
		
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * end of gui 
	 * 
	 * 
	 * 
	 */
	
//returns false if a textbox is empty	
private boolean checkSettings(Combo modelCombo, Text KmeansClusterNumText, Combo descriptorCombo,  Combo KMeansCombo,
		Combo comboColorSpace){
	
	if(modelCombo.getText()==""){
		MessageDialog.openError(shlClassification, "Error","Choose Model");
		return false;
	}else if(projectNameText.getText()==""){
		MessageDialog.openError(shlClassification, "Error","Give Project Name ");
		return false;
	}else if(DBSourcePath==null){
		MessageDialog.openError(shlClassification, "Error","Give Database Source");
		return false;
	}else if(descriptorCombo.getText()==""){
		MessageDialog.openError(shlClassification, "Error","Choose Descriptor");
		return false;
	}else if(KmeansClusterNumText.getText()==""){
		MessageDialog.openError(shlClassification, "Error","Give number of Clusters");
		return false;
	}else if(KMeansCombo.getText()==""){
		MessageDialog.openError(shlClassification, "Error","Choose Kmeans");
		return false;
	}else if(comboColorSpace.getText()=="" && (modelCombo.getText().equals("Bag of Colors") ||
			modelCombo.getText().equals("Mixed (Bovw-Boc)") ) ){
		
		MessageDialog.openError(shlClassification, "Error","Choose Color Space");
		return false;
	}
	return true;
}
//displays metrics in results tab	
private void statistics(Label indexLabel,Label imageLabel,Display display,Label classifiedLabel,Label categorizedlab
		,Label probLabel,Label SafeLabel,boolean hasTrueLabels){
	
	statisticsLabelCounter= statisticsCounter +1;
	indexLabel.setText("Image : "+ statisticsLabelCounter +"/"+metrics.getImagesSize());
	imageLabel.setImage (resizeImageToLabelSize(display));
	classifiedLabel.setText(""+ metrics.getPredictedCategory(statisticsCounter));
	
	if(metrics.getProbabilitySize()!=0)
		probLabel.setText(""+ (double)metrics.getProbability(statisticsCounter)/100 +"%");

	if (!hasTrueLabels)
		return;
	
	categorizedlab.setText(""+metrics.getRealCategory(statisticsCounter));
	if(metrics.getPredictedCategory(statisticsCounter)==metrics.getRealCategory(statisticsCounter)){
		SafeLabel.setForeground(display.getSystemColor(SWT.NULL));
		SafeLabel.setText("Correct");
	}else {
		SafeLabel.setForeground(display.getSystemColor(SWT.COLOR_RED));
		SafeLabel.setText("WRONG");
	}
}
//get image from disc and resize to fit label
private Image resizeImageToLabelSize(Display display){
	File picture =  new File(createProjectPath()+"\\"+metrics.getImage(statisticsCounter).getName());
	
		try {
			ImageIO.write(ImageUtility.resizeImage(ImageUtility.getImage(metrics.getImage(statisticsCounter).getAbsolutePath())
					, 620, 290, false), "jpg" ,picture);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	Image image = new Image(display,createProjectPath()+"\\"+metrics.getImage(statisticsCounter).getName());
	picture.delete();
	return image;
	
}
//open file dialog to choose file
private String fileDialog(Text textBoxToShow){
	String path="";
	FileDialog fd = new FileDialog(shlClassification, SWT.OPEN);
	fd.setText("Open");
	fd.setFilterPath("C:/");
	String[] filterExt = { "*.*",  "*.txt" };
	fd.setFilterExtensions(filterExt);
	try{
		path = fd.open();
		textBoxToShow.setText(Utilities.getName(path));
		return path;
	}catch(Exception e)    {    }
	return path;
		
}
//open directory dialog to choose directory
private String directoryDialog(Text textBoxToShow){
	String path="";
	DirectoryDialog dialog = new DirectoryDialog(shlClassification);
	dialog.setFilterPath("c:\\"); // Windows specific
	try{
		path=dialog.open();
		textBoxToShow.setText(path);
		return path;
	}catch(Exception e)    {     }
	return path;

}
//if file exists show message to user
private boolean sameFileMessageBox(File toCheck , String message){
	boolean continued=false;
	if(toCheck.exists()){	
		MessageBox messageBox = new MessageBox(shlClassification, SWT.ICON_QUESTION
	            | SWT.YES | SWT.NO);
	        messageBox.setMessage(message);
	        messageBox.setText("Project Name Exists!");
	        int response = messageBox.open();
	        if (response == SWT.NO)
				return continued=true;
	} 
	return continued;
}

private clusteringBovwFactory setClusterObjectBoVW(clusteringBovwFactory cluster,String clusterDBPath,String ProjectPath,int numOfClusters,int sampleLimit,
		String KMeansChoice){
	
	cluster.setClusterDBPath(clusterDBPath);
	cluster.setProjectPath(ProjectPath);
	cluster.setNumOfClusters(numOfClusters);
	cluster.setSampleLimit(sampleLimit);
	cluster.setComboKMeansChoice(KMeansChoice);
	cluster.setSampleLimit(sampleLimit);
	cluster.setShell(shlClassification);
	
	return cluster;
}

private BoVWModel setObjectBoVW(BoVWModel bovw,List <double[]> descriptorCenters1,List <double[]> descriptorCenters2
		, Features descriptor1, Features descriptor2){
	
	bovw.setDescriptorCentroids1(descriptorCenters1);
	bovw.setDescriptorCentroids2(descriptorCenters2);
	bovw.setDescriptor1(descriptor1);
	bovw.setDescriptor2(descriptor2);
	return bovw;
}

private String createProjectPath(){
	return System.getProperty("user.dir")+"\\"+projectNameText.getText(); 
}

private void createProjectFile(File projectFile){
	if(!projectFile.exists())
		projectFile.mkdir();	
}
//if cluster file is given ovewrite cluster results
private boolean checkIfClusterFileIsGiven(String descriptorChoice, String model) throws Exception{

	if(model.equals("Bag of Visual Words")){    //if both centroid texts are not null then the only valid choice is (sift-surf) 
		if(CentroidsPath!=null && CentroidsPath2!=null ) {	
			if(!descriptorChoice.equals("Sift-Surf")){
				MessageDialog.openError(shlClassification, "Error","Sift-Surf must be chosen in order to choose two centroids files!");
				return false;
			}
			//read centroids from file in disc
	    	descriptorCenters1=Utilities.readFromFile(CentroidsPath,shlClassification);
	    	descriptorCenters2=Utilities.readFromFile(CentroidsPath2,shlClassification);
		
		}else if(CentroidsPath!=null && CentroidsPath2==null){    // if one text is not null then it cant be sift-surf
	    	if(descriptorChoice.equals("Sift-Surf")){
				MessageDialog.openError(shlClassification, "Error","Sift surf requires two files!");
				return false;
			}
			//read centroids from file in disc
			descriptorCenters1=Utilities.readFromFile(CentroidsPath,shlClassification);
	    }
	}else if(model.equals("Bag of Colors") || model.equals("Graph BoC")){
		if(PaletePath==null) return false;
		
			//read palete from file
	  		clusterBoc.setPalette(Utilities.binaryFileTo2DIntArray(PaletePath) );
	}
	return true;
}

private void Modeling(IRMFactory factory,String descriptorChoice,String KMeansChoice, String model, String colorSpace
		,int numOfClusters,int patches,String normalization) throws Exception {

	String ProjectPath=createProjectPath();
	createProjectFile(new File(ProjectPath));

	if(model.equals("Bag of Visual Words")) {
		//if cluster file is given centers from cluster are overriden 
		checkIfClusterFileIsGiven(descriptorChoice,model);
				  
		if (descriptorChoice.equals("Sift-Surf")) {
			setObjectBoVW(factory.getBovw(),descriptorCenters1,descriptorCenters2,new SiftFeatures(),new SurfFeatures());
		}else if( descriptorChoice.equals("Phow") || descriptorChoice.equals("Dense Sift") ) {
			
			//phow and dense sift are supported only by vlfeat (matlab)
			if (!KMeansChoice.equals("VLFeat")) {
				MessageDialog.openError(shlClassification, "Completed", "Only VLFeat is supported by Phow");  
				return;
			}
			//TODO: matlab does not fill with libsvm format
			BoVWMatlab bovwMat= new BoVWMatlab();
			
			bovwMat.setClusterNum(numOfClusters);
			bovwMat.setDBSourcePath(DBSourcePath);
			bovwMat.setCentroidsPath(CentroidsPath);
			bovwMat.setDescriptorChoice(descriptorChoice);
			bovwMat.setProjectPath(ProjectPath);
			bovwMat.setShell(shlClassification);
			
			Thread threadBowMat = new Thread(bovwMat);
			threadBowMat.start();
		}else {
			setObjectBoVW(factory.getBovw(),descriptorCenters1,null,Utilities.getDescriptor(descriptorChoice),null);
		}		
		factory.getBovw().setNormalization(normalization);
		factory.getBovw().setClusterNum(numOfClusters);
		factory.getBovw().setDescriptorChoice(descriptorChoice);	
		factory.getBovw().setKMeansChoice(KMeansChoice);

	}else if(model.equals("Bag of Colors")) {
		//if cluster file is given palete from cluster are overriden 
		checkIfClusterFileIsGiven(null,"Bag of Colors");
		
		factory.getBoc().setNormalization(normalization);
		factory.getBoc().setColorspace(colorSpace);
		factory.getBoc().setNoOfColors(numOfClusters);
		factory.getBoc().setPalete(clusterBoc.getPalette());
	}else if(model.equals("Graph BoC")){
		//if cluster file is given palete from cluster are overriden 
		checkIfClusterFileIsGiven(null,"Bag of Colors");
		
		//TODO: matlab does not fill with libsvm format
		factory.getGboc().setCS(Utilities.findColorSpace(colorSpace));
		factory.getGboc().setDBSourcePath(DBSourcePath);
		factory.getGboc().setNumberOfColors(numOfClusters);
		factory.getGboc().setNumberOfPatches(patches);
		factory.getGboc().setProjectPath(ProjectPath);
		factory.getGboc().setShell(shlClassification);
	}		  
}	

//set descriptor for clustering depending on descriptor choice
private void setDescriptorForCluster(String model, String descriptorChoice,String KMeansChoice){
		//set cluster2 so it wont be null
		cluster2.setDescriptorChoice("");
		if(descriptorChoice.equals("Sift-Surf")){
			cluster1.setDescriptorChoice("Sift");
			cluster2.setDescriptorChoice("Surf");	
		}else
			cluster1.setDescriptorChoice(descriptorChoice);		
}
//cluster model using parameters given by user
private void Cluster(String KMeansChoice,String descriptorChoice, String model, String colorSpace,int numOfClusters,int sampleLimit,boolean isDistinctColors) throws Exception{
	 
		String ProjectPath=createProjectPath();
		createProjectFile(new File(ProjectPath));
		
		if(model.equals("Bag of Visual Words")){
			setDescriptorForCluster(model,descriptorChoice,KMeansChoice);
					
			cluster1= setClusterObjectBoVW(cluster1,DBSourcePath,ProjectPath,numOfClusters,sampleLimit,KMeansChoice);		
			Thread threadingCluster1 = new Thread(cluster1);
			threadingCluster1.start();
			//waiting thread to get centroids 
			threadingCluster1.join();
			descriptorCenters1= cluster1.getDescriptorcentroids();
			
			//only possibility for cluster 2 to be active is for descriptor to be surf
			if(cluster2.getDescriptorChoice().equals("Surf")){
				cluster2= setClusterObjectBoVW(cluster2,DBSourcePath,ProjectPath,numOfClusters,sampleLimit,KMeansChoice);		
		
				Thread threadingCluster2 = new Thread(cluster2);
				threadingCluster2.start();
				//waiting thread to get centroids 
				threadingCluster2.join();
				descriptorCenters2= cluster2.getDescriptorcentroids();
			}

		} else if(model.equals("Bag of Colors") || model.equals("Graph BoC") ){	//graph boc and boc share the same clustering options
			clusterBoc.setClusterDBPath(DBSourcePath);
			clusterBoc.setComboKMeansChoice(KMeansChoice);
			clusterBoc.setNumOfColors(numOfClusters);
			clusterBoc.setProjectPath(ProjectPath);
			clusterBoc.setSampleLimit(sampleLimit);
			clusterBoc.setShell(shlClassification);
			clusterBoc.setColorspace(colorSpace);
			clusterBoc.setModel(model);
			clusterBoc.setDistinctColors(isDistinctColors);
			Thread threadingBoc = new Thread(clusterBoc);
			threadingBoc.start();
			threadingBoc.join();
		
		}
				
}
//createXML 
private void CreateXML()
{
	if(projectNameText.getText()==""){
		MessageDialog.openError(shlClassification, "Error","Give project name");
		return;
	}else if(DBSourcePath==null){
		MessageDialog.openError(shlClassification, "Error","Give database source");
		return;
	}
	
	String xmlFilePath=createProjectPath()+"\\"+projectNameText.getText()+".xml";
	createProjectFile(new File(createProjectPath()));
	
		if(sameFileMessageBox(new File(xmlFilePath),"Xml file "+xmlFilePath+" already exists\nDo you want to overwrite?"))
			return;
		
			CreateXml xml=new CreateXml();
			try {
				if (xml.CreateXML(DBSourcePath, xmlFilePath, KeywordsPath, shlClassification))
					MessageDialog.openInformation(shlClassification, "Completed", "File "+projectNameText.getText()+".xml created  Successfully");
			} 
			catch (Exception e1){
	 			MessageDialog.openError(shlClassification, "Error","Error Creating xml file");
	 			return;
			}
}


private void ReadMetadata() throws IOException{
	String projectPath = System.getProperty("user.dir");
	int countLines =0;
	   BufferedReader reader = null;	    
	 	try {
	 		reader = new BufferedReader(new FileReader(new File(projectPath+"\\metadata.xml")));
		} catch (FileNotFoundException e1) {
			Utilities.showMessage("Could not Read File!", shlClassification, true);
			return;
		}	
		String line=null;
		   try {
			   while ((line = reader.readLine()) != null){ 
				 	   
				  countLines++;
				  if(countLines == 3)
				  {
					 projectNameText.setText(line.substring(line.indexOf(">")+1, line.indexOf("</"))  ) ;
				  }
				  else if(countLines == 4){
					  DBSourcePath = line.substring(line.indexOf(">")+1, line.indexOf("</"));
					  DBSourceText.setText(line.substring(line.indexOf(">")+1, line.indexOf("</"))  ) ;
				  }
			   }
		   }catch(Exception e){
			    System.out.println(e);
				Utilities.showMessage("Error reading file!", shlClassification, true);
	 			return;
		   }
		   try {
			   reader.close();
		   }catch (FileNotFoundException e) {
				Utilities.showMessage("Could not close File!", shlClassification, true);
	 			return;
		   }
}

private void EditMetadata() throws IOException{
	String projectPath = System.getProperty("user.dir");	  		   
   
   BufferedWriter Writer = null;
	 File File=null;//to arxeio xml pou tha grapsoume
	
	 try{
		 File = new File (projectPath+"\\metadata.xml");
	 }
	 catch(NullPointerException e ){
		 Utilities.showMessage("File Not Found!", shlClassification, true);
		 e.printStackTrace();
	 }
	 try{
		 Writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(File))); 
	 }catch(FileNotFoundException ex){
		 Utilities.showMessage("Error opening file!", shlClassification, true);
		 ex.printStackTrace();
	 }
	 Writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\">\n");
	 Writer.write("<startupMetadata>\n");
	 Writer.write("  <ProjectName>"+projectNameText.getText()+"</ProjectName>\n");
	 Writer.write("  <DatabaseSource>"+DBSourcePath+"</DatabaseSource>\n");
	 Writer.write("</startupMetadata>");


	 
	 try {
		 Writer.close();	
	 }catch (FileNotFoundException e) {
		 Utilities.showMessage("Could not close File!", shlClassification, true);
		 return;
	 }	   
}

//exit program dialog
private void ExitProgram()
{	
		 MessageBox messageBox = new MessageBox(shlClassification, SWT.ICON_QUESTION
		            | SWT.YES | SWT.NO);
		        messageBox.setMessage("Do you really want to exit?");
		        messageBox.setText("Exiting Application");
		        int response = messageBox.open();
		        if (response == SWT.YES)
		          System.exit(0);		
}
}
