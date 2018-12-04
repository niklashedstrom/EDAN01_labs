include "globals.mzn";

var 1..9: a;
var 1..9: b;
var 1..9: c;
var 1..9: d;
var 1..9: e;
var 1..9: f;
var 1..9: g;
var 1..9: h;
var 1..9: i;

array[1..9] of var int : weights = [
    a,b,c,d,e,f,g,h,i
];

% All are different
constraint all_different(weights);

% Left sub chandelier
constraint a*2 = b + c*2;

% Left right sub chandelier
constraint d*2 + e = f;

% Right right sub chandelier
constraint g*2 + h = i*3;

% Total balance
constraint
    a*3 +
    b*3 +
    c*3 =
    d*2 +
    e*2 +
    f*2 +
    g*3 +
    h*3 +
    i*3;

solve satisfy;

output[show(weights)];