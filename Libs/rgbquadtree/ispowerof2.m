% % check if given number 'n'is power of 2.
% % return 1 if given number 'n'is power of 2, otherwise return 0

function flag=ispowerof2(n)

flag=0;

table=[1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144,524288,...
    1048576,2097152,4194304,8388608,16777216,33554432,67108864,134217728,268435456,536870912];

if(n>table(end) || n<1)
    error('Out out range, can not check either this value is power of 2 or not')
end

if( length(find(table == n)>0) )
    flag=1;
end

% % % --------------------------------
% % % Author: Dr. Murtaza Khan
% % % Email : drkhanmurtaza@gmail.com
% % % --------------------------------
    