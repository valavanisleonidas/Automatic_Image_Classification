% % This method uses three thresholds for red,green and blue blocks.
% % For any R/G/B block if the maximum value of the block elements minus the
% % minimum value of the block elements is greater than its respective threshold
% % then the RGB image is splitted into 4 blocks. 

% % Thresholds are specified as a value between 0 and 1, even if I is of class uint8 or
% % uint16. If I is uint8, the threshold value you supply is multiplied by
% % 255 to determine the actual threshold to use; if I is uint16, the
% % threshold value you supply is multiplied by 65535

% % The function also pad the image if its size is not power of 2, so that
% % image can be divided until block of size 1.

% % Input
% % image I (true color) image matrix of size M-by-N-by-3.
% % thvec: threshold values of rgb b/w 0 and 1 e.g. thvec=[0.5,0.5,0.5]
% %        (this optional argument, defualt threshold is [0 0 0] i.e.
% %        lossless encoding 

% % Output
% % S: the quadtree structure in the sparse matrix S. If S(k,m) is
% %    nonzero, then (k,m) is the upper left corner (ULC) of a block in the
% %    decomposition, and the size of the block is given by S(k,m). 
% % valRGB(n,1:3):  mean (average) of RGB compoents for nth block

% % Exmaple of usage: [S,valRGB]=qt3ddecom(I,thvec);
% %                   or [S,valRGB]=qt3ddecom(I);

function [S,valRGB]=qt3ddecom(I,minBlockSize,varargin)
% disp('RGB Image Quadtree Decomposition');
% str=sprintf('%s', '@ Copyright M Khan'); disp(str);
% disp('Email: mak2000sw@yahoo.com');
%disp('---------------------------------');
% %--------------------------------------------------------------
% % Default Values 
thvec=[0 0 0];                %lossless encoding
defaultValues = {thvec};      %threshold vector
% % % Assign Values
nonemptyIdx = ~cellfun('isempty',varargin);
defaultValues(nonemptyIdx) = varargin(nonemptyIdx);
[thvec] = deal(defaultValues{:});
% %--------------------------------------------------------------
classtr=class(I);
[ir ic id]=size(I); % size of image before padding
if(id~=3)
    error('Input Matrix (Image) must be of size M-by-N-by-3 (true color)');    
end
if(~ispowerof2(ir) || ~ispowerof2(ic))
    I=padrgbtomakepowof2(I);
    % % sizeIafterPad=size(I);
    % % disp('padding is done to make input Matrix/Image power of 2');
end
[irp,icp,id]=size(I); % size of image after padding
S=zeros(irp,icp);

% %--------------------------------------------------------------
% % Matrix S is intitially of same size as input image/matrix and holds
% % zeros in all locations.
% % stru1 holds information about those images that are to be tested
% % for splitting.
% % initially we would put coordinates of ULC of image into stru1 then
% % we will execute a loop until stru1 becomes empty. 
% % Inside loop we would take coordinates of last image block from stru1
% % and remove them from stru1, then test if for splitting.
% % If it split then we would put four new blocks into stru1. If it does
% % not split then we would (ULC) of that single block into matrix S.
% % This process would continue until stru1 becomes emtpy, that means no
% % more splitting is required and we would be outside while loop. Then
% % simple we would converte matrix S into sparse matrix.

count1=1;                    % counter to save data in S
stru1(count1).rc=[1 1];      % beginning (upper left corner) coordinates of image
stru1(count1).sz=size(I,1);  % size of image (width and height are equal)

while(size(stru1,2)>=1)    
    r1=stru1(end).rc(1);
    r2=r1+stru1(end).sz-1;
    c1=stru1(end).rc(2);
    c2=c1+stru1(end).sz-1; 
    stru1(end)=[];          % remove from stru1
    count1=count1-1;
    % % splitting (return splitting coordiantes with respect to segment
    % % (block) of image (matrix)
    [b1r,b1c,b2r,b2c,b3r,b3c,b4r,b4c,sz]=qtrgbsplit(I(r1:r2,c1:c2,1:3),minBlockSize,thvec);
    if(sz==-1) % Not splitted
        % % disp('not splitted');
        S(r1,c1)=r2-r1+1;
    else      % splitted here we can find the pieces to split elseif (sz>=4)
        % % disp('splitted');
        %% converting local coordinates to global coordinates
        b1r=r1+b1r-1;
        b1c=c1+b1c-1;
        b2r=r1+b2r-1;
        b2c=c1+b2c-1;
        b3r=r1+b3r-1;
        b3c=c1+b3c-1;
        b4r=r1+b4r-1;
        b4c=c1+b4c-1;        
        % % adding fourth block data in stru1
        count1=count1+1;
        stru1(count1).rc=[b4r,b4c];      
        stru1(count1).sz=sz;
        % % adding third block data in stru1
        count1=count1+1;
        stru1(count1).rc=[b3r,b3c];      
        stru1(count1).sz=sz;             
        % % adding second block data in stru1
        count1=count1+1;
        stru1(count1).rc=[b2r,b2c];      
        stru1(count1).sz=sz;             
        % % adding first block data in stru1
        count1=count1+1;
        stru1(count1).rc=[b1r,b1c];      
        stru1(count1).sz=sz;
        
    end    
end
% %--------------------------------------------------------------
S=sparse(S); %converting S into sparse matrix
[i,j,blkSz] = find(S);
if(length(find(thvec))==0) %lossless encoding (all entries in thvec are 0)
    for k=1:length(blkSz)        
        valRGB(k,1:3)=I(i(k),j(k),1:3); % values of corresponding indices of S of RGB
    end
else             %lossy encoding
    valR=avgofmatblks(I(:,:,1),i,j,blkSz,blkSz); % mean of Red
    valG=avgofmatblks(I(:,:,2),i,j,blkSz,blkSz); % mean of Green
    valB=avgofmatblks(I(:,:,3),i,j,blkSz,blkSz); % mean of Blue
    valRGB=[valR', valG', valB'];
end
% %--------------------------------------------------------------
% % % Now Removing those rows from S and valRGB that exceed original image size(ir,ic)
% % % ir,ic,irp,icp
if (ir<irp && ic<icp)
    [S,valRGB]=delsparsekeybyxANDy(S,i,j,valRGB,ir,ic);
end
% %--------------------------------------------------------------
% % Trim S
% S=S(1:max(i),1:max(j)); %use previously computed [i,j,v]=find(S)
S=trimsparse(S);          %computes again [i,j,v]=find(S)
% %--------------------------------------------------------------
% % next stpe is to convert to same class as that of original image.
% % if original image is of type integer this operation would also round
% % off
valRGB=converttoclass(valRGB,classtr); 
% %  sizeSAfter=size(S)
% %--------------------------------------------------------------
% % % Plotting
%figure, image(I)
%title('\bfOriginal Padded Image');
% drawquadblocks(S);
% title('\bfQuadtree blocks on Padded Image');
% %--------------------------------------------------------------


% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------
