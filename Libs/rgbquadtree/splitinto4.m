% % given the size of rectangle (matrix end points), it split into four
% % regions and return the starting coordinates (top left corner) and size of each region. 
% % ------------------
% % |        |       |
% % ------------------
% % |        |       |
% % ------------------
function [b1r,b1c,b2r,b2c,b3r,b3c,b4r,b4c,szr,szc]=splitinto4(ir,ic)

szr=round(ir/2);
szc=round(ic/2);

b1r=1;
b1c=1;

b2r=1;
b2c=szc+1;

b3r=szr+1;
b3c=1;

b4r=szr+1;
b4c=szc+1;

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------

