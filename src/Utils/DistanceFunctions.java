/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;


/**
 *
 * @author Spyridon Stathopoulos
 */

public class DistanceFunctions{

    /**
     * @param point1   first point
     * @param point2   second point
     * @return  the  Euclidean distance of two points
     */
    public static double getL2Distance(double[] point1, double[] point2) {
        double dist = 0;
        for (int i = 0; i < point1.length; i++) {
            dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        return java.lang.Math.sqrt(dist);
    }
    public static double getL2Distance(float[] point1, float[] point2) {
        double dist = 0;
        for (int i = 0; i < point1.length; i++) {
            dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        return java.lang.Math.sqrt(dist);
    }
    public static double getL2Distance(int[] point1, int[] point2) {
        double dist = 0;
        for (int i = 0; i < point1.length; i++) {
            dist += (point1[i] - point2[i]) * (point1[i] - point2[i]);
        }
        return java.lang.Math.sqrt(dist);
    }

     /**
     * Jeffrey Divergence or Jensen-Shannon divergence (JSD) from
     * Deselaers, T.; Keysers, D. & Ney, H. Features for image retrieval:
     * an experimental comparison Inf. Retr., Kluwer Academic Publishers, 2008, 11, 77-107
     *
     * @param h1
     * @param h2
     * @return
     */
    public static double getJSD(double[] h1, double[] h2) {
        double sum = 0.0;
        for (int i = 0; i < h1.length; i++) {
            sum += h1[i] > 0 ? h1[i] * java.lang.Math.log(2.0 * h1[i] / (h1[i] + h2[i])) : 0 + h2[i] > 0 ? h2[i] * java.lang.Math.log(2.0 * h2[i] / (h1[i] + h2[i])) : 0;
        }
        return sum;
    }
    public static double getJSD(int[] h1, int[] h2) {
        double sum = 0.0;
        for (int i = 0; i < h1.length; i++) {
            sum += h1[i] > 0 ? h1[i] * java.lang.Math.log(2.0 * (float) h1[i] / (float) (h1[i] + h2[i])) : 0 + h2[i] > 0 ? h2[i] * java.lang.Math.log(2.0 * (float) h2[i] / (float) (h1[i] + h2[i])) : 0;
        }
        return sum;
    }
   

    /**
     *
     * @param point1 first point
     * @param point2 second point
     * @return the L1 distance of two points
     */
    public static double getL1Distance(double[] point1, double[] point2) {
        double dist = 0.0;
        for (int i = 0; i < point1.length; i++) {
            dist += java.lang.Math.abs(point1[i] - point2[i]);
        }
        return dist / point1.length;
    }
    public static double getL1Distance(float[] point1, float[] point2) {
        double dist = 0.0;
        for (int i = 0; i < point1.length; i++) {
            dist += java.lang.Math.abs(point1[i] - point2[i]);
        }
        return dist / point1.length;
    }
    public static double getL1Distance(int[] point1, int[] point2) {
        double dist = 0;
        for (int i = 0; i < point1.length; i++) {
            dist += java.lang.Math.abs(point1[i] - point2[i]);
        }
        return dist / point1.length;
    }

    /**
     * Tanimoto Coefficient (not tested!!)
     * http://en.wikipedia.org/wiki/Cosine_
     * @param v1
     * @param v2
     * @return
     */
    public static double getTanimotoCoeff(double[] v1, double[] v2) {
        double sumab = 0;
        double magV1 = 0;
        double magV2 = 0;
        for (int i = 0; i < v1.length; i++) {
            sumab += v1[i] + v2[i];
            magV1 += v1[i] * v1[i];
            magV2 += v2[i] * v2[i];
        }
        if (magV1 + magV2 - sumab == 0) {
            return 0;
        }
        return sumab / (magV1 + magV2 - sumab);
    }
    public static double getTanimotoCoeff(int[] v1, int[] v2) {
        double sumab = 0;
        double magV1 = 0;
        double magV2 = 0;
        for (int i = 0; i < v1.length; i++) {
            sumab += v1[i] + v2[i];
            magV1 += v1[i] * v1[i];
            magV2 += v2[i] * v2[i];
        }
        if (magV1 + magV2 - sumab == 0) {
            return 0;
        }
        return sumab / (magV1 + magV2 - sumab);
    }

    /**
     * Calculates the Cosine similarity between two vectors
     * http://en.wikipedia.org/wiki/Cosine_similarity
     * http://en.wikipedia.org/wiki/Magnitude_(mathematics)#Euclidean_vectors
     * @param v1
     * @param v2
     * @return
     */
    public static float getCosineSimilarity(double[] v1, double[] v2) {
        double dotProduct = 0;
        double eucledianDist = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        eucledianDist = java.lang.Math.sqrt(norm1 * norm2);
        if (eucledianDist == 0) {
            return 0;
        }
        return (float) (dotProduct / eucledianDist);
    }
    public static float getCosineSimilarity(int[] v1, int[] v2) {
        double dotProduct = 0;
        double eucledianDist = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        eucledianDist = java.lang.Math.sqrt(norm1 * norm2);
        if (eucledianDist == 0) {
            return 0;
        }
        return (float) (dotProduct / eucledianDist);
    }
      
    public interface IDistanceFunction {
        public String getName();
        public float getDistance(double[] v1, double[] v2);        
        public float getDistance(int[] v1, int[] v2);   
    }
    
    public static IDistanceFunction getL1Function(){
        return new IDistanceFunction(){
            @Override
            public String getName(){
                return "L1";
            }
            
            @Override
            public float getDistance(double[] v1, double[] v2) {
                return (float)DistanceFunctions.getL1Distance(v1, v2);
            }

            @Override
            public float getDistance(int[] v1, int[] v2) {
                return (float)DistanceFunctions.getL1Distance(v1, v2);
            }
        };
    }    
    public static IDistanceFunction getL2Function(){
        return new IDistanceFunction(){
            @Override
            public String getName(){
                return "L2";
            }
            
            @Override
            public float getDistance(double[] v1, double[] v2) {
                return (float)DistanceFunctions.getL2Distance(v1, v2);
            }

            @Override
            public float getDistance(int[] v1, int[] v2) {
                return (float)DistanceFunctions.getL2Distance(v1, v2);
            }
        };
    }    
    public static IDistanceFunction getJSDFunction(){
        return new IDistanceFunction(){
            @Override
            public String getName(){
                return "JSD";
            }
            
            @Override
            public float getDistance(double[] v1, double[] v2) {
                return (float)DistanceFunctions.getJSD(v1, v2);
            }

            @Override
            public float getDistance(int[] v1, int[] v2) {
                return (float)DistanceFunctions.getJSD(v1, v2);
            }
        };
    }    
    public static IDistanceFunction getCosFunction(){
        return new IDistanceFunction(){
            @Override
            public String getName(){
                return "Cos";
            }
            
            @Override
            public float getDistance(double[] v1, double[] v2) {
                return 1-(float)DistanceFunctions.getCosineSimilarity(v1, v2);                
            }

            @Override
            public float getDistance(int[] v1, int[] v2) {
                return 1-(float)DistanceFunctions.getCosineSimilarity(v1, v2);             
            }
        };               
    }    
    public static IDistanceFunction getTanimotoFunction(){
        return new IDistanceFunction(){
            @Override
            public String getName(){
                return "Tanimoto";
            }
            
            @Override
            public float getDistance(double[] v1, double[] v2) {
                return (float)DistanceFunctions.getTanimotoCoeff(v1, v2);                
            }

            @Override
            public float getDistance(int[] v1, int[] v2) {
                return (float)DistanceFunctions.getTanimotoCoeff(v1, v2); 
            }
        };               
    }
    
    
    
}


