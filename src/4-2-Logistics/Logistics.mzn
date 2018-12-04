include "globals.mzn";
include "Logistics3.dzn";

int: graph_size;
int: start;
int: n_dests;
array[1..n_dests] of int: dest;
int: n_edges;
array[1..n_edges] of int: from;
array[1..n_edges] of int: to;
array[1..n_edges] of int: cost;

array[1..n_edges] of var bool: is_used;

% Start node has atleast one connected edge used.
constraint
  sum(edge in 1..n_edges where from[edge] = start \/ to[edge] = start)(is_used[edge]) >= 1;

% Dest nodes has atleast one connected edge used.
constraint forall (node in 1..n_dests) (
  sum(edge in 1..n_edges where to[edge] = dest[node] \/ from[edge] = dest[node])(is_used[edge]) >= 1
);

% Other nodes has either 0 or >=2 connected edges used.
constraint forall (node in 1..graph_size) (
  if (node != start /\ sum(n in 1..n_dests where node = dest[n])(1) == 0) then % Better way to check if array contains value?
    sum(edge in 1..n_edges where to[edge] = node \/ from[edge] = node)(is_used[edge]) != 1
  endif
);

var 0..sum(edge in 1..n_edges)(cost[edge]): total_cost;

constraint
    total_cost = sum(i in 1..n_edges where is_used[i])(cost[i]);

solve minimize total_cost;

output[show(is_used), "\n", show(total_cost)];