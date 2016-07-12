% % draw square quad blocks of quadtree in eight basic colors.
% % S: Sparse matrix that holds quadtree information
function drawquadblocks(S)

mycol = strvcat('r','y','g','c','b','m','k','w');
[i,j,v]=find(S); %i,j are indices, v is value (i.e. block size) at (i,j)
figure, hold on
for k=1:length(v)
    M = mod(k-1,length(mycol))+1;
    fillrect_ulwh([j(k),i(k)],v(k),v(k),mycol(M));
end
axis tight 

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------