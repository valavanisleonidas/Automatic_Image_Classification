main
----
Test program for Quadtree decomposition (Encoding) and Decoding of RGB image


qt3ddecom
----------
Quadtree decomposition method for RGB image of size M-by-N-by-3. This method uses three thresholds for red,green and blue blocks. For any R or G or B block if the maximum value of the block elements minus the minimum value of the block elements is greater than its respective threshold then the RGB image is splitted into 4 blocks. 
 
Thresholds are specified as a value between 0 and 1, even if I is of class uint8 or uint16. If I is uint8, the threshold value you supply is multiplied by 255 to determine the actual threshold to use; if I is uint16, the threshold value you supply is multiplied by 65535.
 
The function also pad the image if its size is not power of 2, so that
image can be divided until block of size 1.
 
Input
image I (true color) image matrix of size M-by-N-by-3.
thvec: threshold values of rgb b/w 0 and 1 e.g. thvec=[0.5,0.5,0.5]
       (this is optional argument, defualt threshold is thvec=[0 0 0] i.e.
       lossless encoding 


Output
S: the quadtree structure in the sparse matrix S. If S(k,m) is
   nonzero, then (k,m) is the upper left corner (ULC) of a block in the
   decomposition, and the size of the block is given by S(k,m). 

valRGB(n,1:3):  mean (average) of RGB compoents for nth block

Exmaples of usage:
[S,valRGB]=qt3ddecom(I,thvec);
or
[S,valRGB]=qt3ddecom(I);        %lossless encoding

qtreergbdecode
--------------
Method to decode Quadtree date that is decomposed (encoded) by qt3ddecom method

Input
S: the quadtree structure in the sparse matrix S. If S(k,m) is
   nonzero, then (k,m) is the upper left corner (ULC) of a block in the
   decomposition, and the size of the block is given by S(k,m). 
valRGB(n,1:3):  mean (average) of RGB compoents for nth block

Output
I: Decoded Image

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------