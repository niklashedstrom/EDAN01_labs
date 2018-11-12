import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;


public class GoodBurger {


    public static void main(String[] args) {

        Store store = new Store();

        IntVar beef = new IntVar(store, "Beef", 1, 5);
        IntVar bun = new IntVar(store, "Bun", 1, 5 ;
        IntVar cheese = new IntVar(store, "Cheese", 1, 5);
        IntVar onions = new IntVar(store, "Onions", 1, 5);
        IntVar pickles = new IntVar(store, "Pickles", 1, 5);
        IntVar lettuce = new IntVar(store, "Lettuce", 1, 5);
        IntVar ketchup  = new IntVar(store, "Ketchup", 1, 5);
        IntVar tomato   = new IntVar(store, "Tomato", 1, 5);

        IntVar[] digits = {beef, bun, cheese, onions, pickles, lettuce, ketchup, tomato};
    }

}
