function [test ,train] = BoVW_DSift(DirPath, path ,centroidsPath , ClustersNum, testTrain )
run([pwd ,'\..\Libs\VLFEAT\toolbox\vl_setup' ]);

if( strcmp( 'null' , centroidsPath ) == 1 )
    centroidFileName=[DirPath '\VLFeatKMeans-Dense Sift-'  num2str(ClustersNum) '-Centroids.txt'];
    centers=load(centroidFileName);
else
	centers= load(centroidsPath);
end
	
fileList = getAllFiles(path);
 descriptorFileName=[DirPath '\VLFeatKMeans-DenseSift-'  num2str(ClustersNum) '-' testTrain '.txt' ];
 
fid = fopen(descriptorFileName, 'wt');  % uppercase W
centers=centers';
kdtree = vl_kdtreebuild(centers);
	  
for i=1:size(fileList,1)

	% FINAL VECTOR
	imgVocVector = zeros(1,ClustersNum);
	X = sprintf(' iter : %d image path  : %s ' ,i, fileList{i}  );
	binSize = 8 ;
	magnif = 3 ;
	disp(X);
			ImageDSift = imread(fullfile(fileList{i})) ;
			
			 if  size(ImageDSift, 3)>1
				ImageDSift = single(vl_imdown(rgb2gray(ImageDSift))) ; % single , rgb
			 else
					ImageDSift = single(vl_imdown(ImageDSift)) ; % single , grayscale
			 end
		  
	 
			ImageSmooth = vl_imsmooth(ImageDSift, sqrt((binSize/magnif)^2 - .25)) ;
			% EXAGOUME TA FEATURES TIS EIKONAS
			[frames_dift, features_dsift] = vl_dsift(ImageSmooth, 'size', binSize,'FloatDescriptors') ;
			features_dsift=double(features_dsift);
	   
		%%  1) COMPUTE EUCLIDEAN DISTANCE AND FIND FOR EACH FEATURE THE SMALLER DISTANCE (KDTREE)  - create ImgVocVector
	for j=1:size(features_dsift,2)
			[index, distance] = vl_kdtreequery(kdtree, centers, features_dsift(:,j) );
			imgVocVector(index) = imgVocVector(index)+1;   
	end
	  
		
		
	%%%    2) NORMALIZE IMGVOCVECTOR INTO [0,1]
	for j=1:size(imgVocVector,1)
				norma  = sqrt(sum( imgVocVector(j,:) .^ 2));
			imgVocVector(j,:)=imgVocVector(j,:)/norma;
	end

	%%%%   3) WRITE TO TXT IN LIBSVM FORMAT
	tokens = strsplit(fileList{i},'\');
	category=tokens{size(tokens,2)-1};
			fprintf(fid,'%s ' ,category);


	for j = 1:size(imgVocVector,1)
	   for h =1:size(imgVocVector,2) 
		  
		   if(imgVocVector(j,h) ~= 0 )
			fprintf(fid,'%d:%g ' ,h,imgVocVector (j,h) );
		   end
	   end
		fprintf(fid,'\n');
	end

    
end
 fclose(fid);

end

