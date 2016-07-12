function [descriptorCentroids,features_dsiftConcat]  = DSIFTClustering(DirPath ,numOfClusters,clustersPath,sampleSize)
run([pwd ,'\..\Libs\VLFEAT\toolbox\vl_setup' ]);

fileList = getAllFiles(clustersPath);
 
if(sampleSize>size(fileList,1))
    sampleSize=size(fileList,1);
end
% parameters for extracting features
binSize = 8 ;
magnif = 3 ;
features_dsiftConcat=[];
 for i=1:sampleSize
      X = sprintf(' iter : %d image path  : %s ' ,i, fileList{i}  );
      disp(X);
  
        ImageDSift = imread(fullfile(fileList{i})) ;
         
         if  size(ImageDSift, 3)>1
            ImageDSift = single(vl_imdown(rgb2gray(ImageDSift))) ; % single , rgb
         else
                ImageDSift = single(vl_imdown(ImageDSift)) ; % single , grayscale
         end
         % process image
		ImageSmooth = vl_imsmooth(ImageDSift, sqrt((binSize/magnif)^2 - .25)) ;

         % extract features using dense Sift
        [frames_dift, features_dsift] = vl_dsift(ImageSmooth, 'size', binSize,'FloatDescriptors') ;
		% concatenate all features into one array
        features_dsiftConcat = [features_dsiftConcat;features_dsift'];
          
 end  

 disp('creating centroids ');
[descriptorCentroids, Ax] = vl_kmeans(features_dsiftConcat', numOfClusters); % trexoume ton kmeans gia sift
 disp('finished ');
 
 descriptorCentroids=descriptorCentroids';
 descriptorCentroids=double(descriptorCentroids);
 save ( [DirPath '\VLFeatKMeans-Dense Sift-'  num2str(numOfClusters) '-Centroids.txt' ] , 'descriptorCentroids' ,'-ASCII' ,'-double')

disp('Completed');
 
          
end

