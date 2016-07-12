function ConvertCsvToLibSVM( csvFile )

% example csv file
% 1,0,52,0,0,73,66,72
% 1,72,62,69,67,78,82
%
% First column labels seperated all by commas 
% rest columns features

SPECTF = csvread(csvFile); % read a csv file
labels = SPECTF(:, 1); % labels from the 1st column
features = SPECTF(:, 2:end); % features from the rest columns
features_sparse = sparse(features); % features must be in a sparse matrix
libsvmwrite('SPECTFlibsvm.train', labels, features_sparse);


