import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class Photo {

    public static void main(String[] args) {
        int n = 9;
        int n_prefs = 17;
        int[][] prefs = {{1,3}, {1,5}, {1,8},
            {2,5}, {2,9}, {3,4}, {3,5}, {4,1},
            {4,5}, {5,6}, {5,1}, {6,1}, {6,9},
            {7,3}, {7,8}, {8,9}, {8,7}};


        int choice = 0;
        if (args.length >= 2) {
            choice = Integer.parseInt(args[1]);
        }
        switch (choice) {
            case 0:
                n = 9;
                n_prefs = 17;
                int[][] tmp = { { 1, 3 }, { 1, 5 }, { 1, 8 }, { 2, 5 }, { 2, 9 }, { 3, 4 }, { 3, 5 }, { 4, 1 }, { 4, 5 },
                        { 5, 6 }, { 5, 1 }, { 6, 1 }, { 6, 9 }, { 7, 3 }, { 7, 8 }, { 8, 9 }, { 8, 7 } };
                prefs = tmp;
                break;
            case 1:
                n = 11;
                n_prefs = 20;
                int[][] tmp2 = {{1,3}, {1,5}, {2,5},
                                {2,8}, {2,9}, {3,4}, {3,5}, {4,1},
                                {4,5}, {4,6}, {5,1}, {6,1}, {6,9},
                                {7,3}, {7,5}, {8,9}, {8,7}, {8,10},
                                {9, 11}, {10, 11}};
                prefs = tmp2;
                break;
            case 2:
                n = 15;
                n_prefs = 20;
                int[][] tmp3 =  {{1,3}, {1,5}, {2,5},
                                {2,8}, {2,9}, {3,4}, {3,5}, {4,1},
                                {4,15}, {4,13}, {5,1}, {6,10}, {6,9},
                                {7,3}, {7,5}, {8,9}, {8,7}, {8,14},
                                {9, 13}, {10, 11}};
                prefs = tmp3;
                break;
        }

        int choice2 = 1;

        if (args.length != 0) {
            choice2 = Integer.parseInt(args[0]);
        }

        if (choice2 == 1) {
            solve(n, n_prefs, prefs);
        } else if (choice2 == 2) {
            solve2(n, n_prefs, prefs);
        } else {
            System.out.println("not an option");
        }
    }

    private static void solve(int n, int n_prefs, int[][] prefs) {

        Store store = new Store();

        List<IntVar> persons = new ArrayList<IntVar>();

        for (int i = 1; i <= n; i++) {
            persons.add(new IntVar(store, "p_" + i, 1, n));
        }

        store.impose(new Alldiff(persons.toArray(new IntVar[0])));

        List<IntVar> relations = new ArrayList<IntVar>();

        for (int i = 1; i <= n_prefs; i++) {
            int pers0 = prefs[i - 1][0];
            int pers1 = prefs[i - 1][1];

            IntVar dist = new IntVar(store, "d_" + i, 1, n - 1);
            store.impose(new Distance(
                persons.get(pers0 - 1),
                persons.get(pers1 - 1),
                dist));

            IntVar rel = new IntVar(store, "r_" + i, 0, 1);
            relations.add(rel);

            store.impose(new Reified(new XeqC(dist, 1), rel));
        }

        IntVar totalRel = new IntVar(store, "total", -relations.size(), 0);

        List<IntVar> allVars = new ArrayList<IntVar>();
        allVars.addAll(persons);
        allVars.addAll(relations);

        int[] inverters = new int[n_prefs];
        Arrays.fill(inverters, -1);


        store.impose(new LinearInt(relations.toArray(new IntVar[0]),
                inverters,
                "==",totalRel));

        long T1, T2;
        T1 = System.currentTimeMillis();

        SelectChoicePoint<IntVar> select = new SimpleSelect<>(
            allVars.toArray(new IntVar[0]),
            null,
            new IndomainMin<>());

        Search<IntVar> search = new DepthFirstSearch<>();

        boolean result = search.labeling(store, select, totalRel);

        if (result)
            store.print();

        T2 = System.currentTimeMillis();

        System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
    }

    private static void solve2(int n, int n_prefs, int[][] prefs) {

        Store store = new Store();

        List<IntVar> persons = new ArrayList<IntVar>();

        for (int i = 1; i <= n; i++) {
            persons.add(new IntVar(store, "p_" + i, 1, n));
        }

        store.impose(new Alldiff(persons.toArray(new IntVar[0])));

        List<IntVar> distances = new ArrayList<IntVar>();

        for (int i = 1; i <= n_prefs; i++) {
            int pers0 = prefs[i - 1][0];
            int pers1 = prefs[i - 1][1];

            IntVar dist = new IntVar(store, "d_" + i, 1, n - 1);
            distances.add(dist);

            store.impose(new Distance(
                persons.get(pers0 - 1),
                persons.get(pers1 - 1),
                dist));
        }

        IntVar maxDist = new IntVar(store, "maxDist", 0, n - 1);

        List<IntVar> allVars = new ArrayList<IntVar>();
        allVars.addAll(persons);
        allVars.addAll(distances);

        int[] inverters = new int[n_prefs];
        Arrays.fill(inverters, -1);


        store.impose(new Max(distances.toArray(new IntVar[0]),
                maxDist));

        long T1, T2;
        T1 = System.currentTimeMillis();

        SelectChoicePoint<IntVar> select = new SimpleSelect<>(
            allVars.toArray(new IntVar[0]),
            null,
            new IndomainMin<>());

        Search<IntVar> search = new DepthFirstSearch<>();

        boolean result = search.labeling(store, select, maxDist);

        if (result)
            store.print();

        T2 = System.currentTimeMillis();

        System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
    }
}