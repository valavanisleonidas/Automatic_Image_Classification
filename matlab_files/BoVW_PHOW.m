function [test ,train] = BoVW_PHOW(DirPath, path ,centroidsPath , ClustersNum ,testTrain )
run([pwd ,'\..\Libs\VLFEAT\toolbox\vl_setup' ]);

if( strcmp( 'null' , centroidsPath ) == 1 )
    centroidFileName=[DirPath '\VLFeatKMeans-PHOW-'  num2str(ClustersNum) '-Centroids.txt'];
    centers=load(centroidFileName);
else
	centers= load(centroidsPath);
end

fileList = getAllFiles(path);
 descriptorFileName=[DirPath '\VLFeatKMeans-Phow-'  num2str(ClustersNum) '-' testTrain '.txt' ];

fid = fopen(descriptorFileName, 'wt');  
centers=centers';
kdtree = vl_kdtreebuild(centers);
  
for i=1:size(fileList,1)

			imgVocVector = zeros(1,ClustersNum);
			X = sprintf(' iter : %d image path  : %s ' ,i, fileList{i}  );
			disp(X);
			 
			%read Image
			imagePHOW = imread(fullfile(fileList{i})) ;
			%extract features
			[frames_phow, features_phow]=vl_phow(im2single(imagePHOW),'FloatDescriptors','true') ;
			featuresSizePHOW=size(features_phow);

		   features_phow=double(features_phow);

  		   % COMPUTE EUCLIDEAN DISTANCE AND FIND FOR EACH FEATURE THE SMALLER DISTANCE (KDTREE) - create ImgVocVector
			for j=1:size(features_phow,2)     
					[index, distance] = vl_kdtreequery(kdtree, centers, features_phow(:,j));
					imgVocVector(index) = imgVocVector(index)+1;   
			end
		   
				
			% NORMALIZE IMGVOCVECTOR INTO [0,1]
			for j=1:size(imgVocVector,1)
						norma  = sqrt(sum( imgVocVector(j,:) .^ 2));
					imgVocVector(j,:)=imgVocVector(j,:)/norma;
			end

			%   3) WRITE TO TXT IN LIBSVM FORMAT
			tokens = strsplit(fileList{i},'\');
			category=tokens{size(tokens,2)-1};
					fprintf(fid,'%s ' ,category);

			% write final vector to txt file 
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