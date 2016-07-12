% % Test program for Quadtree Encoding and Decoding of RGB image
clc, close all, clear all
thvec=[0.5,0.5,0.5];  % threshold for red,green and blue (values b/w 0 and 1)
picname='rafting.png';
I = imread(picname);  
% [ir ic d]=size(I);    % size of I must be M-by-N-by-3
% figure, image(I)          % original input image
% title('\bfOriginal Image');
% % ----------------------------------
% % RGB Quadtree Encoding
%[S,valRGB]=qt3ddecom(I,thvec);  %lossy encoding
% % OR
[S,valRGB]=qt3ddecom(I);      %lossless encoding
% % ----------------------------------
% % RGB Quadtree Decoding
% Iback=qtreergbdecode(S,valRGB);  %Iback is approximated (reconstructed) image  
% IbackTrim=Iback(1:ir,1:ic,:); %trim to original image size
% % % ----------------------------------
% figure, image(IbackTrim); % decoded image
% title('\bfQuadtree Decoded Image');

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------