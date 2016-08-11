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
package ImageRepresentationModels.BoC.LBOC;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Utility class for saving arrays to files
 * 3 Solutions in total
 * - DataIOStreams, Worst Speed, good for nothing
 * - BufferedIOStreams, Best write speed, good read speed, JDK<=1.5
 * - FileChannels, Best read speed, average write speed, JDK>=1.8
 * @author Spyridon Stathopoulos
 */
public class ArrayIO {
    
    public static void toFile(double[] a, String file){
        BufferedIOStream.toFile(a, file);
    }
    public static void toFile(double[][] a, String file){
        BufferedIOStream.toFile(a, file);
    }
    public static void toFile(float[] a, String file){
        BufferedIOStream.toFile(a, file);
    }
    public static void toFile(int[] a, String file){
        BufferedIOStream.toFile(a, file);
    }
    public static void toFile(int[][] a, String file){
        BufferedIOStream.toFile(a, file);
    }
                
    public static double[] doublesFromFile(String file){
        return BufferedIOStream.doublesFromFile(file);
    }
    public static double[][] doubles2DFromFile(String file){
        return BufferedIOStream.doubles2DFromFile(file);
    }
    public static float[] floatsFromFile(String file){
         return BufferedIOStream.floatsFromFile(file);
    }
    public static int[] intsFromFile(String file){
        return BufferedIOStream.intsFromFile(file);
    }
    public static int[][] ints2DFromFile(String file){
        return BufferedIOStream.ints2DFromFile(file);
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
    
    private static void safeClose(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            // do nothing
        }
    }
    private static void safeClose(InputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            // do nothing
        }
    }
    
    public static class BufferedIOStream{
        
        public static void toFile(double[] a, String file) {
            DataOutputStream outs=null;
            try {                
                // Create an output stream to the file.
                outs =new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );     
                // Write the data to the file
                // [0]<integer>: length (integer)
                outs.writeInt(a.length);
                // [...]<integer>: array data
                //outs.write(ByteConverter.toByteArray(a));
                for(int i=0;i<a.length;i++){
                    outs.writeDouble(a[i]);
                }
                outs.flush();          
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }
        public static void toFile(double[][] A, String file) {
            DataOutputStream outs=null;
            try {                
                // Create an output stream to the file.
                outs =new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );     
                // Write the data to the file
                // [0]<integer>: rows (integer)
                outs.writeInt(A.length);
                // [1]<integer>: cols (integer)
                outs.writeInt(A[0].length);
                // [...]<integer>: array data
                //outs.write(ByteConverter.toByteArray(a));
                for(int i=0;i<A.length;i++){
                    for(int j=0;j<A[0].length;j++){
                        outs.writeDouble(A[i][j]);
                    }
                }                    
                outs.flush();          
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }
        public static void toFile(float[] a, String file) {
            DataOutputStream outs=null;
            try {                
                // Create an output stream to the file.
                outs =new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );     
                // Write the data to the file
                // [0]<integer>: length (integer)
                outs.writeInt(a.length);
                // [...]<integer>: array data
//                outs.write(ByteConverter.toByteArray(a));
                for(int i=0;i<a.length;i++){
                    outs.writeFloat(a[i]);
                }
                outs.flush();          
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }
        public static void toFile(int[] a, String file) {
            DataOutputStream outs=null;
            try {                
                // Create an output stream to the file.
                outs =new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );     
                // Write the data to the file
                // [0]<integer>: length (integer)
                outs.writeInt(a.length);
                // [...]<integer>: array data
//                outs.write(ByteConverter.toByteArray(a));
                for(int i=0;i<a.length;i++){
                    outs.writeInt(a[i]);
                }
                outs.flush();          
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }    
        public static void toFile(int[][] A, String file) {
            DataOutputStream outs=null;
            try {                
                // Create an output stream to the file.
                outs =new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(file)
                        )
                );     
                // Write the data to the file
                // [0]<integer>: rows (integer)
                outs.writeInt(A.length);
                // [1]<integer>: cols (integer)
                outs.writeInt(A[0].length);
                // [...]<integer>: array data
                //outs.write(ByteConverter.toByteArray(a));
                for(int i=0;i<A.length;i++){
                    for(int j=0;j<A[0].length;j++){
                        outs.writeInt(A[i][j]);
                    }
                }                    
                outs.flush();          
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }
                        
        public static double[] doublesFromFile(String file){
            double[] ret = null;
            DataInputStream ins= null;
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins=new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)
                        )
                );                
                // [0]<integer>: length (integer)                
                ret=new double[ins.readInt()];
//                // [...]<integer>: array data                
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = ins.readDouble();
                }                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(ins);
            }
            return ret;
        }
        public static double[][] doubles2DFromFile(String file){
            double[][] ret = null;
            DataInputStream ins= null;
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins=new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)
                        )
                );                
                // [0]<integer>: rows (integer)                
                int rows=ins.readInt();
                // [0]<integer>: cols (integer)                
                int cols=ins.readInt();
                // [...]<integer>: array data                
                ret=new double[rows][cols];                
                for (int i = 0; i < ret.length; i++) {
                    for (int j = 0; j < ret[0].length; j++) {
                        ret[i][j] = ins.readDouble();
                    }
                }                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(ins);
            }
            return ret;
        }
        public static float[] floatsFromFile(String file){
            float[] ret = null;
            DataInputStream ins= null;
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins=new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)
                        )
                );                
                // [0]<integer>: length (integer)                
                ret=new float[ins.readInt()];
//                // [...]<integer>: array data                
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = ins.readFloat();
                }                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(ins);
            }
            return ret;
        }
        public static int[] intsFromFile(String file){
            int[] ret = null;
            DataInputStream ins= null;
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins=new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)
                        )
                );                
                // [0]<integer>: length (integer)                
                ret=new int[ins.readInt()];
//                // [...]<integer>: array data                
                for (int i = 0; i < ret.length; i++) {
                    ret[i] = ins.readInt();
                }                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(ins);
            }
            return ret;
        }
        public static int[][] ints2DFromFile(String file){
            int[][] ret = null;
            DataInputStream ins= null;
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins=new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)
                        )
                );                
                // [0]<integer>: rows (integer)                
                int rows=ins.readInt();
                // [0]<integer>: cols (integer)                
                int cols=ins.readInt();
                // [...]<integer>: array data                
                ret=new int[rows][cols];                
                for (int i = 0; i < ret.length; i++) {
                    for (int j = 0; j < ret[0].length; j++) {
                        ret[i][j] = ins.readInt();
                    }
                }                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(ins);
            }
            return ret;
        }
        
        
    }
    
    public static class FileChannels{
        //Ideas from http://nadeausoftware.com/articles/2008/02/java_tip_how_read_files_quickly#Zoomedplot        
        public static void toFile(double[] a, String file) {
            FileOutputStream outs =null;
            try {                
                // Create an output stream to the file.
                outs = new FileOutputStream(file);  
                FileChannel fs=outs.getChannel();                                                                
                ByteBuffer bf = ByteBuffer.allocateDirect((Integer.SIZE/8)+a.length*(Double.SIZE/8));
                // Write the data to the file
                // [0]<integer>: length (integer)
                bf.putInt(a.length);
                // [...]<integer>: array data
                for(int i=0;i<a.length;i++){
                    bf.putDouble(a[i]);
                }                
                //Close the file.
                bf.flip();
                fs.write(bf);
                fs.force(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }
        
        public static double[] doublesFromFile(String file){
            double[] ret = null;
            FileInputStream ins = null;
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins = new FileInputStream(file);                
                FileChannel fs=ins.getChannel();   
                //The following doesn't unmap correctly in Windows, possibly leading to an 
                //Exception: (The requested operation cannot be performed on a file with a user-mapped section open)
                //MappedByteBuffer buf = fs.map(FileChannel.MapMode.READ_ONLY, 0,fs.size());                                                
                ByteBuffer buf = ByteBuffer.allocateDirect((int)fs.size());    
                int nRead=fs.read(buf);
                buf.flip();
                // [0]<integer>: length (integer)
                int len = buf.getInt();                
                // [...]<integer>: array data
                ret = new double[len];
                for (int i = 0; i < len; i++) {
                    ret[i] = buf.getDouble();
                }
                fs.close();                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(ins);
            }
            return ret;
        }
        
    }        
    
    public static class DataIOStream{
        
        public static void toFile(double[] a, String file) {
            DataOutputStream outs=null;
            try {                
                // Create an output stream to the file.
                outs = new DataOutputStream(new FileOutputStream(file));                                    
                // Write the data to the file
                // [0]<integer>: length (integer)
                outs.writeInt(a.length);
                // [...]<double>: array data
                //dataOut.write(ByteConverter.toByteArray(a));
                for(int i=0;i<a.length;i++){
                    outs.writeDouble(a[i]);
                }
                outs.flush();                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{
                safeClose(outs);
            }
        }
        
        public static double[] doublesFromFile(String file){
            double[] ret = null;
            DataInputStream ins = null;            
            try {
                // Wrap the FileInputStream with a DataInputStream
                ins =new DataInputStream(new FileInputStream(file));
                // [0]<integer>: length (integer)
                ret = new double[ins.readInt()];
                for(int i=0;i<ret.length;i++){
                    ret[i]=ins.readDouble();
                }                                          
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally{   
                safeClose(ins);
            }
            return ret;
        }
                
    }
            
    //<editor-fold desc="Benchmarks">
    private static void Benchmark(double a[]){                
        File fl=null;
        try {
            System.out.print("Running for: "+a.length+" doubles...");
            fl=File.createTempFile("iotests_", ".dat");
            String f=fl.getPath();
            
            FileChannels.toFile(a, f);
            double[] b = FileChannels.doublesFromFile(f);
            
            BufferedIOStream.toFile(a, f);
            b = BufferedIOStream.doublesFromFile(f);                       
            
//            DataIOStream.toFile(a,f);
//            b = DataIOStream.doublesFromFile(f);
                        
            System.out.println("[OK]");            
        } catch (IOException ex) {            
            ex.printStackTrace();
        } finally{
            if(fl!=null){fl.deleteOnExit();}
        }
    }
       
    //</editor-fold>
}
