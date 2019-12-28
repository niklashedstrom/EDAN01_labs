include "globals.mzn";
include "3.dzn";

%% INPUTS

% area square size
int: n;

% number of commercial blocks
int: n_commercial;

% number of residential blocks
int: n_residential;

% point distribution
array[0..n] of int: point_distribution;

% 0 represents the commercial lots, 1 represents the residential lots
array[1..n,1..n] of var 0..1: area;

constraint
    sum(area) = n_residential;

var (2 * n * min(point_distribution))..(2 * n * max(point_distribution)): cost;

array[1..n*2] of var min(point_distribution)..max(point_distribution): sums;

constraint forall(i in 1..n)(
    point_distribution[sum([area[i,j] | j in 1..n])] == sums[i] /\
    point_distribution[sum([area[j,i] | j in 1..n])] == sums[i + n]
);

constraint
    sum(sums) == cost;

constraint
    lex2(area);

solve :: int_search(area, input_order, indomain_min) maximize cost;

