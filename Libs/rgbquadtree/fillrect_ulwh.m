% % fill the rectangle region specified by upper left corner width
% % and height of recangle.

% % INPUT
% % ul=[x0,y0] i.e coordinates of upper left corner of rectangle
% % w: width of rectangle
% % h: height of rectangle
% % colorStr: color string one of eight predefined colors 
% %           'r' or 'y' or 'g' or 'c' or 'b' or 'm' or 'w' or 'b'
function fillrect_ulwh(ul,w,h,varargin)

% % % Default Values 
colorStr='g'; %default filling color green
defaultValues = {colorStr};
% % % Assign Values
nonemptyIdx = ~cellfun('isempty',varargin);
defaultValues(nonemptyIdx) = varargin(nonemptyIdx);
[colorStr] = deal(defaultValues{:});
% % %---------------------

% fill([ ul(1)+w, ul(1)+w, ul(1), ul(1) ],...
%      [ ul(2)+h, ul(2)+h, ul(2), ul(2) ],...
%        colorStr);

% figure, ,hold on
% axis ij
fill([ ul(1), ul(1),   ul(1)+w, ul(1)+w ],...
     [ ul(2), ul(2)+h, ul(2)+h, ul(2) ],...
       colorStr);
axis ij

% figure,
% axis normal

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------