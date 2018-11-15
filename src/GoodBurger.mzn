include "globals.mzn";

var 1..5: beef;
var 1..5: bun;
var 1..5: cheese;
var 1..5: onions;
var 1..5: pickles;
var 1..5: lettuce;
var 1..5: ketchup;
var 1..5: tomato;
var 0..360: cost;

array[1..8] of var int : ingredients =
    [
        beef,
        bun,
        cheese,
        onions,
        pickles,
        lettuce,
        ketchup,
        tomato
    ];

% Sodium
constraint
    beef*50 + bun*330 + cheese*310 +
    onions + pickles*260 + lettuce*3 +
    ketchup*160 + tomato*3 < 3000;

% Fat
constraint
    beef*17 + bun*9 + cheese*6 +
    onions*2 < 150;

% Calories
constraint
    beef*220 + bun*260 + cheese*70 +
    onions*10 + pickles*5 + lettuce*4 +
    ketchup*20 + tomato*9 < 3000;

constraint
    ketchup = lettuce /\
    pickles = tomato;

% cost
constraint
    beef*25 + bun*15 + cheese*10 +
    onions*9 + pickles*3 + lettuce*4 +
    ketchup*2 + tomato*4 = cost;

solve maximize cost;

output[show(ingredients)];
output[show(cost)];
