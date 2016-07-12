function [bestc , bestg , bestcv] = gridSearch(filepath)
addpath([ pwd '\..\Libs\libsvm\matlab']);

[label_vector, instance_matrix] = libsvmread(filepath);
fprintf('grid search for : %s \n', filepath);

bestcv = 0;
for log2c = -1:5,
  for log2g = -4:4,
    fprintf('starting %g %g \n', log2c, log2g);
    cmd = ['-v 5 -c ', num2str(2^log2c), ' -g ', num2str(2^log2g), ' -q'];
    tic
    cv = svmtrain(label_vector, instance_matrix, cmd);
    toc
    if (cv >= bestcv),
      bestcv = cv; bestc = 2^log2c; bestg = 2^log2g;
    end
    fprintf('%g %g %g (best c=%g, g=%g, rate=%g)\n', log2c, log2g, cv, bestc, bestg, bestcv);
  end
end

