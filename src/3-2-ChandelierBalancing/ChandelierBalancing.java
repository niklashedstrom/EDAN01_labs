import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

class ChandelierBalancing {

    public static void main(String[] args) {

        Store store = new Store();

        IntVar a = new IntVar(store, "a", 1, 9);
        IntVar b = new IntVar(store, "b", 1, 9);
        IntVar c = new IntVar(store, "c", 1, 9);
        IntVar d = new IntVar(store, "d", 1, 9);
        IntVar e = new IntVar(store, "e", 1, 9);
        IntVar f = new IntVar(store, "f", 1, 9);
        IntVar g = new IntVar(store, "g", 1, 9);
        IntVar h = new IntVar(store, "h", 1, 9);
        IntVar i = new IntVar(store, "i", 1, 9);

        IntVar[] weights = { a, b, c, d, e, f, g, h, i };

        store.impose(new Alldiff(weights));

        // left sub chandelier
        store.impose(new LinearInt(new IntVar[] { a, b, c }, new int[] { 2, -1, -2 }, "==", 0));

        // left right sub chandelier
        store.impose(new LinearInt(new IntVar[] { d, e, f }, new int[] { 2, 1, -1 }, "==", 0));

        // right right sub chandelier
        store.impose(new LinearInt(new IntVar[] { g, h, i }, new int[] { 2, 1, -3 }, "==", 0));

        // Total balance
        store.impose(new LinearInt(weights, new int[] { 3, 3, 3, -2, -2, -2, -3, -3, -3 }, "==", 0));

        System.out.println(
                "Number of variables: " + store.size() + "\nNumber of constraints: " + store.numberConstraints());
        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(weights, null, new IndomainMin<IntVar>());
        search.setSolutionListener(new PrintOutListener<IntVar>());
        boolean Result = search.labeling(store, select);
        if (Result) {
            System.out.println("\n*** Yes");
            System.out.println("Solution : " + java.util.Arrays.asList(weights));
        }
        else System.out.println("\n*** No");
    }
}