function [descriptorCentroids,features_phowConcat]  = PHOWClustering(DirPath ,numOfClusters ,clustersPath,sampleSize)
run([pwd ,'\..\Libs\VLFEAT\toolbox\vl_setup' ]);

fileList = getAllFiles(clustersPath);
if(sampleSize>size(fileList,1))
    sampleSize=size(fileList,1);
end
features_phowConcat=[];

 for i=1:sampleSize
      X = sprintf(' iter : %d image path  : %s ' ,i, fileList{i}  );
      disp(X);
	 % read image
     imagePHOW = imread(fullfile(fileList{i})) ;
	% extract features 
    [frames_phow, features_phow]=vl_phow(im2single(imagePHOW),'FloatDescriptors','true') ;
     %concat all features into one array         
	features_phowConcat =[features_phowConcat;features_phow'];
          
 end  
 
 disp('creating centroids ');
[descriptorCentroids, Ax] = vl_kmeans(features_phowConcat', numOfClusters); % trexoume ton kmeans gia sift
 disp('finished ');

 descriptorCentroids=descriptorCentroids';
 descriptorCentroids=double(descriptorCentroids);
 save ( [DirPath '\VLFeatKMeans-PHOW-'  num2str(numOfClusters) '-Centroids.txt' ] , 'descriptorCentroids' ,'-ASCII' ,'-double')
 disp('Completed');
          
end
