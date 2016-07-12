% % if required then add padvalue (defualt 0)
% % to make size of A power of 2.
% %  e.g. if initiall size of A is 2x3
% % the after padding, size of A would be
% % 4x4
function A =padtomakepowof2(A,varargin)

% % % Default Values
padvalue=0;
defaultValues = {padvalue};
% % % Assign Values
nonemptyIdx = ~cellfun('isempty',varargin);
defaultValues(nonemptyIdx) = varargin(nonemptyIdx);
[padvalue] = deal(defaultValues{:});
% % ------------------------------------
B=A;
[M,N]=size(B);
type_A=class(A);
A=converttoclass(A,'double');
padvalue=converttoclass(padvalue,'double');
npo2=2^nextpow2(max(size(A)));
% % ------------------------------------
% % Padding process
A(1:npo2,1:npo2)=padvalue; % A expanded to required size
A(1:M,1:N)=B(1:M,1:N);     % 1:M,1:N locations filled with origincal values
% % OR
% % alternatively A can be padded using image processing tool
% % box method 'padarray' by following statement
% % A=padarray(A,[npo2-M,npo2-N],padvalue,'post'); 

% % ------------------------------------

A=converttoclass(A,type_A); % back to original type

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------