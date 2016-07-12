% % Decode QuadTree Represenation of Image given as Sparse matrix 'S' and
% % correonding values in 'values'.
% % Decodeed image 'I' is returned.
% % type of decoded image is depend on values. 
function I=qtreedecode(S,values)
valuesclass=class(values);

[i,j,s] = find(S);
% % kth  (i,j) contains beginning location of kth block
% % kth  s     contains size of kth block e.g. if s(2)=3
% % then size of 2nd block is 3x3.


% % (lastblk_i,lastblk_j) contains beginning location of last block
% % We assume last entry in Sparse matrix 'S' is of last block
lastblk_i=i(end);
lastblk_j=j(end);
blkcount=length(i);

ri=lastblk_i+s(blkcount)-1; % # of rows in decoded image
ci=lastblk_j+s(blkcount)-1; % # of columns in decoded image
I = zeros(ri,ci);
for k=1:blkcount
     I(i(k):i(k)+s(k)-1,j(k):j(k)+s(k)-1)=values(k);
end
I=converttoclass(I,valuesclass); % convert to same class as that of values


% % it seems that MATLAB construct the sparse matrix such that last
% % entry of sparse matrix is of last block.
% % If last entry in Sparse matrix 'S' is NOT of last block
% % then we can find it by following algorithm
% blkcount=1;
% lastblk_i=1;
% lastblk_j=1;
% maxij=lastblk_i*lastblk_j;
% for k=1:length(i)
%      if(i(k)*j(k)>maxij)
%          blkcount=k;
%          lastblk_i=i(k);
%          lastblk_j=j(k);
%      end
% end

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------
