function y = extractingVW(numOfClusters , DirPath, descriptorChoice)
clc;
run([pwd ,'\..\Libs\VLFEAT\toolbox\vl_setup' ]);

disp('loading features');
descriptor = load( [DirPath , '\' ,'VLFeatSample', descriptorChoice , 'Features.txt' ]); %loading txt

disp('creating centroids ');
%run kmeans
[descriptorCentroids, Ax] = vl_kmeans(descriptor', numOfClusters); 

descriptorCentroids=descriptorCentroids';

descriptorFileName=[DirPath '\VLFeatKMeans-' descriptorChoice '-' num2str(numOfClusters) '-Centroids.txt' ];

save ( descriptorFileName , 'descriptorCentroids' ,'-ASCII' ,'-double')

disp('Completed');

end

