filename = 'D:/Documents/NetBeans/genetics/fitnessResults';
delimiter = ' ';
data = importdata(filename);
n = length(data);
gen = 1:n;
fitness = zeros(n, 1);
survivors = zeros(n, 1);
avgLife = zeros(n, 1); 
f1 = figure;
f2 = figure;
f3 = figure;
% f1('finess');
% f2('survivors');
% f3('life');
for i=1:n
    survivors(i) = data(i, 3);
    fitness(i) = data(i, 2);
    avgLife(i)  = data(i, 4);
end
figure(f1);
plot(gen, fitness);
figure(f2);
plot(gen, survivors);
figure(f3);
plot(gen, avgLife);