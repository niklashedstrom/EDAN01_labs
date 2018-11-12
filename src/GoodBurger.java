import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;


public class GoodBurger {


    public static void main(String[] args) {

        Store store = new Store();

        IntVar beef = new IntVar(store, "Beef", 1, 5);
        IntVar bun = new IntVar(store, "Bun", 1, 5);
        IntVar cheese = new IntVar(store, "Cheese", 1, 5);
        IntVar onions = new IntVar(store, "Onions", 1, 5);
        IntVar pickles = new IntVar(store, "Pickles", 1, 5);
        IntVar lettuce = new IntVar(store, "Lettuce", 1, 5);
        IntVar ketchup  = new IntVar(store, "Ketchup", 1, 5);
        IntVar tomato   = new IntVar(store, "Tomato", 1, 5);

        IntVar[] ingredients = {beef, bun, cheese, onions, pickles, lettuce, ketchup, tomato};

        //Sodium constraint
        store.impose(new LinearInt(
            ingredients,
            new int[] {50, 330, 310, 1, 260, 3, 160, 3},
            "<", 3000));

        //Fat constraint
        store.impose(new LinearInt(
            ingredients,
            new int[] {17, 9, 6, 2, 0, 0, 0, 0},
            "<", 150));

        //Calories constraint
        store.impose(new LinearInt(
            ingredients,
            new int[] {220, 260, 70, 10, 5, 4, 20, 9},
            "<", 3000));

        //Ketchup == Lettuce
        store.impose(new XeqY(ketchup, lettuce));

        //Pickles == Tomatoes
        store.impose(new XeqY(pickles, tomato));

        //Cost
        IntVar cost = new IntVar(store, "cost", -5*(25+15+10+9+3+4+2+4), 0);

        store.impose(new LinearInt(
            ingredients,
            new int[] {-25, -15, -10, -9, -3, -4, -2, -4},
            "==", cost));

        long T1, T2;
        T1 = System.currentTimeMillis();

        SelectChoicePoint<IntVar> select = new SimpleSelect<>(
            ingredients,
            null,
            new IndomainMin<>());

        Search<IntVar> search = new DepthFirstSearch<>();

        boolean result = search.labeling(store, select, cost);

        if (result)
            store.print();

        T2 = System.currentTimeMillis();

        System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
    }

}
