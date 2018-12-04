include "globals.mzn";
include "Photo1.dzn";

int: n;
int: n_prefs;
array[1..n_prefs, 1..2] of 1..n: prefs;

% Create persons array
array[1..n] of var 1..n: persons;

constraint all_different(persons);

% Create distances array
set of int: PreferencesIndex = 1..n_prefs;
array[PreferencesIndex] of var 1..(n-1): distances;

constraint forall(i in PreferencesIndex)(
    abs(persons[prefs[i, 1]] - persons[prefs[i, 2]]) = distances[i]
);

% Create preferences array
array[PreferencesIndex] of var 0..1: preferences;

constraint forall(i in PreferencesIndex)(
    if distances[i] == 1 then
        preferences[i] = 1
    else
        preferences[i] = 0
    endif);

var 0..n_prefs: total;

constraint total = sum(i in 1..n_prefs)(preferences[i]);

%Max
var 1..(n-1): maxDist;

constraint maxDist = max(i in PreferencesIndex)(distances[i]);

solve maximize total;
output[show(total)];

%solve minimize maxDist;
%output[show(maxDist)];