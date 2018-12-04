import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

class Logistics {
    private static int[] GRAPH_SIZE = {6,6,6};
    private static int[] START = {1,1,1};
    private static int[] N_DESTS = {1,2,2};
    private static int[][] DEST ={{6},{5,6},{5,6}};
    private static int[] N_EDGES = {7,7,9};
    private static int[][] FROM = {{1,1,2,2,3,4,4},{1,1,2,2,3,4,4},{1,1,1,2,2,3,3,3,4}};
    private static int[][] TO = {{2,3,3,4,5,5,6},{2,3,3,4,5,5,6},{2,3,4,3,5,4,5,6,6}};
    private static int[][] COST = {{4,2,5,10,3,4,11},{4,2,5,10,3,4,11}, {6,1,5,5,3,5,6,4,2}};

    private static int graph_size;
    private static int start;
    private static int n_dests;
    private static int[] dest;
    private static int n_edges;
    private static int[] from;
    private static int[] to;
    private static int[] cost;

    private static int max_cost;
    private static int[][] cost_matrix;

    public static void main(String[] args) {

        int choice = Integer.parseInt(args[0]) - 1;

        graph_size = GRAPH_SIZE[choice];
        start = START[choice];
        n_dests = N_DESTS[choice];
        dest = DEST[choice];
        n_edges = N_EDGES[choice];
        from = FROM[choice];
        to = TO[choice];
        cost = COST[choice];

        generate_cost_matrix();
        generate_max_cost();

        solve();
    }

    private static void generate_cost_matrix() {
        cost_matrix = new int[graph_size][graph_size];

        //Fill with -1 (no connection)
        for (int row = 0; row < graph_size; row++) {
            for (int col = 0; col < graph_size; col++) {
                cost_matrix[row][col] = -1;
            }
        }

        //Add cost for edges in both directions
        for (int i = 0; i < n_edges; i++) {
            cost_matrix[from[i] - 1][to[i] - 1] = cost[i];
            cost_matrix[to[i] - 1][from[i] - 1] = cost[i];
        }

        //Add a zero cost from all destinations to
        //start to make the sub circuits circular.
        for (int d: dest) {
            cost_matrix[d-1][start-1] = 0;
        }

        //Allow nodes to go to them self for free in case they aren't part of main subcircuit.
        for (int i = 0; i < graph_size; i++) {
            cost_matrix[i][i] = 0;
        }
    }

    private static void generate_max_cost() {
        for (int c : cost) {
            max_cost += c;
        }
    }

    private static void solve() {
        Store store = new Store();

        //2D vector for saving the sub circuit used to reach a destination.
        IntVar[][] sub_circuits = new IntVar[n_dests][graph_size];

        //Matrix to store which edges are used.
        BooleanVar[][] used_edges = new BooleanVar[graph_size][graph_size];

        //Create variables for sub circuits.
        for (int dest_n = 0; dest_n < n_dests; dest_n++) {
            for (int node_n = 0; node_n < graph_size; node_n++) {
                sub_circuits[dest_n][node_n] = new IntVar(store, "To dest " + dest[dest_n] + ", node " + (node_n + 1), 1, graph_size);
            }
        }

        //Create variables for which edges are used.
        for (int row = 0; row < graph_size; row++) {
            for (int col = 0; col < graph_size; col++) {
                used_edges[row][col] = new BooleanVar(store, "Used from " + (row + 1) + " to " + (col + 1));
            }
        }

        //Constraints for sub circuits
        for (int dest_n = 0; dest_n < n_dests; dest_n++) {
            //Constraint: start can't go to start
            store.impose(new XneqC(sub_circuits[dest_n][start-1], start));

            //Constraint: dest can't go to dest
            store.impose(new XneqC(sub_circuits[dest_n][dest[dest_n]-1], dest[dest_n]));

            //Constraint: ensures a subcircuit for each dest
            store.impose(new Subcircuit(sub_circuits[dest_n]));

            //Constraint: don't allow two nodes to connect to each other if cost == -1.
            for (int f = 0; f < graph_size; f++) {
                for (int t = 0; t < graph_size; t++) {
                    if (cost_matrix[f][t] == -1) {
                        store.impose(new XneqC(sub_circuits[dest_n][f], t + 1));
                    }
                }
            }
        }

        //Constraint: if an edge is included in a sub circuit, set edge to used.
        for (int f = 0; f < graph_size; f++) {
            for (int t = 0; t < graph_size; t++) {
                XeqC[] all_dests = new XeqC[n_dests];
                for (int dest_n = 0; dest_n < n_dests; dest_n++) {
                    all_dests[dest_n] = new XeqC(sub_circuits[dest_n][f], t + 1);
                }
                store.impose(new Reified(new Or(all_dests), used_edges[f][t]));
            }
        }

        //Variable for total cost
        IntVar total_cost = new IntVar(store, "total cost", 0, max_cost);

        store.impose(new LinearInt(flat_used_edges(used_edges), flat_cost(), "==", total_cost));

        SelectChoicePoint<IntVar> select =
                new SimpleSelect<IntVar>(flat_sub_circuits(sub_circuits),
                        null,
                        new IndomainMin<IntVar>()
                );

        Search<IntVar> search = new DepthFirstSearch<>();

        boolean result = search.labeling(store,select,total_cost);

        if(result){
            System.out.println("\n**** YES");
            System.out.println("Solution : " + java.util.Arrays.asList(flat_sub_circuits(sub_circuits)));
        }else{
            System.out.println("NO SOLUTION");
        }
    }

    private static IntVar[] flat_used_edges(BooleanVar[][] used_edges) {
        IntVar[] flat_used_edges = new IntVar[graph_size * graph_size];
        for (int f = 0; f < graph_size; f++) {
            for(int t = 0; t < graph_size; t++) {
                flat_used_edges[f*graph_size + t] = used_edges[f][t];
            }
        }
        return flat_used_edges;
    }

    private static IntVar[] flat_sub_circuits(IntVar[][] sub_circuits) {
        IntVar[] flat_sub_circuits = new IntVar[n_dests * graph_size];
        for (int d = 0; d < n_dests; d++) {
            for(int n = 0; n < graph_size; n++) {
                flat_sub_circuits[d*graph_size + n] = sub_circuits[d][n];
            }
        }
        return flat_sub_circuits;
    }

    private static int[] flat_cost() {
        int[] flat_cost = new int[graph_size * graph_size];
        for (int f = 0; f < graph_size; f++) {
            for(int t = 0; t < graph_size; t++) {
                flat_cost[f*graph_size + t] = cost_matrix[f][t];
            }
        }
        return flat_cost;
    }
}