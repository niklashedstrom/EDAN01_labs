include "globals.mzn";
include "ar_2_4.dzn";

%% INPUTS
% clock cycles
int: del_add; int: del_mul;

% number of operators
int: number_add; int: number_mul;

% number of operations
int: n;

% set of indecies for last nodes
set of int: last;
% set of indecies for add nodes
set of int: add;
% set of indecies for mul nodes
set of int: mul;
% array of dependencies between nodes
array[1..n] of set of int: dependencies;

%% CONSTANTS
int : max_time = card(add) * del_add + card(mul) * del_mul;

%% VARIABLES
% variables for start time for all nodes
array[1..n] of var 0..max_time: start_time;

% create durations array for use when adding constriants for dependencies
array[1..2] of int: del_arr = [del_add, del_mul];
array[1..n] of var min(del_arr)..max(del_arr): durations;
constraint
    forall (node in add)(
        durations[node] = del_add
    );
constraint
    forall (node in mul)(
        durations[node] = del_mul
    );

% add constraint for dependencies
constraint
    forall(node in 1..n)(
        forall(dep_node in dependencies[node])(
            start_time[node] + durations[node] <= start_time[dep_node]
        )
    );

% split start time for cumulative
array[1..card(add)] of var 0..max_time: start_time_add;
array[1..card(mul)] of var 0..max_time: start_time_mul;
constraint
    start_time_add = [start_time[node] | node in 1..n where node in add];
constraint
    start_time_mul = [start_time[node] | node in 1..n where node in mul];

% create arrays for cumulative
array[1..card(add)] of int: duration_add = [del_add | i in 1..card(add)];
array[1..card(mul)] of int: duration_mul = [del_mul | i in 1..card(mul)];
array[1..card(add)] of int: resurces_add = [1 | i in 1..card(add)];
array[1..card(mul)] of int: resurces_mul = [1 | i in 1..card(mul)];

constraint
    cumulative(start_time_add, duration_add, resurces_add, number_add);

constraint
    cumulative(start_time_mul, duration_mul, resurces_mul, number_mul);

var 0..max_time: end_time;

constraint
    end_time = max([start_time[node] + durations[node] | node in last]);

array[1..card(add)] of var 1..number_add: assigned_add;
constraint
    diffn(start_time_add, assigned_add, duration_add, resurces_add);

array[1..card(mul)] of var 1..number_mul: assigned_mul;
constraint
    diffn(start_time_mul, assigned_mul, duration_mul, resurces_mul);

solve :: seq_search ([
    int_search(start_time, first_fail, indomain_min, complete),
    int_search(assigned_add ++ assigned_mul, first_fail, indomain_min, complete)
    ]) minimize end_time;
output[show(end_time)];
output[show(assigned_add), show(assigned_mul)];