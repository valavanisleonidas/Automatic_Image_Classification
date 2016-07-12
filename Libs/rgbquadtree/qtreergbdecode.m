% % Decode QuadTree date that is decomposed (encode) by qt3ddecom method

% % Input
% % S: the quadtree structure in the sparse matrix S. If S(k,m) is
% %    nonzero, then (k,m) is the upper left corner (ULC) of a block in the
% %    decomposition, and the size of the block is given by S(k,m). 
% % valRGB(n,1:3):  mean (average) of RGB compoents for nth block

% % Output
% % I: Decoded Image

function I=qtreergbdecode(S,valRGB)

I(:,:,1)=qtreedecode(S,valRGB(:,1)); 
I(:,:,2)=qtreedecode(S,valRGB(:,2));
I(:,:,3)=qtreedecode(S,valRGB(:,3));

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------