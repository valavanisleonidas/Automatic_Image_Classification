/* 
 * Copyright (C) 2015 Spyridon Stathopoulos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Utils.Image.display;

import Utils.Image.ImageFilter;
import Utils.Image.ImageUtility;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Spyridon Stathopoulos
 */
public class ImageViewer extends JFrame{
    
    private BufferedImage bimg;    
    private JLabel lblImg;

    public ImageViewer(String title, String file,boolean show) throws Exception {
        this(title,ImageUtility.getImage(file),show);
    }
    
    public ImageViewer(String title, BufferedImage img,boolean show){
        this.setTitle(title);
        this.bimg = img;
        init(show);
    }
    
    private void init(boolean show){
        //Set system's lool and feel
         try {            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }  
        
        //Add image label
        lblImg = new JLabel(new ImageIcon(bimg));        
        this.getContentPane().add(lblImg, BorderLayout.CENTER);                
        
        //Add save toolbar
        JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
        //java.net.URL imageURL = ImageViewer.class.getResource("../resources/save.png");
        //JButton svbutton = new JButton(new ImageIcon(imageURL));
        JButton svbutton = new JButton("Save");
        svbutton.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e)
            { saveImage(); }
        });  
        svbutton.setToolTipText("Save");
        toolbar.add(svbutton);        
        this.getContentPane().add(toolbar,BorderLayout.PAGE_START);
        //Set opening localtion to center
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(bimg.getWidth(), bimg.getHeight()+toolbar.getHeight());
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);        
        //The application will stop only if nothing else is running
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);        
        pack();
        //Display frame        
        pack();
        if(show){this.setVisible(true);}
    }    
    
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equalsIgnoreCase("Save")){
            
        }
    }
    
    public void update(String file, boolean bringToFront) throws Exception{
        update(ImageUtility.getImage(file),bringToFront);
    }
    
    public void update(BufferedImage img, boolean bringToFront){
        this.bimg = img;
        this.lblImg.setIcon(new ImageIcon(bimg));
        this.setVisible(true);
        this.toFront();
    }
    
    public void saveImage() {
        JFileChooser chooser = new JFileChooser();
        //Set file filters
        chooser.setAcceptAllFileFilterUsed(false);      
        FileNameExtensionFilter filt[] = ImageFilter.getImageFileFilters();
        ImageFilter iff = new ImageFilter();
        for(FileNameExtensionFilter f:filt){
            chooser.addChoosableFileFilter(f);
        }
        //Show dialog        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            //Correct extension if neccesary
            String selExt = chooser.getFileFilter().getDescription().toLowerCase();
            String file = chooser.getSelectedFile().getAbsolutePath();
            String ext = getExtension(file);
            if(ext.trim().equals("") || !iff.accept(new File(file))){  
                //Extension is not correct, add selected extension
                file += "." + selExt; 
            }            
            File fl = new File(file);            
            //Check if file already exists
            boolean save = true;
            if(fl.exists()){
               int conf = JOptionPane.showConfirmDialog(this,file + " already exists.\nDo you want to replace it?",
                          "Confirm Save As",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);                
               if(conf == JOptionPane.NO_OPTION){ save = false; }                  
            }            
            //Save the file
            if(save){                               
                try {
                    ImageIO.write(bimg, selExt, fl);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private static String getExtension(String filePath){
        File fl = new File(filePath);
        return getExtension(fl);
    }
    
    private static String getExtension(File fl){
         int indx = fl.getName().lastIndexOf(".");
        if(indx>0)
            return fl.getName().substring(indx, fl.getName().length());
        else
            return "";
    }
}
