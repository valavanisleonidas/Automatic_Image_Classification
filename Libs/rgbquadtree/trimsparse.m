% % trim sparse matrix by removing extra rows/columns that have all elements zeros.

function S=trimsparse(S)

[i,j,v] = find(S);
S=S(1:max(i),1:max(j)); 