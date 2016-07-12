% % finds average of different blocks of input matrix
% % INPUT
% % i,j,w,h are vectors
% % mat : input matrix
% % i(k): beginning row index of kth block
% % j(k): beginning column index of kth block
% % w(k): width of kth block   (e.g. column 1 to colum 5 of mat)
% % h(k): height of kth block  (e.g. row 1 to row 3 of mat)
% % OUTPUT
% % avg(k): average of kth block (avg is a vector)
function avg=avgofmatblks(mat,i,j,w,h)

blkcount=length(i);
for k=1:blkcount        
    fromRow=i(k);
    toRow=i(k)+h(k)-1;
    fromCol=j(k);
    toCol=j(k)+w(k)-1;
    count=(toRow-fromRow+1).*(toCol-fromCol+1);
    avg(k)=sum(sum(mat(fromRow:toRow,fromCol:toCol)))/count; %avg/mean of block values
end

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------